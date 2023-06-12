package iri.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;

public class ProductWrapper {
    URI uri;
    Product product;
    ObjectMapper objectMapper;

    public ProductWrapper(Long product_id) {
        objectMapper = new ObjectMapper();
        uri = URI.create(String.format("http://localhost:8099/products/%d", product_id));
        this.product=null;

        try {
            this.product = objectMapper.readValue(sendGet(), Product.class);
        } catch (Exception e) {
            System.out.println("got an error in ProductWrapper");
            //e.printStackTrace();
        }

    }

    private String sendGet() throws Exception {


        HttpGet request = new HttpGet(this.uri);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try (CloseableHttpResponse response = httpClient.execute(request)){

            // Get HttpResponse Status
            System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            System.out.println(headers);

            if (entity != null) {
                // return it as a String
                result = EntityUtils.toString(entity);

            }
        }
        System.out.println(result);
        return result;

    }


    public URI geturi() {
        return uri;
    }

    public void seturi(URI uri) {
        this.uri = uri;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
