package com.example.project_c0824m1_jv103.service.product;

import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.dto.StorageImportProductDTO;
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

    Product findProductById(Integer id);

    void updateProductRetail(Product product);

    // Lấy danh sách sản phẩm với phân trang và có giá và số lượng lớn hơn 0
    Page<ProductDTO> findAllWithQuantityAndPrice(Pageable pageable);

    // Lấy danh sách sản phẩm có quantity > 0
    Page<ProductDTO> findAllWithQuantity(Pageable pageable);

    // Lấy danh sách sản phẩm có quantity > 0 và price = 0
    Page<ProductDTO> findAllWithQuantityAndZeroPrice(Pageable pageable);

    Page<ProductDTO> findAll(Pageable pageable);
    Page<Product> findActiveProducts(Pageable pageable);

    // Lấy danh sách sản phẩm không trùng tên (chỉ lấy sản phẩm đầu tiên của mỗi tên)
    Page<Product> findActiveProductsWithoutDuplicates(Pageable pageable);

    // Tìm kiếm sản phẩm với nhiều điều kiện
    Page<ProductDTO> searchProducts(
            String productName,
            Double minPrice,
            Double maxPrice,
            Integer minQuantity,
            Integer maxQuantity,
            Pageable pageable);
    
    // Tìm kiếm sản phẩm theo tên (overload method)
    Page<ProductDTO> searchProducts(String productName, String searchType, Pageable pageable);

    // Tìm sản phẩm theo ID
    ProductDTO findById(Long id);

    Product findProductByName(String name);

    // Cập nhật sản phẩm
    ProductDTO updateProduct(Long id, ProductDTO productDTO, List<MultipartFile> imageFiles, List<String> captions, List<String> deletedImageUrls) throws IOException;

    // Xóa sản phẩm
    void deleteProduct(Long id);

    // Lấy tất cả sản phẩm (không phân trang)
    List<ProductDTO> getAllProducts();
    List<Category> getAllCategories();
    List<Supplier> getAllSuppliers();

    // Tạo sản phẩm mới từ modal nhập kho
    void createProductFromImport(StorageImportProductDTO dto);

    // Tạo sản phẩm mới từ modal nhập kho, trả về Product vừa tạo
    Product createProductFromImportReturnProduct(StorageImportProductDTO dto, List<MultipartFile> imageFiles);

    Page<ProductDTO> searchProductsWithQuantity(String productName, Pageable pageable);
    Page<ProductDTO> searchProductsWithQuantityAndZeroPrice(String productName, Pageable pageable);
    
    // Product Variants
    List<Product> findProductVariants(Product product);
}