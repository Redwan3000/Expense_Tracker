package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.handler.Resolver;
import com.arits.expense_trancker.handler.Verifier;
import com.arits.expense_trancker.repository.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionTypeRepo transactionTypeRepo;
    private final PaymentMethodRepo paymentMethodRepo;
    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;
    private final Resolver resolver;
private final Verifier verifier;



    public AddTransactionResponseDto addTransaction(User user, long userId, long subuserId, AddTransactionRequestDto dto, MultipartFile invoiceFile) {

    Long targetUserId=resolver.getTargetUserId(user, userId,subuserId);

    TransactionType tType= transactionTypeRepo.findById(dto.getTransactionType()).orElseThrow(() -> new RuntimeException("invalid Account Type"));
    Account account=accountRepo.findByUserIdAndId(targetUserId,dto.getAccountId()).orElseThrow(()->new RuntimeException("invalid Account"));


        String savedInvoicePath = null;
        if (invoiceFile != null && !invoiceFile.isEmpty()) {

            try {
                String uploadDir = "uploads/invoices/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = System.currentTimeMillis() + "_" + invoiceFile.getOriginalFilename();

                Path filePath = Paths.get(uploadDir + fileName);

                Files.write(filePath, invoiceFile.getBytes());

                savedInvoicePath = filePath.toString();

            } catch (IOException e) {
                throw new RuntimeException("failed to store the invoices" + e.getMessage());
            }
        }

        Transactions addTrnx= Transactions.builder()
              .amount(dto.getAmount())
              .itemName(dto.getItemName())
              .description(dto.getDescription())
              .date(LocalDate.now())
              .invoicePath(savedInvoicePath)
              .account(account)
              .transactionType(tType)
              .build();

return AddTransactionResponseDto.builder().
        itemName(addTrnx.getItemName()).
        amount(addTrnx.getAmount()).
        tMethod(addTrnx.getTransactionType().toString()).
        tType(addTrnx.getTransactionType().toString()).
        accountId(addTrnx.getAccount().getId()).
        description(addTrnx.getDescription()).
        invoicePath(addTrnx.getInvoicePath()).
        build();

    }










    public GetTransactionHistoryAllDto getTransactions(User user, Long userId, Long subUserId, Long tId ,TrnxHistoryRequestDto dto) {

        Long targetUserID= resolver.getTargetUserId(user,userId,subUserId);
        Long trnxId = verifier.checkTransactionExistance(tId);

        List<Transactions> allTransactionsOfUser =new ArrayList<>();


        if(trnxId!= null && tId != 0) {

            transactionRepo.findById(trnxId).ifPresent(allTransactionsOfUser::add);

        }else {
            allTransactionsOfUser = transactionRepo.findTransactionsOfOwnerAndSubowner(
                    targetUserID,
                    dto.getItemName(),
                    dto.getUsername(),
                    dto.getToDate(),
                    dto.getFromDate(),
                    dto.getSingleDate());
        }



        List<GetTransactionHistoryDto> owner = allTransactionsOfUser.stream().filter(t -> t.getAccount().getUser().getId() == targetUserID)
                .map(t -> GetTransactionHistoryDto.builder()
                        .transactionId(t.getId())
                        .userId(t.getAccount().getUser().getId())
                        .username(t.getAccount().getUser().getUsername())
                        .amount(t.getAmount())
                        .paymentMethod(t.getAccount().getPaymentMethod().getName())
                        .transactionType(t.getTransactionType().getTypeName())
                        .accountId(t.getAccount().getId())
                        .itemName(t.getItemName())
                        .description(t.getDescription())
                        .hasInvoice(t.getInvoicePath() != null ? true : false)
                        .build()
                ).toList();

        List<GetTransactionHistoryDto> subowner = allTransactionsOfUser.stream().filter(t -> t.getAccount().getUser().getId() != targetUserID)
                .map(t -> GetTransactionHistoryDto.builder()
                        .transactionId(t.getId())
                        .userId(t.getAccount().getUser().getId())
                        .username(t.getAccount().getUser().getUsername())
                        .amount(t.getAmount())
                        .paymentMethod(t.getAccount().getPaymentMethod().getName())
                        .transactionType(t.getTransactionType().getTypeName())
                        .accountId(t.getAccount().getId())
                        .itemName(t.getItemName())
                        .description(t.getDescription())
                        .hasInvoice(t.getInvoicePath() != null ? true : false)
                        .build()
                ).toList();

        return GetTransactionHistoryAllDto.builder().owners(owner).subOwners(subowner).build();

    }





    public AddTransactionResponseDto modifyTransaction(User user, AddTransactionRequestDto addTransactionRequestDTO, MultipartFile multipartFile, long id) {

        Transactions transaction = transactionRepo.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("Transaction not found"));
Account accountOfTransaction= accountRepo.findById(transaction.getAccount().getId()).orElseThrow(()->new RuntimeException("invalid Account"));

        if (addTransactionRequestDTO.getAmount() != null) {
            String inverseTtype = transaction.getTransactionType().getTypeName().equalsIgnoreCase("EXPENSE") ? "INCOME" : "EXPENSE";

        }

        transaction.setAmount((addTransactionRequestDTO.getAmount() != null) ? addTransactionRequestDTO.getAmount() : transaction.getAmount());

        transaction.setItemName((addTransactionRequestDTO.getItemName() != null) ? addTransactionRequestDTO.getItemName() : transaction.getItemName());

        transaction.setDescription((addTransactionRequestDTO.getDescription() != null) ? addTransactionRequestDTO.getDescription() : transaction.getDescription());

        accountOfTransaction.setPaymentMethod(null);


        transaction.setAccount(null);
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



        return AddTransactionResponseDto.builder()
                .trxId(transaction.getId())
                .itemName(transaction.getItemName())
                .amount(transaction.getAmount())
                .tMethod(transaction.getAccount().getPaymentMethod().getName())
                .tType(transaction.getTransactionType().getTypeName())
                .accountId(transaction.getAccount().getId())
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
                .tMethod(p.getAccount().getPaymentMethod().getName())
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
                .userId(transaction.getAccount().getUser().getId())
                .transactionMethod(transaction.getAccount().getPaymentMethod().getName())
                .transactionType(transaction.getTransactionType().getTypeName())
                .description(transaction.getDescription())
                .build();

        transaction.setDeleted(false);
        transaction.setDeletedAt(null);
        transactionRepo.save(transaction);
        return revivedTransaction;
    }





    //Seeders Service
    public PaymentMethod pMethodSeeding(String name) {

        return paymentMethodRepo.findByMethodName(name).orElseGet(() -> {
                    return paymentMethodRepo.save(PaymentMethod.builder()
                            .name(name)
                            .build());
                }
        );


    }

    public TransactionType tTypeSeeding(String name) {


        return transactionTypeRepo.findByTypeName(name).orElseGet(() -> {
                    return transactionTypeRepo.save(TransactionType.builder()
                            .typeName(name)
                            .build());
                }
        );

    }



}
