package org.Abhinandan_Basu_Project.client;

import io.restassured.response.Response;

import org.Abhinandan_Basu_Project.Config.configManager;
import org.Abhinandan_Basu_Project.POJOS.InventoryRequest;
import org.Abhinandan_Basu_Project.auth.AuthTokenProvider;
import org.Abhinandan_Basu_Project.builder.InventoryRequestBuilder;
import org.testng.Assert;

import static io.restassured.RestAssured.given;


public class InventoryClient {
    public Response createInventory(){

        InventoryRequest req =
                InventoryRequestBuilder.build();

        return sendRequest(req);
    }

    public Response createInventory(String Code){

        InventoryRequest req =
                InventoryRequestBuilder.build(Code);

        return sendRequest(req);
    }

    private Response sendRequest(InventoryRequest req){

        return given()
                .log().all()
                .baseUri(configManager.getProperty("BASE_URL"))

                .header("Authorization",
                        "Bearer " + AuthTokenProvider.getToken())

                .header("x-user-id",
                        AuthTokenProvider.getUserId())

                .header("x-organisation-id",
                        configManager.getProperty("organisationId"))

                .header("Content-Type","application/json")

                .body(req)

                .when()
                .post("/inventory/inward-from-vendor")

                .then()
                .log().all()
                .extract()
                .response();

    }

}
