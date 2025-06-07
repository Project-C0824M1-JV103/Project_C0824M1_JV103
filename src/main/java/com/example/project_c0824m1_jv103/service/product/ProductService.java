package com.example.project_c0824m1_jv103.service.product;

import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.model.ProductImages;
import com.example.project_c0824m1_jv103.repository.IProductImagesRepository;
import com.example.project_c0824m1_jv103.repository.IProductRepository;
//import com.example.project_c0824m1_jv103.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

//    @Autowired
//    private CloudinaryService cloudinaryService;

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
//            String imageUrl = cloudinaryService.uploadImage(file);

            // Tạo ProductImages
            ProductImages productImage = new ProductImages(savedProduct, null, caption);
            productImages.add(productImage);
        }

        // Lưu danh sách ProductImages
        productImagesRepository.saveAll(productImages);
        savedProduct.setProductImages(productImages);

        return savedProduct;
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> searchProducts(String keyword, String field, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return productRepository.findAll(pageable);
        }
        switch (field) {
            case "productName":
                return productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
            case "price":
                try {
                    Double price = Double.parseDouble(keyword);
                    return productRepository.findByPriceLessThanEqual(price, pageable);
                } catch (NumberFormatException e) {
                    return Page.empty();
                }
            case "quantity":
                try {
                    Integer quantity = Integer.parseInt(keyword);
                    return productRepository.findByQuantityLessThanEqual(quantity, pageable);
                } catch (NumberFormatException e) {
                    return Page.empty();
                }
            default:
                return productRepository.findAll(pageable);
        }
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id.intValue()).orElse(null);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id.intValue());
    }
}