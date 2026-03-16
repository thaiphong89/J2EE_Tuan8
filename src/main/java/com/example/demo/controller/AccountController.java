package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.Role;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;

@Controller
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("account", new Account());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("account") Account account, Model model) {
        // Kiểm tra xem tên đăng nhập đã tồn tại chưa
        if (accountRepository.findByLoginName(account.getLoginName()).isPresent()) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại, vui lòng chọn tên khác.");
            return "register";
        }
        
        // Mã hóa mật khẩu
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        
        // Gán Role mặc định: ROLE_USER
        // Nếu role chưa có trong DB, tự động tạo mới
        Role defaultRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role newRole = new Role("ROLE_USER");
            return roleRepository.save(newRole);
        });
        
        account.setRoles(Collections.singleton(defaultRole));
        
        // Lưu xuống DB
        accountRepository.save(account);
        return "redirect:/login?registered"; // Chuyển hướng đến trang đăng nhập sau khi tạo thành công
    }
}
