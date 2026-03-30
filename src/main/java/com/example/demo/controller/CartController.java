package com.example.demo.controller;

import com.example.demo.model.CartItem;
import com.example.demo.service.CartService;
import com.example.demo.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("totalAmount", cartService.getAmount());
        return "cart";
    }

    @GetMapping("/add/{id}")
    public String add(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        productService.getById(id).ifPresent(product -> {
            CartItem item = new CartItem(product.getId(), product.getName(), product.getPrice(), 1);
            cartService.add(item);
            redirectAttributes.addFlashAttribute("message", "Đã thêm " + product.getName() + " vào giỏ hàng!");
        });
        
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/products";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable Long id) {
        cartService.remove(id);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clear() {
        cartService.clear();
        return "redirect:/cart";
    }
}
