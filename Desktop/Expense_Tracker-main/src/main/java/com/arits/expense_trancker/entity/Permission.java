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
@SQLDelete(sql = "UPDATE permission SET is_deleted = true ,deleted_at=NOW() WHERE permission_id=?")
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
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolesDefaultPermissions> defaultPermissions = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsersPermissions> usersPermissions = new HashSet<>();


    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;

}
