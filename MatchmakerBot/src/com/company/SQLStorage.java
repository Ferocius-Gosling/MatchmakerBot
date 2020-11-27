package com.company;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import com.company.bot.DialogState;
import com.company.bot.User;


public class SQLStorage implements Closeable {
    private Connection connection;
    private final Properties properties;
    private final String host;
    private static final String selectUsersDataQueryById =
            "SELECT * FROM users_data WHERE id=?";
    private static final String selectLikesQueryByID =
            "SELECT * FROM likes WHERE who_liked=? and whom_liked=?";
    private static final String updateUsersDataQueryById =
            "UPDATE users_data SET username=?, name=?, age=?, city=?, " +
                    "description=?, photo=?, dialog=?, user_in_question=?" +
                    " WHERE id=?";
    private static final String updateUserLastFindById =
            "UPDATE users_data SET last_find=? WHERE id=?";
    private static final String insertLikesQueryById =
            "INSERT INTO likes (who_liked, whom_liked) values(?, ?)";
    private static final String insertMatchesQueryById =
            "INSERT INTO matches (who_liked, whom_liked) values(?, ?);";
    private static final String deleteUsersDataQueryById =
            "DELETE FROM users_data WHERE id=?";
    private static final String deleteLikesQueryById =
            "DELETE FROM likes WHERE who_liked=? and whom_liked=?;";
    private static final String deleteMatchesQueryById =
            "DELETE FROM matches WHERE who_liked=?";
    private static final String insertUsersDataQueryById =
            "INSERT INTO users_data (id, dialog) values (?, ?)";
    private static final String selectTableQueryById =
            "SELECT * FROM %s WHERE who_liked=?";
    private static final String selectUsersOrderedByTime =
            "SELECT * FROM users_data WHERE id!=? ORDER BY last_find DESC";

    public SQLStorage(String hostname, String dbLogin, String pass) {
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

    public void createConnection() throws SQLException {
       // Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(host, properties);
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }

    public void deleteUser(User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(deleteUsersDataQueryById)) {
            statement.setLong(1, user.getId());
            statement.executeUpdate();
        }
    }

    public void registerUser(User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(insertUsersDataQueryById)) {
            statement.setLong(1, user.getId());
            statement.setString(2, user.getCurrentState().toString());
            statement.executeUpdate();
        }
    }

    public void updateUser(User user) throws SQLException, IOException {
        try (PreparedStatement statement = connection.prepareStatement(updateUsersDataQueryById)) {
            var photo = user.getUserPhoto(); //todo refactor if and try
            FileInputStream fileInputStream = null;
            if (photo != null) {
                fileInputStream = new FileInputStream(photo);
                statement.setBinaryStream(6, fileInputStream, (int) photo.length());
            } else {
                statement.setBinaryStream(6, null);
            }
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getName());
            statement.setInt(3, user.getAge());
            statement.setString(4, user.getCity());
            statement.setString(5, user.getInfo());
            statement.setString(7, user.getCurrentState().toString());
            statement.setLong(8, user.getUserInQuestionId());
            statement.setLong(9, user.getId());
            statement.executeUpdate();
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    public void updateLastFind(User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(updateUserLastFindById)){
            var date = new Date();
            statement.setTimestamp(1, new Timestamp(date.getTime()));
            statement.setLong(2, user.getId());
            statement.executeUpdate();
        }
    }

    public boolean isRowInLikesExist(User userWhoLiked, User userWhomLiked) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(selectLikesQueryByID)) {
            statement.setLong(1, userWhoLiked.getId());
            statement.setLong(2, userWhomLiked.getId());
            return statement.executeQuery().next();
        }
    }

    public void updateLikes(User userWhoLiked, User userWhomLiked) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(insertLikesQueryById)) {
            statement.setLong(1, userWhoLiked.getId());
            statement.setLong(2, userWhomLiked.getId());
            statement.executeUpdate();
        }
    }

    public void updateMatches(User userWhoLiked, User userWhomLiked) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(insertMatchesQueryById)) {
            statement.setLong(1, userWhoLiked.getId());
            statement.setLong(2, userWhomLiked.getId());
            statement.execute();
        }
    }

    public void deleteLikes(User userWhoLiked, User userWhomLiked) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(deleteLikesQueryById)) {
            statement.setLong(1, userWhoLiked.getId());
            statement.setLong(2, userWhomLiked.getId());
            statement.executeUpdate();
        }
    }

    public void deleteFromMatches(User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(deleteMatchesQueryById)) {
            statement.setLong(1, user.getId());
            statement.executeUpdate();
        }
    }

    public ArrayList<Long> getIdLikedUser(String table, User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                String.format(selectTableQueryById, table))) {
            statement.setLong(1, user.getId());
            var result = statement.executeQuery();
            var ids = new ArrayList<Long>();
            while (result.next())
                ids.add(result.getLong("whom_liked"));
            return ids;
        }
    }

    public User loadUser(User userWhoLiked) throws SQLException, IOException {
        String query;
        if (userWhoLiked.getCity() == null) query = selectUsersDataQueryById;
        else query = selectUsersOrderedByTime;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            User user;
            statement.setLong(1, userWhoLiked.getId());
            var result = statement.executeQuery();
            if (result.next()) {
                var id = result.getLong("id");
                var state = DialogState.valueOf(result.getString("dialog"));
                var photoContent = result.getBlob("photo");
                File photo = new File(System.getenv("path_to_photos") + "/" + id);
                if (photoContent != null) {
                    try (var fileOutputStream = new FileOutputStream(photo)) {
                        fileOutputStream.write(photoContent.getBytes(1, (int) photoContent.length()));
                    }
                } else
                    photo = null;
                user = new User(id, result.getString("username"),
                        result.getString("name"),
                        result.getInt("age"),
                        result.getString("city"),
                        result.getString("description"),
                        state, photo, result.getLong("user_in_question"));
            } else return null;
            return user;
        }
    }
}
