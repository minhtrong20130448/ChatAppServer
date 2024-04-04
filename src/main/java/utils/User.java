package utils;

import java.util.List;

public class User<T>{
    private String username;
    private T password;

    public User(String username, T password ) {
        this.username = username;
        this.password = password;
    }
    static {
        System.out.println("Static block");
    }
    public String getUsername() {
        return username;
    }
    public T getPassword() {
        return password;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(T password) {
        this.password = password;
    }

    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password=" + password +
                '}';
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> listuser = List.of("a", "b", "c");
        List<String> listfriedn = List.of("a", "b", "c");
    }
}
