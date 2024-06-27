package com.laptrinhJava.demo.Controller;

import com.laptrinhJava.demo.Model.Category;
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
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    private final Logger logger = Logger.getLogger(ProductController.class.getName());

    @Value("${upload.path}")
    private String uploadPath;

    @ModelAttribute("categories")
    public List<Category> populateCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam("keyword") String keyword,
                                @RequestParam(value = "category", required = false) Long categoryId,
                                @RequestParam(value = "categoryName", required = false) String categoryName,
                                Model model) {
        List<Product> searchResults;
        if (categoryId != null) {
            searchResults = productService.searchProductsByKeywordAndCategory(keyword, categoryId);
        } else if (categoryName != null && !categoryName.isEmpty()) {
            searchResults = productService.searchProductsByKeywordAndCategoryName(keyword, categoryName);
        } else {
            searchResults = productService.searchProducts(keyword);
        }
        model.addAttribute("products", searchResults);
        return "products/product-list";
    }

    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products/product-list";
    }

    @GetMapping("/{id}")
    public String showProductDetails(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "products/product-details";
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
                                @RequestParam("images") MultipartFile imageProduct,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            product.setId(id);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/update-product";
        }

        if (imageProduct != null && !imageProduct.isEmpty()) {
            try {
                Path saveDirectoryPath = Paths.get("src/main/resources/static/images");
                File saveDirectory = new File(saveDirectoryPath.toUri());

                if (!saveDirectory.exists()) {
                    saveDirectory.mkdirs();
                }

                String newImageFile = UUID.randomUUID() + ".png";
                Path path = saveDirectoryPath.resolve(newImageFile);
                Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                product.setImage(newImageFile);

                logger.info("Image saved at: " + path.toString());
                logger.info("Product image file name: " + product.getImage());
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("product", product);
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("errorMessage", "Error saving image");
                return "products/update-product";
            }
        }

        productService.updateProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }
}
