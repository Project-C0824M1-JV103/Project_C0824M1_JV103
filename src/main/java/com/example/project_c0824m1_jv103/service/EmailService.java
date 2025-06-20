package com.example.project_c0824m1_jv103.service;

import com.example.project_c0824m1_jv103.model.Sale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    // Lưu OTP tạm thời (email -> [otp, expiry])
    private final Map<String, OtpInfo> otpStorage = new ConcurrentHashMap<>();

    private static class OtpInfo {
        String otp;
        LocalDateTime expiry;
        OtpInfo(String otp, LocalDateTime expiry) {
            this.otp = otp;
            this.expiry = expiry;
        }
    }

    public boolean sendOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        otpStorage.put(email, new OtpInfo(otp, expiry));
        System.out.println(otp);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Mã xác thực OTP");
            message.setText("Mã OTP của bạn là: " + otp + "\nMã có hiệu lực trong 5 phút.");
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyOtp(String email, String otp) {
        OtpInfo info = otpStorage.get(email);
        if (info == null) return false;
        if (info.expiry.isBefore(LocalDateTime.now())) {
            otpStorage.remove(email);
            return false;
        }
        boolean valid = info.otp.equals(otp);
        if (valid) otpStorage.remove(email); // Xác thực xong thì xóa
        return valid;
    }

    public boolean sendOrderConfirmation(Sale sale) {
        if (sale == null || sale.getCustomer() == null || sale.getCustomer().getEmail() == null) {
            return false;
        }
        String email = sale.getCustomer().getEmail();
        String subject = "Xác nhận đơn hàng từ TPQDT Phone";
        String html = buildOrderConfirmationHtml(sale);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String buildOrderConfirmationHtml(Sale sale) {
        StringBuilder html = new StringBuilder();
        html.append("<div style='font-family:sans-serif;background:#f8f9fa;padding:24px;'>");
        html.append("<div style='max-width:700px;margin:auto;background:#fff;border-radius:8px;box-shadow:0 2px 10px rgba(0,0,0,0.05);padding:32px;'>");
        html.append("<div style='text-align:center;margin-bottom:24px;'>");
        html.append("<div style='font-size:3rem;color:#28a745;margin-bottom:8px;'>&#10004;</div>");
        html.append("<h2 style='color:#667eea;margin-bottom:8px;'>Thanh toán thành công!</h2>");
        html.append("<p style='color:#6c757d;'>Mã đơn hàng: <b>" + sale.getSaleId() + "</b></p>");
        html.append("</div>");
        // Customer info
        html.append("<fieldset style='border:1px solid #e0e0e0;border-radius:8px;padding:16px;margin-bottom:24px;'><legend style='color:#667eea;font-weight:600;'>KHÁCH HÀNG</legend>");
        html.append("<div style='display:flex;flex-wrap:wrap;'>");
        html.append("<div style='flex:1 1 200px;min-width:200px;'>");
        html.append("<div><b>Họ và tên:</b> " + sale.getCustomer().getCustomerName() + "</div>");
        html.append("<div><b>Số điện thoại:</b> " + sale.getCustomer().getPhoneNumber() + "</div>");
        html.append("</div>");
        html.append("<div style='flex:1 1 200px;min-width:200px;'>");
        html.append("<div><b>Email:</b> " + sale.getCustomer().getEmail() + "</div>");
        html.append("<div><b>Địa chỉ:</b> " + sale.getCustomer().getAddress() + "</div>");
        html.append("</div></div></fieldset>");
        // Product details
        html.append("<fieldset style='border:1px solid #e0e0e0;border-radius:8px;padding:16px;margin-bottom:24px;'><legend style='color:#667eea;font-weight:600;'>Chi tiết sản phẩm</legend>");
        html.append("<table style='width:100%;border-collapse:collapse;'>");
        html.append("<thead><tr style='background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff;'><th style='padding:8px;border:none;'>Sản phẩm</th><th style='padding:8px;border:none;'>Đơn giá</th><th style='padding:8px;border:none;'>Số lượng</th><th style='padding:8px;border:none;'>Thành tiền</th></tr></thead><tbody>");
        for (var detail : sale.getSaleDetails()) {
            double lineTotal = detail.getUniquePrice() * detail.getQuantity();
            html.append("<tr style='text-align:center;background:#f8f9fa;'>");
            html.append("<td style='padding:8px;'>" + detail.getProduct().getProductName() + "</td>");
            html.append("<td style='padding:8px;'>" + String.format("%,.0f", detail.getUniquePrice()) + " VNĐ</td>");
            html.append("<td style='padding:8px;'>" + detail.getQuantity() + "</td>");
            html.append("<td style='padding:8px;'>" + String.format("%,.0f", lineTotal) + " VNĐ</td>");
            html.append("</tr>");
        }
        html.append("</tbody></table></fieldset>");
        // Order info
        html.append("<fieldset style='border:1px solid #e0e0e0;border-radius:8px;padding:16px;margin-bottom:24px;'><legend style='color:#667eea;font-weight:600;'>ĐƠN HÀNG</legend>");
        html.append("<div style='display:flex;flex-wrap:wrap;align-items:center;'>");
        html.append("<div style='flex:1 1 200px;min-width:200px;'>");
        html.append("<div><b>Ngày đặt hàng:</b> " + sale.getSaleDate().toString().replace('T', ' ') + "</div>");
        html.append("<div><b>Phương thức thanh toán:</b> " + ("VNPAY".equals(sale.getPaymentMethod()) ? "VNPay" : "Tiền mặt") + "</div>");
        html.append("</div>");
        html.append("<div style='flex:1 1 200px;min-width:200px;text-align:right;'>");
        html.append("<div><b style='color:#667eea;'>Tổng tiền:</b> <span style='color:#667eea;font-weight:600;'>" + String.format("%,.0f", sale.getAmount()) + " VNĐ</span></div>");
        html.append("<div style='display:flex;align-items:center;justify-content:flex-end;margin-top:8px;'><img src='https://ideogram.ai/assets/progressive-image/balanced/response/jJ_0S-HwQPqKmnTgESV2fQ' alt='Logo' style='width:32px;height:32px;object-fit:contain;margin-right:8px;'/><span style='font-weight:600;color:#667eea;'>TPQDT Phone</span></div>");
        html.append("</div></div><hr style='margin:16px 0;'/>");
        html.append("<div style='text-align:center;color:#6c757d;font-size:0.95rem;'>© 2025 Công ty cổ phần C0824M1. Trụ sở tại Codegym Đà Nẵng</div>");
        html.append("</fieldset>");
        html.append("</div></div></div>");
        return html.toString();
    }
}
