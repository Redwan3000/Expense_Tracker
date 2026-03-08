package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {


    private final TransactionTypeRepo transactionTypeRepo;
    private final TransactionMethodRepo transactionMethodRepo;
    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;
    private final AccountRepo accountRepo;


    public TransactionMethods tmSeedding(String name) {

        return transactionMethodRepo.findByMethodName(name).orElseGet(() -> {
                    return transactionMethodRepo.save(TransactionMethods.builder()
                            .methodName(name)
                            .build());
                }
        );


    }

    public TransactionType ttSeeding(String name) {


        return transactionTypeRepo.findByTypeName(name).orElseGet(() -> {
                    return transactionTypeRepo.save(TransactionType.builder()
                            .typeName(name)
                            .build());
                }
        );

    }


    @Transactional
    public AddTransactionResponseDTO addTransaction(User user, AddTransactionRequestDTO addTransactionRequestDTO, MultipartFile multipartFile) {

        String savedFilePath = null;
        if (multipartFile != null && !multipartFile.isEmpty()) {

            try {

                String uploadDir = "uploads/invoices/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();

                Path filePath = Paths.get(uploadDir + fileName);

                Files.write(filePath, multipartFile.getBytes());

                savedFilePath = filePath.toString();

            } catch (IOException e) {
                throw new RuntimeException("failed to store the invoices" + e.getMessage());
            }
        }
        Transactions transactions = Transactions.builder()
                .amount(addTransactionRequestDTO.getAmount())
                .itemName(addTransactionRequestDTO.getItemName())
                .description(addTransactionRequestDTO.getDescription())
                .date(LocalDate.now())
                .user(user)
                .transactionType(transactionTypeRepo.findById(addTransactionRequestDTO.getTType()).orElseThrow(() -> new RuntimeException("transaction Type not found")))
                .transactionMethods(transactionMethodRepo.findByMethodId(addTransactionRequestDTO.getTMethod()).orElseThrow(() -> new RuntimeException("method not found")))
                .invoicePath(savedFilePath)
                .build();

        transactionRepo.save(transactions);


        Account currentAccount = accountRepo.findByUser(user).orElseThrow(() -> new RuntimeException("users account does not exist"));

        BigDecimal totalIncome = currentAccount.getTotalIncome() != null ? currentAccount.getTotalIncome() : BigDecimal.ZERO;
        BigDecimal totalExpense = currentAccount.getTotalExpense() != null ? currentAccount.getTotalExpense() : BigDecimal.ZERO;
        BigDecimal transactionAmount = transactions.getAmount();


        if (transactions.getTransactionType().getTypeName().contains("EXPENSE")) {
            totalExpense = totalExpense.add(transactionAmount);
            currentAccount.setTotalExpense(totalExpense);
        }

        if (transactions.getTransactionType().getTypeName().contains("INCOME")) {
            totalIncome = totalIncome.add(transactionAmount);
            currentAccount.setTotalIncome(totalIncome);
        }

        currentAccount.setCurrentBalance(totalIncome.subtract(totalExpense));
        currentAccount.setUpdatedAt(LocalDateTime.now());
        accountRepo.save(currentAccount);


        return AddTransactionResponseDTO.builder()
                .itemName(transactions.getItemName())
                .amount(transactions.getAmount())
                .tMethod(transactions.getTransactionMethods().getMethodName())
                .tType(transactions.getTransactionType().getTypeName())
                .description(transactions.getDescription())
                .invoicePath(transactions.getInvoicePath())
                .build();
    }

    public GetTransactionHistoryAllDto showExpenses(User user, GetTransactionHistoryRequestDto request) {

        long mainId = (user.getParent() != null) ? user.getParent().getUserId() : user.getUserId();

        List<Transactions> allTransactions = transactionRepo.findTransactionsByUserIDAndParentID(mainId);


        List<GetTransactionHistoryDto> expenseHistory = allTransactions.stream().filter(t -> request == null ||
                        ((request.getUsername() == null || t.getUser().getUsername().equalsIgnoreCase(request.getUsername()))

                                && (request.getItemName() == null || t.getItemName().equalsIgnoreCase(request.getItemName()))
                                && (request.getSingleDate() == null || t.getDate().equals(request.getSingleDate()))
                                && (request.getToDate() == null || !t.getDate().isAfter(request.getToDate()))
                                && (request.getFromDate() == null || !t.getDate().isBefore(request.getFromDate()))

                        )).map(t -> GetTransactionHistoryDto.builder()
                        .transactionId(t.getTransactionId())
                        .username(t.getUser().getUsername())
                        .amount(t.getAmount())
                        .transactionMethod(t.getTransactionMethods().getMethodName())
                        .transactionType(t.getTransactionType().getTypeName())
                        .itemName(t.getItemName())
                        .description(t.getDescription())
                        .userId(t.getUser().getUserId())
                        .hasInvoice(t.getInvoicePath() != null && !t.getInvoicePath().isEmpty())
                        .build())

                .collect(Collectors.toList());

        List<GetTransactionHistoryDto> owner = expenseHistory.stream()
                .filter(e -> e.getUserId().equals(mainId)).collect(Collectors.toList());
        List<GetTransactionHistoryDto> subOwner = expenseHistory.stream()
                .filter(e -> !e.getUserId().equals(mainId)).collect(Collectors.toList());


        return GetTransactionHistoryAllDto.builder().owners(owner).subOwners(subOwner).build();


    }

    public AddTransactionResponseDTO modifyTransaction(User user, AddTransactionRequestDTO addTransactionRequestDTO, MultipartFile multipartFile, long id) {


        List<Transactions> transactionsList = transactionRepo.findTransactionsByUserIDAndParentID(user.getUserId());
        Transactions transactions = transactionsList.stream().filter(t -> t.getTransactionId().equals(id)).findFirst().orElseThrow(() -> new RuntimeException("transaction not found"));

        transactions.setAmount((addTransactionRequestDTO.getAmount() != null) ? addTransactionRequestDTO.getAmount() : transactions.getAmount());

        transactions.setItemName((addTransactionRequestDTO.getItemName() != null) ? addTransactionRequestDTO.getItemName() : transactions.getItemName());

        transactions.setDescription((addTransactionRequestDTO.getDescription() != null) ? addTransactionRequestDTO.getDescription() : transactions.getDescription());

        transactions.setTransactionMethods(transactionMethodRepo.findById
                (
                        (addTransactionRequestDTO.getTMethod() != null)
                                ? addTransactionRequestDTO.getTMethod()
                                : transactions.getTransactionMethods()
                                .getTmId()).orElseThrow(() -> new RuntimeException("transaction method not found")));

        transactions.setTransactionType(transactionTypeRepo.findById
                (
                        (addTransactionRequestDTO.getTType() != null)
                                ? addTransactionRequestDTO.getTType()
                                : transactions.getTransactionType()
                                .getTtId()).orElseThrow(() -> new RuntimeException("transaction type not found")));

        if (multipartFile != null && !multipartFile.isEmpty()) {

            try {
                if (transactions.getInvoicePath() != null) {
                    Files.deleteIfExists(Paths.get(transactions.getInvoicePath()));
                }
                String uploadDir = "uploads/invoices/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();

                Path filePath = Paths.get(uploadDir + fileName);

                Files.write(filePath, multipartFile.getBytes());
                transactions.setInvoicePath(filePath.toString());
            } catch (IOException e) {
                throw new RuntimeException("failed to add invoice file" + e.getMessage());
            }

        }


        Account currentAccount = accountRepo.findByUser(user).orElseThrow(() -> new RuntimeException("users account does not exist"));

        BigDecimal totalIncome = currentAccount.getTotalIncome() != null ? currentAccount.getTotalIncome() : BigDecimal.ZERO;
        BigDecimal totalExpense = currentAccount.getTotalExpense() != null ? currentAccount.getTotalExpense() : BigDecimal.ZERO;
        BigDecimal transactionAmount = transactions.getAmount();


        if (transactions.getTransactionType().getTypeName().contains("EXPENSE")) {
            totalExpense = totalExpense.add(transactionAmount);
            currentAccount.setTotalExpense(totalExpense);
        }

        if (transactions.getTransactionType().getTypeName().contains("INCOME")) {
            totalIncome = totalIncome.add(transactionAmount);
            currentAccount.setTotalIncome(totalIncome);
        }

        currentAccount.setCurrentBalance(totalIncome.subtract(totalExpense));
        currentAccount.setUpdatedAt(LocalDateTime.now());
        accountRepo.save(currentAccount);


        transactionRepo.save(transactions);
        return AddTransactionResponseDTO.builder()
                .itemName(transactions.getItemName())
                .amount(transactions.getAmount())
                .tMethod(transactions.getTransactionMethods().getMethodName())
                .tType(transactions.getTransactionType().getTypeName())
                .description(transactions.getDescription())
                .invoicePath(transactions.getInvoicePath())
                .build();
    }

    public String deleteTransaction(User user, Long tId) {


        Account currentAccount = accountRepo.findByUser(user).orElseThrow(()-> new RuntimeException("user not found"));

        List<Transactions> transactionsList = transactionRepo.findTransactionsByUserIDAndParentID(user.getUserId());

        Transactions transactions= transactionsList.stream().filter(p->p.getTransactionId().equals(tId)).findFirst().orElseThrow(()->new RuntimeException("transaction not found"));

        BigDecimal totalIncome = currentAccount.getTotalIncome() != null ? currentAccount.getTotalIncome() : BigDecimal.ZERO;
        BigDecimal totalExpense = currentAccount.getTotalExpense() != null ? currentAccount.getTotalExpense() : BigDecimal.ZERO;
        BigDecimal transactionAmount = transactions.getAmount();

        if(transactions.getTransactionType().getTypeName().contains("EXPENSE")){
            totalExpense= totalExpense.subtract(transactions.getAmount());
            currentAccount.setTotalExpense(totalExpense);
        }
        if(transactions.getTransactionType().getTypeName().contains("INCOME")){
            totalIncome=totalIncome.subtract(transactions.getAmount());
            currentAccount.setTotalExpense(totalIncome);
        }

        currentAccount.setCurrentBalance(totalIncome.subtract(totalExpense));
        currentAccount.setUpdatedAt(LocalDateTime.now());

        transactionRepo.delete(transactions);

        return " transaction deleted successfully";













    }
}
