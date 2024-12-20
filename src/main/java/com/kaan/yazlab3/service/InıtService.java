/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.yazlab3.service;

import com.kaan.yazlab3.model.Product;
import com.kaan.yazlab3.model.User;
import com.kaan.yazlab3.model.UserType;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author root
 */
public class InıtService {

    private final UserService userService;
    private final ProductService productService;
    private final Random random;
    private final Integer MIN_USER_AMOUNT_ORIGIN;
    private final Integer MIN_USER_AMOUNT_BOUND;
    private final Integer MIN_PREMIUM_USER_AMOUNT;
    private final Float BUDGET_ORIGIN;
    private final Float BUDGER_BOUND;
    private final List<User> users;
    private final List<Product> products;

    public InıtService() {
        this.userService = UserService.getInstance();
        this.productService = ProductService.getInstance();
        random = new Random();
        MIN_PREMIUM_USER_AMOUNT = 2;
        MIN_USER_AMOUNT_BOUND = 10;
        MIN_USER_AMOUNT_ORIGIN = 5;
        BUDGET_ORIGIN = 500F;
        BUDGER_BOUND = 3000F;
        users = new LinkedList();
        products = new LinkedList() ;
    }

//    public void init() {
//        userService.
//    }

    private boolean isValidİnitalization() {
        Integer premiumCounter = 0 ;
        for (User user : users) {
            if (user.getUserType().equals(UserType.PREMIUM)) {
                premiumCounter++;
            }
        }
        return premiumCounter >= MIN_PREMIUM_USER_AMOUNT ;
    }

}
