package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roleId;
    @Column(name = "role_name")
    private String roleName;

    @OneToMany(mappedBy = "role")
    private Set<User>user;

    @ManyToMany
    @Builder.Default
    @JoinTable
            (
                    name = "roles_default_permission",
                    joinColumns = @JoinColumn(name = "role_id"),
                    inverseJoinColumns = @JoinColumn(name = "permission_id")
            )
    private Set<Permission>permission= new HashSet<>();




    public Role (long roleId , String roleName){
        this.roleId=roleId;
        this.roleName=roleName;
    }

}
