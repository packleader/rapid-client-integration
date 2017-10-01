package com.packleader.rapid.example.store.resources.v1;

import com.packleader.rapid.example.store.domain.Product;
import com.packleader.rapid.example.store.services.ProductService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@RequestMapping("v1/products")
@Api(value = "v1/products",
     description = "Products available for sale from the RAPID Store",
     tags = {"ProductsResource"})
@RestController
public class ProductResource {

    @Autowired
    private ProductService productService;

    @RequestMapping(method = RequestMethod.GET, value = "{productId}", produces = "application/json")
    @ApiOperation(value = "Fetches a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found the product"),
            @ApiResponse(code = 404, message = "Product was not found")
    })
    public Product findProduct(
            @ApiParam(name = "productId", required = true, value = "product Id") @PathVariable("productId") UUID productId) {
        return productService.find(productId);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "Fetches all products")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found the products")
    })
    public List<Product> findAllProducts() {
        return productService.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "Adds a new product to the store")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Product was successfully created")
    })
    public Product createProduct(
            @ApiParam(name = "Product", required = true, value = "Product to create in the store") @RequestBody Product product,
            HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CREATED);
        return productService.create(product);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    @ApiOperation(value = "Updates an existing product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product was successfully updated"),
            @ApiResponse(code = 404, message = "Product was not found")
    })
    public Product updateProduct(
            @ApiParam(name = "Product", required = true, value = "Product to update in the store") @RequestBody Product product) {
        return productService.update(product);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{productId}", produces = "application/json")
    @ApiOperation(value = "Deletes a product from the store")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Product was successfully deleted"),
            @ApiResponse(code = 404, message = "Product was not found")
    })
    public void deleteProduct(
            @ApiParam(name = "productId", required = true, value = "product Id") @PathVariable("productId") UUID productId,
            HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        productService.delete(productId);
    }
}
