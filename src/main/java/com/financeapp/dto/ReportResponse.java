package com.financeapp.dto;

import java.math.BigDecimal;
import java.util.Map;

// Resumo do relatorio mensal
public class ReportResponse {
    private BigDecimal monthlyBalance;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private Map<String, BigDecimal> totalsByCategory;

    public ReportResponse() {
    }

    public ReportResponse(
        BigDecimal monthlyBalance,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        Map<String, BigDecimal> totalsByCategory
    ) {
        this.monthlyBalance = monthlyBalance;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.totalsByCategory = totalsByCategory;
    }

    public BigDecimal getMonthlyBalance() {
        return monthlyBalance;
    }

    public void setMonthlyBalance(BigDecimal monthlyBalance) {
        this.monthlyBalance = monthlyBalance;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public Map<String, BigDecimal> getTotalsByCategory() {
        return totalsByCategory;
    }

    public void setTotalsByCategory(Map<String, BigDecimal> totalsByCategory) {
        this.totalsByCategory = totalsByCategory;
    }
}
