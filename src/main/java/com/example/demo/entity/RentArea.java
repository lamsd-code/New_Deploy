package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "rentarea")
public class RentArea extends Base {
    @ManyToOne
    @JoinColumn(name = "buildingid")
    private Building building;

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    @Column(name = "value", nullable = false)
    private Integer value;
}