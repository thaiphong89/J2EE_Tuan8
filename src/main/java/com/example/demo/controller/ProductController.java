package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // Thư mục lưu ảnh upload
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.getAll());
        return "product-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAll());
        return "add-product";
    }

    @PostMapping("/add")
    public String save(@Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAll());
            return "add-product";
        }

        // Xử lý Upload ảnh
        if (!imageFile.isEmpty()) {
            try {
                String fileName = imageFile.getOriginalFilename();
                Path dirPath = Paths.get(uploadDir);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }
                Path filePath = dirPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                product.setImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        productService.add(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        productService.getById(id).ifPresent(product -> model.addAttribute("product", product));
        model.addAttribute("categories", categoryService.getAll());
        return "edit-product";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAll());
            return "edit-product";
        }

        if (!imageFile.isEmpty()) {
            try {
                String fileName = imageFile.getOriginalFilename();
                Path dirPath = Paths.get(uploadDir);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }
                Path filePath = dirPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                product.setImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        product.setId(id);
        productService.update(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/products";
    }
}