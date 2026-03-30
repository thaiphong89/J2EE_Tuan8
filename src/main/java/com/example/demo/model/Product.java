package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @Range(min = 1, max = 1000000000, message = "Giá sản phẩm phải từ 1 đến 1,000,000,000")
    private Double price;

    @Size(max = 200, message = "Tên hình ảnh không quá 200 kí tự")
    private String image;

    @NotNull(message = "Vui lòng chọn danh mục sản phẩm")
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Product() {
    }

    public Product(Long id, String name, Double price, String image, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}