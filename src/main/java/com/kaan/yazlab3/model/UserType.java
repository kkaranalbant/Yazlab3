/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.yazlab3.model;

/**
 *
 * @author root
 */
public enum UserType {

    NORMAL(10),
    PREMIUM(15);

    private int priority;

    private UserType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

}
