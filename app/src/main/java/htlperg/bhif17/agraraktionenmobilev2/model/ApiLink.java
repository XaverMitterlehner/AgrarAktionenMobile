package htlperg.bhif17.agraraktionenmobilev2.model;

import lombok.Data;

@Data
public class ApiLink {

    private int apiId;
    private String description;
    private String password;
    private String user;
    private String url;
    private Shop shop;



}

