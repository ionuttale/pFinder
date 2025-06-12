package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AddPetsRedirectController {
    @GetMapping("/add_pets")
    public String redirectToPetsAddPets() {
        return "redirect:/pets/add_pets";
    }
}