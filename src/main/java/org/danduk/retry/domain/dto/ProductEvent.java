package org.danduk.retry.domain.dto;

import com.google.gson.Gson;

public class ProductEvent {
    int iri_id;
    public String toString(){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }

    public int getIri_id() {
        return iri_id;
    }

    public void setIri_id(int iri_id) {
        this.iri_id = iri_id;
    }
}
