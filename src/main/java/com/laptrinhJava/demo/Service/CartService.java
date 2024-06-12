package com.laptrinhJava.demo.Service;

import com.laptrinhJava.demo.Model.Product;
import com.laptrinhJava.demo.repository.ProductRepository;
import com.laptrinhJava.demo.Model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private ProductRepository productRepository;

    private List<CartItem> cartItems;

    public List<CartItem> getCartItems() {
        if (this.cartItems == null) {
            this.cartItems = new ArrayList<>(); // Khởi tạo danh sách mới nếu là null
        }
        return this.cartItems;
    }

    public void addToCart(Long productId, int quantity) {
        if (this.cartItems == null) {
            this.cartItems = new ArrayList<>(); // Khởi tạo danh sách mới nếu là null
        }
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            CartItem item = findCartItem(productId);
            if (item != null) {
                item.setQuantity(item.getQuantity() + quantity);
            } else {
                cartItems.add(new CartItem(product, quantity));
            }
        }
    }

    public double calculateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }

    public void increaseQuantity(Long productId) {
        if (this.cartItems == null) {
            this.cartItems = new ArrayList<>(); // Khởi tạo danh sách mới nếu là null
        }
        CartItem item = findCartItem(productId);
        if (item != null) {
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                if (item.getQuantity() < product.getQuantity()) {
                    item.setQuantity(item.getQuantity() + 1);
                }
            }
        }
    }

    public void decreaseQuantity(Long productId) {
        CartItem item = findCartItem(productId);
        if (item != null && item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
        }
    }

    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void clearCart() {
        cartItems.clear();
    }

    private CartItem findCartItem(Long productId) {
        return cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }
}
