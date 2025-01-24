/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.yazlab3.service;

import com.kaan.yazlab3.exception.LogException;
import com.kaan.yazlab3.model.Log;
import com.kaan.yazlab3.model.LogType;
import com.kaan.yazlab3.model.Order;
import com.kaan.yazlab3.model.User;
import com.kaan.yazlab3.repo.ICrud;
import com.kaan.yazlab3.repo.LogRepo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author root
 */
public class LogService {

    private static LogService logService;

    private ICrud<Log> logRepo;

    private LogService() {
        logRepo = LogRepo.getInstance();
    }

    public static LogService getInstance() {
        if (logService == null) {
            synchronized (LogService.class) {
                if (logService == null) {
                    logService = new LogService();
                }
            }
        }
        return logService;
    }

    public void save (User user , Order order , LogType logType , String logDetails) {
        Log log = new Log () ;
        log.setUser(user);
        log.setOrder(order);
        log.setLogType(logType);
        log.setLogDetails(logDetails);
        log.setOrderQuantity(order.getQuantity());
        log.setUserType(user.getUserType());
        log.setLogDate(LocalDateTime.now());
        logRepo.save(log);
    }
    
    public void remove (Long id) throws LogException {
        Optional<Log> logOptional = Optional.ofNullable(logRepo.getById(id)) ;
        if (logOptional.isEmpty()) {
            throw new LogException ("Log Not Found") ;
        }
        logRepo.deleteById(id);
    }
    
    public List<Log> getAll () {
        return logRepo.getAll() ;
    }
    
}
