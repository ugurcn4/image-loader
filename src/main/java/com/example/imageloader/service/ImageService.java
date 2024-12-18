package com.example.imageloader.service;

import com.example.imageloader.model.Image;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class ImageService {

    @Value("${pexels.api.url}")
    private String apiUrl;

    @Value("${pexels.api.key}")
    private String apiKey;

    private final ExecutorService executorService;

    public ImageService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    // Senkron
    public List<Image> loadImagesSync(String query, int page, int perPage, String orientation, String size, String color, String locale) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = buildUrl(query, page, perPage, orientation, size, color, locale);
        String jsonResponse = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        ObjectMapper mapper = new ObjectMapper();
        PexelsResponse response;
        try {
            response = mapper.readValue(jsonResponse, PexelsResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        }
        List<Image> images = Arrays.asList(response.getPhotos());
        for (Image image : images) {
            try {
                Thread.sleep(2000); // 2 saniye bekle
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted", e);
            }
        }
        return images;
    }

    // Asenkron
    @Async
    public CompletableFuture<List<Image>> loadImagesAsync(String query, int page, int perPage, String orientation, String size, String color, String locale) {
        return CompletableFuture.supplyAsync(() -> {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String url = buildUrl(query, page, perPage, orientation, size, color, locale);
            String jsonResponse = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
            ObjectMapper mapper = new ObjectMapper();
            PexelsResponse response;
            try {
                response = mapper.readValue(jsonResponse, PexelsResponse.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse JSON response", e);
            }
            List<Image> images = Arrays.asList(response.getPhotos());
            images.parallelStream().forEach(image -> {
                try {
                    Thread.sleep(2000); // 2 saniye bekle
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted", e);
                }
            });
            return images;
        }, executorService);
    }

    private String buildUrl(String query, int page, int perPage, String orientation, String size, String color, String locale) {
        StringBuilder url = new StringBuilder(apiUrl + query + "&page=" + page + "&per_page=" + perPage);
        if (orientation != null && !orientation.isEmpty()) url.append("&orientation=").append(orientation);
        if (size != null && !size.isEmpty()) url.append("&size=").append(size);
        if (color != null && !color.isEmpty()) url.append("&color=").append(color);
        if (locale != null && !locale.isEmpty()) url.append("&locale=").append(locale);
        return url.toString();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PexelsResponse {
        private Image[] photos;

        public Image[] getPhotos() {
            return photos;
        }

        public void setPhotos(Image[] photos) {
            this.photos = photos;
        }
    }
}