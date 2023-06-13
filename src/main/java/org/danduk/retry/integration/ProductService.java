package org.danduk.retry.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.danduk.retry.GisServiceException;
import org.danduk.retry.domain.dto.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.net.URI;

@Service
public class ProductService {
    URI uri;
    Product product;
    ObjectMapper objectMapper;

    static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public ProductService() {
        this.objectMapper = new ObjectMapper();

    }

    public Product getProductById(Long product_id) throws GisServiceException {
        uri = URI.create(String.format("http://localhost:8099/products/%d", product_id));
        this.product = null;

        try {
            this.product = objectMapper.readValue(this.sendGet(), Product.class);
        } catch (Exception e) {
            logger.error("got an error in ProductWrapper");
            throw new GisServiceException(e);
        }
        return this.product;
    }


    private String sendGet() throws Exception {


        HttpGet request = new HttpGet(this.uri);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try (CloseableHttpResponse response = httpClient.execute(request)){

            // Get HttpResponse Status
            logger.debug(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            logger.debug(headers.toString());

            if (entity != null) {
                // return it as a String
                result = EntityUtils.toString(entity);

            }
        }
        logger.debug(result);
        return result;

    }

    @Override
    public String toString() {
        return this.product.toString();
    }

}
