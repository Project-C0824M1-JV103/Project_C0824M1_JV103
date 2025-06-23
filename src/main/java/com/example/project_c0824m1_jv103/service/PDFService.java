package com.example.project_c0824m1_jv103.service;

import com.example.project_c0824m1_jv103.model.Sale;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PDFService {

    public byte[] generateInvoicePDF(Sale sale) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Load Arial font from resources using classpath
            InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/arial.ttf");
            if (fontStream == null) {
                throw new RuntimeException("Font file not found in resources/fonts/arial.ttf");
            }
            byte[] fontBytes = fontStream.readAllBytes();
            PdfFont font = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H);

            // Add header
            Paragraph header = new Paragraph("HÓA ĐƠN BÁN HÀNG")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold()
                    .setFont(font);
            document.add(header);

            // Add company info
            Paragraph companyInfo = new Paragraph("TPQDT Phone\nTrụ sở tại Codegym Đà Nẵng")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12)
                    .setFont(font);
            document.add(companyInfo);

            // Add order info
            Table orderInfo = new Table(2).useAllAvailableWidth();
            orderInfo.addCell(new Cell().add(new Paragraph("Mã đơn hàng:").setBold().setFont(font)));
            orderInfo.addCell(new Cell().add(new Paragraph(sale.getSaleId().toString()).setFont(font)));
            orderInfo.addCell(new Cell().add(new Paragraph("Ngày đặt hàng:").setBold().setFont(font)));
            orderInfo.addCell(new Cell().add(new Paragraph(sale.getSaleDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).setFont(font)));
            orderInfo.addCell(new Cell().add(new Paragraph("Phương thức thanh toán:").setBold().setFont(font)));
            orderInfo.addCell(new Cell().add(new Paragraph(sale.getPaymentMethod().equals("VNPAY") ? "VNPay" : "Tiền mặt").setFont(font)));
            document.add(orderInfo);

            // Add customer info
            Paragraph customerHeader = new Paragraph("THÔNG TIN KHÁCH HÀNG")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold()
                    .setFont(font);
            document.add(customerHeader);

            Table customerInfo = new Table(2).useAllAvailableWidth();
            customerInfo.addCell(new Cell().add(new Paragraph("Họ và tên:").setBold().setFont(font)));
            customerInfo.addCell(new Cell().add(new Paragraph(sale.getCustomer().getCustomerName()).setFont(font)));
            customerInfo.addCell(new Cell().add(new Paragraph("Số điện thoại:").setBold().setFont(font)));
            customerInfo.addCell(new Cell().add(new Paragraph(sale.getCustomer().getPhoneNumber()).setFont(font)));
            customerInfo.addCell(new Cell().add(new Paragraph("Email:").setBold().setFont(font)));
            customerInfo.addCell(new Cell().add(new Paragraph(sale.getCustomer().getEmail()).setFont(font)));
            customerInfo.addCell(new Cell().add(new Paragraph("Địa chỉ:").setBold().setFont(font)));
            customerInfo.addCell(new Cell().add(new Paragraph(sale.getCustomer().getAddress()).setFont(font)));
            document.add(customerInfo);

            // Add product details
            Paragraph productHeader = new Paragraph("CHI TIẾT SẢN PHẨM")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold()
                    .setFont(font);
            document.add(productHeader);

            Table productTable = new Table(new float[]{3, 2, 1, 2}).useAllAvailableWidth();
            productTable.addHeaderCell(new Cell().add(new Paragraph("Sản phẩm").setBold().setFont(font)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("Đơn giá").setBold().setFont(font)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("Số lượng").setBold().setFont(font)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("Thành tiền").setBold().setFont(font)));

            sale.getSaleDetails().forEach(detail -> {
                productTable.addCell(new Cell().add(new Paragraph(detail.getProduct().getProductName()).setFont(font)));
                productTable.addCell(new Cell().add(new Paragraph(String.format("%,.0f VNĐ", detail.getUniquePrice())).setFont(font)));
                productTable.addCell(new Cell().add(new Paragraph(detail.getQuantity().toString()).setFont(font)));
                productTable.addCell(new Cell().add(new Paragraph(String.format("%,.0f VNĐ", detail.getUniquePrice() * detail.getQuantity())).setFont(font)));
            });

            document.add(productTable);

            // Add total amount
            Paragraph total = new Paragraph(String.format("Tổng tiền: %,.0f VNĐ", sale.getAmount()))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(14)
                    .setBold()
                    .setFont(font);
            document.add(total);

            // Add footer
            Paragraph footer = new Paragraph("© 2025 Công ty cổ phần C0824M1. Trụ sở tại Codegym Đà Nẵng")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setFont(font);
            document.add(footer);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
} 