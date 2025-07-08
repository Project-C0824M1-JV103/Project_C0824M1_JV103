package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.price > 0 and p.quantity > 0")
    Page<Product> findAllWithQuantityAndPrice(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.quantity > 0")
    Page<Product> findAllWithQuantity(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.quantity > 0 AND p.price = 0")
    Page<Product> findAllWithQuantityAndZeroPrice(Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCase(String name, Pageable pageable);

    Product findByProductId(Integer productId);

    // Tìm sản phẩm theo nhà cung cấp
    List<Product> findBySupplier_SuplierId(Integer supplierId);
    
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceBetween(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.quantity BETWEEN :minQuantity AND :maxQuantity")
    Page<Product> findByQuantityBetween(@Param("minQuantity") Integer minQuantity, @Param("maxQuantity") Integer maxQuantity, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.price > 0 and p.quantity > 0 AND" +
           "(:productName IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:minQuantity IS NULL OR p.quantity >= :minQuantity) AND " +
           "(:maxQuantity IS NULL OR p.quantity <= :maxQuantity)")
    Page<Product> searchProducts(
            @Param("productName") String productName,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minQuantity") Integer minQuantity,
            @Param("maxQuantity") Integer maxQuantity,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.quantity > 0 AND" +
            "(:productName IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%')))")
    Page<Product> searchProducts(
            @Param("productName") String productName,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.quantity > 0 AND (:productName IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%')))")
    Page<Product> searchProductsWithQuantity(@Param("productName") String productName, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.quantity > 0 AND p.price = 0 AND (:productName IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%')))")
    Page<Product> searchProductsWithQuantityAndZeroPrice(@Param("productName") String productName, Pageable pageable);
}
