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
            // Lấy dữ liệu theo từng ngày của tháng
            int y = year;
            int m = month;
            LocalDate now = LocalDate.now();
            int daysInMonth = LocalDate.of(y, m, 1).lengthOfMonth();
            int lastDay = (y == now.getYear() && m == now.getMonthValue()) ? now.getDayOfMonth() : daysInMonth;
            for (int d = 1; d <= lastDay; d++) {
                LocalDate date = LocalDate.of(y, m, d);
                int orderCount = saleService.countSalesByDate(date);
                BigDecimal totalAmount = saleService.sumAmountByDate(date);
                Map<String, Object> entry = new HashMap<>();
                entry.put("label", date.toString());
                entry.put("orderCount", orderCount);
                entry.put("totalAmount", totalAmount);
                data.add(entry);
            }
        } else if ("year".equals(type)) {
            // Lấy dữ liệu theo từng tháng của năm
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
        result.put("data", data);
        return result;
    }
}
