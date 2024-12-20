/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.yazlab3.service;

import com.kaan.yazlab3.exception.ProductException;
import com.kaan.yazlab3.model.Product;
import com.kaan.yazlab3.repo.ICrud;
import com.kaan.yazlab3.repo.ProductRepo;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author root
 */
public class ProductService {

    private static ProductService productService;

    private final ICrud<Product> productRepo;

    private ProductService() {
        this.productRepo = ProductRepo.getInstance();
    }

    public static ProductService getInstance() {
        if (productService == null) {
            synchronized (OrderService.class) {
                if (productService == null) {
                    productService = new ProductService();
                }
            }
        }
        return productService;
    }

    public Product getById(Long productId) throws ProductException {
        Optional<Product> productOptional = Optional.ofNullable(productRepo.getById(productId));
        return productOptional.orElseThrow(() -> new ProductException("Product Not Found"));
    }

    public void saveProduct(Product product) throws ProductException {
        if (product.getName() == null || product.getName().isEmpty() || product.getStock() < 0 || product.getPrice() < 0) {
            throw new ProductException("Invalid Value");
        }
        productRepo.save(product);
    }

    public void deleteProductById(Long productId) throws ProductException {
        if (productRepo.getById(productId) == null) {
            throw new ProductException("Product Not Found");
        }
        OrderService.getInstance().deleteByProductId(productId);
        productRepo.deleteById(productId);
    }

    public List<Product> getAll() {
        return productRepo.getAll();
    }

}
