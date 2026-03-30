package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.model.Order;
import com.example.demo.model.OrderDetail;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Transactional
    public Order createOrder(Account account, String customerName, String address, String phone, CartService cartService) {
        Order order = new Order();
        order.setAccount(account);
        order.setCustomerName(customerName);
        order.setCustomerAddress(address);
        order.setCustomerPhone(phone);
        order.setCreatedAt(new Date());
        order.setTotalAmount(cartService.getAmount());

        List<OrderDetail> details = cartService.getItems().stream().map(item -> {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());
            detail.setProduct(productService.getById(item.getProductId()).orElse(null));
            return detail;
        }).collect(Collectors.toList());

        order.setOrderDetails(details);
        orderRepository.save(order);

        return order;
    }
}
