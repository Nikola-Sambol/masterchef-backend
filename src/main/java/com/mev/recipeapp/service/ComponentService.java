package com.mev.recipeapp.service;

import com.mev.recipeapp.dtos.ComponentDTO;
import com.mev.recipeapp.dtos.requests.CreateComponentRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ComponentService {


    List<CreateComponentRequest> parseComponents(
            MultiValueMap<String, String> formMap,
            Map<String, MultipartFile> fileMap
    );

    void saveComponents(Long recipeId, List<CreateComponentRequest> components);

    List<ComponentDTO> getComponentsForRecipe(Long recipeId);

    void updateComponents(Long recipeId, List<CreateComponentRequest> components);

    void deleteComponent(Long componentId);
}
