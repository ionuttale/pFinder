package com.example.demo.controller;

import com.example.demo.model.Pet;
import com.example.demo.model.PetPhoto;
import com.example.demo.model.User;
import com.example.demo.repository.PetRepository;
import com.example.demo.repository.PetPhotoRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pets")
public class PetController {
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private PetPhotoRepository petPhotoRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/add")
    public String showAddPetForm(Model model) {
        model.addAttribute("pet", new Pet());
        return "add_pets";
    }

    @PostMapping("/add")
    public String addPet(@ModelAttribute Pet pet,
                         @RequestParam("photos") List<MultipartFile> photos,
                         Principal principal) throws IOException {
        User owner = userRepository.findByUsername(principal.getName());
        pet.setOwner(owner);
        Pet savedPet = petRepository.save(pet);
        for (MultipartFile file : photos) {
            if (!file.isEmpty()) {
                PetPhoto photo = new PetPhoto();
                photo.setImage(file.getBytes());
                photo.setPet(savedPet);
                petPhotoRepository.save(photo);
            }
        }
        return "redirect:/main";
    }

    @GetMapping("/all")
    public String getAllPets(Model model) {
        List<Pet> pets = petRepository.findAll();
        model.addAttribute("pets", pets);
        return "swipe";
    }

    @GetMapping("/photo/{photoId}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long photoId) {
        Optional<PetPhoto> photoOpt = petPhotoRepository.findById(photoId);
        if (photoOpt.isPresent()) {
            return ResponseEntity.ok().body(photoOpt.get().getImage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @RequestMapping("/add_pets")
    public String addPetPage(Model model) {
        model.addAttribute("pet", new Pet());
        return "add_pets";
    }

    @PostMapping("/add_pets")
    public String addPetSubmit(
            @RequestParam String name,
            @RequestParam Integer age,
            @RequestParam String breed,
            @RequestParam String color,
            @RequestParam String description,
            @RequestParam String fav_toy,
            @RequestParam String fav_activity,
            @RequestParam String fav_treat,
            @RequestParam String gender,
            @RequestParam("vaccination_status") Boolean vaccinationStatus,
            @RequestParam String personality_traits,
            @RequestParam String size,
            @RequestParam Double weight,
            @RequestParam("photos") List<MultipartFile> photos,
            Principal principal) throws IOException {
        User owner = userRepository.findByUsername(principal.getName());
        Pet pet = new Pet();
        pet.setName(name);
        pet.setAge(age);
        pet.setBreed(breed);
        pet.setColor(color);
        pet.setDescription(description);
        pet.setFav_toy(fav_toy);
        pet.setFav_activity(fav_activity);
        pet.setFav_treat(fav_treat);
        pet.setGender(gender);
        pet.setVaccination_status(vaccinationStatus);
        pet.setPersonality_traits(personality_traits);
        pet.setSize(size);
        pet.setWeight(weight);
        pet.setOwner(owner);
        Pet savedPet = petRepository.save(pet);
        for (MultipartFile file : photos) {
            if (!file.isEmpty()) {
                PetPhoto photo = new PetPhoto();
                photo.setImage(file.getBytes());
                photo.setPet(savedPet);
                petPhotoRepository.save(photo);
            }
        }
        return "redirect:/main";
    }
}
