package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationStatusRepo extends JpaRepository<NotificationStatus, Long> {
}
