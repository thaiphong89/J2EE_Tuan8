package com.example.demo.controller;

import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String home(@RequestParam(value = "categoryId", required = false) Long categoryId, Model model) {
        model.addAttribute("categories", categoryService.getAll());
        if (categoryId != null) {
            model.addAttribute("products", productService.getByCategoryId(categoryId));
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            model.addAttribute("products", productService.getAll());
        }
        return "home";
    }

    @GetMapping("/home")
    public String homeRedirect() {
        return "redirect:/";
    }
}
