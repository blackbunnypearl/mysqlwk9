package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
  private Scanner scanner = new Scanner(System.in);
  private ProjectService projectService = new ProjectService();
  private Project curProject; // Declare the current project variable


    // Available menu operations
    private List<String> operations = List.of(
        "1) Add a project",
        "2) List projects",
        "3) Select a project"
    );

    public static void main(String[] args) {
        new ProjectsApp().processUserSelections();
    }

    public void processUserSelections() {
        boolean done = false;
        while (!done) {
            try {
                int selection = getUserSelection();
                switch (selection) {
                    case -1:
                        done = exitMenu();
                        break;
                    case 1:
                        createProject();
                        break;
                    case 2:
                      listProjects();
                      break;
                    case 3:
                      selectProject();
                      break;
                      
                    default:
                        System.out.println("\n" + selection + " is not a valid selection. Try again.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("\nError: " + e + ". Try again.");
            }
        }
    }

    private void selectProject() {
      // Display the list of projects
      listProjects();
      
      // Prompt the user to enter a project ID
      Integer projectId = getIntInput("Enter a project ID to select a project");
      
      // Attempt to fetch the project with the provided ID
      try {
          curProject = projectService.fetchProjectById(projectId).orElse(null);
          
          // Check if a project was found
          if (curProject == null) {
              System.out.println("No project found with ID: " + projectId);
          } else {
              System.out.println("Project selected: " + curProject.getProjectName());
          }
      } catch (Exception e) {
          System.err.println("Error selecting project: " + e.getMessage());
      }
  }


    private void listProjects() {
      List<Project> projects = projectService.fetchallProjects();
      System.out.println("\nProjects:");
      projects.forEach(project -> System.out.println
          ("    " + project.getProjectId() + ": " + project.getProjectName()));
  }

    
    private void printOperations() {
      System.out.println("\nThese are the available selections. Press Enter key to quit");
      operations.forEach(line -> System.out.println("    " + line));
      if (Objects.isNull(curProject)) {
        System.out.println("\nYou are not working with a project.");
      } 
      else {
        System.out.println("\nYou are working with the project:" + curProject);
      }
    }

    private void createProject() {
        // Gather project details from user
        String projectName = getStringInput("Enter the project name");
        BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
        BigDecimal actualHours = getDecimalInput("Enter the actual hours");
        Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
        String notes = getStringInput("Enter the project notes");

        // Create a new Project object
        Project project = new Project();
        project.setProjectName(projectName);
        project.setEstimatedHours(estimatedHours);
        project.setActualHours(actualHours);
        project.setDifficulty(difficulty);
        project.setNotes(notes);

        // Add the project using the ProjectService
        Project dbProject = projectService.addProject(project);
        System.out.println("You have successfully created project: " + dbProject);
    }

    private BigDecimal getDecimalInput(String prompt) {
        String input = getStringInput(prompt);
        if (Objects.isNull(input)) {
            return null;
        }
        try {
            return new BigDecimal(input).setScale(2); // Set scale to 2 decimal places
        } catch (NumberFormatException e) {
            throw new DbException(input + " is not a valid decimal number.");
        }
    }

    private int getUserSelection() {
        printOperations();
        Integer input = getIntInput("Enter a menu selection");
        return Objects.isNull(input) ? -1 : input;
    }

    private Integer getIntInput(String prompt) {
        String input = getStringInput(prompt);
        if (Objects.isNull(input)) {
            return null;
        }
        try {
            return Integer.valueOf(input);
        } catch (NumberFormatException e) {
            throw new DbException(input + " is not a valid number.");
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt + ": ");
        String input = scanner.nextLine();
        return input.isBlank() ? null : input.trim();
    }

    private boolean exitMenu() {
        System.out.println("\nExiting the menu. TTFN!"); // TTFN: Ta-Ta For Now
        return true;
    }
}