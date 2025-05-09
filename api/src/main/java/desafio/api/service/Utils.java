package desafio.api.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.text.DecimalFormat;
import java.text.ParseException;

public class Utils {

    public static Double convertValue(Double value) {
        try {
            DecimalFormat df = new DecimalFormat("#.##");
            String formatado = df.format(value);
            return df.parse(formatado).doubleValue();
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

}
