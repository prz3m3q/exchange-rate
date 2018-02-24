package pl.com.bottega.exchangerate.api;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.com.bottega.exchangerate.domain.NoRateException;
import pl.com.bottega.exchangerate.domain.Rate;
import pl.com.bottega.exchangerate.domain.commands.ExchangeRateCommand;
import pl.com.bottega.exchangerate.domain.commands.Validatable;
import pl.com.bottega.exchangerate.domain.repositories.RateRepository;

@Component
public class ExchangeRateHandler implements Handler<ExchangeRateCommand, Void> {

    private RateRepository rateRepository;

    public ExchangeRateHandler(RateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    @Transactional
    public Void handle(ExchangeRateCommand command) {
        Rate rate = null;
        try {
            rate = rateRepository.get(command.getDate(), command.getCurrency());
            rate.updateValues(command);
            rateRepository.update(rate);
        } catch (NoRateException e) {
            rate = new Rate(command);
            rateRepository.save(rate);
        }
        return null;
    }

    @Override
    public Class<? extends Validatable> getSupportedCommandClass() {
        return ExchangeRateCommand.class;
    }
}
