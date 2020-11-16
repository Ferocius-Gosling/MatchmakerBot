package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.company.bot.DialogState;
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
            var result = statement.executeQuery(query).next();
            if (result){
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
                userPhoto = null;
            else {
                var photo = user.getUserPhoto();
                userPhoto = photo.getName();
                var fileToSave = new File(getPathToPhotos(), userPhoto);
                if (fileToSave.createNewFile()) {
                    FileInputStream fileInputStream = new FileInputStream(photo);
                    FileOutputStream fileOutputStream = new FileOutputStream(fileToSave);
                    byte[] fileContent = new byte[(int) photo.length()];
                    fileInputStream.read(fileContent);
                    fileInputStream.close();
                    fileOutputStream.write(fileContent);
                    fileOutputStream.close();
                }
            }
            var query = String.format("UPDATE users_data SET " +
                            "username=\"%s\", name=\"%s\", age=%s, city=\"%s\", description=\"%s\"," +
                            "photo=\"%s\", dialog=\"%s\" WHERE id=%s",
                    user.getUserName(), user.getName(), user.getAge(),
                    user.getCity(), user.getInfo(), userPhoto, user.getCurrentState().toString(),
                    user.getId());
            statement.executeUpdate(query);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLikes(User userWhoLiked, User userWhomLiked) {
        try(Statement statement = connection.createStatement()){
            var query = String.format("SELECT * FROM likes WHERE " +
                    "who_liked=%s and whom_liked=%s",
                    userWhoLiked.getId(),
                    userWhomLiked.getId());
            var result = statement.executeQuery(query).next();
            if (result)
                return;
            query = String.format("INSERT INTO likes (who_liked, whom_liked)" +
                    " values(%s, %s)", userWhoLiked.getId(), userWhomLiked.getId());
            statement.executeUpdate(query);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateMatches(User userWhoLiked, User userWhomLiked) {
        try(Statement statement = connection.createStatement()){
            var query = String.format("INSERT INTO matches (who_liked, whom_liked)" +
                    " values(%s, %s)", userWhoLiked.getId(), userWhomLiked.getId());
            statement.executeUpdate(query);
            query = String.format("INSERT INTO matches (who_liked, whom_liked)" +
                    " values(%s, %s)", userWhomLiked.getId(), userWhoLiked.getId());
            statement.executeUpdate(query);
            query = String.format("DELETE FROM likes WHERE " +
                    "who_liked=%s and whom_liked=%s", userWhoLiked.getId(),
                    userWhomLiked.getId());
            statement.executeUpdate(query);
            query = String.format("DELETE FROM likes WHERE " +
                            "who_liked=%s and whom_liked=%s", userWhomLiked.getId(),
                    userWhoLiked.getId());
            statement.executeUpdate(query);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteFromMatches(User user) {
        try(Statement statement = connection.createStatement()){
            var query = String.format("DELETE FROM matches WHERE " +
                            "who_liked=%s", user.getId());
            statement.executeUpdate(query);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public ArrayList<Long> getIdLikedUser(String table, User user) {
        try(Statement statement = connection.createStatement()){
            var query = String.format("SELECT * FROM %s " +
                    "WHERE who_liked=%s", table, user.getId());
            var result = statement.executeQuery(query);
            var ids = new ArrayList<Long>();
            while (result.next())
                ids.add(result.getLong("whom_liked"));
            return ids;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public HashMap<Long, User> load(){
        try(Statement statement = connection.createStatement()){
            var users = new HashMap<Long, User>();
            var query = "SELECT * FROM users_data";
            var result = statement.executeQuery(query);
            while (result.next()) {
                var id = result.getLong("id");
                var state = DialogState.valueOf(result.getString("dialog"));
                var photo = new File(getPathToPhotos(), result.getString("photo"));
                var user = new User(id, result.getString("username"),
                        result.getString("name"),
                        result.getInt("age"),
                        result.getString("city"),
                        result.getString("description"),
                        state, photo);
                users.put(id, user);
            }
            return users;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return new HashMap<>();
    }


    private String getHost(){ return System.getenv("hostname"); }
    private String getLogin(){ return System.getenv("db-login"); }
    private String getPassword(){ return System.getenv("db-password"); }
    private String getPathToPhotos() { return System.getenv("path_to_photos"); }

}
