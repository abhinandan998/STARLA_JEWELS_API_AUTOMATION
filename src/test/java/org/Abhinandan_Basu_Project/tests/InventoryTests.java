package org.Abhinandan_Basu_Project.tests;

import io.restassured.response.Response;


import org.Abhinandan_Basu_Project.client.InventoryClient;

import org.Abhinandan_Basu_Project.utils.DataGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InventoryTests extends  BaseTest{

    InventoryClient inventoryClient =
            new InventoryClient();

    @Test
    public void positiveInventory(){

        Response res =
                inventoryClient.createInventory();

        Assert.assertEquals(res.statusCode(),200);
        Assert.assertTrue(res.getTime() < 2000);

        Assert.assertEquals(
                res.jsonPath().getString("message"),
                "Item inwarded successfully"
        );

    }


    @Test
    public void duplicateInventory(){

        String code = DataGenerator.vendorCode();

        Response first = inventoryClient.createInventory(code);

        Assert.assertEquals(first.statusCode(),200);

        Response second = inventoryClient.createInventory(code);

        Assert.assertEquals(second.statusCode(),200);

        Boolean skipped = second.jsonPath().getBoolean("skipped");

        Assert.assertTrue(skipped);

        String message = second.jsonPath().getString("message");

        Assert.assertTrue(message.contains("already exists"));
        Assert.assertTrue(
                second.jsonPath().getBoolean("skipped")
        );

    }

    @Test
    public void rollbackScenario(){

        String code = DataGenerator.vendorCode();

        Response first = inventoryClient.createInventory(code);

        // First request should succeed
        Assert.assertEquals(first.statusCode(),200);

        Assert.assertEquals(
                first.jsonPath().getString("message"),
                "Item inwarded successfully"
        );

        // Send duplicate request
        Response second = inventoryClient.createInventory(code);

        Assert.assertEquals(second.statusCode(),200);

        Boolean skipped = second.jsonPath().getBoolean("skipped");

        // System correctly skips duplicate
        Assert.assertTrue(skipped);

        String message = second.jsonPath().getString("message");

        Assert.assertTrue(message.contains("already exists"));
        Assert.assertTrue(
                second.jsonPath().getBoolean("skipped")
        );
    }



}

