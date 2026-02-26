package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE permission SET is_deleted = true WHERE permission_id=?")
@SQLRestriction("is_deleted = false")
public class Permission {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long permissionId;

    @Column(name = "permission_name", unique = true)
    private String permissionName;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @ManyToMany(mappedBy = "permission",fetch = FetchType.EAGER)
    private Set<Role> role = new HashSet<>();


    @ManyToMany(mappedBy = "permissions",fetch = FetchType.EAGER)
    private Set<User> user = new HashSet<>();

    private boolean isDeleted = false;
}
