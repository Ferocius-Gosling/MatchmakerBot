package com.company;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import com.company.bot.DialogState;
import com.company.bot.User;
import com.mysql.cj.jdbc.Blob;


public class SQLStorage implements Closeable {
    private Connection connection;
    private final Properties properties;
    private final String host;
    private static final String selectUsersDataQueryById =
            "SELECT * FROM users_data WHERE id=?";
    private static final String updateUsersDataQueryById =
            "UPDATE users_data SET username=?, name=?, age=?, city=?, " +
                    "description=?, photo=?, dialog=? WHERE id=?";
    private static final String deleteUsersDataQueryById =
            "DELETE FROM users_data WHERE id=?";
    private static final String insertUsersDataQueryById =
            "INSERT INTO users_data (id, dialog) values (?, ?)";


    public SQLStorage(String hostname, String dbLogin, String pass){
        host = System.getenv(hostname);
        var login = System.getenv(dbLogin);
        var password = System.getenv(pass);
        properties = new Properties();
        properties.put("User", login);
        properties.put("password", password);
        properties.put("characterUnicode", "true");
        properties.put("useUnicode", "true");
        properties.put("useSSL", "false");
        properties.put("autoReconnect", "true");
    }

    public void createConnection() throws SQLException, ClassNotFoundException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(host, properties);
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void registerUser(User user) throws SQLException{
        try(PreparedStatement statement = connection.prepareStatement(selectUsersDataQueryById)) {
            statement.setLong(1, user.getId());
            var result = statement.executeQuery().next();
            if (result){
                try(PreparedStatement statement1 = connection.prepareStatement(deleteUsersDataQueryById)) {
                    statement1.setLong(1, user.getId());
                    statement1.executeUpdate();
                }
            }
        }
        try(PreparedStatement statement = connection.prepareStatement(insertUsersDataQueryById)) {
            statement.setLong(1, user.getId());
            statement.setString(2, user.getCurrentState().toString());
            statement.executeUpdate();
        }
    }

    public void updateUser(User user) throws SQLException, IOException{
        try(PreparedStatement statement = connection.prepareStatement(updateUsersDataQueryById)) {
            var photo = user.getUserPhoto(); //todo refactor if and try
            if (photo != null) {
                try (FileInputStream fileInputStream = new FileInputStream(photo);) {
                    statement.setBinaryStream(6, fileInputStream, (int)photo.length());
                    statement.setString(1, user.getUserName());
                    statement.setString(2, user.getName());
                    statement.setInt(3, user.getAge());
                    statement.setString(4, user.getCity());
                    statement.setString(5, user.getInfo());
                    statement.setString(7, user.getCurrentState().toString());
                    statement.setLong(8, user.getId());
                    statement.executeUpdate();
                }
            }
            else {
                statement.setBinaryStream(6, null);
                statement.setString(1, user.getUserName());
                statement.setString(2, user.getName());
                statement.setInt(3, user.getAge());
                statement.setString(4, user.getCity());
                statement.setString(5, user.getInfo());
                statement.setString(7, user.getCurrentState().toString());
                statement.setLong(8, user.getId());
                statement.executeUpdate();
            }
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
                var photoContent = result.getBlob("photo");
                File photo = new File(System.getenv("path_to_photos") + "/" + id);
                try (var fileOutputStream = new FileOutputStream(photo)) {
                    fileOutputStream.write(photoContent.getBytes(1, (int) photoContent.length()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private String getPathToPhotos() { return System.getenv("path_to_photos"); }

}
