package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    /**
     * Upload ảnh sản phẩm
     */
    @PostMapping("/upload/product")
    public ResponseEntity<Map<String, Object>> uploadProductImage(
            @RequestParam("file") MultipartFile file) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Vui lòng chọn file ảnh");
                return ResponseEntity.badRequest().body(response);
            }

            String imageUrl = cloudinaryService.uploadImage(file, "product-images");
            
            response.put("success", true);
            response.put("message", "Upload ảnh thành công");
            response.put("imageUrl", imageUrl);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi upload ảnh: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Upload avatar employee
     */
    @PostMapping("/upload/avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatarImage(
            @RequestParam("file") MultipartFile file) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Vui lòng chọn file ảnh");
                return ResponseEntity.badRequest().body(response);
            }

            // Upload avatar với kích thước 300x300
            String imageUrl = cloudinaryService.uploadImageWithSize(file, "employee-avatars", 300, 300);
            
            response.put("success", true);
            response.put("message", "Upload avatar thành công");
            response.put("imageUrl", imageUrl);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi upload avatar: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Upload nhiều ảnh cùng lúc
     */
    @PostMapping("/upload/multiple")
    public ResponseEntity<Map<String, Object>> uploadMultipleImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "folder", defaultValue = "uploads") String folder) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (files.length == 0) {
                response.put("success", false);
                response.put("message", "Vui lòng chọn ít nhất 1 file ảnh");
                return ResponseEntity.badRequest().body(response);
            }

            Map<String, String> uploadedImages = new HashMap<>();
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String imageUrl = cloudinaryService.uploadImage(file, folder);
                    uploadedImages.put(file.getOriginalFilename(), imageUrl);
                }
            }
            
            response.put("success", true);
            response.put("message", "Upload " + uploadedImages.size() + " ảnh thành công");
            response.put("images", uploadedImages);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi upload ảnh: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Xóa ảnh
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteImage(
            @RequestParam("imageUrl") String imageUrl) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = cloudinaryService.deleteImageByUrl(imageUrl);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "Xóa ảnh thành công");
            } else {
                response.put("success", false);
                response.put("message", "Không thể xóa ảnh");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi xóa ảnh: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Tạo URL với kích thước khác
     */
    @GetMapping("/transform")
    public ResponseEntity<Map<String, Object>> getTransformedUrl(
            @RequestParam("publicId") String publicId,
            @RequestParam(value = "width", defaultValue = "400") int width,
            @RequestParam(value = "height", defaultValue = "400") int height,
            @RequestParam(value = "crop", defaultValue = "fill") String crop) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String transformedUrl = cloudinaryService.getTransformedUrl(publicId, width, height, crop);
            
            response.put("success", true);
            response.put("transformedUrl", transformedUrl);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi tạo URL: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 