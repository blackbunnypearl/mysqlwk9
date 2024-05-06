package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectDao {
    private static final String PROJECT_TABLE = "project";

    public Project insertProject(Project project) {
        String sql = "INSERT INTO " + PROJECT_TABLE + " "
                + "(project_name, estimated_hours, actual_hours, difficulty, notes) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
                setParameter(stmt, 5, project.getNotes(), String.class);

                stmt.executeUpdate();
                Integer projectID = getLastInsertId(conn, PROJECT_TABLE);
                commitTransaction(conn);

                project.setProjectId(projectID);
                return project;
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    private Integer getLastInsertId(Connection conn, String projectTable) {
      String tableName = "project_table";

      int lastInsertId = getLastInsertId(conn, tableName);
      System.out.println("Last inserted ID: " + lastInsertId);
        return 42;
    }

    private void commitTransaction(Connection conn) {
      try {
          conn.commit(); 
          System.out.println("Transaction committed successfully!");
      } catch (SQLException e) {
          System.err.println("Error during transaction: " + e.getMessage());
          rollbackTransaction(conn); 
      }
  }


    private void rollbackTransaction(Connection conn) {
      try {
          conn.rollback(); 
          System.out.println("Transaction rolled back successfully!");
      } catch (SQLException e) {
          
          System.err.println("Error during rollback: " + e.getMessage());
      }
  }


    private <T> void setParameter(PreparedStatement stmt, int i, T value, Class<T> valueType) throws SQLException {
      if (value == null) {
          stmt.setNull(i, Types.NULL);
      } else if (valueType == String.class) {
          stmt.setString(i, (String) value);
      } else if (valueType == Integer.class) {
          stmt.setInt(i, (Integer) value);
      } else if (valueType == Double.class) {
          stmt.setDouble(i, (Double) value);
      } else if (valueType == Boolean.class) {
          stmt.setBoolean(i, (Boolean) value);
      } else {
      }
      
  }


    private void startTransaction(Connection conn) {
      try {
          conn.setAutoCommit(false);
          System.out.println("Transaction started successfully.");
      } catch (SQLException e) {
          try {
              conn.rollback();
              System.err.println("Error during rollback: " + e.getMessage());
          } catch (SQLException rollbackException) {
              rollbackException.printStackTrace();
          }
      }
   
    }
}

