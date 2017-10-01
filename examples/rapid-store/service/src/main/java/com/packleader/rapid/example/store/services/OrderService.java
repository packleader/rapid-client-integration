package com.packleader.rapid.example.store.services;

import com.packleader.rapid.example.store.domain.Order;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface OrderService extends BaseService<Order> {

    List<Order> findAll(Order.Status status);

    List<Order> findByCustomerId(@NonNull UUID customerId, Order.Status status);

}
