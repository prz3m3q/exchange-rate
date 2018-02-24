package pl.com.bottega.exchangerate.domain.repositories;

import pl.com.bottega.exchangerate.domain.Rate;

import java.time.LocalDate;

public interface RateRepository {
    Rate get(LocalDate date, String currency);
    void save(Rate rate);
    void update(Rate rate);
}
