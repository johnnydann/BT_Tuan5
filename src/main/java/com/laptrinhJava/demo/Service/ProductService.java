package com.laptrinhJava.demo.Service;

import com.laptrinhJava.demo.Model.Product;
import com.laptrinhJava.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;


    public List<Product> getProductsByCategory(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
    }

    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Retrieve a product by its id
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Add a new product to the database
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // Update an existing product
    public Product updateProduct(@NotNull Product product) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        product.getId() + " does not exist."));
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setImage(product.getImage()); // Ensure image is updated
        existingProduct.setCategory(product.getCategory());
        existingProduct.setQuantity(product.getQuantity());
        return productRepository.save(existingProduct);
    }

    // Delete a product by its id
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        productRepository.deleteById(id);
    }

    public void decreaseProductQuantity(Product product, int quantity) {
        if (product.getQuantity() >= quantity) {
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
        } else {
            throw new IllegalArgumentException("Not enough quantity in stock for product: " + product.getName());
        }
    }

    public Page<Product> getProductsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    public Page<Product> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByKeyword(keyword, pageable);
    }

    public Page<Product> searchProductsByKeywordAndCategory(String keyword, Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (categoryId != null) {
            return productRepository.findByKeywordAndCategory(keyword, categoryId, pageable);
        } else {
            return productRepository.findByKeyword(keyword, pageable);
        }
    }

    public Page<Product> searchProductsByKeywordAndCategoryName(String keyword, String categoryName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByKeywordAndCategoryName(keyword, categoryName, pageable);
    }

    public List<Product> autocompleteProducts(String keyword) {
        return productRepository.findTop5ByNameContaining(keyword); // Lấy top 5 sản phẩm chứa keyword trong tên
    }
}