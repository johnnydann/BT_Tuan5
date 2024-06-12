package com.laptrinhJava.demo.Controller;

import com.laptrinhJava.demo.Model.Product;
import com.laptrinhJava.demo.Service.CategoryService;
import com.laptrinhJava.demo.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping
    public String showProductList(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<Product> products;
        if (keyword != null && !keyword.isEmpty()) {
            products = productService.searchProductsByName(keyword);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "/products/product-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result,
                             @RequestParam("images") MultipartFile imageFile) {
        if (result.hasErrors()) {
            return "products/add-product";
        }
        try {
            if (!imageFile.isEmpty()) {
                byte[] bytes = imageFile.getBytes();
                Path path = Paths.get("src/main/resources/static/images/" + imageFile.getOriginalFilename());
                Files.write(path, bytes);
                product.setImage(imageFile.getOriginalFilename());
            }
            productService.addProduct(product);
        } catch (IOException e) {
            e.printStackTrace();
            return "products/add-product";
        }
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/update-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product,
                                BindingResult result, @RequestParam("images") MultipartFile imageFile) {
        if (result.hasErrors()) {
            product.setId(id);
            return "products/update-product";
        }
        try {
            Product existingProduct = productService.getProductById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

            if (!imageFile.isEmpty()) {
                byte[] bytes = imageFile.getBytes();
                Path path = Paths.get(uploadPath + imageFile.getOriginalFilename());
                Files.write(path, bytes);
                product.setImage(imageFile.getOriginalFilename());
            } else {
                product.setImage(existingProduct.getImage());
            }

            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setQuantity(product.getQuantity()); // Update quantity

            productService.updateProduct(existingProduct);
        } catch (IOException e) {
            e.printStackTrace();
            return "products/update-product";
        }
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }
}
