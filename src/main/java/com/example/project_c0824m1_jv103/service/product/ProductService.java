package com.example.project_c0824m1_jv103.service.product;

import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.mapper.ProductMapper;
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
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IProductImagesRepository productImagesRepository;
    
    @Autowired
    private ProductMapper productMapper;

//    @Autowired
//    private CloudinaryService cloudinaryService;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO, List<MultipartFile> imageFiles, List<String> captions) throws IOException {
        // Convert DTO to Entity
        Product product = productMapper.toEntity(productDTO);
        
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

        // Convert Entity back to DTO
        return productMapper.toDTO(savedProduct);
    }

    @Override
    public Page<ProductDTO> findAll(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::toDTO);
    }

    @Override
    public Page<ProductDTO> searchProducts(String keyword, String field, Pageable pageable) {
        Page<Product> productPage;
        
        if (keyword == null || keyword.isEmpty()) {
            productPage = productRepository.findAll(pageable);
        } else {
            switch (field) {
                case "productName":
                    productPage = productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
                    break;
                case "price":
                    try {
                        Double price = Double.parseDouble(keyword);
                        productPage = productRepository.findByPriceLessThanEqual(price, pageable);
                    } catch (NumberFormatException e) {
                        return Page.empty();
                    }
                    break;
                case "quantity":
                    try {
                        Integer quantity = Integer.parseInt(keyword);
                        productPage = productRepository.findByQuantityLessThanEqual(quantity, pageable);
                    } catch (NumberFormatException e) {
                        return Page.empty();
                    }
                    break;
                default:
                    productPage = productRepository.findAll(pageable);
            }
        }
        
        return productPage.map(productMapper::toDTO);
    }

    @Override
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id.intValue()).orElse(null);
        return product != null ? productMapper.toDTO(product) : null;
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO, List<MultipartFile> imageFiles, List<String> captions) throws IOException {
        // Tìm product hiện tại
        Product existingProduct = productRepository.findById(id.intValue()).orElse(null);
        if (existingProduct == null) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }

        // Cập nhật thông tin từ DTO
        productMapper.updateEntityFromDTO(productDTO, existingProduct);

        // Lưu sản phẩm đã cập nhật
        Product updatedProduct = productRepository.save(existingProduct);

        // Xử lý ảnh mới (nếu có)
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<ProductImages> newProductImages = new ArrayList<>();
            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile file = imageFiles.get(i);
                if (!file.isEmpty()) {
                    String caption = (captions != null && i < captions.size()) ? captions.get(i) : null;
                    
                    // Upload ảnh lên Cloudinary
//                    String imageUrl = cloudinaryService.uploadImage(file);
                    
                    // Tạo ProductImages mới
                    ProductImages productImage = new ProductImages(updatedProduct, null, caption);
                    newProductImages.add(productImage);
                }
            }
            
            // Lưu ảnh mới
            if (!newProductImages.isEmpty()) {
                productImagesRepository.saveAll(newProductImages);
                // Cập nhật danh sách ảnh cho sản phẩm
                List<ProductImages> allImages = new ArrayList<>(existingProduct.getProductImages());
                allImages.addAll(newProductImages);
                updatedProduct.setProductImages(allImages);
            }
        }

        // Convert Entity back to DTO
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id.intValue());
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }
}