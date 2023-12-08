import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CurrencyService {

    private final long cacheDurationMillis;
    private final ApiService apiService;
    private final Map<String, CacheRate> exchangeRates = new HashMap<>();
    private final Map<String, Lock> locks = new HashMap<>();

    public CurrencyService(String cacheDurationMillis, ApiService apiService) {
        this.cacheDurationMillis = Long.parseLong(cacheDurationMillis);
        this.apiService = apiService;
    }

    public double exchange(String fromCurrency, String toCurrency, double amount) {
        double exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        return amount * exchangeRate;
    }

    public double getExchangeRate(String fromCurrency, String toCurrency) {
        var name = fromCurrency + toCurrency;
        Lock lock = getLockForKey(name);
        lock.lock();
        try {
            var cacheRate = exchangeRates.get(name);
            if (cacheRate == null || System.currentTimeMillis() - cacheRate.time > cacheDurationMillis) {
                exchangeRates.put(name, new CacheRate(apiService.getRateFromExternal(fromCurrency, toCurrency)));
                cacheRate = exchangeRates.get(name);
            }
            return cacheRate.rate;
        } finally {
            lock.unlock();
        }
    }

    private Lock getLockForKey(String key) {
        locks.putIfAbsent(key, new ReentrantLock());
        return locks.get(key);
    }

    @Getter
    @Setter
    private static class CacheRate {
        private final Double rate;
        private final long time;

        public CacheRate(Double rate) {
            this.rate = rate;
            this.time = System.currentTimeMillis();
        }
    }
}
