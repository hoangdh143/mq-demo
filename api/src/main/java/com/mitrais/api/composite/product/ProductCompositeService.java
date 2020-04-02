package com.mitrais.api.composite.product;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface ProductCompositeService {

    @PostMapping(
        value    = "/product-composite",
        consumes = "application/json")
    Mono<Void> createCompositeProduct(@RequestBody ProductAggregate body);

    @GetMapping(
        value    = "/product-composite/{productId}",
        produces = "application/json")
    Mono<ProductAggregate> getCompositeProduct(
            @RequestHeader HttpHeaders headers,
            @PathVariable int productId,
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    );


    @DeleteMapping(value = "/product-composite/{productId}")
    Mono<Void> deleteCompositeProduct(@PathVariable int productId);
}
