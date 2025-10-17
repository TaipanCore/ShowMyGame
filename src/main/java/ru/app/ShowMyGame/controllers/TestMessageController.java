package ru.app.ShowMyGame.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestMessageController
{
    @GetMapping("/hello")
    public String hello()
    {
        return "Hello, Spring Boot enable!";
    }
}
