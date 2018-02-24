package pl.com.bottega.exchangerate.api;

import pl.com.bottega.exchangerate.domain.commands.CalculationCommand;

import java.time.LocalDate;

public class CalculationDto {
    private String from;
    private String to;
    private Double amount;
    private Double calculatedAmount;
    private LocalDate date;

    public CalculationDto(CalculationCommand command) {
        from = command.getFrom();
        to = command.getTo();
        amount = command.getAmount();
        date = command.getDate();
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getCalculatedAmount() {
        return calculatedAmount;
    }

    public void setCalculatedAmount(Double calculatedAmount) {
        this.calculatedAmount = calculatedAmount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
