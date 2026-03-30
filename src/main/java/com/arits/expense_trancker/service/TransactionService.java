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
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TransactionService {


    private final TransactionTypeRepo transactionTypeRepo;
    private final TransactionMethodRepo transactionMethodRepo;
    private final TransactionRepo transactionRepo;
    private final BankAccountRepo bankAccountRepo;
    private final MobileBankingRepo mobileBankingRepo;
    private final CashAccountRepo cashAccountRepo;


    private void updateBalance(User user, Long accountId, BigDecimal amount, String typeName, String methodName) {
        boolean isIncome = typeName.equalsIgnoreCase("INCOME");

        switch (methodName.toUpperCase()) {
            case "BANK" -> updateBankBalance(user, accountId, amount, isIncome);
            case "MOBILE_BANKING" -> updateMobileBalance(user, accountId, amount, isIncome);
            case "CASH" -> updateCashBalance(user, amount, isIncome);
            default -> throw new RuntimeException("Unsupported transaction method: " + methodName);
        }
    }


    private void updateBankBalance(User user, long id, BigDecimal amount, boolean isIncome) {
        BankAccount account = bankAccountRepo.findByUserAndId(user, id)
                .orElseThrow(() -> new RuntimeException("Bank account not found."));

        account.setCurrentBalance(calculateNewBalance(account.getCurrentBalance(), amount, isIncome));
        bankAccountRepo.save(account);
    }

    private void updateMobileBalance(User user, Long accountId, BigDecimal amount, boolean isIncome) {
        MobileBanking account = mobileBankingRepo.findByUserAndId(user, accountId)
                .orElseThrow(() -> new RuntimeException("Mobile banking account not found."));

        account.setCurrentBalance(calculateNewBalance(account.getCurrentBalance(), amount, isIncome));
        mobileBankingRepo.save(account);
    }

    private void updateCashBalance(User user, BigDecimal amount, boolean isIncome) {
        CashAccount account = cashAccountRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cash wallet not found."));

        account.setCurrentBalance(calculateNewBalance(account.getCurrentBalance(), amount, isIncome));
        cashAccountRepo.save(account);
    }


    private BigDecimal calculateNewBalance(BigDecimal current, BigDecimal amount, boolean isIncome) {
        if (isIncome) {
            return current.add(amount);
        } else {
            if (current.compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance for this transaction.");
            }
            return current.subtract(amount);
        }
    }


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
    public AddTransactionResponseDto addTransaction(User user, AddTransactionRequestDto addTransactionRequestDTO, MultipartFile multipartFile) {

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

        TransactionMethods method = transactionMethodRepo.findByMethodId(addTransactionRequestDTO.getTMethod()).orElseThrow(() -> new RuntimeException("Transaction method not found"));
        TransactionType type = transactionTypeRepo.findById(addTransactionRequestDTO.getTType()).orElseThrow(() -> new RuntimeException("Transaction type not found"));

        updateBalance(user, addTransactionRequestDTO.getAccountId(), addTransactionRequestDTO.getAmount(), type.getTypeName(), method.getMethodName());


        Transactions transactions = Transactions.builder()
                .amount(addTransactionRequestDTO.getAmount())
                .itemName(addTransactionRequestDTO.getItemName())
                .description(addTransactionRequestDTO.getDescription())
                .date(LocalDate.now())
                .user(user)
                .accountId(addTransactionRequestDTO.getAccountId())
                .transactionType(type)
                .transactionMethods(method)
                .invoicePath(savedFilePath)
                .build();

        transactionRepo.save(transactions);


        return AddTransactionResponseDto.builder()
                .trxId(transactions.getTransactionId())
                .itemName(transactions.getItemName())
                .amount(transactions.getAmount())
                .tMethod(transactions.getTransactionMethods().getMethodName())
                .tType(transactions.getTransactionType().getTypeName())
                .description(transactions.getDescription())
                .invoicePath(transactions.getInvoicePath())
                .accountId(transactions.getAccountId())
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
                        .accountId(t.getAccountId())
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



    public AddTransactionResponseDto modifyTransaction(User user, AddTransactionRequestDto addTransactionRequestDTO, MultipartFile multipartFile, long id) {

        Transactions transaction = transactionRepo.findByUserAndTransactionId(user, id).orElseThrow(() -> new RuntimeException("Transaction not found"));


        if (addTransactionRequestDTO.getAmount() != null) {
            String inverseTtype = transaction.getTransactionType().getTypeName().equalsIgnoreCase("EXPENSE") ? "INCOME" : "EXPENSE";
            updateBalance(user, transaction.getAccountId(), transaction.getAmount(), inverseTtype, transaction.getTransactionMethods().getMethodName());

        }

        transaction.setAmount((addTransactionRequestDTO.getAmount() != null) ? addTransactionRequestDTO.getAmount() : transaction.getAmount());

        transaction.setItemName((addTransactionRequestDTO.getItemName() != null) ? addTransactionRequestDTO.getItemName() : transaction.getItemName());

        transaction.setDescription((addTransactionRequestDTO.getDescription() != null) ? addTransactionRequestDTO.getDescription() : transaction.getDescription());

        transaction.setTransactionMethods(transactionMethodRepo.findById
                (
                        (addTransactionRequestDTO.getTMethod() != null)
                                ? addTransactionRequestDTO.getTMethod()
                                : transaction.getTransactionMethods()
                                .getTmId()).orElseThrow(() -> new RuntimeException("transaction method not found")));

        transaction.setAccountId(addTransactionRequestDTO.getAccountId());
        transaction.setTransactionType(transactionTypeRepo.findById
                (
                        (addTransactionRequestDTO.getTType() != null)
                                ? addTransactionRequestDTO.getTType()
                                : transaction.getTransactionType()
                                .getTtId()).orElseThrow(() -> new RuntimeException("transaction type not found")));


        if (multipartFile != null && !multipartFile.isEmpty()) {

            try {
                if (transaction.getInvoicePath() != null) {
                    Files.deleteIfExists(Paths.get(transaction.getInvoicePath()));
                }
                String uploadDir = "uploads/invoices/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();

                Path filePath = Paths.get(uploadDir + fileName);

                Files.write(filePath, multipartFile.getBytes());
                transaction.setInvoicePath(filePath.toString());
            } catch (IOException e) {
                throw new RuntimeException("failed to add invoice file" + e.getMessage());
            }

        }


        transactionRepo.save(transaction);


        updateBalance(user, transaction.getAccountId(), transaction.getAmount(), transaction.getTransactionType().getTypeName(), transaction.getTransactionMethods().getMethodName());


        return AddTransactionResponseDto.builder()
                .trxId(transaction.getTransactionId())
                .itemName(transaction.getItemName())
                .amount(transaction.getAmount())
                .tMethod(transaction.getTransactionMethods().getMethodName())
                .tType(transaction.getTransactionType().getTypeName())
                .accountId(transaction.getAccountId())
                .description(transaction.getDescription())
                .invoicePath(transaction.getInvoicePath())
                .build();
    }


    public DeleteTransactionResponseDTO deleteTransaction(User user, Long tId) {

        Transactions transactions = transactionRepo.findByUserAndTransactionId(user, tId).orElseThrow(() -> new RuntimeException("transaction not found"));
        DeleteTransactionResponseDTO response=  DeleteTransactionResponseDTO.builder()
                .trxId(transactions.getTransactionId())
                .description(transactions.getDescription())
                .deletedBy(user.getUserId())
                .build();
        transactionRepo.softDeleteTransactions(transactions.getTransactionId());

   return response;

    }

    public List<DeletedExpensesResponseDto> getDeletedList(User user) {


        List<Transactions> transactions = transactionRepo.findTransactionsByUserIDAndParentID(user.getUserId());

        return transactions.stream().map(p -> DeletedExpensesResponseDto.builder()
                .tId(p.getTransactionId())
                .itemName(p.getItemName())
                .amount(p.getAmount())
                .tMethod(p.getTransactionMethods().getMethodName())
                .tType(p.getTransactionType().getTypeName())
                .description(p.getDescription())
                .invoicePath(p.getInvoicePath())
                .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public ReviveTransactionResponseDto reviveTransactionFromDeath(User user, Long tId) {

        Transactions transaction = transactionRepo.findDeletedTransactionsByUserId(user.getUserId(), tId).orElseThrow(() -> new RuntimeException("transaction does not exist in deleted list"));

ReviveTransactionResponseDto revivedTransaction= ReviveTransactionResponseDto.builder()
        .trxId(transaction.getTransactionId())
        .userId(transaction.getUser().getUserId())
        .transactionMethod(transaction.getTransactionMethods().getMethodName())
        .transactionType(transaction.getTransactionType().getTypeName())
        .description(transaction.getDescription())
        .build();

        transaction.setDeleted(false);
        transaction.setDeletedAt(null);
        transactionRepo.save(transaction);
        return revivedTransaction;
    }
}
