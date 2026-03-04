package org.Abhinandan_Basu_Project.POJOS;

public class UserCredentials {

    public String user_email;
    public String password;

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserCredentials(String user_email, String password) {
        this.user_email = user_email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserCredentials{" +
                "user_email='" + user_email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
