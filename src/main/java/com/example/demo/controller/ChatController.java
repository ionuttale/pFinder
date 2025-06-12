package com.example.demo.controller;

import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChatController {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/chat/{username}")
    public String chatPage(@PathVariable String username, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername());
        User chatWith = userRepository.findByUsername(username);
        List<Message> messages = messageRepository.findChatMessages(currentUser, chatWith);
        model.addAttribute("chatWith", username);
        model.addAttribute("currentUser", userDetails.getUsername());
        model.addAttribute("messages", messages);
        return "chat";
    }

    @PostMapping("/chat/{username}")
    public String sendMessage(@PathVariable String username, @AuthenticationPrincipal UserDetails userDetails, @RequestParam String content, Model model) {
        User sender = userRepository.findByUsername(userDetails.getUsername());
        User receiver = userRepository.findByUsername(username);
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
        return "redirect:/chat/" + username;
    }
}
