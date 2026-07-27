package com.adebisi.coursecompass.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Enter a course title.")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters.")
    @Column(nullable = false, length = 100)
    private String title;

    @NotBlank(message = "Enter the instructor's name.")
    @Size(min = 2, max = 80, message = "Instructor name must be between 2 and 80 characters.")
    @Column(nullable = false, length = 80)
    private String instructor;

    @NotBlank(message = "Tell learners what this course covers.")
    @Size(min = 20, max = 1000, message = "Description must be between 20 and 1,000 characters.")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "Choose a category.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Category category;

    @NotNull(message = "Choose a difficulty level.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @NotNull(message = "Enter the course duration.")
    @Min(value = 1, message = "Duration must be at least 1 hour.")
    @Max(value = 500, message = "Duration cannot exceed 500 hours.")
    @Column(nullable = false)
    private Integer durationHours;

    @NotNull(message = "Enter a price (use 0 for a free course).")
    @DecimalMin(value = "0.00", message = "Price cannot be negative.")
    @DecimalMax(value = "1000000.00", message = "Price cannot exceed ₦1,000,000.")
    @Digits(integer = 7, fraction = 2, message = "Use a valid price with no more than two decimal places.")
    @Column(nullable = false, precision = 9, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public Integer getDurationHours() { return durationHours; }
    public void setDurationHours(Integer durationHours) { this.durationHours = durationHours; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
