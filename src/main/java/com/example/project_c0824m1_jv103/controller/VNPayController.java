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

//    @GetMapping("/payment-callback")
//    public ResponseEntity<?> paymentCallback(@RequestParam Map<String, String> allParams) {
//        // Xử lý callback từ VNPay
//        String vnp_ResponseCode = allParams.get("vnp_ResponseCode");
//        String vnp_TransactionNo = allParams.get("vnp_TransactionNo");
//        String vnp_OrderInfo = allParams.get("vnp_OrderInfo");
//        String vnp_TxnRef = allParams.get("vnp_TxnRef");
//
//        Map<String, String> response = new HashMap<>();
//        response.put("status", vnp_ResponseCode.equals("00") ? "success" : "failed");
//        response.put("message", vnp_ResponseCode.equals("00") ? "Payment successful" : "Payment failed");
//        response.put("transactionNo", vnp_TransactionNo);
//        response.put("orderInfo", vnp_OrderInfo);
//        response.put("orderId", vnp_TxnRef);
//
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/payment-callback")
    public String paymentCallback(@RequestParam Map<String, String> allParams) {
        // Xử lý callback từ VNPay
        String vnp_ResponseCode = allParams.get("vnp_ResponseCode");
        String vnp_TxnRef = allParams.get("vnp_TxnRef");
        String vnp_OrderInfo = allParams.get("vnp_OrderInfo");
        boolean printPdf = vnp_OrderInfo != null && vnp_OrderInfo.contains("printPdf=true");

        // Nếu thanh toán thành công (mã 00)
        if ("00".equals(vnp_ResponseCode)) {
            // Lấy saleId từ vnp_TxnRef (đã được format là "ORDER_saleId")
            String saleId = vnp_TxnRef.replace("ORDER_", "");

            // Chuyển hướng đến trang xác nhận đơn hàng với tham số printPdf
            return "redirect:/Sale/confirmation/" + saleId + (printPdf ? "?printPdf=true" : "");
        }

        // Nếu thanh toán thất bại, chuyển hướng về trang bán hàng với thông báo lỗi
        return "redirect:/Sale?error=payment_failed";
    }
} 