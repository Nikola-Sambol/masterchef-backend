package com.mev.recipeapp.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    String saveImage(MultipartFile file);

    String saveImageAndDeleteExisting(MultipartFile file, Long recipeId);

    void deleteImage(Long recipeId);

    void deleteImageByPath(String path);
}
