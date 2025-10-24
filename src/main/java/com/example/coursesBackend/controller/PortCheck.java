package com.example.coursesBackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortCheck {
    @GetMapping("/port-check")
    public String portCheck(){
        return "Health is OK";
    }

}
