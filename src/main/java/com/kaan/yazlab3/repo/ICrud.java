/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kaan.yazlab3.repo;

import com.kaan.yazlab3.model.BaseEntity;
import java.util.List;

/**
 *
 * @author root
 */
public interface ICrud <T extends BaseEntity> {
    
    public void save (T entity) ;
    
    public void delete (T entity) ;
    
    public void deleteById (Long id) ;
    
    public T getById (Long id) ;
    
    public List<T> getAll () ;
    
}
