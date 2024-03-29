import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import java.sql.CallableStatement;



public class UserProfile {
    private JFrame userProfileFrame;
    private Connection connection;
    private String username;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/gym_management_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "sqlbawater@12"; // Replace with your actual database password

    public UserProfile(Connection connection, String username) {
        this.connection = connection;
        this.username = username;

        // Create the main frame for the user profile
        userProfileFrame = new JFrame("User Profile");
        userProfileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userProfileFrame.setSize(800, 600);
        userProfileFrame.setLocationRelativeTo(null);

        // Create buttons for View Profile, View Workout Schedule, and View Nutrition Plan
        JButton viewProfileButton = new JButton("View Profile");
        JButton viewWorkoutButton = new JButton("View Workout Schedule");
        JButton viewNutritionButton = new JButton("View Nutrition Plan");
        


        // Add a new button for checking BMI
        JButton checkBmiButton = new JButton("Check BMI");
        checkBmiButton.addActionListener(e -> checkBMI());


        // Add action listeners to the buttons
        viewProfileButton.addActionListener(e -> viewProfile());
        viewWorkoutButton.addActionListener(e -> viewWorkoutSchedule());
        viewNutritionButton.addActionListener(e -> viewNutritionPlan());

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewProfileButton);
        buttonPanel.add(viewWorkoutButton);
        buttonPanel.add(viewNutritionButton);

