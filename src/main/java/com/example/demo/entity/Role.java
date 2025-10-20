package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "role")
public class Role extends Base {
    private static final long serialVersionUID = -6525302831793188081L;

    @Column(name="name")
    private String name;

    @Column(name="code")
    private String code;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();
}
