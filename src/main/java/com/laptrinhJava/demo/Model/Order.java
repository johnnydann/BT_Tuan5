package com.laptrinhJava.demo.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String shippingAddress;
    private String phoneNumber;
    private String notes;
    private double totalPrice;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

    public List<OrderDetail> getOrderDetails() {
        return Optional.ofNullable(orderDetails).orElse(new ArrayList<>());
    }

}
