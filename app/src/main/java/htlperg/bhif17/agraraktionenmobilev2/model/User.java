package htlperg.bhif17.agraraktionenmobilev2.model;

import lombok.Data;

@Data
public class User {
    private String email;
    private int id;
    private boolean loggedIn;
    private String password;
    private String username;
}
