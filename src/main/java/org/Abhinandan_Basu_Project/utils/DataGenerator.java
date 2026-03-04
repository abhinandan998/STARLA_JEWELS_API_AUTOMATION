package org.Abhinandan_Basu_Project.utils;



import java.util.UUID;

public class DataGenerator {

    private DataGenerator(){

    }

    public static String vendorCode(){
        return "VD-" + UUID.randomUUID().toString().substring(0,8);
    }
}
