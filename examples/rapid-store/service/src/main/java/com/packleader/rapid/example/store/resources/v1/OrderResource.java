package com.packleader.rapid.example.store.resources.v1;

import com.packleader.rapid.example.store.domain.Order;
import com.packleader.rapid.example.store.services.OrderService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@RequestMapping("v1/orders")
@Api(value = "v1/orders",
     description = "Orders in the RAPID Store",
     tags = {"OrdersResource"})
@RestController
public class OrderResource {

    @Autowired
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.GET, value = "{orderId}", produces = "application/json")
    @ApiOperation(value = "Fetches an order by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found the order"),
            @ApiResponse(code = 404, message = "Order was not found")
    })
    public Order findOrder(
            @ApiParam(name = "orderId", required = true, value = "order Id") @PathVariable("orderId") UUID orderId) {
        return orderService.find(orderId);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "Fetches all orders")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found the orders")
    })
    public List<Order> findAllOrders(
            @ApiParam(name = "status", required = false, value = "status") @RequestParam(required = false) Order.Status status) {
        return orderService.findAll(status);
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "Creates a new order in the store")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Order was successfully created")
    })
    public Order createOrder(
            @ApiParam(name = "Order", required = true, value = "Order to place in the store") @RequestBody Order order,
            HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CREATED);
        return orderService.create(order);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    @ApiOperation(value = "Updates an existing order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Order was successfully updated"),
            @ApiResponse(code = 404, message = "Order was not found")
    })
    public Order updateOrder(
            @ApiParam(name = "Order", required = true, value = "Order to update in the store") @RequestBody Order order) {
        return orderService.update(order);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{orderId}", produces = "application/json")
    @ApiOperation(value = "Deletes an order from the store")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Order was successfully deleted"),
            @ApiResponse(code = 404, message = "Order was not found")
    })
    public void deleteOrder(
            @ApiParam(name = "orderId", required = true, value = "order Id") @PathVariable("orderId") UUID orderId,
            HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        orderService.delete(orderId);
    }
}
