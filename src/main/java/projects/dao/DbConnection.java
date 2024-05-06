package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import projects.exception.DbException;

public class DbConnection {
    private static final String HOST = "localhost";
    private static final String PASSWORD = "projects";
    private static final String PORT = "3306";
    private static final String SCHEMA = "projects";
    private static final String USER = "projects";

    // Private constructor to prevent instantiation
    private DbConnection() {
    }

    // Public and static method to get a database connection
    public static Connection getConnection() {
        String uri = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s",
                HOST, PORT, SCHEMA, USER, PASSWORD);

        try {
            // Attempt to obtain a connection
            Connection connection = DriverManager.getConnection(uri);

            // Print a success message if the connection is successful
            System.out.println("Database connection established successfully.");
            return connection;
        } catch (SQLException e) {
            // Print an error message and throw a custom exception if the connection fails
            System.err.println("Error connecting to the database: " + e.getMessage());
            throw new DbException("Database connection failed.", e);
        }
    }

   
   
}

  