package com.laptrinhJava.demo.Controller;


import com.laptrinhJava.demo.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String showAdminDashboard(Model model) {
        // Add any attributes needed for the dashboard
        return "admin/layout-admin";
    }

    @GetMapping("/products")
    public String showProductList(Model model) {
        //show product list
        model.addAttribute("products", productService.getAllProducts());
        return "/admin/admin-product-list";
    }
}