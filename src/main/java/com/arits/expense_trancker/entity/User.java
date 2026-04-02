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


    @ManyToOne
    @JoinColumn(name = "gender_id")
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @Builder.Default
    private User parent=null;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    private Set<Transactions> transactions;

    @OneToMany(mappedBy = "user")
    private Set<PaymentMethod> paymentMethods;

    //    @Builder.Default
//    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<UsersPermissions> usersPermissions = new HashSet<>();
//
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsersPermissions> usersPermissions = new HashSet<>();


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
