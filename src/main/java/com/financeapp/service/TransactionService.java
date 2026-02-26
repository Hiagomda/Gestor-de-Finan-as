package com.financeapp.service;

import com.financeapp.model.Transaction;
import com.financeapp.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

// Regras de negocio das transacoes
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction create(Transaction transaction) {
        validate(transaction);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> list() {
        return transactionRepository.findAll();
    }

    public Transaction update(Long id, Transaction transaction) {
        Transaction existing = transactionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        existing.setType(transaction.getType());
        existing.setAmount(transaction.getAmount());
        existing.setCategory(transaction.getCategory());
        existing.setDate(transaction.getDate());
        existing.setDescription(transaction.getDescription());

        validate(existing);
        return transactionRepository.save(existing);
    }

    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
        }
        transactionRepository.deleteById(id);
    }

    private void validate(Transaction transaction) {
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than zero");
        }
        if (transaction.getDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date is required");
        }
        if (transaction.getCategory() == null || transaction.getCategory().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category is required");
        }
        if (transaction.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Type is required");
        }
    }
}
