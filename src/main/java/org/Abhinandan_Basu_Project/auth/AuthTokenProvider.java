package org.Abhinandan_Basu_Project.auth;

;

public class AuthTokenProvider {

    private static String token;
    private static String userId;

    private AuthTokenProvider(){}

    public static String getToken() {


        return token;
    }

    public static void setToken(String token) {
        AuthTokenProvider.token = token;
    }

    public static String getUserId() {


        return userId;
    }

    public static void setUserId(String id) {
       userId = id;
    }
}
