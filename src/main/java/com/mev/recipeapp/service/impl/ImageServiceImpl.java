package com.mev.recipeapp.service.impl;

import com.mev.recipeapp.models.Recipe;
import com.mev.recipeapp.repository.RecipeRepository;
import com.mev.recipeapp.service.ImageService;
import com.mev.recipeapp.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {

    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png");
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of(".jpg", ".jpeg", ".png");

    private final RecipeRepository recipeRepository;

    @Value("${app.image-upload-dir}")
    private String imageUploadDir;

    @Override
    public String saveImage(MultipartFile file) {

        try {
            validateImage(file);

            Path uploadPath = Paths.get(imageUploadDir);

            String newFileName = generateFileName(file);
            Path filePath = uploadPath.resolve(newFileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Greška prilikom spremanja slike", e);
        }
    }

    @Override
    public String saveImageAndDeleteExisting(MultipartFile file, Long recipeId) {
        deleteImage(recipeId);

        return saveImage(file);
    }

    @Override
    public void deleteImage(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + recipeId));

    }

    @Override
    public void deleteImageByPath(String path) {
        if (path == null || path.isBlank()) return;
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateFileName(MultipartFile file) {

        String originalFileName = file.getOriginalFilename();
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        } else {
            throw new RuntimeException("Ne postoji originalni naziv");
        }

        String newFileName = UUID.randomUUID().toString() + extension;

        return newFileName;
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (file.isEmpty() || contentType == null ) {
            throw new RuntimeException("Niste predali sliku!");
        }
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Nepodrzani format slike: " + contentType);
        }

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Nepodrzani format slike: " + extension);
        }
    }
}
