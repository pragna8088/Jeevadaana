package com.jeevadaana.controller;

import com.jeevadaana.service.ServiceException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public String handleServiceException(ServiceException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}
