package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("is_deleted=false")
public class NotificationStatus {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "status_name")
    private String statusName;

    @Builder.Default
    private boolean isDeleted = false;

    private LocalDateTime deletedAt;



    @OneToMany(mappedBy = "notificationStatus")
    private Set<Notifications> notifications;




}
