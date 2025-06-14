package com.example.project_c0824m1_jv103.service.product;

import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.model.Category;
import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IProductService {
    // Tạo sản phẩm mới với DTO
    ProductDTO createProduct(ProductDTO productDTO, List<MultipartFile> imageFiles, List<String> captions) throws IOException;

    // Lấy danh sách sản phẩm với phân trang
    Page<ProductDTO> findAll(Pageable pageable);

    // Tìm kiếm sản phẩm với nhiều điều kiện
    Page<ProductDTO> searchProducts(
            String productName,
            Double minPrice,
            Double maxPrice,
            Integer minQuantity,
            Integer maxQuantity,
            Pageable pageable);

    // Tìm sản phẩm theo ID
    ProductDTO findById(Long id);

    Product findProductByName(String name);

    // Cập nhật sản phẩm
    ProductDTO updateProduct(Long id, ProductDTO productDTO, List<MultipartFile> imageFiles, List<String> captions, List<String> deletedImageUrls) throws IOException;

    // Xóa sản phẩm
    void deleteProduct(Long id);

    // Lấy tất cả sản phẩm (không phân trang)
    List<ProductDTO> getAllProducts();
}