package iri.events;

import com.google.gson.Gson;

import java.sql.Date;
import java.sql.Timestamp;

public class Product {
    long product_id;
    String cell_id;
    Timestamp iritimestamp;
    Float longitude;
    Float latitude;
    int id;
    public String toString(){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }
    public long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(long product_id) {
        this.product_id = product_id;
    }

    public String getCell_id() {
        return cell_id;
    }

    public void setCell_id(String cell_id) {
        this.cell_id = cell_id;
    }

    public Timestamp getIritimestamp() {
        return iritimestamp;
    }

    public void setIritimestamp(Timestamp iritimestamp) {
        this.iritimestamp = iritimestamp;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
