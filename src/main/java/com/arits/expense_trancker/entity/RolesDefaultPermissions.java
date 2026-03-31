package com.arits.expense_trancker.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.processing.SQL;

import java.time.LocalDateTime;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "default_roles_permission")
@SQLDelete(sql="update default_roles_permission set is_deleted=true ,deleted_at=NOW() where id=?")
@SQLRestriction("is_deleted=false")
public class RolesDefaultPermissions {


@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

@ManyToOne
@JoinColumn(name = "role_id")
private Role role;

@ManyToOne
@JoinColumn(name = "permission_id")
private Permission permission;

    @Builder.Default
    private boolean isDeleted = false;

    private LocalDateTime deletedAt;
}
