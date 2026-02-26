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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE role SET is_deleted = true WHERE role_id=?")
@SQLRestriction("is_deleted = false")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roleId;
    @Column(name = "role_name")
    private String roleName;

    @OneToMany(mappedBy = "role" , fetch = FetchType.EAGER)
    private Set<User>user;

    @ManyToMany(fetch = FetchType.EAGER)
    @Builder.Default
    @JoinTable
            (
                    name = "roles_default_permission",
                    joinColumns = @JoinColumn(name = "role_id"),
                    inverseJoinColumns = @JoinColumn(name = "permission_id")
            )
    private Set<Permission>permission= new HashSet<>();

    private boolean isDeleted = false;


    public Role (long roleId , String roleName){
        this.roleId=roleId;
        this.roleName=roleName;
    }

}
