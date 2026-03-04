package org.Abhinandan_Basu_Project.builder;

import org.Abhinandan_Basu_Project.Config.configManager;
import org.Abhinandan_Basu_Project.POJOS.InventoryRequest;
import org.Abhinandan_Basu_Project.utils.DataGenerator;

public class InventoryRequestBuilder {


    public static InventoryRequest build() {

        InventoryRequest req = new InventoryRequest();

        req.item_name =
                configManager.getProperty("item_name");

        req.item_description =
                configManager.getProperty("item_description");

        req.category_id =
                Integer.parseInt(configManager.getProperty("categoryId"));

        req.vendor_id =
                Integer.parseInt(configManager.getProperty("vendorId"));

        req.vendor_design_code =
                configManager.getProperty("vendor_design_code");

        req.vendor_jewel_code =
                DataGenerator.vendorCode();

        req.gross_weight =
                Double.parseDouble(configManager.getProperty("gross_weight"));

        req.cost =
                Integer.parseInt(configManager.getProperty("cost"));

        req.metals = "[{\"metal_type_id\":1,\"metal_weight\":10.5}]";

        req.stones = "[]";

        req.stock_owner_type =
                configManager.getProperty("stock_owner_type");

        req.stock_status_id =
                Integer.parseInt(configManager.getProperty("stockStatusId"));

        return req;
    }
    public static InventoryRequest build(String code){

        InventoryRequest req = build();

        req.vendor_jewel_code = code;

        return req;

    }
}
