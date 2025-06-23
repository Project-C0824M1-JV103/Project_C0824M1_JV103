package com.example.project_c0824m1_jv103.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        System.out.println("=== CLOUDINARY UPLOAD START ===");
        System.out.println("File name: " + file.getOriginalFilename());
        System.out.println("File size: " + file.getSize() + " bytes");
        System.out.println("Content type: " + file.getContentType());
        System.out.println("Uploading to Cloudinary...");
        
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            
            String imageUrl = uploadResult.get("url").toString();
            String publicId = uploadResult.get("public_id").toString();
            
            System.out.println("UPLOAD SUCCESS!");
            System.out.println("Public ID: " + publicId);
            System.out.println("Image URL: " + imageUrl);
            System.out.println("Upload timestamp: " + new java.util.Date());
            System.out.println("=== CLOUDINARY UPLOAD END ===");
            
            return imageUrl;
            
        } catch (IOException e) {
            System.err.println("UPLOAD FAILED!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("=== CLOUDINARY UPLOAD END ===");
            throw e;
        }
    }
}
