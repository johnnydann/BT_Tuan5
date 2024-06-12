package com.laptrinhJava.demo.Service;

import com.laptrinhJava.demo.Model.CartItem;
import com.laptrinhJava.demo.Model.Order;
import com.laptrinhJava.demo.Model.OrderDetail;
import com.laptrinhJava.demo.Model.Product;
import com.laptrinhJava.demo.repository.OrderDetailRepository;
import com.laptrinhJava.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService; // Inject ProductService

    public Order createOrder(String customerName, List<CartItem> cartItems, String shippingAddress, String phoneNumber, String notes) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setNotes(notes);
        order = orderRepository.save(order);
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);
            productService.decreaseProductQuantity(item.getProduct(), item.getQuantity());
        }
        cartService.clearCart();
        return order;
    }

}
