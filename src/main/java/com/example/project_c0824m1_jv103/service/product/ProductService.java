package com.example.project_c0824m1_jv103.service.product;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.project_c0824m1_jv103.model.Category;
import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.model.ProductImages;
import com.example.project_c0824m1_jv103.model.Supplier;
import com.example.project_c0824m1_jv103.repository.ICategoryRepository;
import com.example.project_c0824m1_jv103.repository.IProductImagesRepository;
import com.example.project_c0824m1_jv103.repository.IProductRepository;
import com.example.project_c0824m1_jv103.repository.ISupplierRepository;
import com.example.project_c0824m1_jv103.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IProductImagesRepository productImagesRepository;

    @Autowired
    private CloudinaryService cloudinaryService;


    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private ISupplierRepository supplierRepository;

    @Autowired
    private Cloudinary cloudinary;
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
    @Override
    public void saveProduct(Product product, MultipartFile[] images) {
        // Lưu sản phẩm
        Product savedProduct = productRepository.save(product);

        // Lưu hình ảnh vào Cloudinary và cập nhật bảng product_images
        if (images != null && images.length > 0) {
            List<ProductImages> productImagesList = new ArrayList<>();
            int displayOrder = 1;

            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                        String imageUrl = uploadResult.get("url").toString();

                        ProductImages productImage = new ProductImages();
                        productImage.setProduct(savedProduct);
                        productImage.setImageUrl(imageUrl);
                        productImage.setCaption("Hình ảnh sản phẩm " + savedProduct.getProductName());
                        productImage.setDisplayOrder(displayOrder++);

                        productImagesList.add(productImage);
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi khi tải ảnh lên Cloudinary: " + e.getMessage());
                    }
                }
            }
            productImagesRepository.saveAll(productImagesList);
        }
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }
}