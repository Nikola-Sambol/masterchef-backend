package com.mev.recipeapp.service;

import org.springframework.web.multipart.MultipartFile;

public interface VideoService {

    String saveVideo(MultipartFile file);

    String saveVideoAndDeleteExisting(MultipartFile file, Long recipeId);

    void deleteVideo(Long recipeId);
}
