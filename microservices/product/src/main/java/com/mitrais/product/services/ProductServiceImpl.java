package com.mitrais.product.services;


import com.mitrais.api.core.product.Product;
import com.mitrais.api.core.product.ProductService;
import com.mitrais.product.persistence.ProductEntity;
import com.mitrais.product.persistence.ProductRepository;
import com.mitrais.util.exceptions.InvalidInputException;
import com.mitrais.util.exceptions.NotFoundException;
import com.mitrais.util.http.ServiceUtil;
import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@RestController
public class ProductServiceImpl implements ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final ProductRepository repository;

    private final ProductMapper mapper;

    @Autowired
    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Product createProduct(Product body) {
        if (body.getProductId() < 1) throw new InvalidInputException("Invalid productId: " + body.getProductId());
        ProductEntity entity = mapper.apiToEntity(body);
        Mono<Product> newEntity = repository.save(entity).log(null, Level.FINE)
            .onErrorMap(
                DuplicateKeyException.class,
                ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId())
            )
            .map(mapper::entityToApi);
        return newEntity.block();
    }

    @Override
    public Mono<Product> getProduct(HttpHeaders headers, int productId, int faultPercent) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
        if (faultPercent > 0) throwErrorIfBadLuck(faultPercent);
        LOG.info("Will get product info for id={}", productId);
        return repository.findByProductId(productId)
            .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " + productId)))
            .log(null, Level.FINE)
            .map(mapper::entityToApi)
            .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
    }

    private void throwErrorIfBadLuck(int faultPercent) {
        int randomThreshold = 70;
        if (faultPercent < randomThreshold) {
            LOG.debug("We got lucky, no error occurred, {} < {}", faultPercent, randomThreshold);
        } else {
            LOG.warn("Bad luck, an error occurred, {} >= {}", faultPercent, randomThreshold);
            throw new RuntimeException("Something went wrong...");
        }
    }

    @Override
    public void deleteProduct(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        LOG.debug("deletePRoduct: tries to delete and entity with productId: {}", productId);
        repository.findByProductId(productId).log(null, Level.FINE).map(repository::delete).flatMap(e -> e).block();
    }
}
