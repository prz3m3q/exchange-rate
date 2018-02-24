package pl.com.bottega.exchangerate.domain.commands;

import java.time.LocalDate;

public class CalculationCommand implements Validatable {

    private LocalDate date;
    private String from;
    private String to;
    private Double amount;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    @Override
    public void validate(ValidationErrors errors) {
        if (isEmpty(date)) {
            errors.add("date", "is required");
        }

        if (from != null && to != null && from == to) {
            errors.add("from", "must be different than to");
            errors.add("to", "must be different than from");
        }

        if (isEmpty(from)) {
            errors.add("from", "is required");
        }

        if (isEmpty(to)) {
            errors.add("to", "is required");
        }

        if (isEmpty(amount)) {
            errors.add("amount", "is required");
        }
    }
}
