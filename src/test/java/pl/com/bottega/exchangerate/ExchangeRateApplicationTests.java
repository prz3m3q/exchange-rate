package pl.com.bottega.exchangerate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ExchangeRateApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DbCleaner dbCleaner;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void cleanDb() {
        dbCleaner.clean();
    }

    @Test
    public void exchangeFromMainCurrency() throws Exception {
        saveExchangeRate("2017-01-01", "USD", 3.5);

        mvc.perform(get("/calculation").
                param("date", "2017-01-01").
                param("from", "PLN").
                param("to", "USD").
                param("amount", "100").
                contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.date").value("2017-01-01")).
                andExpect(jsonPath("$.from").value("PLN")).
                andExpect(jsonPath("$.to").value("USD")).
                andExpect(jsonPath("$.amount").value(100.0)).
                andExpect(jsonPath("$.calculatedAmount").value(28.57))

        ;
    }

    @Test
    public void exchangeRateOverriding() throws Exception {
        saveExchangeRate("2017-01-01", "USD", 3.5);
        saveExchangeRate("2017-01-01", "USD", 4.0);

        mvc.perform(get("/calculation").
                param("date", "2017-01-01").
                param("from", "PLN").
                param("to", "USD").
                param("amount", "100").
                contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.calculatedAmount").value(25.0))

        ;
    }

    @Test
    public void exchangeMainCurrency() throws Exception {
        saveExchangeRate("2017-01-01", "USD", 4.0);

        mvc.perform(get("/calculation").
                param("date", "2017-01-01").
                param("from", "USD").
                param("to", "PLN").
                param("amount", "100").
                contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.calculatedAmount").value(400.0))

        ;
    }

    @Test
    public void exchangeNotMainCurrencies() throws Exception {
        saveExchangeRate("2017-01-01", "USD", 4.0);
        saveExchangeRate("2017-01-01", "GBP", 5.0);

        mvc.perform(get("/calculation").
                param("date", "2017-01-01").
                param("from", "USD").
                param("to", "GBP").
                param("amount", "100").
                contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.calculatedAmount").value(80.0))

        ;
    }

    @Test
    public void missingExchangeRate() throws Exception {
        saveExchangeRate("2017-01-02", "USD", 4.0);
        saveExchangeRate("2017-01-03", "GBP", 5.0);

        mvc.perform(get("/calculation").
                param("date", "2017-02-01").
                param("from", "USD").
                param("to", "GBP").
                param("amount", "100").
                contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnprocessableEntity()).
                andExpect(jsonPath("$.error").value("no exchange rate defined"));

        ;
    }

    @Test
    public void sameCurrencyToConvert() throws Exception {
        saveExchangeRate("2017-01-02", "USD", 4.0);

        mvc.perform(get("/calculation").
                param("date", "2017-02-02").
                param("from", "USD").
                param("to", "USD").
                param("amount", "100").
                contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnprocessableEntity()).
                andExpect(jsonPath("$.errors.from").value("must be different than to")).
                andExpect(jsonPath("$.errors.to").value("must be different than from"));
        ;
    }

    @Test
    public void validationErrorsWhenDefiningRates() throws Exception {
        ExchangeRate rate = new ExchangeRate(null, null, null);
        mvc.perform(
                put("/exchange-rate").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(rate))
        ).andExpect(status().isUnprocessableEntity()).
                andExpect(jsonPath("$.errors.date").value("is required")).
                andExpect(jsonPath("$.errors.currency").value("is required")).
                andExpect(jsonPath("$.errors.rate").value("is required"))
        ;
    }

    @Test
    public void invalidCurrencyAndRateWhenDefiningRates() throws Exception {
        ExchangeRate rate = new ExchangeRate(null, "invalid", new BigDecimal(-1.0));
        mvc.perform(
                put("/exchange-rate").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(rate))
        ).andExpect(status().isUnprocessableEntity()).
                andExpect(jsonPath("$.errors.currency").value("has invalid format")).
                andExpect(jsonPath("$.errors.rate").value("must be > than 0.0"))
        ;
    }

    @Test
    public void validationErrorsWhenExchanging() throws Exception {
        mvc.perform(get("/calculation").
                contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnprocessableEntity()).
                andExpect(jsonPath("$.errors.date").value("is required")).
                andExpect(jsonPath("$.errors.amount").value("is required")).
                andExpect(jsonPath("$.errors.from").value("is required")).
                andExpect(jsonPath("$.errors.to").value("is required"))
        ;
    }

    private void saveExchangeRate(String date, String currency, double amount) throws Exception {
        ExchangeRate rate = new ExchangeRate(date, currency, new BigDecimal(amount));
        mvc.perform(
                put("/exchange-rate").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(rate))
        ).andExpect(status().isOk());
    }

    class ExchangeRate {

        private String date;
        private String currency;
        private BigDecimal rate;

        public ExchangeRate(String date, String currency, BigDecimal rate) {
            this.date = date;
            this.currency = currency;
            this.rate = rate;
        }

        public String getDate() {
            return date;
        }

        public String getCurrency() {
            return currency;
        }

        public BigDecimal getRate() {
            return rate;
        }

    }

}
