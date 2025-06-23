package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/vnpay")
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    @PostMapping("/create-payment")
    @ResponseBody
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> request) {
        try {
            String orderId = (String) request.get("orderId");
            Long amount = Long.parseLong(request.get("amount").toString());
            boolean printPdf = Boolean.parseBoolean(request.get("printPdf").toString());
            
            String paymentUrl = vnPayService.createPaymentUrl(orderId, amount, printPdf);
            
            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            
            return ResponseEntity.ok(response);
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.badRequest().body("Error creating payment URL: " + e.getMessage());
        }
    }

    @GetMapping("/payment-callback")
    public String paymentCallback(@RequestParam Map<String, String> allParams) {
        // Redirect to SaleController's callback endpoint
        StringBuilder redirectUrl = new StringBuilder("/Sale/vnpay-callback?");
        
        // Add all parameters to redirect URL
        allParams.forEach((key, value) -> {
            if (redirectUrl.charAt(redirectUrl.length() - 1) != '?') {
                redirectUrl.append("&");
            }
            redirectUrl.append(key).append("=").append(value);
        });
        
        return "redirect:" + redirectUrl.toString();
    }
} 