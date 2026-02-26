package com.financeapp.service;

import com.financeapp.model.Transaction;
import com.financeapp.model.TransactionType;
import com.financeapp.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Testes unitarios das regras de transacao
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createValidTransaction() {
        Transaction transaction = buildTransaction(new BigDecimal("50.00"));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction created = transactionService.create(transaction);

        assertEquals(transaction, created);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void createInvalidAmount() {
        Transaction transaction = buildTransaction(BigDecimal.ZERO);

        assertThrows(ResponseStatusException.class, () -> transactionService.create(transaction));
    }

    @Test
    void updateNotFound() {
        when(transactionRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> transactionService.update(10L, buildTransaction(new BigDecimal("10.00"))));
    }

    @Test
    void deleteNotFound() {
        when(transactionRepository.existsById(7L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> transactionService.delete(7L));
    }

    private Transaction buildTransaction(BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.INCOME);
        transaction.setAmount(amount);
        transaction.setCategory("Salary");
        transaction.setDate(LocalDate.now());
        transaction.setDescription("Test");
        return transaction;
    }
}
