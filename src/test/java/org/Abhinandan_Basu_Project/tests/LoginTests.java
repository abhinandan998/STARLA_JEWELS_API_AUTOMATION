package org.Abhinandan_Basu_Project.tests;

import io.restassured.response.Response;
import org.Abhinandan_Basu_Project.Config.configManager;
import org.Abhinandan_Basu_Project.client.AuthClient;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends  BaseTest {

    @Test
    public void userPreconditionValidation(){

        Response res = AuthClient.login();

        Assert.assertEquals(res.statusCode(),200);

        String token = res.jsonPath().getString("token");

        Assert.assertNotNull(token);

    }

    @Test
    public void positiveLogin(){

        Response res = AuthClient.login();

        Assert.assertEquals(res.statusCode(),200);

        String token = res.jsonPath().getString("token");
        Integer userId = res.jsonPath().getInt("userId");

        Assert.assertNotNull(token);
        Assert.assertNotNull(userId);

        // Security validation
        Assert.assertNull(res.jsonPath().get("password"));

        Assert.assertTrue(res.getTime() < 2000);
    }

    @Test
    public void negativeLogin(){

        Response res =
                AuthClient.login(
                        configManager.getProperty("invalid_email"),
                        configManager.getProperty("invalid_password"));

        Assert.assertTrue(res.statusCode()==401
                || res.statusCode()==400);

    }

    @Test
    public void sqlInjectionTest(){

        Response res =
                AuthClient.login(
                        configManager.getProperty("sql_injection"),
                        configManager.getProperty("valid_password"));

        Assert.assertTrue(res.statusCode()==401
                || res.statusCode()==400);

    }

}
