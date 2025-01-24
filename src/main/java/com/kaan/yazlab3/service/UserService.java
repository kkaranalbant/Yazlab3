/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.yazlab3.service;

import com.kaan.yazlab3.exception.UserException;
import com.kaan.yazlab3.model.User;
import com.kaan.yazlab3.model.UserType;
import com.kaan.yazlab3.repo.ICrud;
import com.kaan.yazlab3.repo.UserRepo;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author root
 */
public class UserService {

    private static UserService userService;

    private final ICrud<User> userRepo;

    private final Integer premiumBound;

    private final LogService logService;

    private UserService() {
        logService = LogService.getInstance();
        this.userRepo = UserRepo.getInstance();
        premiumBound = 2000;
    }

    public static UserService getInstance() {
        if (userService == null) {
            synchronized (UserService.class) {
                if (userService == null) {
                    userService = new UserService();
                }
            }
        }
        return userService;
    }

    public void premiumCheck(User user) {
        if (user.getUserType().equals(UserType.NORMAL) && user.getTotalSpent() >= premiumBound) {
            user.setUserType(UserType.PREMIUM);
            userRepo.save(user);
        }
    }

    public void saveUser(User user) throws UserException {
        if (user.getBudget() < 0 || user.getName() == null || user.getName().isEmpty()) {
            throw new UserException("Invalıd Values");
        }
        userRepo.save(user);
    }

    public User getByUserId(Long userId) throws UserException {
        Optional<User> userOptional = Optional.ofNullable(userRepo.getById(userId));
        return userOptional.orElseThrow(() -> new UserException("User Not Found"));
    }

    public void deleteUserById(Long userId) {
        OrderService.getInstance().deleteByUserId(userId);
        userRepo.deleteById(userId);
    }

    public List<User> getAll() {
        return userRepo.getAll();
    }

    public User getByUsername(String username) throws UserException {
        UserRepo userRepoo = (UserRepo) userRepo;
        User user = userRepoo.getByUsername(username);
        if (user == null) {
            throw new UserException("User Not Found");
        }
        return user;
    }

    public boolean login(String username, String password) throws UserException {
        System.out.println(username);
        User user = userService.getByUsername(username);
        if (user == null) {
            throw new UserException("Invalid Username or Password");
        } else if (!user.getPassword().equals(password)) {
            throw new UserException("Invalid Username or Password");
        }
        return user.getId() == 1L;
    }

}
