package com.example.project_c0824m1_jv103.service.product;

import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.dto.StorageImportProduct;
import com.example.project_c0824m1_jv103.mapper.ProductMapper;
import com.example.project_c0824m1_jv103.model.Category;
import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.model.ProductImages;
import com.example.project_c0824m1_jv103.model.Supplier;
import com.example.project_c0824m1_jv103.repository.ICategoryRepository;
import com.example.project_c0824m1_jv103.repository.IProductImagesRepository;
import com.example.project_c0824m1_jv103.repository.IProductRepository;
//import com.example.project_c0824m1_jv103.service.CloudinaryService;
import com.example.project_c0824m1_jv103.repository.ISupplierRepository;
import com.example.project_c0824m1_jv103.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IProductImagesRepository productImagesRepository;
    
    @Autowired
    private ProductMapper productMapper;

    // Temporarily disabled for testing
     @Autowired
     private CloudinaryService cloudinaryService;
    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private ISupplierRepository supplierRepository;

//    // Phương thức để lưu ảnh local
//    private String saveImageLocally(MultipartFile file) throws IOException {
//        // Tạo thư mục uploads nếu chưa tồn tại
//        String uploadDir = "src/main/resources/static/uploads/products/";
//        Path uploadPath = Paths.get(uploadDir);
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        // Tạo tên file unique
//        String originalFilename = file.getOriginalFilename();
//        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        String uniqueFilename = UUID.randomUUID().toString() + extension;
//
//        // Lưu file
//        Path filePath = uploadPath.resolve(uniqueFilename);
//        Files.copy(file.getInputStream(), filePath);
//
//        // Trả về URL để truy cập ảnh
//        return "/uploads/products/" + uniqueFilename;
//    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO, List<MultipartFile> imageFiles, List<String> captions) throws IOException {

        // Convert DTO to Entity
        Product product = productMapper.toEntity(productDTO);
        product.setCategory(categoryRepository.findById(productDTO.getCategoryId()).get());
        product.setSupplier(supplierRepository.findById(productDTO.getSupplierId()).get());
        
        // Lưu sản phẩm trước
        Product savedProduct = productRepository.save(product);

        // Tải ảnh lên Cloudinary và lưu vào ProductImages
        List<ProductImages> productImages = new ArrayList<>();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            System.out.println("Processing " + imageFiles.size() + " image files...");
            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile file = imageFiles.get(i);
                if (!file.isEmpty()) {
                    System.out.println("Processing file: " + file.getOriginalFilename() + " - Size: " + file.getSize());
                    String caption = (captions != null && i < captions.size()) ? captions.get(i) : null;

                    try {
                        // Upload ảnh lên Cloudinary
                        String imageUrl = cloudinaryService.uploadImage(file);
                        System.out.println("Uploaded to Cloudinary: " + imageUrl);

                        // Tạo ProductImages
                        ProductImages productImage = new ProductImages(savedProduct, imageUrl, caption);
                        productImages.add(productImage);
                    } catch (Exception e) {
                        System.err.println("Error uploading file " + file.getOriginalFilename() + ": " + e.getMessage());
                        e.printStackTrace();
                        throw new IOException("Lỗi upload ảnh: " + e.getMessage(), e);
                    }
                }
            }
        } else {
            System.out.println("No image files to process.");
        }

        // Lưu danh sách ProductImages
        if (!productImages.isEmpty()) {
            System.out.println("Saving " + productImages.size() + " images to database...");
            productImagesRepository.saveAll(productImages);
            savedProduct.setProductImages(productImages);
            System.out.println("Successfully created product with images!");
        }

        // Convert Entity back to DTO
        return productMapper.toDTO(savedProduct);
    }

    @Override
    public Page<ProductDTO> findAll(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::toDTO);
    }

    @Override
    public Page<ProductDTO> searchProducts(
            String productName,
            Double minPrice,
            Double maxPrice,
            Integer minQuantity,
            Integer maxQuantity,
            Pageable pageable) {
        
        // Xử lý các giá trị null
        if (minPrice == null) minPrice = 0.0;
        if (maxPrice == null) maxPrice = Double.MAX_VALUE;
        if (minQuantity == null) minQuantity = 0;
        if (maxQuantity == null) maxQuantity = Integer.MAX_VALUE;
        
        // Nếu không có điều kiện tìm kiếm nào được chỉ định
        if (productName == null && minPrice == 0.0 && maxPrice == Double.MAX_VALUE 
            && minQuantity == 0 && maxQuantity == Integer.MAX_VALUE) {
            return findAll(pageable);
        }
        
        // Thực hiện tìm kiếm với tất cả các điều kiện
        Page<Product> productPage = productRepository.searchProducts(
            productName,
            minPrice,
            maxPrice,
            minQuantity,
            maxQuantity,
            pageable
        );
        
        return productPage.map(productMapper::toDTO);
    }

    @Override
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id.intValue()).orElse(null);
        return product != null ? productMapper.toDTO(product) : null;
    }

    @Override
    public Product findProductByName(String name) {
        // Sử dụng findAll và lọc theo tên vì findByProductName chưa có trong repository
        List<Product> products = productRepository.findAll();
        return products.stream()
                .filter(product -> name.equals(product.getProductName()))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO, List<MultipartFile> imageFiles, List<String> captions, List<String> deletedImageUrls) throws IOException {
        System.out.println("=== DEBUG UPDATE PRODUCT ===");
        System.out.println("Product ID: " + id);
        System.out.println("Image files count: " + (imageFiles != null ? imageFiles.size() : 0));
        System.out.println("Deleted images count: " + (deletedImageUrls != null ? deletedImageUrls.size() : 0));
        
        // Tìm product hiện tại
        Product existingProduct = productRepository.findById(id.intValue()).orElse(null);
        if (existingProduct == null) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }

        // Cập nhật thông tin từ DTO
        productMapper.updateEntityFromDTO(productDTO, existingProduct);

        // Xử lý xóa ảnh cũ TRƯỚC
        if (deletedImageUrls != null && !deletedImageUrls.isEmpty()) {
            System.out.println("Danh sách URL cần xóa: " + deletedImageUrls);
            
            // Debug: In ra tất cả URL hiện tại
            existingProduct.getProductImages().forEach(img -> 
                System.out.println("URL hiện tại: " + img.getImageUrl() + " (ID: " + img.getImageId() + ")"));
            
            // Tìm ảnh cần xóa
            List<ProductImages> imagesToDelete = existingProduct.getProductImages().stream()
                .filter(img -> deletedImageUrls.contains(img.getImageUrl()))
                .collect(Collectors.toList());
            
            System.out.println("Tìm thấy " + imagesToDelete.size() + " ảnh để xóa");
            
            if (!imagesToDelete.isEmpty()) {
                imagesToDelete.forEach(img -> 
                    System.out.println("Chuẩn bị xóa ID: " + img.getImageId() + ", URL: " + img.getImageUrl()));
                
                try {
                    // Xóa từng ảnh một để debug
                    for (ProductImages img : imagesToDelete) {
                        System.out.println("Đang xóa image ID: " + img.getImageId());
                        productImagesRepository.delete(img);
                        System.out.println("Đã xóa image ID: " + img.getImageId() + " thành công");
                    }
                    
                    // Cũng xóa khỏi memory
                    existingProduct.getProductImages().removeAll(imagesToDelete);
                    System.out.println("Đã xóa " + imagesToDelete.size() + " ảnh khỏi database và memory");
                    
                    // Verify deletion
                    System.out.println("Số ảnh còn lại trong memory: " + existingProduct.getProductImages().size());
                    
                } catch (Exception e) {
                    System.err.println("Lỗi khi xóa ảnh: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            } else {
                System.out.println("Không tìm thấy ảnh nào để xóa!");
            }
        }

        // Lưu sản phẩm đã cập nhật
        Product updatedProduct = productRepository.save(existingProduct);

        // Xử lý ảnh mới (nếu có)
        if (imageFiles != null && !imageFiles.isEmpty()) {
            System.out.println("Processing " + imageFiles.size() + " image files...");
            List<ProductImages> newProductImages = new ArrayList<>();
            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile file = imageFiles.get(i);
                if (!file.isEmpty()) {
                    System.out.println("Processing file: " + file.getOriginalFilename() + " - Size: " + file.getSize());
                    String caption = (captions != null && i < captions.size()) ? captions.get(i) : null;
                    
                    try {
                        // Upload ảnh lên Cloudinary
                        String imageUrl = cloudinaryService.uploadImage(file);
                        System.out.println("Uploaded to Cloudinary: " + imageUrl);
                        
                        // Tạo ProductImages mới
                        ProductImages productImage = new ProductImages(updatedProduct, imageUrl, caption);
                        newProductImages.add(productImage);
                    } catch (Exception e) {
                        System.err.println("Error uploading file " + file.getOriginalFilename() + ": " + e.getMessage());
                        e.printStackTrace();
                        throw new IOException("Lỗi upload ảnh: " + e.getMessage(), e);
                    }
                }
            }
            
            // Lưu ảnh mới
            if (!newProductImages.isEmpty()) {
                System.out.println("Saving " + newProductImages.size() + " new images to database...");
                productImagesRepository.saveAll(newProductImages);
                // Cập nhật danh sách ảnh cho sản phẩm
                List<ProductImages> allImages = new ArrayList<>(existingProduct.getProductImages());
                allImages.addAll(newProductImages);
                updatedProduct.setProductImages(allImages);
                System.out.println("Successfully updated product with new images!");
            }
        } else {
            System.out.println("No image files to process.");
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
    
    @Override
    public Page<ProductDTO> searchProducts(String productName, String searchType, Pageable pageable) {
        // Tìm kiếm sản phẩm chỉ theo tên
        Page<Product> productPage = productRepository.findByProductNameContainingIgnoreCase(productName, pageable);
        return productPage.map(productMapper::toDTO);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public Product createProductFromImport(StorageImportProduct dto) {
        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setPrice(0.0);
        product.setQuantity(0);
        product.setSize(dto.getSize());
        product.setCameraBack(dto.getCameraBack());
        product.setCameraFront(dto.getCameraFront());
        product.setCpu(dto.getCpu());
        product.setMemory(dto.getMemory());
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            product.setCategory(category);
        }
        if (dto.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(dto.getSupplierId()).orElse(null);
            product.setSupplier(supplier);
        }
        Product savedProduct = productRepository.save(product);
        List<ProductImages> images = new ArrayList<>();
        ProductImages defaultImage1 = new ProductImages(savedProduct, null, null);
        ProductImages defaultImage2 = new ProductImages(savedProduct, null, null);
        images.add(defaultImage1);
        images.add(defaultImage2);
        productImagesRepository.saveAll(images);
        savedProduct.setProductImages(images);
        return savedProduct;
    }
}