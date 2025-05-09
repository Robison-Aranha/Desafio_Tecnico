package desafio.api.mapper.model;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductMapperObj {


    private Integer productId;

    private Double value;

    private OrderMapperObj order;

}
