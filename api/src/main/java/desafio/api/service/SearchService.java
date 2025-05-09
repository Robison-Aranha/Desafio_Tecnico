package desafio.api.service;

import desafio.api.domain.Order;
import desafio.api.domain.Product;
import desafio.api.domain.User;
import desafio.api.repository.OrderRepository;
import desafio.api.repository.ProductRepository;
import desafio.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class SearchService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public Optional<User> userbyId(String name) {
        return userRepository.findById(name);
    }

    public Optional<Order> orderByIdAndUserName(Integer id, String username) {
        return orderRepository.findByOrderIdAndUserName(id, username);
    }

    public Page<Order> findFilteredOrders(Integer orderId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return orderRepository.findFilteredOrders(orderId, startDate, endDate, pageable);
    }

    public Optional<Product> productByProductIdAndValueAndUserName(Integer id, Double value, Integer orderId, String userName) {
        return productRepository.findByProductIdAndValueAndOrderOrderIdAndOrderUserName(id, value, orderId, userName);
    }

}
