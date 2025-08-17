package com.mev.recipeapp.service.impl;

import com.mev.recipeapp.models.Recipe;
import com.mev.recipeapp.repository.RecipeRepository;
import com.mev.recipeapp.service.VideoService;
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
public class VideoServiceImpl implements VideoService {

    private static final List<String> ALLOWED_VIDEO_TYPES = List.of("video/mp4", "video/webm");
    private static final List<String> ALLOWED_VIDEO_EXTENSIONS = List.of(".mp4", ".webm");

    private final RecipeRepository recipeRepository;

    @Value("${app.video-upload-dir}")
    private String videoUploadDir;

    @Override
    public String saveVideo(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            validateVideo(file);

            Path uploadPath = Paths.get(videoUploadDir);

            String newFileName = generateFileName(file);
            Path filePath = uploadPath.resolve(newFileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Greška prilikom spremanja videa", e);
        }
    }

    @Override
    public String saveVideoAndDeleteExisting(MultipartFile file, Long recipeId) {
        deleteVideo(recipeId);

        return saveVideo(file);
    }

    @Override
    public void deleteVideo(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + recipeId));
        String existingImagePath = recipe.getVideoPath();
        try {
            Path deletePath = Paths.get(existingImagePath);

            Files.delete(deletePath);
        } catch (IOException e) {
            log.error("Greška prilikom brisanja slike: " + existingImagePath, e);
        }
    }

    private String generateFileName(MultipartFile file) {

        String originalFileName = file.getOriginalFilename();
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String newFileName = UUID.randomUUID().toString() + extension;

        return newFileName;
    }

    private void validateVideo(MultipartFile file) {
        String contentType = file.getContentType();

        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        if (!ALLOWED_VIDEO_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Nepodrzani format videa: " + contentType);
        }

        if (!ALLOWED_VIDEO_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Nepodrzani format videa: " + extension);
        }
    }
}
