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

    /**
     * Upload ảnh lên Cloudinary
     * @param file File ảnh cần upload
     * @param folder Thư mục lưu trữ (vd: "product-images", "employee-avatars")
     * @return URL công khai của ảnh đã upload
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh");
        }

        // Validate file size (10MB limit)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File quá lớn! Tối đa 10MB");
        }

        try {
            // Upload với transformation options
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "image",
                    "quality", "auto:good",
                    "fetch_format", "auto",
                    "width", 1200,
                    "height", 1200,
                    "crop", "limit"
                )
            );

            return (String) uploadResult.get("secure_url");

        } catch (Exception e) {
            throw new IOException("Lỗi upload ảnh: " + e.getMessage(), e);
        }
    }

    /**
     * Upload ảnh với kích thước tùy chỉnh
     */
    public String uploadImageWithSize(MultipartFile file, String folder, int width, int height) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "image",
                    "quality", "auto:good",
                    "fetch_format", "auto",
                    "width", width,
                    "height", height,
                    "crop", "fill"
                )
            );

            return (String) uploadResult.get("secure_url");

        } catch (Exception e) {
            throw new IOException("Lỗi upload ảnh: " + e.getMessage(), e);
        }
    }

    /**
     * Xóa ảnh từ Cloudinary
     * @param publicId Public ID của ảnh (extract từ URL)
     */
    public boolean deleteImage(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa ảnh bằng URL
     */
    public boolean deleteImageByUrl(String imageUrl) {
        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            return deleteImage(publicId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extract public ID từ Cloudinary URL
     * URL format: https://res.cloudinary.com/cloud-name/image/upload/v123456/folder/filename.jpg
     */
    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            // Find the start of public ID (after '/upload/')
            int uploadIndex = imageUrl.indexOf("/upload/");
            if (uploadIndex == -1) return null;

            String afterUpload = imageUrl.substring(uploadIndex + 8);
            
            // Remove version if exists (v123456/)
            if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
                int versionEnd = afterUpload.indexOf("/") + 1;
                afterUpload = afterUpload.substring(versionEnd);
            }

            // Remove file extension
            int dotIndex = afterUpload.lastIndexOf(".");
            if (dotIndex > 0) {
                afterUpload = afterUpload.substring(0, dotIndex);
            }

            return afterUpload;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Tạo URL với transformation
     */
    public String getTransformedUrl(String publicId, int width, int height, String crop) {
        return cloudinary.url()
            .transformation(
                ObjectUtils.asMap(
                    "width", width,
                    "height", height,
                    "crop", crop,
                    "quality", "auto:good",
                    "fetch_format", "auto"
                )
            )
            .generate(publicId);
    }
} 