package com.example.demo.controller;

import com.example.demo.model.Pet;
import com.example.demo.model.Swipe;
import com.example.demo.model.User;
import com.example.demo.repository.PetRepository;
import com.example.demo.repository.SwipeRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class SwipeController {
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private SwipeRepository swipeRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/swipe")
    public String getSwipePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            model.addAttribute("pets", List.of());
            return "swipe";
        }
        User user = userRepository.findByUsername(userDetails.getUsername());
        List<Pet> pets = petRepository.findAllWithPhotos();
        // Remove pets owned by the current user
        pets.removeIf(p -> p.getOwner() != null && p.getOwner().getId().equals(user.getId()));
        // Remove pets already swiped by the user
        List<Swipe> swipes = swipeRepository.findByUser(user);
        java.util.Set<Long> swipedPetIds = new java.util.HashSet<>();
        for (Swipe s : swipes) {
            swipedPetIds.add(s.getPet().getId());
        }
        pets.removeIf(p -> swipedPetIds.contains(p.getId()));
        model.addAttribute("pets", pets);
        return "swipe";
    }

    @PostMapping("/swipe")
    public String handleSwipe(@RequestParam Long petId,
                             @RequestParam String direction,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        if (userDetails != null) {
            Pet pet = petRepository.findById(petId).orElse(null);
            User user = userRepository.findByUsername(userDetails.getUsername());
            if (pet != null && user != null) {
                // Prevent swiping on own pet
                if (pet.getOwner() != null && pet.getOwner().getId().equals(user.getId())) {
                    // Do nothing, skip
                } else {
                    Swipe swipe = new Swipe();
                    swipe.setPet(pet);
                    swipe.setUser(user);
                    swipe.setSwipeDirection(direction);
                    swipe.setTimestamp(LocalDateTime.now());
                    // Check for match (mutual right swipe)
                    boolean isMatch = false;
                    if ("RIGHT".equals(direction)) {
                        List<Swipe> petSwipes = swipeRepository.findByPet(pet);
                        for (Swipe s : petSwipes) {
                            if ("RIGHT".equals(s.getSwipeDirection()) && s.getUser().getId().equals(pet.getOwner().getId())) {
                                isMatch = true;
                                break;
                            }
                        }
                    }
                    swipe.setMatched(isMatch);
                    swipeRepository.save(swipe);
                    // If match, also update the other swipe
                    if (isMatch) {
                        List<Swipe> otherSwipes = swipeRepository.findByPet(pet);
                        for (Swipe s : otherSwipes) {
                            if ("RIGHT".equals(s.getSwipeDirection()) && s.getUser().getId().equals(pet.getOwner().getId())) {
                                s.setMatched(true);
                                swipeRepository.save(s);
                            }
                        }
                    }
                }
            }
            // After swipe, reload pets list with correct filtering
            List<Pet> pets = petRepository.findAllWithPhotos();
            pets.removeIf(p -> p.getOwner() != null && p.getOwner().getId().equals(user.getId()));
            List<Swipe> swipes = swipeRepository.findByUser(user);
            java.util.Set<Long> swipedPetIds = new java.util.HashSet<>();
            for (Swipe s : swipes) {
                swipedPetIds.add(s.getPet().getId());
            }
            pets.removeIf(p -> swipedPetIds.contains(p.getId()));
            model.addAttribute("pets", pets);
        } else {
            model.addAttribute("pets", List.of());
        }
        return "swipe";
    }
}
