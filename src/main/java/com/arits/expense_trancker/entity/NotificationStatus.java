package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationStatus {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "status_name")
    private String statusName;



    @OneToMany(mappedBy = "notificationStatus")
    private Set<Notifications> notifications;




}
