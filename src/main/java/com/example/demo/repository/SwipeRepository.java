package com.example.demo.repository;

import com.example.demo.model.Swipe;
import com.example.demo.model.User;
import com.example.demo.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SwipeRepository extends JpaRepository<Swipe, Long> {
    List<Swipe> findByUser(User user);
    List<Swipe> findByPet(Pet pet);

    @Query("SELECT s FROM Swipe s WHERE s.user = :user AND s.matched = true")
    List<Swipe> findMatchesByUser(@Param("user") User user);
}
