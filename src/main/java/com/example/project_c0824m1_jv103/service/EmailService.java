package com.example.project_c0824m1_jv103.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
}
