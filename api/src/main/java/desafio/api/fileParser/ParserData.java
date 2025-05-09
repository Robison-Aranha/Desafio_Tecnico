package desafio.api.fileParser;

import lombok.Data;

@Data
public class ParserData {
    private Integer userId;
    private String userName;
    private Integer orderId;
    private Integer prodId;
    private double value;
    private Integer date;

    public ParserData(String linha) {
        this.userId = Integer.parseInt(linha.substring(0, 10).trim());
        this.userName = linha.substring(10, 55).trim();
        this.orderId = Integer.parseInt(linha.substring(55, 65).trim());
        this.prodId = Integer.parseInt(linha.substring(65, 75).trim());
        this.value = Double.parseDouble(linha.substring(75, 87).trim());
        this.date = Integer.parseInt(linha.substring(87, 95));
    }
}
