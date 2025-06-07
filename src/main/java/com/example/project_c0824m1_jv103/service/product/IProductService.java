package com.example.project_c0824m1_jv103.service.product;

import com.example.project_c0824m1_jv103.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IProductService {
    Product createProduct(Product product, List<MultipartFile> imageFiles, List<String> captions) throws IOException;
} 