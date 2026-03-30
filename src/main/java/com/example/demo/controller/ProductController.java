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
import org.springframework.data.domain.Page;

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
    public String list(@RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "categoryId", required = false) Long categoryId,
                       @RequestParam(value = "page", defaultValue = "1") int page,
                       @RequestParam(value = "sort", required = false) String sort,
                       Model model) {
        
        int pageSize = 5;
        Page<Product> productPage;
        
        String sortBy = "id";
        String sortDir = "asc";

        if (sort != null && !sort.isEmpty()) {
            if (sort.equals("price_asc")) {
                sortBy = "price";
                sortDir = "asc";
            } else if (sort.equals("price_desc")) {
                sortBy = "price";
                sortDir = "desc";
            }
            model.addAttribute("sort", sort);
        }

        productPage = productService.searchProduct(keyword, categoryId, page - 1, pageSize, sortBy, sortDir);
        
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("keyword", keyword);
        }
        if (categoryId != null) {
            model.addAttribute("categoryId", categoryId);
        }
        
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
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