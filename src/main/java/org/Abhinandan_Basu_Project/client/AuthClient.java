package org.Abhinandan_Basu_Project.client;

import io.restassured.response.Response;
import org.Abhinandan_Basu_Project.Config.configManager;
import org.Abhinandan_Basu_Project.POJOS.UserCredentials;
import org.Abhinandan_Basu_Project.auth.AuthTokenProvider;

import static io.restassured.RestAssured.given;

public class AuthClient {
    public static Response login(){

        String payload =
                "{ \"user_email\": \"" +
                        configManager.getProperty("valid_email") +
                        "\", \"password\": \"" +
                        configManager.getProperty("valid_password") +
                        "\" }";


        return given()
                .log().all()
                .baseUri(configManager.getProperty("BASE_URL"))
                .contentType("application/json")
                .body(payload)
                .when()
                .post("/auth/login")
                .then()
                .log().all()
                .extract()
                .response();


    }
    public static Response login(String email,String password){

        String payload =
                "{ \"user_email\": \"" +
                        email +
                        "\", \"password\": \"" +
                        password +
                        "\" }";

        return given()
                .baseUri(configManager.getProperty("BASE_URL"))
                .contentType("application/json")
                .body(payload)
                .post("/auth/login")
                .then()
                .extract()
                .response();
    }
}
