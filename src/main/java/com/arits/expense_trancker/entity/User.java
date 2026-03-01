package com.arits.expense_trancker.entity;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE user_id=?")
@SQLRestriction("is_deleted = false")
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String username;

    private String firstName;

    private String lastName;

    private String email;
    private String phone;
    private String password;
    private LocalDate dob;
    private boolean isDeleted = false;

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

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Transactions> transactions;

    @ManyToMany(fetch = FetchType.EAGER)

    @JoinTable
            (
                    name = "users_permissions",
                    joinColumns = @JoinColumn(name = "user_id"),
                    inverseJoinColumns = @JoinColumn(name = "permission_id")
            )
    @SQLRestriction("is_deleted = false")
    private Set<Permission> permissions = new HashSet<>();



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<SimpleGrantedAuthority> authorities = new java.util.HashSet<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));

        Set<SimpleGrantedAuthority> userPermission = this.permissions.stream().map(permission -> new SimpleGrantedAuthority(permission.getPermissionName())).collect(Collectors.toSet());
        authorities.addAll(userPermission);


        return authorities;
    }

}
