package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> request) {
        try {
            String orderId = (String) request.get("orderId");
            Long amount = Long.parseLong(request.get("amount").toString());
            
            String paymentUrl = vnPayService.createPaymentUrl(orderId, amount);
            
            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            
            return ResponseEntity.ok(response);
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.badRequest().body("Error creating payment URL: " + e.getMessage());
        }
    }

    @GetMapping("/payment-callback")
    public ResponseEntity<?> paymentCallback(@RequestParam Map<String, String> allParams) {
        // Xử lý callback từ VNPay
        String vnp_ResponseCode = allParams.get("vnp_ResponseCode");
        String vnp_TransactionNo = allParams.get("vnp_TransactionNo");
        String vnp_OrderInfo = allParams.get("vnp_OrderInfo");
        String vnp_TxnRef = allParams.get("vnp_TxnRef");
        
        Map<String, String> response = new HashMap<>();
        response.put("status", vnp_ResponseCode.equals("00") ? "success" : "failed");
        response.put("message", vnp_ResponseCode.equals("00") ? "Payment successful" : "Payment failed");
        response.put("transactionNo", vnp_TransactionNo);
        response.put("orderInfo", vnp_OrderInfo);
        response.put("orderId", vnp_TxnRef);
        
        return ResponseEntity.ok(response);
    }
} 