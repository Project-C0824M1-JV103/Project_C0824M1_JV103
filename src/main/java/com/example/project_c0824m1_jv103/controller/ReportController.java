package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.model.Sale;
import com.example.project_c0824m1_jv103.service.sale.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/Report")
public class ReportController extends BaseAdminController {

    @Autowired
    private ISaleService saleService;

    @GetMapping("")
    public String showReportController(Model model) {
        model.addAttribute("currentPage", "report");
        return "report/report-page";
    }

    // API báo cáo doanh thu bán hàng
    @GetMapping("/sale-report")
    public String showSaleReportPage(Model model) {
        model.addAttribute("currentPage", "report");
        return "report/sale-report";
    }

    @GetMapping("/sale-report/data")
    @ResponseBody
    public Map<String, Object> getSaleReportData(
            @RequestParam String type, // "day", "month", "year"
            @RequestParam(required = false) String fromDate, // yyyy-MM-dd
            @RequestParam(required = false) String toDate,   // yyyy-MM-dd
            @RequestParam(required = false) Integer year,    // nếu type=year
            @RequestParam(required = false) Integer month    // nếu type=month
    ) {
        // type: "day" (theo ngày), "month" (theo tháng), "year" (theo năm)
        // fromDate, toDate: nếu chọn khoảng ngày
        // year: nếu chọn năm
        // month: nếu chọn tháng
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        if ("day".equals(type)) {
            // Lấy dữ liệu theo từng ngày trong khoảng fromDate - toDate
            LocalDate from = LocalDate.parse(fromDate);
            LocalDate to = LocalDate.parse(toDate);
            for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
                int orderCount = saleService.countSalesByDate(date);
                BigDecimal totalAmount = saleService.sumAmountByDate(date);
                Map<String, Object> entry = new HashMap<>();
                entry.put("label", date.toString());
                entry.put("orderCount", orderCount);
                entry.put("totalAmount", totalAmount);
                data.add(entry);
            }
        } else if ("month".equals(type)) {
            int y = year;
            int m = month;
            LocalDate firstDay = LocalDate.of(y, m, 1);
            int daysInMonth = firstDay.lengthOfMonth();

            LocalDate now = LocalDate.now();
            int lastDay = daysInMonth;
            if (y == now.getYear() && m == now.getMonthValue()) {
                lastDay = now.getDayOfMonth();
            }

            for (int d = 1; d <= lastDay; d++) {
                LocalDate date = LocalDate.of(y, m, d);
                int orderCount = saleService.countSalesByDate(date);
                BigDecimal totalAmount = saleService.sumAmountByDate(date);
                Map<String, Object> entry = new HashMap<>();
                entry.put("label", date.toString()); // yyyy-MM-dd
                entry.put("orderCount", orderCount);
                entry.put("totalAmount", totalAmount);
                data.add(entry);
            }
        } else if ("year".equals(type)) {
            int y = year;
            for (int m = 1; m <= 12; m++) {
                int orderCount = saleService.countSalesByMonth(y, m);
                BigDecimal totalAmount = saleService.sumAmountByMonth(y, m);
                Map<String, Object> entry = new HashMap<>();
                entry.put("label", y + "-" + (m < 10 ? ("0" + m) : m));
                entry.put("orderCount", orderCount);
                entry.put("totalAmount", totalAmount);
                data.add(entry);
            }
        }
        // Thống kê tổng quan
        int totalOrders = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Map<String, Object> entry : data) {
            totalOrders += (int) entry.get("orderCount");
            BigDecimal amount = (BigDecimal) entry.get("totalAmount");
            if (amount != null) totalRevenue = totalRevenue.add(amount);
        }
        BigDecimal avgRevenue = totalOrders > 0 ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 0, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
        result.put("data", data);
        result.put("totalOrders", totalOrders);
        result.put("totalRevenue", totalRevenue);
        result.put("avgRevenue", avgRevenue);
        return result;
    }
}
