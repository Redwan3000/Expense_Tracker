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
    private final PaymentMethodRepo paymentMethodRepo;
    private final TransactionRepo transactionRepo;
    private final BankAccountRepo bankAccountRepo;
    private final MobileBankingRepo mobileBankingRepo;
    private final CashWalletRepo cashWalletRepo;


    private void updateBalance(User user, Long accountId, BigDecimal amount, String typeName, String methodName) {
        boolean isIncome = typeName.equalsIgnoreCase("INCOME");

        switch (methodName.toUpperCase()) {
            case "BANK" -> bankAccountRepo.updateBankBalance(user.getId(), accountId, amount, isIncome);
            case "MOBILE_BANKING" -> mobileBankingRepo.updateMobileBalance(user.getId(), accountId, amount, isIncome);
            case "CASH" -> cashWalletRepo.updateCashBalance(user.getId(), amount, isIncome);
            default -> throw new RuntimeException("Unsupported transaction method: " + methodName);
        }

    }


    public TransactionMethod tmSeedding(String name) {

        return paymentMethodRepo.findByMethodName(name).orElseGet(() -> {
                    return paymentMethodRepo.save(TransactionMethod.builder()
                            .name(name)
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
    public AddTransactionResponseDto addTransaction(User user, AddTransactionRequestDto dto, MultipartFile multipartFile) {


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

        TransactionMethod method = paymentMethodRepo.findById(dto.getPaymentMethod()).orElseThrow(() -> new RuntimeException("Transaction method not found"));
        TransactionType type = transactionTypeRepo.findById(dto.getTransactionType()).orElseThrow(() -> new RuntimeException("Transaction type not found"));

        updateBalance(user, dto.getAccountId(), dto.getAmount(), type.getTypeName(), method.getName());

        Transactions transactions = Transactions.builder()
                .amount(dto.getAmount())
                .itemName(dto.getItemName())
                .description(dto.getDescription())
                .date(LocalDate.now())
                .user(user)
                .accountId(dto.getAccountId())
                .transactionType(type)
                .transactionMethod(method)
                .invoicePath(savedFilePath)
                .build();

        transactionRepo.save(transactions);


        return AddTransactionResponseDto.builder()
                .trxId(transactions.getId())
                .itemName(transactions.getItemName())
                .amount(transactions.getAmount())
                .tMethod(transactions.getTransactionMethod().getName())
                .tType(transactions.getTransactionType().getTypeName())
                .description(transactions.getDescription())
                .invoicePath(transactions.getInvoicePath())
                .accountId(transactions.getAccountId())
                .build();


    }



    public GetTransactionHistoryAllDto showExpenses(User user, GetTransactionHistoryRequestDto request) {

        long mainId = (user.getParent() != null) ? user.getParent().getId() : user.getId();

        List<Transactions> allTransactionsOfUser = transactionRepo.findTransactionsOfOwnerAndSubowner(
                mainId,
                request.getItemName(),
                request.getUsername(),
                request.getToDate(),
                request.getFromDate(),
                request.getSingleDate());

        List<GetTransactionHistoryDto> owner = allTransactionsOfUser.stream().filter(t -> t.getUser().getId() == mainId)
                .map(t -> GetTransactionHistoryDto.builder()
                        .transactionId(t.getId())
                        .userId(t.getUser().getId())
                        .username(t.getUser().getUsername())
                        .amount(t.getAmount())
                        .transactionMethod(t.getTransactionMethod().getName())
                        .transactionType(t.getTransactionType().getTypeName())
                        .accountId(t.getAccountId())
                        .itemName(t.getItemName())
                        .description(t.getDescription())
                        .hasInvoice(t.getInvoicePath() != null ? true : false)
                        .build()
                ).toList();

        List<GetTransactionHistoryDto> subowner = allTransactionsOfUser.stream().filter(t -> t.getUser().getId() != mainId)
                .map(t -> GetTransactionHistoryDto.builder()
                        .transactionId(t.getId())
                        .userId(t.getUser().getId())
                        .username(t.getUser().getUsername())
                        .amount(t.getAmount())
                        .transactionMethod(t.getTransactionMethod().getName())
                        .transactionType(t.getTransactionType().getTypeName())
                        .accountId(t.getAccountId())
                        .itemName(t.getItemName())
                        .description(t.getDescription())
                        .hasInvoice(t.getInvoicePath() != null ? true : false)
                        .build()
                ).toList();

        return GetTransactionHistoryAllDto.builder().owners(owner).subOwners(subowner).build();

    }





    public AddTransactionResponseDto modifyTransaction(User user, AddTransactionRequestDto addTransactionRequestDTO, MultipartFile multipartFile, long id) {

        Transactions transaction = transactionRepo.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("Transaction not found"));


        if (addTransactionRequestDTO.getAmount() != null) {
            String inverseTtype = transaction.getTransactionType().getTypeName().equalsIgnoreCase("EXPENSE") ? "INCOME" : "EXPENSE";
            updateBalance(user, transaction.getAccountId(), transaction.getAmount(), inverseTtype, transaction.getTransactionMethod().getName());

        }

        transaction.setAmount((addTransactionRequestDTO.getAmount() != null) ? addTransactionRequestDTO.getAmount() : transaction.getAmount());

        transaction.setItemName((addTransactionRequestDTO.getItemName() != null) ? addTransactionRequestDTO.getItemName() : transaction.getItemName());

        transaction.setDescription((addTransactionRequestDTO.getDescription() != null) ? addTransactionRequestDTO.getDescription() : transaction.getDescription());

        transaction.setTransactionMethod(paymentMethodRepo.findById
                (
                        (addTransactionRequestDTO.getPaymentMethod() != null)
                                ? addTransactionRequestDTO.getPaymentMethod()
                                : transaction.getTransactionMethod()
                                  .getId()).orElseThrow(() -> new RuntimeException("transaction method not found")));

        transaction.setAccountId(addTransactionRequestDTO.getAccountId());
        transaction.setTransactionType(transactionTypeRepo.findById
                (
                        (addTransactionRequestDTO.getTransactionType() != null)
                                ? addTransactionRequestDTO.getTransactionType()
                                : transaction.getTransactionType()
                                  .getId()).orElseThrow(() -> new RuntimeException("transaction type not found")));


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


        updateBalance(user, transaction.getAccountId(), transaction.getAmount(), transaction.getTransactionType().getTypeName(), transaction.getTransactionMethod().getName());


        return AddTransactionResponseDto.builder()
                .trxId(transaction.getId())
                .itemName(transaction.getItemName())
                .amount(transaction.getAmount())
                .tMethod(transaction.getTransactionMethod().getName())
                .tType(transaction.getTransactionType().getTypeName())
                .accountId(transaction.getAccountId())
                .description(transaction.getDescription())
                .invoicePath(transaction.getInvoicePath())
                .build();
    }


    public DeleteTransactionResponseDTO deleteTransaction(User user, Long tId) {

        Transactions transactions = transactionRepo.findByUserAndId(user, tId).orElseThrow(() -> new RuntimeException("transaction not found"));
        DeleteTransactionResponseDTO response = DeleteTransactionResponseDTO.builder()
                .trxId(transactions.getId())
                .description(transactions.getDescription())
                .deletedBy(user.getId())
                .build();
        transactionRepo.softDeleteTransactions(transactions.getId());

        return response;

    }

    public List<DeletedExpensesResponseDto> getDeletedList(User user) {


        List<Transactions> transactions = transactionRepo.findTransactionsOfOwnerAndSubowner(user.getId(), null, null, null, null, null);

        return transactions.stream().map(p -> DeletedExpensesResponseDto.builder()
                .tId(p.getId())
                .itemName(p.getItemName())
                .amount(p.getAmount())
                .tMethod(p.getTransactionMethod().getName())
                .tType(p.getTransactionType().getTypeName())
                .description(p.getDescription())
                .invoicePath(p.getInvoicePath())
                .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public ReviveTransactionResponseDto reviveTransactionFromDeath(User user, Long tId) {

        Transactions transaction = transactionRepo.findDeletedTransactionsByUserId(user.getId(), tId).orElseThrow(() -> new RuntimeException("transaction does not exist in deleted list"));

        ReviveTransactionResponseDto revivedTransaction = ReviveTransactionResponseDto.builder()
                .trxId(transaction.getId())
                .userId(transaction.getUser().getId())
                .transactionMethod(transaction.getTransactionMethod().getName())
                .transactionType(transaction.getTransactionType().getTypeName())
                .description(transaction.getDescription())
                .build();

        transaction.setDeleted(false);
        transaction.setDeletedAt(null);
        transactionRepo.save(transaction);
        return revivedTransaction;
    }
}
