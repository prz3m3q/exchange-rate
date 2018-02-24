package pl.com.bottega.exchangerate.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.com.bottega.exchangerate.domain.NoRateException;
import pl.com.bottega.exchangerate.domain.Rate;
import pl.com.bottega.exchangerate.domain.commands.CalculationCommand;
import pl.com.bottega.exchangerate.domain.commands.InvalidCommandException;
import pl.com.bottega.exchangerate.domain.commands.Validatable;
import pl.com.bottega.exchangerate.domain.repositories.RateRepository;

import javax.persistence.NoResultException;
import java.time.LocalDate;

@Component
public class CalculationHandler implements Handler<CalculationCommand, CalculationDto> {

    @Value("${exchange.mainCurrency}")
    private String mainCurrency;

    private RateRepository rateRepository;

    public CalculationHandler(RateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    @Transactional
    public CalculationDto handle(CalculationCommand command) {
        CalculationDto calculationDto = new CalculationDto(command);
        if (command.getFrom().equals(mainCurrency)) {
            Rate rate = rateRepository.get(command.getDate(), command.getTo());
            calculationDto.setCalculatedAmount(Rate.round(command.getAmount() / rate.getRate(), 2));
            return calculationDto;
        }
        if (command.getTo().equals(mainCurrency)) {
            Rate rate = rateRepository.get(command.getDate(), command.getFrom());
            calculationDto.setCalculatedAmount(Rate.round(command.getAmount() * rate.getRate(), 2));
            return calculationDto;
        }
        Rate rateFrom = rateRepository.get(command.getDate(), command.getFrom());
        Rate rateTo = rateRepository.get(command.getDate(), command.getTo());
        calculationDto.setCalculatedAmount(Rate.round((command.getAmount() * rateFrom.getRate()) / rateTo.getRate(), 2));
        return calculationDto;
    }

    @Override
    public Class<? extends Validatable> getSupportedCommandClass() {
        return CalculationCommand.class;
    }
}
