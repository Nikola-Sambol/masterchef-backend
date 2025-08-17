package com.mev.recipeapp.controllers;

import com.mev.recipeapp.dtos.ComponentDTO;
import com.mev.recipeapp.dtos.requests.CreateComponentRequest;
import com.mev.recipeapp.service.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/components")
@RequiredArgsConstructor
public class ComponentController {

    private final ComponentService componentService;
    // TODO Update brisanje slika kod updatea komponente
    @GetMapping("/public/{recipeId}")
    public ResponseEntity<List<ComponentDTO>> getComponentsForRecipe(@PathVariable Long recipeId) {
        List<ComponentDTO> components = componentService.getComponentsForRecipe(recipeId);
        return ResponseEntity.ok(components);
    }

    @PostMapping("/{recipeId}")
    public void createComponent(@PathVariable Long recipeId,
                             @RequestParam MultiValueMap<String, String> data,
                             @RequestParam Map<String, MultipartFile> images) {

        List<CreateComponentRequest> components = componentService.parseComponents(data, images);
        componentService.saveComponents(recipeId, components);

    }

    @PostMapping("/update/{recipeId}")
    public void updateComponent(@PathVariable Long recipeId,
                                @RequestParam MultiValueMap<String, String> data,
                                @RequestParam Map<String, MultipartFile> images) {

        List<CreateComponentRequest> components = componentService.parseComponents(data, images);
        componentService.updateComponents(recipeId, components);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteComponent(@PathVariable Long id) {
        componentService.deleteComponent(id);
    }
}
