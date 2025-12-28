package com.academix.schedule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rooms")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_code", nullable = false, unique = true)
    private String roomCode;

    @Column(nullable = false)
    private String name;

    private String building;
    private Integer floor;
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    @Column(name = "has_projector")
    private Boolean hasProjector = false;

    @Column(name = "has_computer")
    private Boolean hasComputer = false;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    // Getters/Setters explicites pour éviter les problèmes Lombok
    public Boolean getHasProjector() {
        return hasProjector;
    }

    public void setHasProjector(Boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    public Boolean getHasComputer() {
        return hasComputer;
    }

    public void setHasComputer(Boolean hasComputer) {
        this.hasComputer = hasComputer;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}