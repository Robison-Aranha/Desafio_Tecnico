package desafio.api.service;


import desafio.api.Utils.Utils;
import desafio.api.controller.response.OrderResponse;
import desafio.api.controller.response.ParsedFilesResponse;
import desafio.api.controller.response.UserResponse;
import desafio.api.domain.Order;
import desafio.api.domain.Product;
import desafio.api.domain.User;
import desafio.api.fileParser.ParserData;
import desafio.api.mapper.EntitysMapper;
import desafio.api.mapper.model.OrderMapperObj;
import desafio.api.mapper.model.ProductMapperObj;
import desafio.api.mapper.model.UserMapperObj;
import desafio.api.repository.OrderRepository;
import desafio.api.repository.ProductRepository;
import desafio.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class AddFileService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private final SearchService searchService;

    ParsedFilesResponse parsedFilesResponses = new ParsedFilesResponse();

    public ParsedFilesResponse addFile(MultipartFile file) {

        List<UserMapperObj> usersMapper = new ArrayList<>();
        List<OrderMapperObj> ordersMapper = new ArrayList<>();
        List<ProductMapperObj> productsMapper = new ArrayList<>();
        List<ParserData> parsedDataList = new ArrayList<>();

        this.extractEntitys(file, usersMapper, ordersMapper, productsMapper, parsedDataList);
        this.createLinkEntitiesAndConvertToResponse(usersMapper, ordersMapper, productsMapper, parsedDataList);
        this.filterValues(usersMapper, ordersMapper, productsMapper);

        List<User> savedUsers;
        if (!usersMapper.isEmpty()) {
            savedUsers = userRepository.saveAll(
                    usersMapper.stream()
                            .map(EntitysMapper::toEntityUserMapper)
                            .collect(Collectors.toList())
            );
        } else {
            savedUsers = new ArrayList<>();
        }

        List<Order> savedOrders;
        if (!ordersMapper.isEmpty()) {
            List<Order> ordersToSave = ordersMapper.stream()
                    .map(orderMapper -> {
                        Order order = EntitysMapper.toEntityOrderMapper(orderMapper);
                        savedUsers.stream()
                                .filter(u -> u.getUserId().equals(order.getUser().getUserId()))
                                .findFirst()
                                .ifPresent(order::setUser);
                        return order;
                    })
                    .collect(Collectors.toList());

            savedOrders = orderRepository.saveAll(ordersToSave);
        } else {
            savedOrders = new ArrayList<>();
        }

        if (!productsMapper.isEmpty()) {
            List<Product> productsToSave = productsMapper.stream()
                    .map(productMapper -> {
                        Product product = EntitysMapper.toEntityProductMapper(productMapper);
                        savedOrders.stream()
                                .filter(o -> o.getOrderId().equals(product.getOrder().getOrderId()))
                                .findFirst()
                                .ifPresent(product::setOrder);
                        return product;
                    })
                    .collect(Collectors.toList());

            productRepository.saveAll(productsToSave);
        }

        return parsedFilesResponses;
    }

    public void filterValues(List<UserMapperObj> usersMapper,
                             List<OrderMapperObj> ordersMapper,
                             List<ProductMapperObj> productsMapper) {

        usersMapper.removeIf(u -> searchService.userbyId(u.getName()).isPresent());

        ordersMapper.removeIf(o -> searchService
                .orderByIdAndUserName(o.getOrderId(), o.getUser().getName())
                .isPresent());

        productsMapper.removeIf(p -> searchService
                .productByProductIdAndValueAndUserName(
                        p.getProductId(),
                        p.getValue(),
                        p.getOrder().getOrderId(),
                        p.getOrder().getUser().getName()
                ).isPresent());
    }

    public void createLinkEntitiesAndConvertToResponse(List<UserMapperObj> usersMapper, List<OrderMapperObj> ordersMapper, List<ProductMapperObj> productsMapper, List<ParserData> parsedDataList) {
        parsedFilesResponses.setFilesParsed(new ArrayList<>());

        ordersMapper.forEach(order -> {
            List<ParserData> parserDatas = parsedDataList.stream().filter(p -> p.getOrderId().equals(order.getOrderId())).toList();
            if (!parserDatas.isEmpty()) {
                Optional<UserMapperObj> optionalUser = usersMapper.stream().filter(u -> u.getUserId().equals(parserDatas.get(0).getUserId())).findAny();
                UserResponse userResponse = null;
                OrderResponse orderResponse;
                if (optionalUser.isPresent()) {
                    UserMapperObj user = optionalUser.get();
                    order.setUser(user);
                    Optional<UserResponse> optionalUserResponse = parsedFilesResponses.getFilesParsed().stream().filter(u -> u.getUser_id().equals(user.getUserId())).findAny();
                    userResponse = optionalUserResponse.orElseGet(() -> EntitysMapper.toResponseUserMapper(user));
                    orderResponse = EntitysMapper.toResponseOrderMapper(order);
                    userResponse.getOrders().add(orderResponse);
                } else {
                    orderResponse = null;
                }
                if (userResponse != null && orderResponse != null) {
                    parserDatas.forEach(p -> {
                        Optional<ProductMapperObj> optionalProduct = productsMapper.stream().filter(u -> u.getProductId().equals(p.getProdId()) && u.getValue().equals(p.getValue())).findAny();
                        if (optionalProduct.isPresent()) {
                            ProductMapperObj product = optionalProduct.get();
                            product.setOrder(order);
                            orderResponse.getProducts().add(EntitysMapper.toResponseProductMapper(product));
                            orderResponse.setTotal(orderResponse.getTotal() + product.getValue());
                        }
                    });
                    orderResponse.setTotal(Utils.convertValue(orderResponse.getTotal()));
                    parsedFilesResponses.getFilesParsed().add(userResponse);
                }
            }
        });
    }

    public void extractEntitys(MultipartFile file, List<UserMapperObj> usersMapper, List<OrderMapperObj> ordersMapper, List<ProductMapperObj> productsMapper, List<ParserData> parsedDataList) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ParserData parserData = new ParserData(line);
                UserMapperObj user = EntitysMapper.extractUserFromParsedData(parserData);
                OrderMapperObj order = EntitysMapper.extractOrderFromParsedData(parserData);
                ProductMapperObj product = EntitysMapper.extractProductFromParsedData(parserData);
                if (!usersMapper.contains(user)) {
                    usersMapper.add(user);
                }
                if (!ordersMapper.contains(order)) {
                    ordersMapper.add(order);
                }
                if (!productsMapper.contains(product)) {
                    productsMapper.add(product);
                }
                parsedDataList.add(parserData);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
