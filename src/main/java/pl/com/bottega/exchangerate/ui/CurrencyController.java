package pl.com.bottega.exchangerate.ui;

import org.springframework.web.bind.annotation.*;
import pl.com.bottega.exchangerate.api.CalculationDto;
import pl.com.bottega.exchangerate.api.CommandGateway;
import pl.com.bottega.exchangerate.domain.commands.CalculationCommand;
import pl.com.bottega.exchangerate.domain.commands.ExchangeRateCommand;

import java.time.LocalDate;

@RestController
public class CurrencyController {

    private CommandGateway commandGateway;

    public CurrencyController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PutMapping("/exchange-rate")
    public void exchangeRate(@RequestBody ExchangeRateCommand cmd) {
        commandGateway.execute(cmd);
    }

    @GetMapping("/calculation")
    public CalculationDto calculation(
        @RequestParam(required = false) LocalDate date,
        @RequestParam(required = false) String from,
        @RequestParam(required = false) String to,
        @RequestParam(required = false) Double amount
    ) {
        CalculationCommand calculationCommand = new CalculationCommand();
        calculationCommand.setDate(date);
        calculationCommand.setFrom(from);
        calculationCommand.setTo(to);
        calculationCommand.setAmount(amount);
        return commandGateway.execute(calculationCommand);
    }

}
