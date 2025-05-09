package desafio.api.controller.response;
import lombok.Data;

import java.util.List;

@Data
public class ParsedFilesResponse {

    private List<UserResponse> filesParsed;

}
