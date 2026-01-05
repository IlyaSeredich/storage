package com.cloud.cloudstorage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(schema = "storage", name = "roles")
@Entity
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
}
