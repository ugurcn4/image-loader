package com.example.imageloader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // json formattan java nesnelerine çevir tanımsızları es geç 
public class Image {
    private String id;
    private Src src;

    // Getter ve Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Src getSrc() {
        return src;
    }

    public void setSrc(Src src) {
        this.src = src;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Src {
        private String original; // Daha yüksek çözünürlüklü

        // Getter ve Setter
        public String getLarge() {
            return original;
        }

        public void setLarge(String original) {
            this.original = original;
        }
    }
}