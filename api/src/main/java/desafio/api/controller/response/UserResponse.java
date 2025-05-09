package desafio.api.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserResponse {

    private Integer user_id;

    private String name;

    List<OrderResponse> orders;
}
