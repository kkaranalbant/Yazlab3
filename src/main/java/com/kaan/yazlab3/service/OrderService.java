/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.yazlab3.service;

import com.kaan.yazlab3.exception.OrderException;
import com.kaan.yazlab3.model.ConfirmProcess;
import com.kaan.yazlab3.model.LogType;
import com.kaan.yazlab3.model.Order;
import com.kaan.yazlab3.model.OrderStatus;
import com.kaan.yazlab3.model.Product;
import com.kaan.yazlab3.model.User;
import com.kaan.yazlab3.repo.ICrud;
import com.kaan.yazlab3.repo.OrderRepo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author root
 */
public class OrderService {

    private static OrderService orderService;

    private final ProductService productService;

    private final UserService userService;

    private ICrud<Order> orderRepo;

    private final LogService logService;

    private OrderService() {
        logService = LogService.getInstance();
        orderRepo = OrderRepo.getInstance();
        productService = ProductService.getInstance();
        userService = UserService.getInstance();
    }

    public static OrderService getInstance() {
        if (orderService == null) {
            synchronized (OrderService.class) {
                if (orderService == null) {
                    orderService = new OrderService();
                }
            }
        }
        return orderService;
    }

    synchronized public void add(User user, Product product, Integer quantity) throws OrderException {
        if (product.getStock() < quantity) {
            throw new OrderException("Not Sufficent Stock");
        }
        if (user.getBudget() < quantity * product.getPrice()) {
            throw new OrderException("Not Sufficent Balance");
        }
        user.setBudget(user.getBudget() - quantity * product.getPrice());
        user.setTotalSpent(user.getTotalSpent() + quantity * product.getPrice());
        product.setStock(product.getStock() - quantity);
        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(quantity * product.getPrice());
        order.setQuantity(quantity);
        order.setProduct(product);
        order.setOrderStatus(OrderStatus.WAITING);
        LocalDateTime orderDate = LocalDateTime.now();
        order.setOrderDate(LocalDateTime.now());
        userService.premiumCheck(user);
        userService.saveUser(user);
        productService.saveProduct(product);
        orderRepo.save(order);
        ConfirmProcess.createConfirmProcess(user, product, orderDate);
        StringBuilder logDetailsSb = new StringBuilder();
        logDetailsSb.append(user.getId()).append(" id numaralı kullanıcı ")
                .append(product.getId()).append(" id numarali üründen ").append(order.getQuantity())
                .append(" adet sipariş verdi.\nSiparis No : ")
                .append(getByUserIdAndProductIdAndOrderDateTime(user.getId(), product.getId(), orderDate).getId())
                .append("\nFiyat: ").append(quantity * product.getPrice());
        logService.save(user, order, LogType.INFO, logDetailsSb.toString());
    }

    public void deleteByUserId(Long userId) {
        for (Order order : orderRepo.getAll()) {
            if (order.getUser().getId() == userId) {
                orderRepo.delete(order);
            }
        }
    }

    public void deleteById(Long orderId) throws OrderException {
        Order order = orderRepo.getById(orderId);
        if (order == null) {
            throw new OrderException("Order Not Found");
        }
        orderRepo.delete(order);
    }

    public void deleteByProductId(Long productId) throws OrderException {
        for (Order order : orderRepo.getAll()) {
            if (order.getProduct().getId() == productId) {
                orderRepo.delete(order);
            }
        }
    }

    public List<Order> getAllByUserId(Long userId) {
        return orderRepo.getAll().stream().filter((Order order) -> order.getUser().getId() == userId).collect(Collectors.toList());
    }

    public List<Order> getAllByProductId(Long productId) {
        return orderRepo.getAll().stream().filter((Order order) -> order.getProduct().getId() == productId).collect(Collectors.toList());
    }

    public List<Order> getAll() {
        return orderRepo.getAll();
    }

    public List<Order> getByUserIdAndProductId(Long userId, Long productId) throws OrderException {
        OrderRepo orderRepoo = (OrderRepo) OrderRepo.getInstance();
        List<Order> orders = orderRepoo.getByUserIdAndProductId(userId, productId);
        if (orders.isEmpty()) {
            throw new OrderException("Order Not Found");
        }
        orderRepoo = null;
        System.gc();
        return orders;
    }

    public Order getByUserIdAndProductIdAndOrderDateTime(Long userId, Long productId, LocalDateTime orderDateTime) throws OrderException {
        OrderRepo orderRepoo = (OrderRepo) OrderRepo.getInstance();
        Order order = orderRepoo.getByUserIdAndProductIdAndOrderDateTime(userId, productId, orderDateTime);
        if (order == null) {
            throw new OrderException("Order Not Found");
        }
        orderRepoo = null;
        System.gc();
        return order;
    }

    public void confirmById(Long id) throws OrderException {
        Order order = orderRepo.getById(id);
        if (order == null) {
            throw new OrderException("Order Not Found");
        }
        if (order.getOrderStatus().equals(OrderStatus.OK)) {
            throw new OrderException("Order Already Confirmed");
        }
        order.setOrderStatus(OrderStatus.OK);
        orderRepo.save(order);
    }

    public void cancelById(Long id) throws OrderException {
        Order order = orderRepo.getById(id);
        if (order == null) {
            throw new OrderException("Order Not Found");
        }
        if (order.getOrderStatus().equals(OrderStatus.CANCELLED)) {
            throw new OrderException("Order Already Cancelled");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        logService.save(order.getUser(), order, LogType.INFO, id + " id numaralı siparis iptal edildi.");
        orderRepo.save(order);
    }

    public void waitById(Long id) throws OrderException {
        Order order = orderRepo.getById(id);
        if (order == null) {
            throw new OrderException("Order Not Found");
        }
        if (order.getOrderStatus().equals(OrderStatus.WAITING)) {
            throw new OrderException("Order is Already Waiting");
        }
        order.setOrderStatus(OrderStatus.WAITING);
        logService.save(order.getUser(), order, LogType.INFO, id + " id numaralı siparis bekletiliyor.");
        orderRepo.save(order);
    }

    public void addOthersToQueue() {
        List<Order> ordersToAdd = getAll().stream().filter((order) -> order.getOrderStatus().equals(OrderStatus.WAITING)).collect(Collectors.toList());
        for (Order order : ordersToAdd) {
            ConfirmProcess.createOldConfirmProcess(order.getUser(), order.getProduct(), order.getOrderDate());
        }
    }

}
