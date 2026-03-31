package com.arits.expense_trancker.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE notifications SET is_deleted = true , deleted_at = NOW() WHERE id=?")
@SQLRestriction("is_deleted = false")
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


    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;

}
