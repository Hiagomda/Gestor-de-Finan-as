package com.financeapp.service;

import com.financeapp.dto.ReportResponse;
import com.financeapp.model.Transaction;
import com.financeapp.model.TransactionType;
import com.financeapp.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Testes unitarios do calculo de relatorio
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void getMonthlyReportCalculatesBalanceAndTotals() {
        LocalDate date = LocalDate.of(2025, 1, 10);

        Transaction income = new Transaction();
        income.setType(TransactionType.INCOME);
        income.setAmount(new BigDecimal("100.00"));
        income.setCategory("Salary");
        income.setDate(date);

        Transaction expense = new Transaction();
        expense.setType(TransactionType.EXPENSE);
        expense.setAmount(new BigDecimal("40.00"));
        expense.setCategory("Food");
        expense.setDate(date);

        when(transactionRepository.findByDateBetween(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(List.of(income, expense));

        ReportResponse response = reportService.getMonthlyReport(2025, 1);

        assertEquals(0, response.getMonthlyBalance().compareTo(new BigDecimal("60.00")));
        assertEquals(0, response.getTotalsByCategory().get("Salary").compareTo(new BigDecimal("100.00")));
        assertEquals(0, response.getTotalsByCategory().get("Food").compareTo(new BigDecimal("-40.00")));
    }
}