        userProfileFrame.add(buttonPanel, BorderLayout.NORTH);
        userProfileFrame.setVisible(true);
    }
    
    private void viewProfile() {
        // Create a new frame to display user profile
        JFrame profileFrame = new JFrame("User Profile");
        profileFrame.setSize(600, 400);
        profileFrame.setLocationRelativeTo(userProfileFrame);

        try {
            // Fetch the user's profile data from the database
            String query = "SELECT name, email, age, gender, plan FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Create a panel to display the profile data
                JPanel profilePanel = new JPanel(new BorderLayout());
                profilePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                String[] columns = {"Attribute", "Value"};
                Object[][] data = {
                        {"Name", resultSet.getString("name")},
                        {"Email", resultSet.getString("email")},
                        {"Age", resultSet.getString("age")},
                        {"Gender", resultSet.getString("gender")},
                        {"Plan", resultSet.getString("plan")}
                };

                JTable profileTable = new JTable(new DefaultTableModel(data, columns));

                JScrollPane scrollPane = new JScrollPane(profileTable);

                // Create buttons for "Cancel Subscription" and "Upgrade"
                JButton cancelSubscriptionButton = new JButton("Cancel Subscription");
                JButton upgradeButton = new JButton("Upgrade");

                // Add action listeners to the buttons
                cancelSubscriptionButton.addActionListener(e -> cancelSubscription());
                upgradeButton.addActionListener(e -> upgradePlan());

                // Create a panel for the buttons
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(cancelSubscriptionButton);
                buttonPanel.add(upgradeButton);

                profilePanel.add(scrollPane, BorderLayout.CENTER);
                profilePanel.add(buttonPanel, BorderLayout.SOUTH);

                profileFrame.add(profilePanel);
                profileFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(userProfileFrame, "User profile not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(userProfileFrame, "An error occurred while fetching user profile.");
        }
    }
    // ... (previous code)

    private void cancelSubscription() {
        int option = JOptionPane.showConfirmDialog(userProfileFrame, "Are you sure you want to cancel your subscription?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            // Perform cancellation and data deletion
            try {
                // Delete the user's data from the user table
                String deleteUserQuery = "DELETE FROM users WHERE username = ?";
                PreparedStatement deleteStatement = connection.prepareStatement(deleteUserQuery);
                deleteStatement.setString(1, username);
                deleteStatement.executeUpdate();

                // Delete the user's data from the workout_schedule table if it exists
                String deleteWorkoutQuery = "DELETE FROM workout_schedule WHERE username = ?";
                PreparedStatement deleteWorkoutStatement = connection.prepareStatement(deleteWorkoutQuery);
                deleteWorkoutStatement.setString(1, username);
                deleteWorkoutStatement.executeUpdate();

                // Delete the user's data from the nutrition_details table if it exists
                String deleteNutritionQuery = "DELETE FROM nutrition_details WHERE username = ?";
                PreparedStatement deleteNutritionStatement = connection.prepareStatement(deleteNutritionQuery);
                deleteNutritionStatement.setString(1, username);
                deleteNutritionStatement.executeUpdate();
                
                JOptionPane.showMessageDialog(userProfileFrame, "Subscription canceled, and data deleted.");
                userProfileFrame.dispose(); // Close the profile frame after cancellation
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(userProfileFrame, "An error occurred while canceling the subscription.");
            }
        }
    }

// ... (remaining code)


    // ... (previous code)

    private void upgradePlan() {
        int option = JOptionPane.showConfirmDialog(userProfileFrame, "Are you sure you want to upgrade your plan? Additional charges will be applied.", "Confirm Upgrade", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            // Open a dialog to choose the new plan
            String[] plans = {"Basic", "Silver", "Gold", "Platinum"};
            String selectedPlan = (String) JOptionPane.showInputDialog(userProfileFrame, "Choose your new plan:", "Upgrade Plan", JOptionPane.QUESTION_MESSAGE, null, plans, plans[0]);

            if (selectedPlan != null) {
                // Update the user's plan in the database
                try {
                    String updatePlanQuery = "UPDATE users SET plan = ? WHERE username = ?";
                    PreparedStatement updatePlanStatement = connection.prepareStatement(updatePlanQuery);
                    updatePlanStatement.setString(1, selectedPlan);
                    updatePlanStatement.setString(2, username);
                    updatePlanStatement.executeUpdate();

                    JOptionPane.showMessageDialog(userProfileFrame, "Plan upgraded to " + selectedPlan);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(userProfileFrame, "An error occurred while upgrading the plan.");
                }
            }
        }
    }

// ... (remaining code)

    private String getUserPlan() {
        try {
            // Fetch the user's plan from the user table
            String query = "SELECT plan FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("plan");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(userProfileFrame, "An error occurred while fetching user plan.");
        }
        return null;
    }

    private void viewWorkoutSchedule() {
        String userPlan = getUserPlan();
        if (userPlan == null) {
            JOptionPane.showMessageDialog(userProfileFrame, "User plan not found.");
            return;
        }

        if (userPlan.equals("Basic") || userPlan.equals("Silver")) {
            JOptionPane.showMessageDialog(userProfileFrame, "UPGRADE TO GOLD OR PLATINUM PLAN TO AVAIL THESE BENEFITS");
        } else {
            // Create a new frame to display workout schedule
            JFrame workoutFrame = new JFrame("Workout Schedule");
            workoutFrame.setSize(600, 400);
            workoutFrame.setLocationRelativeTo(userProfileFrame);

            try {
                // Fetch the user's workout schedule and goal from the database
                String query = "SELECT mon, tue, wed, thu, fri, sat, sun, goal FROM workout_schedule WHERE username = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Retrieve the goal
                    String goal = resultSet.getString("goal");

                    // Create a panel to display the schedule and goal
                    JPanel workoutPanel = new JPanel(new BorderLayout());
                    workoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                    String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                    String[] columnNames = {"Day", "Schedule"};

                    Object[][] data = new Object[7][2];

                    for (int i = 0; i < 7; i++) {
                        data[i][0] = days[i];
                        data[i][1] = resultSet.getString(i + 1); // Get schedule for the corresponding day
                    }

                    JTable workoutTable = new JTable(new DefaultTableModel(data, columnNames));

                    JScrollPane scrollPane = new JScrollPane(workoutTable);

                    JLabel goalLabel = new JLabel("Goal: " + goal);
                    workoutPanel.add(scrollPane, BorderLayout.CENTER);
                    workoutPanel.add(goalLabel, BorderLayout.SOUTH);

                    workoutFrame.add(workoutPanel);
                    workoutFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(userProfileFrame, "No workout schedule found for the user.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(userProfileFrame, "An error occurred while fetching the workout schedule.");
            }
        }
    }

    private void viewNutritionPlan() {
        String userPlan = getUserPlan();
        if (userPlan == null) {
            JOptionPane.showMessageDialog(userProfileFrame, "User plan not found.");
            return;
        }

        if (userPlan.equals("Basic") || userPlan.equals("Silver")) {
            JOptionPane.showMessageDialog(userProfileFrame, "UPGRADE TO GOLD OR PLATINUM PLAN TO AVAIL THESE BENEFITS");
        } else {
            // Create a new frame to display nutrition plan
            JFrame nutritionFrame = new JFrame("Nutrition Plan");
            nutritionFrame.setSize(600, 400);
            nutritionFrame.setLocationRelativeTo(userProfileFrame);

            try {
                // Fetch the user's nutrition plan and goal from the database
                String query = "SELECT calorie_intake, protein_intake, carbohydrate_intake, trainer_suggestion FROM nutrition_details WHERE username = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Create a panel to display the nutrition plan
                    JPanel nutritionPanel = new JPanel(new BorderLayout());
                    nutritionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                    String[] columns = {"Calorie Intake", "Protein Intake", "Carbohydrate Intake", "Trainer Suggestion"};
                    Object[] data = {
                            resultSet.getString("calorie_intake"),
                            resultSet.getString("protein_intake"),
                            resultSet.getString("carbohydrate_intake"),
                            resultSet.getString("trainer_suggestion")
                    };

                    JTable nutritionTable = new JTable(new DefaultTableModel(new Object[][] { data }, columns));

                    JScrollPane scrollPane = new JScrollPane(nutritionTable);

                    nutritionFrame.add(scrollPane);
                    nutritionFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(userProfileFrame, "No nutrition details found for the user.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(userProfileFrame, "An error occurred while fetching the nutrition details.");
            }
        }
    }

    
    private double calculateBMIWithProcedure(String username, double height, double weight) {
        try {
            CallableStatement callStatement = connection.prepareCall("{CALL CalculateBMI(?, ?, ?)}");
            callStatement.setString(1, username);
            callStatement.setDouble(2, weight);
            callStatement.setDouble(3, height);

            boolean hasResults = callStatement.execute();

            if (hasResults) {
                ResultSet resultSet = callStatement.getResultSet();
                if (resultSet.next()) {
                    String bmiString = resultSet.getString("BMI");

                    int startIndex = bmiString.indexOf("is ") + 3;
                    return Double.parseDouble(bmiString.substring(startIndex));
                }
            }

            JOptionPane.showMessageDialog(userProfileFrame, "BMI calculation failed.");
            return -1.0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(userProfileFrame, "An error occurred while calculating BMI.");
            return -1.0;
        }
    }
    
    
    private void checkBMI() {

        JTextField heightField = new JTextField();
        JTextField weightField = new JTextField();

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Height (in meters):"));
        inputPanel.add(heightField);
        inputPanel.add(new JLabel("Weight (in kg):"));
        inputPanel.add(weightField);

        int result = JOptionPane.showConfirmDialog(
                userProfileFrame,
                inputPanel,
                "Enter Height and Weight for BMI Calculation",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                double height = Double.parseDouble(heightField.getText());
                double weight = Double.parseDouble(weightField.getText());

                double bmi = calculateBMIWithProcedure(username, height, weight);

                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(userProfileFrame, "Please enter valid height and weight.");
            }
        }
    }


    public static void createAndShowProfile(Connection connection, String username) {
        SwingUtilities.invokeLater(() -> new UserProfile(connection, username));
    }

    public static void main(String[] args) {
        Connection connection = null;
        final String username = "testuser"; // Replace with the actual username

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed.");
            System.exit(0);
        }

        createAndShowProfile(connection, username);
    }
}
