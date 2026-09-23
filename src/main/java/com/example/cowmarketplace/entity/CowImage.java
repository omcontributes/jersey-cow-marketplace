package com.example.cowmarketplace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "cow_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CowImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cow_id", nullable = false)
    private Cow cow;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String fileName;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
