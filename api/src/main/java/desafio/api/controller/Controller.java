package desafio.api.controller;

import desafio.api.controller.response.OrderResponse;
import desafio.api.controller.response.ParsedFilesResponse;
import desafio.api.service.AddFileService;
import desafio.api.service.FilterItensService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class Controller {

    private final AddFileService addFileService;

    private final FilterItensService filterItensService;

    @PostMapping()
    public ParsedFilesResponse addFile(@RequestParam("file") MultipartFile file) {
        return addFileService.addFile(file);
    }

    @GetMapping("/order")
    public Page<OrderResponse> filter(@RequestParam(required = false) Integer order_id, @RequestParam(required = false) Integer startDate, @RequestParam(required = false) Integer endDate, Pageable pageable) {
        return filterItensService.filter(order_id, startDate, endDate, pageable);
    }

}
