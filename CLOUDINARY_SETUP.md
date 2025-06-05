# Hướng dẫn Setup Cloudinary cho Project

## 🚀 **Bước 1: Đăng ký Cloudinary Account**

1. Truy cập [Cloudinary.com](https://cloudinary.com)
2. Click **"Sign Up For Free"**
3. Điền form đăng ký hoặc đăng nhập bằng Google/GitHub
4. Verify email nếu cần

## 📊 **Free Tier của Cloudinary:**

- **25GB Storage** (vs Firebase 5GB)
- **25GB Bandwidth/month**
- **25 Credits/month** cho transformations
- **Unlimited images**
- **Auto optimization**

## 🔧 **Bước 2: Lấy API Credentials**

1. Sau khi đăng nhập, vào **Dashboard**
2. Trong phần **Account Details**, bạn sẽ thấy:

   - **Cloud Name**: `your-cloud-name`
   - **API Key**: `123456789012345`
   - **API Secret**: `abcdefghijklmnopqrstuvwxyz123`

3. **Copy** 3 thông tin này

## 🔧 **Bước 3: Cập nhật cấu hình trong project**

### **File `src/main/resources/application.properties`:**

```properties
# Cloudinary Configuration
cloudinary.cloud-name=your-actual-cloud-name
cloudinary.api-key=your-actual-api-key
cloudinary.api-secret=your-actual-api-secret
```

**⚠️ LƯU Ý:** Thay thế `your-actual-*` bằng thông tin thật từ Cloudinary Dashboard

### **Hoặc sử dụng Environment Variables (Bảo mật hơn):**

```properties
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME:your-cloud-name}
cloudinary.api-key=${CLOUDINARY_API_KEY:your-api-key}
cloudinary.api-secret=${CLOUDINARY_API_SECRET:your-api-secret}
```

## 🏃 **Bước 4: Test Upload**

1. **Build project:** `./gradlew build`
2. **Run application:** `./gradlew bootRun`
3. **Truy cập:** `http://localhost:8080/images/upload`
4. **Upload vài ảnh** để test
5. **Kiểm tra** trong Cloudinary Console > Media Library

## 🔗 **API Endpoints đã tạo:**

| Method   | Endpoint                             | Mô tả                     |
| -------- | ------------------------------------ | ------------------------- |
| `POST`   | `/api/images/upload/product`         | Upload ảnh sản phẩm       |
| `POST`   | `/api/images/upload/avatar`          | Upload avatar (300x300px) |
| `POST`   | `/api/images/upload/multiple`        | Upload nhiều ảnh          |
| `DELETE` | `/api/images/delete?imageUrl=...`    | Xóa ảnh                   |
| `GET`    | `/api/images/transform?publicId=...` | Tạo URL với size khác     |
| `GET`    | `/images/upload`                     | Giao diện upload          |

## 🎯 **Ví dụ sử dụng trong JavaScript:**

### **Upload single image:**

```javascript
const formData = new FormData();
formData.append("file", imageFile);

fetch("/api/images/upload/product", {
  method: "POST",
  body: formData,
})
  .then((response) => response.json())
  .then((data) => {
    if (data.success) {
      console.log("Image URL:", data.imageUrl);
      // URL example: https://res.cloudinary.com/your-cloud/image/upload/v123/product-images/abc123.jpg
    }
  });
```

### **Upload multiple images:**

```javascript
const formData = new FormData();
files.forEach((file) => formData.append("files", file));

fetch("/api/images/upload/multiple", {
  method: "POST",
  body: formData,
})
  .then((response) => response.json())
  .then((data) => {
    if (data.success) {
      console.log("Uploaded images:", data.images);
    }
  });
```

## 🎨 **Tính năng Cloudinary:**

### **1. Auto Optimization:**

- **Format**: Tự động chọn WebP, AVIF cho browser hỗ trợ
- **Quality**: Tự động optimize chất lượng
- **Compression**: Giảm size file tự động

### **2. Dynamic Transformations:**

```javascript
// Tạo thumbnail 300x300
const thumbnailUrl =
  "https://res.cloudinary.com/your-cloud/image/upload/w_300,h_300,c_fill/product-images/abc123.jpg";

// Tạo ảnh blur để làm background
const blurUrl =
  "https://res.cloudinary.com/your-cloud/image/upload/e_blur:1000/product-images/abc123.jpg";

// Resize responsive
const responsiveUrl =
  "https://res.cloudinary.com/your-cloud/image/upload/w_auto,dpr_auto/product-images/abc123.jpg";
```

### **3. Upload Presets:**

Có thể tạo upload presets trong Cloudinary Console để:

- Set folder mặc định
- Auto resize
- Add watermark
- Set access permissions

## 💾 **Tích hợp với Database:**

### **ProductImages Entity:**

```java
@Entity
public class ProductImages {
    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl; // Cloudinary URL

    @Column(name = "public_id", length = 255)
    private String publicId; // Để xóa ảnh

    // ... other fields
}
```

### **Service method để lưu:**

```java
public void saveProductImages(Integer productId, List<String> imageUrls) {
    Product product = productRepository.findById(productId).orElse(null);
    if (product != null) {
        for (int i = 0; i < imageUrls.size(); i++) {
            ProductImages img = new ProductImages();
            img.setProduct(product);
            img.setImageUrl(imageUrls.get(i));
            img.setPublicId(extractPublicIdFromUrl(imageUrls.get(i)));
            img.setDisplayOrder(i + 1);
            productImagesRepository.save(img);
        }
    }
}
```

## 🚨 **Troubleshooting:**

### **1. "Invalid API Key" Error:**

- Kiểm tra API credentials trong `application.properties`
- Đảm bảo không có space thừa
- Restart application

### **2. "Upload failed" Error:**

- Kiểm tra file size < 10MB
- Kiểm tra file type là image
- Check network connection

### **3. "CloudinaryService bean not found":**

- Restart IDE để sync dependencies
- Run `./gradlew clean build`
- Kiểm tra `@Service` annotation

### **4. CORS Issues (nếu call từ frontend khác):**

```java
@CrossOrigin(origins = "*")
@RestController
public class ImageUploadController {
    // ...
}
```

## 📈 **Best Practices:**

1. **Folders Organization:**

   - `product-images/` - Ảnh sản phẩm
   - `employee-avatars/` - Avatar nhân viên
   - `banners/` - Banner website

2. **Image Optimization:**

   - Upload original quality
   - Dùng transformations cho display
   - Cache URLs trong database

3. **Security:**

   - Validate file types
   - Set upload limits
   - Use signed URLs cho private images

4. **Performance:**
   - Lazy load images
   - Use responsive images
   - Implement CDN caching

## 🎯 **Next Steps:**

1. Setup Cloudinary account
2. Update credentials trong code
3. Test upload qua `/images/upload`
4. Tích hợp vào product management
5. Optimize images cho production

**Cloudinary rất dễ setup và powerful hơn Firebase!** 🚀
