package desafio.api.mapper;

import desafio.api.controller.response.OrderResponse;
import desafio.api.controller.response.ProductResponse;
import desafio.api.controller.response.UserResponse;
import desafio.api.domain.Order;
import desafio.api.domain.Product;
import desafio.api.domain.User;
import desafio.api.fileParser.ParserData;
import desafio.api.mapper.model.OrderMapperObj;
import desafio.api.mapper.model.ProductMapperObj;
import desafio.api.mapper.model.UserMapperObj;
import desafio.api.service.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntitysMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");


    // parsedData -> mapper

    public static UserMapperObj extractUserFromParsedData(ParserData parserData) {
        return UserMapperObj.builder()
                .userId(parserData.getUserId())
                .name(parserData.getUserName())
                .orders(new ArrayList<>())
                .build();
    }

    public static OrderMapperObj extractOrderFromParsedData(ParserData parserData) {
        return OrderMapperObj.builder()
                .orderId(parserData.getOrderId())
                .date(LocalDate.parse(String.valueOf(parserData.getDate()), formatter))
                .products(new ArrayList<>())
                .build();
    }

    public static ProductMapperObj extractProductFromParsedData(ParserData parserData) {
        return ProductMapperObj.builder()
                .productId(parserData.getProdId())
                .value(parserData.getValue())
                .build();
    }

    // entity -> response

    public static OrderResponse toResponseOrder(Order order) {
        double total = order.getProducts().stream()
                .map(Product::getValue)
                .filter(Objects::nonNull)
                .reduce(0.0, Double::sum);

        return OrderResponse.builder()
                .order_id(order.getOrderId())
                .date(order.getDate().toString())
                .products(!order.getProducts().isEmpty() ? order.getProducts().stream().map(EntitysMapper::toResponseProduct).collect(Collectors.toList()) : new ArrayList<>())
                .total(Utils.convertValue(total))
                .build();
    }

    public static ProductResponse toResponseProduct(Product product) {
        return ProductResponse.builder()
                .product_id(product.getProductId())
                .value(product.getValue())
                .build();
    }


    // mapper -> response

    public static UserResponse toResponseUserMapper(UserMapperObj user) {
        return UserResponse.builder()
                .user_id(user.getUserId())
                .name(user.getName())
                .orders(!user.getOrders().isEmpty() ? user.getOrders().stream().map(EntitysMapper::toResponseOrderMapper).collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }

    public static OrderResponse toResponseOrderMapper(OrderMapperObj order) {
        double total = order.getProducts().stream()
                .map(ProductMapperObj::getValue)
                .filter(Objects::nonNull)
                .reduce(0.0, Double::sum);

        return OrderResponse.builder()
                .order_id(order.getOrderId())
                .date(order.getDate().toString())
                .products(!order.getProducts().isEmpty() ? order.getProducts().stream().map(EntitysMapper::toResponseProductMapper).collect(Collectors.toList()) : new ArrayList<>())
                .total(Utils.convertValue(total))
                .build();
    }

    public static ProductResponse toResponseProductMapper(ProductMapperObj product) {
        return ProductResponse.builder()
                .product_id(product.getProductId())
                .value(product.getValue())
                .build();
    }

    // mapper -> entity

    public static User toEntityUserMapper(UserMapperObj userMapperObj) {
        return User.builder()
                .userId(userMapperObj.getUserId())
                .name(userMapperObj.getName())
                .build();

    }

    public static Order toEntityOrderMapper(OrderMapperObj orderMapperObj) {
        return Order.builder()
                .orderId(orderMapperObj.getOrderId())
                .user(EntitysMapper.toEntityUserMapper(orderMapperObj.getUser()))
                .date(orderMapperObj.getDate())
                .build();
    }

    public static Product toEntityProductMapper(ProductMapperObj productMapperObj) {
        return Product.builder()
                .productId(productMapperObj.getProductId())
                .value(productMapperObj.getValue())
                .order(EntitysMapper.toEntityOrderMapper(productMapperObj.getOrder()))
                .build();
    }
}
