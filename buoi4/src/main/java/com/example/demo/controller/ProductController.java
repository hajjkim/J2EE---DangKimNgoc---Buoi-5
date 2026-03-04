package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import java.nio.file.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService,
                             CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // LIST
    @GetMapping
    public String index(Model model) {
        model.addAttribute("products", productService.findAll());
        return "products/index";
    }

    // CREATE FORM
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "products/form";
    }

    // EDIT FORM
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("categories", categoryService.findAll());
        return "products/form";
    }

    // SAVE (create + update)
    @PostMapping("/save")
public String save(@ModelAttribute Product product,
                   @RequestParam(value = "imageFile", required = false) MultipartFile imageFile)
        throws IOException {

    // Nếu user có chọn file thì lưu file và set image path
    if (imageFile != null && !imageFile.isEmpty()) {
        Path uploadDir = Paths.get("uploads");
        Files.createDirectories(uploadDir);

        String original = imageFile.getOriginalFilename();
        String safeName = (original == null ? "image" : original).replaceAll("[^a-zA-Z0-9._-]", "_");

        // tránh trùng tên: thêm timestamp
        String fileName = System.currentTimeMillis() + "_" + safeName;

        Path target = uploadDir.resolve(fileName);
        Files.copy(imageFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        product.setImage("/uploads/" + fileName);
    }

    productService.save(product);
    return "redirect:/products";
}

    // DELETE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/products";
    }
}