package com.example.demo.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private Long id;

    @Column(name = "age")
    private Integer age;

    @Column(name = "breed", length = 255)
    private String breed;

    @Column(name = "color", length = 255)
    private String color;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "fav_activity", length = 255)
    private String fav_activity;

    @Column(name = "fav_toy", length = 255)
    private String fav_toy;

    @Column(name = "fav_treat", length = 255)
    private String fav_treat;

    @Column(name = "gender", length = 255)
    private String gender;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "personality_traits", length = 255)
    private String personality_traits;

    @Column(name = "size", length = 255)
    private String size;

    @Column(name = "vaccination_status")
    private Boolean vaccination_status;

    @Column(name = "weight")
    private Double weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetPhoto> photos;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFav_activity() {
        return fav_activity;
    }

    public void setFav_activity(String fav_activity) {
        this.fav_activity = fav_activity;
    }

    public String getFav_toy() {
        return fav_toy;
    }

    public void setFav_toy(String fav_toy) {
        this.fav_toy = fav_toy;
    }

    public String getFav_treat() {
        return fav_treat;
    }

    public void setFav_treat(String fav_treat) {
        this.fav_treat = fav_treat;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonality_traits() {
        return personality_traits;
    }

    public void setPersonality_traits(String personality_traits) {
        this.personality_traits = personality_traits;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Boolean getVaccination_status() {
        return vaccination_status;
    }

    public void setVaccination_status(Boolean vaccination_status) {
        this.vaccination_status = vaccination_status;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<PetPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PetPhoto> photos) {
        this.photos = photos;
    }

    // Convenience methods
    public void addPhoto(PetPhoto photo) {
        if (photos == null) {
            photos = new ArrayList<>();
        }
        photos.add(photo);
        photo.setPet(this);
    }

    public void removePhoto(PetPhoto photo) {
        if (photos != null) {
            photos.remove(photo);
            photo.setPet(null);
        }
    }
}