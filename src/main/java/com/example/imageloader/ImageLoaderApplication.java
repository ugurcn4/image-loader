package com.example.imageloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync; //asenkron aktif

@SpringBootApplication
@EnableAsync // Asenkron işlemleri etkinleştirir
public class ImageLoaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageLoaderApplication.class, args);
        System.out.println("Görsel Yükleyici Uygulaması Başladı!");
    }
}
