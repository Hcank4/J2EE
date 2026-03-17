package com.HongCan.course_registration.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String image;

    @Column(nullable = false)
    private Integer credits;

    @Column(nullable = false)
    private String lecturer;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}