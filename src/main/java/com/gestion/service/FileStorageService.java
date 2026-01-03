package com.gestion.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.audio-path}")
    private String audioPath;

    @Value("${app.upload.image-path}")
    private String imagePath;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(audioPath));
            Files.createDirectories(Paths.get(imagePath));
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer les dossiers d'upload", e);
        }
    }

    public String storeAudio(MultipartFile file) {
        return storeFile(file, audioPath, "audio");
    }

    public String storeImage(MultipartFile file) {
        return storeFile(file, imagePath, "image");
    }

    private String storeFile(MultipartFile file, String uploadDir, String type) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String filename = UUID.randomUUID().toString() + extension;
            Path targetPath = Paths.get(uploadDir).resolve(filename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + type + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du stockage du fichier", e);
        }
    }

    public void deleteFile(String filePath) {
        try {
            if (filePath != null && filePath.startsWith("/uploads/")) {
                String relativePath = filePath.replace("/uploads/audio/", audioPath)
                        .replace("/uploads/images/", imagePath);
                Files.deleteIfExists(Paths.get(relativePath));
            }
        } catch (IOException e) {
            // Log error but don't throw
        }
    }
}
