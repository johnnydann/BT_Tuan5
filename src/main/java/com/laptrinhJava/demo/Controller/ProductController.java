package com.laptrinhJava.demo.Controller;

import com.laptrinhJava.demo.Model.Category;
import com.laptrinhJava.demo.Model.Product;
import com.laptrinhJava.demo.Service.CategoryService;
import com.laptrinhJava.demo.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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

    @RequestMapping("/category/{categoryName}")
    public String getProductsByCategory(@PathVariable("categoryName") String categoryName,
                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "9") int size,
                                        Model model) {
        Page<Product> products = productService.getProductsByCategory(categoryName, page, size);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("categoryName", categoryName);
        return "products/product-list";
    }


    @GetMapping("/search")
    public String searchProduct(@RequestParam("keyword") String keyword,
                                @RequestParam(value = "category", required = false) Long categoryId,
                                @RequestParam(value = "categoryName", required = false) String categoryName,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "9") int size,
                                Model model) {
        Page<Product> searchResults;
        if (categoryId != null) {
            searchResults = productService.searchProductsByKeywordAndCategory(keyword, categoryId, page, size);
        } else if (categoryName != null && !categoryName.isEmpty()) {
            searchResults = productService.searchProductsByKeywordAndCategoryName(keyword, categoryName, page, size);
        } else {
            searchResults = productService.searchProducts(keyword, page, size);
        }
        model.addAttribute("products", searchResults);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResults.getTotalPages());
        return "products/product-list";
    }

    @GetMapping("/autocomplete")
    public @ResponseBody List<Product> autocompleteProducts(@RequestParam("keyword") String keyword) {
        return productService.autocompleteProducts(keyword);
    }

    @GetMapping
    public String showProductList(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "9") int size,
                                  Model model) {
        Page<Product> productPage = productService.getProductsPaginated(page, size);
        model.addAttribute("products", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "products/product-list";
    }


    @GetMapping("/details/{id}")
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
                             @RequestParam("images") MultipartFile imageFile, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/add-product";
        }
        try {
            if (!imageFile.isEmpty()) {
                String filename = imageFile.getOriginalFilename();
                Path path = Paths.get(uploadPath, filename);
                Files.write(path, imageFile.getBytes());
                product.setImage(filename);
                product.setImageUrl("/images/" + filename);
            }
            productService.addProduct(product);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/add-product";
        }
        return "redirect:/admin/products";
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
                                @RequestParam(value = "images", required = false) MultipartFile imageProduct,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            product.setId(id);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/update-product";
        }

        // Get the existing product from the database
        Product existingProduct = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        // Update fields that are allowed to be updated
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setQuantity(product.getQuantity());

        // Handle image update
        if (imageProduct != null && !imageProduct.isEmpty()) {
            try {
                // Save new image if provided
                Path saveDirectoryPath = Paths.get("src/main/resources/static/images");
                Files.createDirectories(saveDirectoryPath);

                String newImageFile = UUID.randomUUID() + ".png";
                Path path = saveDirectoryPath.resolve(newImageFile);
                Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                existingProduct.setImage(newImageFile);

                logger.info("Image saved at: " + path.toString());
                logger.info("Product image file name: " + existingProduct.getImage());

                // Delete old image file if exists and different from new one
                String oldImageFile = product.getImage();
                if (oldImageFile != null && !oldImageFile.equals(newImageFile)) {
                    Path oldPath = saveDirectoryPath.resolve(oldImageFile);
                    Files.deleteIfExists(oldPath);
                    logger.info("Deleted old image file: " + oldPath.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("product", product);
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("errorMessage", "Error saving image");
                return "products/update-product";
            }
        }

        // Update the product in the database
        productService.updateProduct(existingProduct);

        return "redirect:/admin/products";
    }



    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/admin/products";
    }
}
