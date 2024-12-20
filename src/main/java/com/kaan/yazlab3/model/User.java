/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.yazlab3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 *
 * @author root
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity{

    private String name;

    private Float budget;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private Float totalSpent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getBudget() {
        return budget;
    }

    public void setBudget(Float budget) {
        this.budget = budget;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Float getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Float totalSpent) {
        this.totalSpent = totalSpent;
    }

}
