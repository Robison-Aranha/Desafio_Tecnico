package desafio.api.controller.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Integer order_id;

    private Double total;

    private String date;

    private List<ProductResponse> products;

}
