package pl.com.bottega.exchangerate.domain;

import pl.com.bottega.exchangerate.domain.commands.ExchangeRateCommand;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
public class Rate {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private LocalDate date;

    @Column(length = 3)
    private String currency;

    @Column
    private Double rate;

    public Rate(ExchangeRateCommand cmd) {
        this.date = cmd.getDate();
        this.currency = cmd.getCurrency();
        this.rate = cmd.getRate();
    }

    public Rate() {
    }

    public void updateValues(ExchangeRateCommand cmd) {
        this.date = cmd.getDate();
        this.currency = cmd.getCurrency();
        this.rate = cmd.getRate();
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCurrency() {
        return currency;
    }

    public Double getRate() {
        return rate;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
