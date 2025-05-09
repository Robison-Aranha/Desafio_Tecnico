package desafio.api.service;


import desafio.api.controller.response.OrderResponse;
import desafio.api.domain.Order;
import desafio.api.mapper.EntitysMapper;
import org.springframework.data.domain.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@RequiredArgsConstructor
@Service
public class FilterItensService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final SearchService searchService;

    public Page<OrderResponse> filter(Integer orderId, Integer startDate, Integer endDate, Pageable pageable) {
        Page<Order> orders;
        LocalDate starDateConverted = startDate != null ? LocalDate.parse(String.valueOf(startDate), formatter) : LocalDate.of(1900, 1, 1);;
        LocalDate endDateConverted = endDate != null ? LocalDate.parse(String.valueOf(endDate), formatter) : LocalDate.of(3000, 1, 1);;
        orders = searchService.findFilteredOrders(orderId, starDateConverted, endDateConverted, pageable);
        Page<OrderResponse> pageResponse = orders.map(EntitysMapper::toResponseOrder);
        return pageResponse;
    }

}
