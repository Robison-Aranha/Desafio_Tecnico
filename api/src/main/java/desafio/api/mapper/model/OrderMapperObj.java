package desafio.api.mapper.model;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderMapperObj {


    private Integer orderId;

    private LocalDate date;

    private UserMapperObj user;

    private List<ProductMapperObj> products = new ArrayList<>();
}
