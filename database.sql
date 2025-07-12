-- Thêm dữ liệu vào bảng Category (chỉ chứa các hãng điện thoại)
INSERT INTO Category (category_name, image_url)
VALUES ('Samsung', 'https://res.cloudinary.com/dbfscepll/image/upload/v1749194441/samsung-logo_myw0it.jpg'),
       ('Apple', 'https://res.cloudinary.com/dbfscepll/image/upload/v1749194441/apple-logo_gmffks.jpg'),
       ('Xiaomi', 'https://res.cloudinary.com/dbfscepll/image/upload/v1749194441/xiaomi-logo_mrogd1.jpg'),
       ('Huawei', 'https://res.cloudinary.com/dbfscepll/image/upload/v1749194441/huawei-logo_euq7yo.jpg'),
       ('Oppo', 'https://res.cloudinary.com/dbfscepll/image/upload/v1749194441/oppo-logo_t2zbbc.jpg');

-- Thêm dữ liệu vào bảng Supplier
INSERT INTO Supplier (suplier_name, address, phone_number, email, image_url)
VALUES ('Digiworld', '350-352 Võ Văn Kiệt, Quận 1, TP. Hồ Chí Minh', '02439388568', 'digiworld@gmail.com',
        'https://res.cloudinary.com/dbfscepll/image/upload/v1749196044/digiworld_o9v4rm.png'),
       ('FPT Trading', 'Tòa nhà FPT, 17 Duy Tân, Cầu Giấy, Hà Nội', '18006601', 'fpttrading@fpt.com',
        'https://res.cloudinary.com/dbfscepll/image/upload/v1749196044/FPT-Trading-logo_kxq9mm.gif'),
       ('Viettel Imex', '1B Đường số 2, Khu chế xuất Tân Thuận, Quận 7, TP. Hồ Chí Minh', '0236123456',
        'viettelimex@viettel.com',
        'https://res.cloudinary.com/dbfscepll/image/upload/v1749196044/viettel-imex-logo_w9gskz.png'),
       ('Thế Giới Di Động (MWG)', '128 Trần Quang Khải, Quận 1, TP. Hồ Chí Minh', '1900 232460', 'tgdd@gmail.com',
        'https://res.cloudinary.com/dbfscepll/image/upload/v1749196044/logo-the-gioi-di-dong_vh7rzd.jpg');

-- Thêm dữ liệu vào bảng Product (chỉ chứa điện thoại)
INSERT INTO Product (product_name, size, price, camera_front, camera_back, memory, cpu, description, category_id,
                     suplier_id, quantity)
VALUES ('Samsung Galaxy S21', '6.2', 15000000, '10', '12', '128', 'Exynos 2100', 'High-performance Samsung smartphone',
        1, 1, 50),
       ('IPhone 13', '6.1', 20000000, '12MP', '12', '256', 'A15 Bionic', 'Premium Apple smartphone', 2, 2, 30),
       ('Xiaomi 12 Pro', '6.73', 18000000, '32', '50', '256', 'Snapdragon 8 Gen 1', 'Flagship Xiaomi smartphone', 3, 3,
        40),
       ('Samsung Galaxy S22 Ultra', '6.8', 22000000, '40', '108', '512', 'Exynos 2200',
        'Premium Samsung flagship with S Pen', 1, 1, 25),
       ('IPhone 14 Pro', '6.1', 25000000, '12', '48', '256', 'A16 Bionic',
        'Advanced Apple smartphone with Dynamic Island', 2, 2, 20),
       ('Oppo Find X5 Pro', '6.7', 19000000, '32', '50', '256', 'Snapdragon 8 Gen 1',
        'High-end Oppo with superior camera', 3, 3, 35),
       ('Xiaomi 15 Ultra', '6.6', 32000000, '13', '50', '256', 'Kirin 9000', 'Xiaomi flagship with advanced photography',
        2, 1, 15);

