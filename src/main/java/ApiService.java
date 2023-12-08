import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiService {
    private final static String URL_PATH = "https://api.apilayer.com/currency_data/live?";
    private final static String METHOD = "GET";
    private final static String PARAM_BASE = "base=";
    private final static String PARAM_CURRENCIES = "&currencies=";
    private final static String APIKEY_HEADER = "apikey";
    private final static String APIKEY_TOKEN = "6KqV3dFZz4erfZDzDY7dEX49Kyi3aLjh";

    public Double getRateFromExternal(String currencyFrom, String currencyTo) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_PATH + PARAM_BASE + currencyTo + PARAM_CURRENCIES + currencyFrom))
                .header(APIKEY_HEADER, APIKEY_TOKEN)
                .method(METHOD, HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            System.out.println(request);
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return (Double) new JSONObject(response.body()).getJSONObject("quotes").get(currencyTo + currencyFrom);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }
}
