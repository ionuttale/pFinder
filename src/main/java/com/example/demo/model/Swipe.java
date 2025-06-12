package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "swipes")
public class Swipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "swipe_id")
    private Long id;

    @Column(name = "swipe_direction", columnDefinition = "ENUM('LIKE','DISLIKE')")
    private String swipeDirection;

    @Column(name = "timestamp", columnDefinition = "DATETIME(6)")
    private java.time.LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "matched", nullable = false)
    private boolean matched = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSwipeDirection() { return swipeDirection; }
    public void setSwipeDirection(String swipeDirection) { this.swipeDirection = swipeDirection; }
    public java.time.LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(java.time.LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public boolean isMatched() { return matched; }
    public void setMatched(boolean matched) { this.matched = matched; }
}