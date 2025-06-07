package com.example.project_c0824m1_jv103.service.product;

import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.model.ProductImages;
import com.example.project_c0824m1_jv103.repository.IProductImagesRepository;
import com.example.project_c0824m1_jv103.repository.IProductRepository;
import com.example.project_c0824m1_jv103.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IProductImagesRepository productImagesRepository;

    @Autowired
    private CloudinaryService cloudinaryService;
    @Override
    public Product createProduct(Product product, List<MultipartFile> imageFiles, List<String> captions) throws IOException {
        // Lưu sản phẩm trước
        Product savedProduct = productRepository.save(product);

        // Tải ảnh lên Cloudinary và lưu vào ProductImages
        List<ProductImages> productImages = new ArrayList<>();
        for (int i = 0; i < imageFiles.size(); i++) {
            MultipartFile file = imageFiles.get(i);
            String caption = (captions != null && i < captions.size()) ? captions.get(i) : null;

            // Upload ảnh lên Cloudinary
            String imageUrl = cloudinaryService.uploadImage(file);

            // Tạo ProductImages
            ProductImages productImage = new ProductImages(savedProduct, imageUrl, caption);
            productImages.add(productImage);
        }

        // Lưu danh sách ProductImages
        productImagesRepository.saveAll(productImages);
        savedProduct.setProductImages(productImages);

        return savedProduct;
    }
}