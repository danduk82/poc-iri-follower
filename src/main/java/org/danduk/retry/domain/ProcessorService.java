package org.danduk.retry.domain;

import org.danduk.retry.domain.mapper.Product;
import org.danduk.retry.integration.ProductWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;

@Service
public class ProcessorService {

        static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

        @Autowired

        private final ProductWrapper productWrapper;


        @Autowired
        public ProcessorService(final ProductWrapper productWrapper){
                this.productWrapper = productWrapper;
        }
        public void processMessage(final Long productId) throws Exception {
                try {
                        Product product = this.productWrapper.getProductById(productId);
                        logger.info(product.toString());
                }
                catch (final Exception e) {
                        throw new Exception(e);
                }
        return;
        }

}
