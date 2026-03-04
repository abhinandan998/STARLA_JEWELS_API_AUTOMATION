package org.Abhinandan_Basu_Project.Config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.Abhinandan_Basu_Project.auth.AuthTokenProvider;

public class RequestSpecFactory {

    private RequestSpecFactory() {
    }

    public static RequestSpecification getSpec(){
        return new RequestSpecBuilder()
                .setBaseUri(configManager.getProperty("BASE_URL"))
                .addHeader("Authorization", "Bearer" + AuthTokenProvider.getToken())
                .addHeader("x-user-id", String.valueOf(AuthTokenProvider.getUserId()))
                .addHeader("x-organisation-id", configManager.getProperty("organisationid"))
                .addHeader("Content-Type", "application/json")
                .build();

    }
}