-- Thêm dữ liệu vào bảng ProductImages
INSERT INTO product_images (product_id, image_url, caption, image_id)
VALUES (1, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752150111/bvteq9px1uiy1pvntegi.png', 'Ảnh chính Samsung Galaxy S21', 1),
       (1, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752150106/v5jzntf44mvi0mtddzrx.jpg', 'Ảnh phụ 1 Samsung Galaxy S21', 2),
       (1, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752150109/idlgswomxqll5hgomvk1.jpg', 'Ảnh phụ 2 Samsung Galaxy S21', 3),
       (2, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752152629/enrfxutddqbv5dtweke5.jpg', 'Ảnh chính IPhone 13', 4),
       (2, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752150040/pnykvweaubd9kks3fxen.jpg', 'Ảnh phụ 1 IPhone 13', 5),
       (2, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752150043/mrxshmz3tjizpqqf3ovp.jpg', 'Ảnh phụ 2 IPhone 13', 6),
       (3, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752152674/vrfgvwbfaaxiwhaglqhx.jpg', 'Ảnh chính Xiaomi 12 Pro', 10),
       (3, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752150581/pgcbhrt2halovipquy6q.jpg', 'Ảnh phụ 1 Xiaomi 12 Pro', 11),
       (3, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752150582/hmtxuhsbui7hnpxclyey.jpg', 'Ảnh phụ 2 Xiaomi 12 Pro', 12),
       (4, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752152625/faqtpic0ihgpaog2dpya.jpg', 'Ảnh chính Samsung Galaxy S22 Ultra', 16),
       (4, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752150950/doeetkbewnygb7fewxos.jpg', 'Ảnh phụ 1 Samsung Galaxy S22 Ultra', 17),
       (4, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752150951/ykqvxszbnvtwfqtjy8li.jpg', 'Ảnh phụ 2 Samsung Galaxy S22 Ultra', 18),
       (5, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752150859/k6kbrziwwy1vuucb5mmr.png', 'Ảnh chính IPhone 14 Pro', 13),
       (5, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752150857/swv3xcxejrvvm0cgcsb7.jpg', 'Ảnh phụ 1 IPhone 14 Pro', 14),
       (5, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752150855/wsg2yl2mh0ujg5hcdqxe.jpg', 'Ảnh phụ 2 IPhone 14 Pro', 15),
       (6, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752152631/wdvuubmiwg8muzai0s9v.jpg', 'Ảnh chính Oppo Find X5 Pro', 7),
       (6, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752150074/bttwvhzvscrnuowpulvu.jpg', 'Ảnh phụ 1 Oppo Find X5 Pro', 8),
       (6, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752150076/jucgw19dwbughqwajgxx.jpg', 'Ảnh phụ 2 Oppo Find X5 Pro', 9),
       (7, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752152267/i9qobybgmx5ar3dreacl.jpg', 'Ảnh chính Xiaomi 15 Ultra', 19),
       (7, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752151231/jni3vhip2j3hsqh70p5s.jpg', 'Ảnh phụ 1 Xiaomi 15 Ultra', 20),
       (7, 'https://res.cloudinary.com/dbfscepll/image/upload/v1752151232/quvsccobokmjigtirzay.jpg', 'Ảnh phụ 2 Xiaomi 15 Ultra', 21);

-- Thêm dữ liệu vào bảng Customer
    INSERT INTO Customer (customer_name, phone_number, address, birthday_date, email)
    VALUES ('Pham Van D', '0931234567', '123 Le Loi, Hanoi', '1990-05-15', 'phamvand@example.com'),
           ('Hoang Thi E', '0942345678', '456 Nguyen Trai, HCMC', '1995-08-20', 'hoangthie@example.com'),
           ('Nguyen Van F', '0953456789', '789 Tran Hung Dao, Da Nang', '1988-12-10', 'nguyenvanf@example.com'),
           ('Tran Thi G', '0964567890', '101 Hai Ba Trung, Hue', '1992-03-25', 'tranthig@example.com'),
           ('Le Van H', '0975678901', '202 Vo Thi Sau, Can Tho', '1985-07-30', 'levanh@example.com'),
           ('Do Thi I', '0986789012', '303 Ly Thuong Kiet, Nha Trang', '1998-11-05', 'dothii@example.com'),
           ('Vu Van J', '0997890123', '404 Pham Ngu Lao, Vinh', '1993-09-12', 'vuvanj@example.com'),
           ('Bui Thi K', '0918901234', '505 Nguyen Hue, Quang Ngai', '1991-04-18', 'buithik@example.com'),
           ('Phan Van L', '0929012345', '606 Hoang Dieu, Hai Phong', '1987-06-22', 'phanvanl@example.com'),
           ('Ngo Thi M', '0930123456', '707 Le Duan, Buon Ma Thuot', '1994-10-30', 'ngothim@example.com');

-- Thêm dữ liệu vào bảng Sale
INSERT INTO Sale (customer_id, employee_id, sale_date, amount)
VALUES (1, 2, '2025-06-01 10:00:00', 15000000),
       (2, 2, '2025-06-01 14:30:00', 20000000),
       (3, 2, '2025-06-02 09:15:00', 36000000);

-- Thêm dữ liệu vào bảng SaleDetails
INSERT INTO SaleDetails (product_id, sale_id, quantity, unique_price)
VALUES (1, 1, 1, 15000000),
       (2, 2, 1, 20000000),
       (3, 3, 2, 18000000);

-- Thêm dữ liệu vào bảng Storage
INSERT INTO Storage (product_id, cost, employee_id, quantity, transaction_date)
VALUES (1, 12000000, 3, 60, '2025-05-30 08:00:00'),
       (2, 17000000, 3, 40, '2025-05-30 09:00:00'),
       (3, 15000000, 3, 50, '2025-05-31 10:00:00');

-- Tạo bảng storage_transaction cho lịch sử giao dịch kho
CREATE TABLE IF NOT EXISTS storage_transaction (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    quantity INT NOT NULL, -- Cho phép số âm (xuất kho) và dương (nhập kho)
    cost DOUBLE,
    employee_id INT,
    transaction_type ENUM('IMPORT', 'EXPORT') NOT NULL,
    transaction_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255),
    FOREIGN KEY (product_id) REFERENCES Product(product_id),
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id)
);