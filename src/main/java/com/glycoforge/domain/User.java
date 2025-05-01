package com.glycoforge.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "users") // "user" is often a reserved keyword in SQL
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column
    private Integer age;

    @Column
    private Integer heightCm;

    @Column
    private Double weightKg;

    @Column
    private Double insulinRatio; // Units of insulin per 10g of carbs (common practice)

    @Column
    private Double sensitivityFactor; // How much 1 unit of insulin lowers blood glucose (mg/dL or mmol/L)

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meal> meals;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Injection> injections;

    // Note: Security details (roles, etc.) will be handled by Spring Security
}

