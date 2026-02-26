package com.arits.expense_trancker.entity;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Column(unique = true, nullable = false)
    private String username;

    private String first_name;

    private String last_name;

    private String email;
    private String phone;
    private String password;
    private LocalDate dob;


    @ManyToOne
    @JoinColumn(name = "gender_id")
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @Nullable
    private User parent;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    private Set<Transactions> transactions;

    @ManyToMany

    @JoinTable
            (
                    name = "users_permissions",
                    joinColumns = @JoinColumn(name = "user_id"),
                    inverseJoinColumns = @JoinColumn(name = "permission_id")
            )
    private Set<Permission> permissions;

    {
        permissions = new HashSet<>();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<SimpleGrantedAuthority> authorities = new java.util.HashSet<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));

        Set<SimpleGrantedAuthority> userPermission = this.permissions.stream().map(permission -> new SimpleGrantedAuthority(permission.getPermissionName())).collect(Collectors.toSet());
        authorities.addAll(userPermission);


        return authorities;
    }

}
