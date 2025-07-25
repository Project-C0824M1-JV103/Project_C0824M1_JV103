package com.example.project_c0824m1_jv103.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {
    @Value("${vnpay.vnp_TmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.vnp_HashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.vnp_Url}")
    private String vnp_Url;

    @Value("${vnpay.vnp_ReturnUrl}")
    private String vnp_ReturnUrl;

    public String getVnp_TmnCode() {
        return vnp_TmnCode;
    }

    public String getVnp_HashSecret() {
        return vnp_HashSecret;
    }

    public String getVnp_Url() {
        return vnp_Url;
    }

    public String getVnp_ReturnUrl() {
        return vnp_ReturnUrl;
    }
}