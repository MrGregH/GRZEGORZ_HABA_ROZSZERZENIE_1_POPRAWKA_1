
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;
import static org.mockito.Mockito.*;

public class CurrencyServiceTest {
    private final String cacheDurationMillis = "100";
    private final ApiService apiService = mock(ApiService.class);
    private final CurrencyService currencyService = new CurrencyService(cacheDurationMillis, apiService);

    @Test
    public void exchangeWithoutCallingExternalTestSecondTimeBecauseOfCacheExistance() {
        //GIVEN
        String currencyFrom = "USD";
        String currencyTo = "GBP";
        double amount = 10.0;
        double rate = 0.5;
        Double expectedResult = amount * rate;
        doReturn(rate).when(apiService).getRateFromExternal(currencyFrom, currencyTo);

        //WHEN
        currencyService.exchange(currencyFrom, currencyTo, amount);
        var result = currencyService.exchange(currencyFrom, currencyTo, amount);

        //THEN
        verify(apiService, times(1)).getRateFromExternal(anyString(), anyString());
        Assertions.assertEquals(result, expectedResult, "exchange result should be equal as expected");
    }

    @Test
    public void exchangeWithCallingExternalTestSecondTimeBecauseOfCacheExpiration() throws InterruptedException {
        //GIVEN
        String currencyFrom = "USD";
        String currencyTo = "GBP";
        double amount = 10.0;
        double rate = 0.6;
        doReturn(rate).when(apiService).getRateFromExternal(currencyFrom, currencyTo);
        Double expectedResult = amount * rate;

        //WHEN
        currencyService.exchange(currencyFrom, currencyTo, amount);
        sleep(200);
        var result = currencyService.exchange(currencyFrom, currencyTo, amount);

        //THEN
        verify(apiService, times(2)).getRateFromExternal(anyString(), anyString());
        Assertions.assertEquals(result, expectedResult, "exchange result should be equal as expected");
    }
}
