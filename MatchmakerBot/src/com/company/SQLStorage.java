package com.company;

import java.sql.*;
import java.util.Properties;

import com.company.bot.User;


public class SQLStorage {
    private Connection connection;

    public SQLStorage(){
        var host = getHost();
        var login = getLogin();
        var password = getPassword();
        Properties properties = new Properties();
        properties.put("User", login);
        properties.put("password", password);
        properties.put("characterUnicode", "true");
        properties.put("useUnicode", "true");
        properties.put("useSSL", "false");
        properties.put("autoReconnect", "true");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(host, properties);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void registerUser(User user) {
        try(Statement statement = connection.createStatement()) {
            var query = String.format("SELECT * FROM users_data WHERE id=%s", user.getId());
            var result = statement.executeQuery(query);
            var res = result.next();
            if (res){
                query = String.format("DELETE FROM users_data WHERE id=%s", user.getId());
                statement.executeUpdate(query);
            }
            query = String.format("INSERT INTO users_data (id) values (%s)",
                    user.getId());
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user) {
        try(Statement statement = connection.createStatement()) {
            var userPhoto = "";
            if (user.getUserPhoto() == null)
                userPhoto = "NULL";
            else
                userPhoto = user.getUserPhoto().getName();
            var query = String.format("UPDATE users_data SET " +
                            "username=\"%s\", name=\"%s\", age=%s, city=\"%s\", description=\"%s\"," +
                            "photo=\"%s\", dialog=\"%s\" WHERE id=%s",
                    user.getUserName(), user.getName(), user.getAge(),
                    user.getCity(), user.getInfo(), userPhoto, user.getCurrentState().toString(),
                    user.getId());
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private String getHost(){ return System.getenv("hostname"); }
    private String getLogin(){ return System.getenv("db-login"); }
    private String getPassword(){ return System.getenv("db-password"); }

}
