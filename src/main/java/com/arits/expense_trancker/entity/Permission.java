package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long permissionId;

    @Column(name = "permission_name", unique = true)
    private String permissionName;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @ManyToMany(mappedBy = "permission")
    private Set<Role> role = new HashSet<>();


    @ManyToMany(mappedBy = "permissions")
    private Set<User> user = new HashSet<>();


}
