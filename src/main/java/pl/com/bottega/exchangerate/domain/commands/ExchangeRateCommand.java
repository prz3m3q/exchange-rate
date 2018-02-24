package pl.com.bottega.exchangerate.domain.commands;

import java.time.LocalDate;

public class ExchangeRateCommand implements Validatable {

    private LocalDate date;
    private String currency;
    private Double rate;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    @Override
    public void validate(ValidationErrors errors) {
        if (isEmpty(date)) {
            errors.add("date", "is required");
        }

        if (isEmpty(currency)) {
            errors.add("currency", "is required");
        }

        if (isEmpty(rate)) {
            errors.add("rate", "is required");
        }

        if (!isEmpty(rate) && rate <= 0) {
            errors.add("rate", "must be > than 0.0");
        }

        if (!isEmpty(currency) && !currency.matches("[a-zA-Z]{3}")) {
            errors.add("currency", "has invalid format");
        }
    }
}
