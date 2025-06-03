package com.example.project_c0824m1_jv103.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = (String) request.getSession().getAttribute("REDIRECT_URL");
        System.out.println("REDIRECT_URL: " + targetUrl);
        request.getSession().removeAttribute("REDIRECT_URL");

        if (targetUrl == null || targetUrl.isEmpty() || targetUrl.contains("favicon.ico") ||
                targetUrl.contains("error") || targetUrl.startsWith("/.well-known") ||
                targetUrl.endsWith(".css") || targetUrl.endsWith(".js") || targetUrl.endsWith(".json")) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                targetUrl = "/";
//            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SALES"))) {
//                targetUrl = "/sales";
//            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_BUSINESS"))) {
//                targetUrl = "/business";
//            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_WAREHOUSE"))) {
//                targetUrl = "/warehouse";
            } else {
                targetUrl = "/";
            }

        }
            System.out.println("LOGOUT SUCCESS - USER: " + authentication.getName());

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}