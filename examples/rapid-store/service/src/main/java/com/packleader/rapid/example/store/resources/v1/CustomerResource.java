package com.packleader.rapid.example.store.resources.v1;

import com.packleader.rapid.example.store.domain.Customer;
import com.packleader.rapid.example.store.domain.Order;
import com.packleader.rapid.example.store.services.CustomerService;
import com.packleader.rapid.example.store.services.OrderService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@RequestMapping("v1/customers")
@Api(value = "v1/customers",
     description = "Customers of the RAPID Store",
     tags = {"CustomersResource"})
@RestController
public class CustomerResource {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.GET, value = "{customerId}", produces = "application/json")
    @ApiOperation(value = "Fetches a customer by their ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found the customer"),
            @ApiResponse(code = 404, message = "Customer was not found")
    })
    public Customer findCustomer(
            @ApiParam(name = "customerId", required = true, value = "customer Id") @PathVariable("customerId") UUID customerId) {
        return customerService.find(customerId);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "Fetches all customers")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found the customers")
    })
    public List<Customer> findAllCustomers() {
        return customerService.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "Enrolls a new customer to the store")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Customer was successfully created")
    })
    public Customer createCustomer(
            @ApiParam(name = "Customer", required = true, value = "Customer to enroll in the store") @RequestBody Customer customer,
            HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CREATED);
        return customerService.create(customer);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    @ApiOperation(value = "Updates an existing customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer was successfully updated"),
            @ApiResponse(code = 404, message = "Customer was not found")
    })
    public Customer updateCustomer(
            @ApiParam(name = "Customer", required = true, value = "Customer to update in the store") @RequestBody Customer customer) {
        return customerService.update(customer);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{customerId}", produces = "application/json")
    @ApiOperation(value = "Deletes a customer from the store")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Customer was successfully deleted"),
            @ApiResponse(code = 404, message = "Customer was not found")
    })
    public void deleteCustomer(
            @ApiParam(name = "customerId", required = true, value = "customer Id") @PathVariable("customerId") UUID customerId,
            HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        customerService.delete(customerId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "{customerId}/orders", produces = "application/json")
    @ApiOperation(value = "Fetches all orders for a customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found the customer"),
            @ApiResponse(code = 404, message = "Customer was not found")
    })
    public List<Order> findOrdersForCustomer(
            @ApiParam(name = "customerId", required = true, value = "customer Id") @PathVariable("customerId") UUID customerId,
            @ApiParam(name = "status", required = false, value = "status") @RequestParam(required = false) Order.Status status) {
        return orderService.findByCustomerId(customerId, status);
    }
}
