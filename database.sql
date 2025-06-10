-- Thêm dữ liệu vào bảng Employee
INSERT INTO Employee (full_name, email, password, phone, role, status) VALUES
('Nguyen Van A', 'nguyenvana@example.com', 'password123', '0901234567', 'Admin', 'active'),
('Tran Thi B', 'tranthib@example.com', 'password456', '0912345678', 'Sales', 'active'),
('Le Van C', 'levanc@example.com', 'password789', '0923456789', 'Warehouse', 'active');

-- Thêm dữ liệu vào bảng Category (chỉ chứa các hãng điện thoại)
INSERT INTO Category (category_name, image_url) VALUES
('Samsung', 'https://res.cloudinary.com/dbfscepll/image/upload/v1749194441/samsung-logo_myw0it.jpg'),
('Apple', 'https://res.cloudinary.com/dbfscepll/image/upload/v1749194441/apple-logo_gmffks.jpg'),
('Xiaomi', 'https://res.cloudinary.com/dbfscepll/image/upload/v1749194441/xiaomi-logo_mrogd1.jpg'),
('Huawei', 'https://res.cloudinary.com/dbfscepll/image/upload/v1749194441/huawei-logo_euq7yo.jpg'),
('Oppo', 'https://res.cloudinary.com/dbfscepll/image/upload/v1749194441/oppo-logo_t2zbbc.jpg');

-- Thêm dữ liệu vào bảng Supplier
INSERT INTO Supplier (suplier_name, address, phone_number, email, image_url) VALUES
('Digiworld', '350-352 Võ Văn Kiệt, Quận 1, TP. Hồ Chí Minh', '02439388568','digiworld@gmail.com', 'https://res.cloudinary.com/dbfscepll/image/upload/v1749196044/digiworld_o9v4rm.png'),
('FPT Trading', 'Tòa nhà FPT, 17 Duy Tân, Cầu Giấy, Hà Nội', '18006601', 'fpttrading@fpt.com', 'https://res.cloudinary.com/dbfscepll/image/upload/v1749196044/FPT-Trading-logo_kxq9mm.gif'),
('Viettel Imex', '1B Đường số 2, Khu chế xuất Tân Thuận, Quận 7, TP. Hồ Chí Minh', '0236123456', 'viettelimex@viettel.com', 'https://res.cloudinary.com/dbfscepll/image/upload/v1749196044/viettel-imex-logo_w9gskz.png'),
('Thế Giới Di Động (MWG)', '128 Trần Quang Khải, Quận 1, TP. Hồ Chí Minh', '1900 232460', 'tgdd@gmail.com', 'https://res.cloudinary.com/dbfscepll/image/upload/v1749196044/logo-the-gioi-di-dong_vh7rzd.jpg');

-- Thêm dữ liệu vào bảng Product (chỉ chứa điện thoại)
INSERT INTO Product (product_name, size, price, camera_front, camera_back, memory, cpu, description, category_id, suplier_id, quantity) VALUES
('Samsung Galaxy S21', '6.2 inch', 15000000, '10MP', '12MP + 64MP + 12MP', '128GB', 'Exynos 2100', 'High-performance Samsung smartphone', 1, 1, 50),
('iPhone 13', '6.1 inch', 20000000, '12MP', '12MP + 12MP', '256GB', 'A15 Bionic', 'Premium Apple smartphone', 2, 2, 30),
('Xiaomi 12 Pro', '6.73 inch', 18000000, '32MP', '50MP + 50MP + 50MP', '256GB', 'Snapdragon 8 Gen 1', 'Flagship Xiaomi smartphone', 3, 3, 40);

-- Thêm dữ liệu vào bảng ProductImages
INSERT INTO product_images (product_id, image_url, caption, display_order) VALUES
(1, 'https://example.com/images/s21_front.jpg', 'Mặt trước Samsung Galaxy S21', 1),
(1, 'https://example.com/images/s21_back.jpg', 'Mặt sau Samsung Galaxy S21', 2),
(2, 'https://example.com/images/iphone13_front.jpg', 'Mặt trước iPhone 13', 1),
(2, 'https://example.com/images/iphone13_back.jpg', 'Mặt sau iPhone 13', 2),
(3, 'https://example.com/images/xiaomi12pro_front.jpg', 'Mặt trước Xiaomi 12 Pro', 1),
(3, 'https://example.com/images/xiaomi12pro_back.jpg', 'Mặt sau Xiaomi 12 Pro', 2);

-- Thêm dữ liệu vào bảng Customer
INSERT INTO Customer (customer_name, phone_number, address, birthday_date, email) VALUES
('Pham Van D', '0931234567', '123 Le Loi, Hanoi', '1990-05-15', 'phamvand@example.com'),
('Hoang Thi E', '0942345678', '456 Nguyen Trai, HCMC', '1995-08-20', 'hoangthie@example.com'),
('Nguyen Van F', '0953456789', '789 Tran Hung Dao, Da Nang', '1988-12-10', 'nguyenvanf@example.com');

-- Thêm dữ liệu vào bảng Sale
INSERT INTO Sale (customer_id, employee_id, sale_date, amount) VALUES
(1, 2, '2025-06-01 10:00:00', 15000000),
(2, 2, '2025-06-01 14:30:00', 20000000),
(3, 2, '2025-06-02 09:15:00', 36000000);

-- Thêm dữ liệu vào bảng SaleDetails
INSERT INTO SaleDetails (product_id, sale_id, quantity, unique_price) VALUES
(1, 1, 1, 15000000),
(2, 2, 1, 20000000),
(3, 3, 2, 18000000);

-- Thêm dữ liệu vào bảng Storage
INSERT INTO Storage (product_id, cost, employee_id, quantity, transaction_date) VALUES
(1, 12000000, 3, 60, '2025-05-30 08:00:00'),
(2, 17000000, 3, 40, '2025-05-30 09:00:00'),
(3, 15000000, 3, 50, '2025-05-31 10:00:00');