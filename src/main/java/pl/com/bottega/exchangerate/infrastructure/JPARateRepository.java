package pl.com.bottega.exchangerate.infrastructure;

import org.springframework.stereotype.Component;
import pl.com.bottega.exchangerate.domain.NoRateException;
import pl.com.bottega.exchangerate.domain.Rate;
import pl.com.bottega.exchangerate.domain.repositories.RateRepository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.time.LocalDate;

@Component
public class JPARateRepository implements RateRepository {

    private EntityManager entityManager;

    public JPARateRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Rate get(LocalDate date, String currency) {
        Rate rate = null;
        try {
            rate = (Rate) entityManager.createQuery("SELECT r FROM Rate r WHERE r.date = :date AND r.currency = :currency")
                .setParameter("date", date)
                .setParameter("currency", currency)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new NoRateException("no exchange rate defined");
        }
        return rate;
    }

    @Override
    public void save(Rate rate) {
        entityManager.persist(rate);
    }

    @Override
    public void update(Rate rate) {
        entityManager.merge(rate);
    }
}
