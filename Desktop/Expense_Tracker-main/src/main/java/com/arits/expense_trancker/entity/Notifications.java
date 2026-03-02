package com.arits.expense_trancker.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notifId;


    @Column(name = "massages", nullable = false)
    private String message;


    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private NotificationStatus notificationStatus;


}
