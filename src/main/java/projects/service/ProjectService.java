package projects.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale.Category;
import java.util.Objects;
import java.util.Optional;
import projects.dao.DbConnection;
import projects.dao.ProjectDao;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;

public class ProjectService {
    private static final String PROJECT_TABLE = "project";
    private static final String MATERIAL_TABLE = "material";
    private static final String STEP_TABLE = "step";
    private static final String CATEGORY_TABLE = "category";
    private static final String PROJECT_CATEGORY_TABLE = "project_category";
    private ProjectDao projectDao = new ProjectDao();

    public Project addProject(Project project) {
        return project;
    }

   

public List<Project> fetchallProjects() {
  String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
  try(Connection conn = DbConnection.getConnection()) {
    commitTransaction(conn);
    
    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
      try(ResultSet rs = stmt.executeQuery()) {
        List<Project> projects = new LinkedList<>();
        while(rs.next()) {
          projects.add(extract(rs, Project.class));
        }
        return projects;
      }
    }
    catch(Exception e) {
      rollbackTransaction(conn);
      throw new DbException(e);
    }
  }
  catch(SQLException e) {
    throw new DbException(e);
  }
}

private Project extract(ResultSet rs, Class<Project> class1) {
  return null;
}



public Optional<Project> fetchProjectById(Integer projectId) {
  String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
  Project project = null;
  
  try (Connection conn = DbConnection.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
          setParameter(stmt, 1, projectId, Integer.class);
          
          try (ResultSet rs = stmt.executeQuery()) {
              if (rs.next()) {
                  project = extract(rs, Project.class);
              }
          }
      } catch (Exception e) {
          throw new DbException(e);
      } finally {
          rollbackTransaction(conn);
      }
      
      if (project != null) {
          project.setMaterials(fetchMaterialsForProject(conn, projectId));
          project.setSteps(fetchStepsForProject(conn, projectId));
          project.setCategories(fetchCategoriesForProject(conn, projectId));
      }
      
      commitTransaction(conn);
  } catch (SQLException e) {
      throw new DbException(e);
  }
  
  return Optional.ofNullable(project);
}



private void rollbackTransaction(Connection conn) {
  if (conn != null) {
      try {
          conn.rollback();
      } catch (SQLException e) {
          System.err.println("Unable to rollback transaction: " + e.getMessage());
      }
  }
}




private void commitTransaction(Connection conn) throws SQLException {
  if (conn != null) {
      conn.commit();
  }
}



  private List<Material> fetchMaterialsForProject(Connection conn,
    Integer projectId) throws SQLException {
  String sql = "SELECT * FROM " + MATERIAL_TABLE + "WHERE project_id = ?";
    
    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, projectId, Integer.class); 
        
        try(ResultSet rs = stmt.executeQuery()) {
          List<Material> materials = new LinkedList<>();
          
          while(rs.next()) {
            materials.add(extract(rs, Material.class));
            
          }
          
          return materials;
          }
        }
      }


  private void setParameter(PreparedStatement stmt, int parameterIndex, Integer value, Class<Integer> type) throws SQLException {
    if (value == null) {
        stmt.setNull(parameterIndex, java.sql.Types.INTEGER);
    } else {
        stmt.setInt(parameterIndex, value);
    }
}




  private List<Step> fetchStepsForProject(Connection conn, 
      Integer projectId) throws SQLException {
    String sql = "SELECT * FROM " + STEP_TABLE + "WHERE project_id = ?";
    
    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, projectId, Integer.class); 
        
        try(ResultSet rs = stmt.executeQuery()) {
          List<Step> steps = new LinkedList<>();
          
          while(rs.next()) {
            steps.add(extract(rs, Step.class));
            
          }
          
          return steps;
          }
        }
      }
  
  private Step extract(ResultSet rs) throws SQLException {
    Step step = new Step();
    step.setId(rs.getInt("step_id"));
    step.setProjectId(rs.getInt("project_id"));
    step.setDescription(rs.getString("description"));
    return step;
}
  private List<Category> fetchCategoriesForProject(Connection conn, 
      Integer projectId) throws SQLException {
    //formatter: off
    String sql = ""
        + "SELECT * FROM " + CATEGORY_TABLE + "c"
        + "JOIN " + PROJECT_CATEGORY_TABLE + "pc USING(category_id)"
        + "WHERE project_id = ?"; 
    //formatter:on
    
    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, projectId, Integer.class);
      
      try(ResultSet rs = stmt.executeQuery()) {
        List<Category> categories = new LinkedList<>();
        
        while(rs.next()) {
          categories.add(extract(rs, Category.class));
          
        }
        
        return categories;
      }
    }
  }
}


  

