package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gender {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gender_id")
    private  Long genderid;

    @Column(nullable = false,unique = true)
    private String name;


    @OneToMany(mappedBy = "gender")
    private Set<User> user;







}
