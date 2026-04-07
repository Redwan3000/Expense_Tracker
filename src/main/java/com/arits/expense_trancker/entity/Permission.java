package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE permission SET is_deleted = true ,deleted_at=NOW() WHERE id=?")
@SQLRestriction("is_deleted = false")
public class Permission {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String description;

    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;



    @Builder.Default
    @OneToMany(mappedBy = "permission")
    private Set<RolesDefaultPermissions> rolesDefaultPermissions = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "permission")
    private Set<UsersPermissions> usersPermissions = new HashSet<>();




}
