package com.example.imageloader.controller;

import com.example.imageloader.model.Image;
import com.example.imageloader.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/loadImages")
    @ResponseBody
    public CompletableFuture<List<Image>> loadImages(@RequestParam String query,
                                                     @RequestParam int page,
                                                     @RequestParam int perPage,
                                                     @RequestParam(required = false) String orientation,
                                                     @RequestParam(required = false) String size,
                                                     @RequestParam(required = false) String color,
                                                     @RequestParam(required = false) String locale) {
        boolean isComplexQuery = isComplexQuery(orientation, size, color, locale);

        if (isComplexQuery) {
            // Asenkron işlem
            return imageService.loadImagesAsync(query, page, perPage, orientation, size, color, locale);
        } else {
            // Senkron işlem
            return CompletableFuture.completedFuture(imageService.loadImagesSync(query, page, perPage, orientation, size, color, locale));
        }
    }

    private boolean isComplexQuery(String orientation, String size, String color, String locale) {
        // Filtreleme seçeneklerinin varlığına göre sorgunun karmaşıklığını belirleme
        return (orientation != null && !orientation.isEmpty()) ||
               (size != null && !size.isEmpty()) ||
               (color != null && !color.isEmpty()) ||
               (locale != null && !locale.isEmpty());
    }
}