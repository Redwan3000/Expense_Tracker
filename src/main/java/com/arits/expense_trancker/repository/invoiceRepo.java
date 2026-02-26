package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface invoiceRepo extends JpaRepository<Invoice,Long> {
}
