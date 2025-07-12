package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.CustomerReportDto;
import com.example.project_c0824m1_jv103.model.Sale;
import com.example.project_c0824m1_jv103.service.CustomerReport.CustomerReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/CustomerReport")
public class CustomerReportController extends BaseAdminController {

    @Autowired
    private CustomerReportService customerReportService;

    @GetMapping("")
    public ModelAndView showCustomerReportList(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "gender", required = false) String gender,
            @RequestParam(name = "minAge", required = false) Integer minAge,
            @RequestParam(name = "maxAge", required = false) Integer maxAge) {
        if (page < 0) {
            page = 0;
        }

        Pageable pageable = PageRequest.of(page, 6);
        Page<CustomerReportDto> reportPage = customerReportService.getCustomerReports(pageable, gender, minAge, maxAge);

        ModelAndView modelAndView = new ModelAndView("customerreport/list");
        modelAndView.addObject("reports", reportPage);
        modelAndView.addObject("currentPage", "customer-report");
        modelAndView.addObject("selectedGender", gender);
        modelAndView.addObject("minAge", minAge);
        modelAndView.addObject("maxAge", maxAge);
        return modelAndView;
    }

    @GetMapping("/view/{customerId}")
    public ModelAndView showCustomerDetails(@PathVariable("customerId") Integer customerId) {
        ModelAndView modelAndView = new ModelAndView("customerreport/detail");
        List<Sale> sales = customerReportService.getCustomerSalesDetails(customerId);
        CustomerReportDto customerInfo = customerReportService.getCustomerReportInfo(customerId);
        modelAndView.addObject("sales", sales);
        modelAndView.addObject("customerInfo", customerInfo);
        modelAndView.addObject("currentPage", "customer-report");
        return modelAndView;
    }
}