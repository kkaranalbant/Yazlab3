/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.yazlab3.model;

import com.kaan.yazlab3.service.ConcurrentPurchaseManager;
import com.kaan.yazlab3.service.OrderService;

/**
 *
 * @author root
 */
public class PurchaseProcess implements Runnable, Comparable<PurchaseProcess> {

    private User user;

    private Product product;

    private Integer quantity;

    private Integer priority;

    private OrderService orderService;

    private PurchaseProcess(User user, Product product, Integer quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.priority = 0;
        orderService = OrderService.getInstance();
    }

    @Override
    public void run() {
        orderService.add(user, product, quantity);
    }

    @Override
    public int compareTo(PurchaseProcess o) {
        return Integer.compare(this.priority, o.priority);
    }

    public static void createProcess(User user, Product product, Integer quantity) {
        ConcurrentPurchaseManager.getInstance().getQueue().put(new PurchaseProcess(user, product, quantity));
    }

}
