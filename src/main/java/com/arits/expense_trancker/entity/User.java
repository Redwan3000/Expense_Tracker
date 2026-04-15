package com.arits.expense_trancker.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET is_deleted = true,deleted_at=NOW() WHERE id=?")
@SQLRestriction("is_deleted = false")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private LocalDate dob;

    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "parent_id")
    @Builder.Default
    private User parent = null;

    @ManyToOne( cascade = {CascadeType.PERSIST , CascadeType.MERGE})
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(cascade = {CascadeType.PERSIST , CascadeType.MERGE})
    @JoinColumn(name = "gender_id")
    private Gender gender;


    @OneToMany(mappedBy = "parent", cascade = {CascadeType.PERSIST , CascadeType.MERGE})
    private Set<User>subUsers;



    @OneToMany(mappedBy = "user",cascade = {CascadeType.PERSIST , CascadeType.MERGE})
    private Set<Account> accounts;

    @Builder.Default
    @OneToMany(mappedBy = "user",cascade = {CascadeType.PERSIST , CascadeType.MERGE})
    private Set<UsersPermissions> usersPermissions = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST , CascadeType.MERGE})
    private Set<Notifications> notifications;




    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<SimpleGrantedAuthority> authorities = new java.util.HashSet<>();
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }

        Set<SimpleGrantedAuthority> userPermissionAuth = this.usersPermissions.stream()
                .map(usersPermissions -> new SimpleGrantedAuthority(usersPermissions.getPermission().getName()))
                .collect(Collectors.toSet());

        authorities.addAll(userPermissionAuth);


        return authorities;
    }

}
