package org.Abhinandan_Basu_Project.tests;

import io.restassured.response.Response;
import org.Abhinandan_Basu_Project.auth.AuthTokenProvider;
import org.Abhinandan_Basu_Project.client.AuthClient;
import org.testng.annotations.BeforeSuite;

public class BaseTest {


        @BeforeSuite
        public void setup(){

            // Login once before all tests
            Response res = AuthClient.login();

            String token = res.jsonPath().getString("token");
            String userId = res.jsonPath().getString("userId");

            AuthTokenProvider.setToken(token);
            AuthTokenProvider.setUserId(userId);

        }
}
