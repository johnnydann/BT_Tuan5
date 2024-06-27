package com.laptrinhJava.demo.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private long quantity;
    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<OrderDetail> orderDetails;
    private String image; // Path to the image
    private String imageUrl; // Đường dẫn URL đến hình ảnh (nếu cần)
}
