package com.packleader.rapid.example.store.services;

import com.packleader.rapid.example.store.domain.Order;
import com.packleader.rapid.example.store.exceptions.BadRequestException;
import com.packleader.rapid.example.store.exceptions.ResourceNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderServiceImpl extends BaseServiceImpl<Order> implements OrderService {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;

    @Override
    public Order create(@NonNull Order entity) {
        validateOrder(entity);
        return super.create(entity);
    }

    @Override
    public Order update(@NonNull Order entity) {
        validateOrder(entity);
        return super.update(entity);
    }

    @Override
    public List<Order> findAll(Order.Status status) {
        return repository.values().stream()
                .filter(order -> matchesStatus(order, status))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByCustomerId(@NonNull UUID customerId, Order.Status status) {
        customerService.find(customerId);
        return repository.values().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .filter(order -> matchesStatus(order, status))
                .collect(Collectors.toList());
    }

    private void validateOrder(@NonNull Order entity) {
        throwExceptionIfCustomerNotFound(entity);
        throwExceptionIfProductNotFound(entity);
    }

    private void throwExceptionIfCustomerNotFound(Order entity) {
        throwExceptionIfProductNotFound(customerService, entity.getCustomerId(), "Customer");
    }

    private void throwExceptionIfProductNotFound(Order entity) {
        entity.getProductIds().stream().forEach(
                pId -> throwExceptionIfProductNotFound(productService, pId, "Product")
        );
    }

    private void throwExceptionIfProductNotFound(BaseService<?> service, UUID id, String entityName) {
        try {
            service.find(id);
        } catch (ResourceNotFoundException ex) {
            throw new BadRequestException(entityName + " " + id + " does not exist");
        }

    }

    private boolean matchesStatus(Order order, Order.Status status) {
        return (status == null) || (order.getStatus().equals(status));
    }
}
