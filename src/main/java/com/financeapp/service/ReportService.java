package com.financeapp.service;

import com.financeapp.dto.ReportResponse;
import com.financeapp.model.Transaction;
import com.financeapp.model.TransactionType;
import com.financeapp.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Calcula dados do relatorio mensal
@Service
public class ReportService {
    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public ReportResponse getMonthlyReport(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> transactions = transactionRepository.findByDateBetween(start, end);

        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        Map<String, BigDecimal> totalsByCategory = new LinkedHashMap<>();

        for (Transaction transaction : transactions) {
            boolean isExpense = transaction.getType() == TransactionType.EXPENSE;
            BigDecimal signedAmount = isExpense
                ? transaction.getAmount().negate()
                : transaction.getAmount();

            if (isExpense) {
                totalExpense = totalExpense.add(transaction.getAmount());
            } else {
                totalIncome = totalIncome.add(transaction.getAmount());
            }

            balance = balance.add(signedAmount);
            totalsByCategory.merge(transaction.getCategory(), signedAmount, BigDecimal::add);
        }

        return new ReportResponse(balance, totalIncome, totalExpense, totalsByCategory);
    }
}
