package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@SQLDelete(sql = "UPDATE gender SET is_deleted = true WHERE gender_id=?")
//@SQLRestriction("is_deleted = false")
public class Gender {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gender_id")
    private  Long genderid;

    @Column(nullable = false,unique = true)
    private String name;
//
//    private boolean isDeleted = false;

    @OneToMany(mappedBy = "gender")
    private Set<User> user;







}
