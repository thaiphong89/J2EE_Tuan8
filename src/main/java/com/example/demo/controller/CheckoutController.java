package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.Order;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping
    public String checkoutForm(Model model, RedirectAttributes redirectAttributes) {
        if (cartService.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Giỏ hàng của bạn đang trống!");
            return "redirect:/cart";
        }

        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("totalAmount", cartService.getAmount());
        return "checkout";
    }

    @PostMapping
    public String processCheckout(@RequestParam("customerName") String customerName,
                                  @RequestParam("customerAddress") String customerAddress,
                                  @RequestParam("customerPhone") String customerPhone,
                                  RedirectAttributes redirectAttributes) {
        if (cartService.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Giỏ hàng của bạn đang trống!");
            return "redirect:/cart";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account account = accountRepository.findByLoginName(username).orElse(null);

        Order order = orderService.createOrder(account, customerName, customerAddress, customerPhone, cartService);
        cartService.clear();

        redirectAttributes.addFlashAttribute("message", "Tiến hành Đặt hàng thành công! Mã đơn hàng của bạn là #" + order.getId() + ". Cảm ơn bạn đã mua sắm!");
        return "redirect:/products";
    }
}
