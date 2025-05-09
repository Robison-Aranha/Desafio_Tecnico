package desafio.api.mapper.model;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserMapperObj {

    private Integer userId;

    private String name;


    private List<OrderMapperObj> orders = new ArrayList<>();

}
