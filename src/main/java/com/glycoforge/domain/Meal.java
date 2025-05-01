package com.glycoforge.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "meals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT") // For potentially long descriptions from LLM
    private String description;

    @Column
    private Double gramsCarbs;

    @Column
    private Double gramsProtein;

    @Column
    private Double gramsFat;

    @Column(nullable = false)
    private LocalDateTime eatenAt;
}

