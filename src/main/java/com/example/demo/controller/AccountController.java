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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class AccountController {
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private SwipeRepository swipeRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/account")
    public String accountPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "account";
    }

    @PostMapping("/account")
    public String updateAccount(@ModelAttribute("user") User updatedUser, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        // Only allow updating allowed fields
        user.setEmail(updatedUser.getEmail());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setFirstname(updatedUser.getFirstname());
        user.setLastname(updatedUser.getLastname());
        user.setBirthday(updatedUser.getBirthday());
        user.setCity(updatedUser.getCity());
        user.setCounty(updatedUser.getCounty());
        userRepository.save(user);
        model.addAttribute("user", user);
        model.addAttribute("success", true);
        return "account";
    }

    @GetMapping("/notifications")
    public String notificationsPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        List<String> notifications = new ArrayList<>();
        List<String> matches = new ArrayList<>();
        List<String> matchUsernames = new ArrayList<>();
        Set<String> alreadyMatched = new HashSet<>();

        // 1. As pet owner: who liked my pets?
        List<Pet> myPets = petRepository.findAllWithPhotos();
        myPets.removeIf(p -> p.getOwner() == null || !p.getOwner().getId().equals(user.getId()));
        for (Pet pet : myPets) {
            List<Swipe> swipes = swipeRepository.findByPet(pet);
            for (Swipe swipe : swipes) {
                if ("RIGHT".equals(swipe.getSwipeDirection())) {
                    notifications.add("Your pet " + pet.getName() + " received a like from " + swipe.getUser().getUsername() + "!");
                    // Check for mutual like
                    List<Swipe> theirSwipes = swipeRepository.findByUser(swipe.getUser());
                    for (Swipe theirSwipe : theirSwipes) {
                        if ("RIGHT".equals(theirSwipe.getSwipeDirection()) && theirSwipe.getPet().getOwner() != null && theirSwipe.getPet().getOwner().getId().equals(user.getId())) {
                            String matchUsername = swipe.getUser().getUsername();
                            if (!alreadyMatched.contains(matchUsername)) {
                                matches.add("You matched with " + matchUsername + "! Start a chat.");
                                matchUsernames.add(matchUsername);
                                alreadyMatched.add(matchUsername);
                            }
                        }
                    }
                }
            }
        }

        // 2. As swiper: did I match with any pets I liked?
        List<Swipe> mySwipes = swipeRepository.findByUser(user);
        for (Swipe mySwipe : mySwipes) {
            if ("RIGHT".equals(mySwipe.getSwipeDirection()) && mySwipe.getPet().getOwner() != null && !mySwipe.getPet().getOwner().getId().equals(user.getId())) {
                User otherOwner = mySwipe.getPet().getOwner();
                List<Swipe> otherSwipes = swipeRepository.findByUser(otherOwner);
                for (Swipe otherSwipe : otherSwipes) {
                    if ("RIGHT".equals(otherSwipe.getSwipeDirection()) && otherSwipe.getPet().getOwner() != null && otherSwipe.getPet().getOwner().getId().equals(user.getId())) {
                        String matchUsername = otherOwner.getUsername();
                        if (!alreadyMatched.contains(matchUsername)) {
                            matches.add("You matched with " + matchUsername + "! Start a chat.");
                            matchUsernames.add(matchUsername);
                            alreadyMatched.add(matchUsername);
                        }
                    }
                }
            }
        }

        // 3. As swiper: did I get likes on my pets from other users? (for completeness)
        for (Swipe mySwipe : mySwipes) {
            if ("RIGHT".equals(mySwipe.getSwipeDirection()) && mySwipe.getPet().getOwner() != null && mySwipe.getPet().getOwner().getId().equals(user.getId())) {
                // Someone liked my pet, already handled above
                continue;
            }
        }

        model.addAttribute("notifications", notifications);
        model.addAttribute("matches", matches);
        model.addAttribute("matchUsernames", matchUsernames);
        return "notifications";
    }
}