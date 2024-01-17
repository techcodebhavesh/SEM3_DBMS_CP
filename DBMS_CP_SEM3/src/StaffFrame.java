import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class StaffFrame {
    private Connection connection;
    private String staffUsername;
    private JFrame memberListFrame;

    public StaffFrame(Connection connection, String staffUsername) {
        this.connection = connection;
        this.staffUsername = staffUsername;

        JFrame staffFrame = new JFrame("Staff Actions");
        staffFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        staffFrame.setSize(800, 600);
        staffFrame.setLocationRelativeTo(null);

        JPanel staffPanel = new JPanel();
        staffPanel.setLayout(new BoxLayout(staffPanel, BoxLayout.Y_AXIS));

        JLabel staffLabel = new JLabel("Welcome, Staff Member!");
        staffLabel.setFont(new Font("Arial", Font.BOLD, 24));
        staffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        staffPanel.add(staffLabel);

        JButton checkMembersButton = new JButton("Check Members Allotted for Training");
        checkMembersButton.setFont(new Font("Arial", Font.PLAIN, 18));
        checkMembersButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkMembersButton.addActionListener(e -> showAllottedMembers());
        staffPanel.add(checkMembersButton);

        staffFrame.add(staffPanel);
        staffFrame.setVisible(true);
    }

    private void showAllottedMembers() {
        try {
            String query = "SELECT username, name FROM users WHERE current_trainer = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, staffUsername);
            ResultSet resultSet = preparedStatement.executeQuery();

            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Username", "Name"}, 0);

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String name = resultSet.getString("name");
                tableModel.addRow(new Object[]{username, name});
            }

            JTable memberTable = new JTable(tableModel);

            if (memberListFrame == null) {
                memberListFrame = new JFrame("Allotted Members");
                memberListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                memberListFrame.setSize(400, 300);
                memberListFrame.setLocationRelativeTo(null);
            }

            memberListFrame.getContentPane().removeAll();

            memberTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        int selectedRow = memberTable.getSelectedRow();
                        String selectedUsername = memberTable.getValueAt(selectedRow, 0).toString();
                        showMemberDetails(selectedUsername);
                    }
                }
            });

            memberListFrame.add(new JScrollPane(memberTable));
            memberListFrame.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while fetching allotted members.");
        }
    }

    private void showMemberDetails(String username) {
        JFrame memberDetailsFrame = new JFrame("Member Details: " + username);
        memberDetailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        memberDetailsFrame.setSize(600, 400);
        memberDetailsFrame.setLocationRelativeTo(null);

        JPanel memberDetailsPanel = new JPanel();
        memberDetailsPanel.setLayout(new BoxLayout(memberDetailsPanel, BoxLayout.Y_AXIS));

        JButton viewWorkoutButton = new JButton("View Workout Schedule");
        JButton viewNutritionButton = new JButton("View Nutrition Schedule");
        JButton updateWorkoutButton = new JButton("Update Workout Schedule");
        JButton updateNutritionButton = new JButton("Update Nutrition Schedule");

        viewWorkoutButton.addActionListener(e -> {
            String workoutScheduleQuery = "SELECT mon, tue, wed, thu, fri, sat, sun FROM workout_schedule WHERE username = ?";
            try {
                PreparedStatement workoutScheduleStatement = connection.prepareStatement(workoutScheduleQuery);
                workoutScheduleStatement.setString(1, username);
                ResultSet scheduleResultSet = workoutScheduleStatement.executeQuery();

                if (scheduleResultSet.next()) {
                    JFrame workoutScheduleFrame = new JFrame("Workout Schedule: " + username);
                    workoutScheduleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    workoutScheduleFrame.setSize(600, 400);
                    workoutScheduleFrame.setLocationRelativeTo(null);

                    JPanel workoutSchedulePanel = new JPanel();
                    workoutSchedulePanel.setLayout(new BoxLayout(workoutSchedulePanel, BoxLayout.Y_AXIS));

                    DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}, 0);

                    Vector<Object> rowData = new Vector<>();
                    for (int i = 1; i <= 7; i++) {
                        rowData.add(scheduleResultSet.getString(i));
                    }
                    tableModel.addRow(rowData);

                    JTable workoutTable = new JTable(tableModel);
                    workoutTable.setPreferredScrollableViewportSize(new Dimension(500, 200));
                    workoutTable.setFillsViewportHeight(true);

                    workoutSchedulePanel.add(new JScrollPane(workoutTable));
                    workoutScheduleFrame.add(workoutSchedulePanel);
                    workoutScheduleFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Workout schedule not found for " + username);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred while fetching workout schedule.");
            }
        });

        // Add other action listeners for viewNutritionButton, updateWorkoutButton, and updateNutritionButton here
        // Add action listener to the "Update Workout Schedule" button
        // Create a button to update workout schedule

// Add action listener to update workout schedule
        updateWorkoutButton.addActionListener(e -> {
            // Create a dialog for staff to input the updated workout schedule
            JDialog updateWorkoutDialog = new JDialog();
            updateWorkoutDialog.setTitle("Update Workout Schedule");
            updateWorkoutDialog.setSize(400, 300);
            updateWorkoutDialog.setLocationRelativeTo(null);
            updateWorkoutDialog.setLayout(new BorderLayout());

            // Create a panel for the input fields
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new GridLayout(7, 2));

            // Create text fields for each day's workout
            JTextField monField = new JTextField(10);
            JTextField tueField = new JTextField(10);
            JTextField wedField = new JTextField(10);
            JTextField thuField = new JTextField(10);
            JTextField friField = new JTextField(10);
            JTextField satField = new JTextField(10);
            JTextField sunField = new JTextField(10);

            // Add labels and text fields for each day
            inputPanel.add(new JLabel("Monday:"));
            inputPanel.add(monField);
            inputPanel.add(new JLabel("Tuesday:"));
            inputPanel.add(tueField);
            inputPanel.add(new JLabel("Wednesday:"));
            inputPanel.add(wedField);
            inputPanel.add(new JLabel("Thursday:"));
            inputPanel.add(thuField);
            inputPanel.add(new JLabel("Friday:"));
            inputPanel.add(friField);
            inputPanel.add(new JLabel("Saturday:"));
            inputPanel.add(satField);
            inputPanel.add(new JLabel("Sunday:"));
            inputPanel.add(sunField);

            // Create a panel for the save button
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            // Create a button to save the updated workout schedule
            JButton saveButton = new JButton("Save");

            // Add the save button to the button panel
            buttonPanel.add(saveButton);

            // Add action listener to the save button
            saveButton.addActionListener(saveEvent -> {
                // Check if the record already exists
                String selectQuery = "SELECT * FROM workout_schedule WHERE username=?";
                try {
                    PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                    selectStatement.setString(1, username);
                    ResultSet resultSet = selectStatement.executeQuery();

                    if (resultSet.next()) {
                        // The record exists, so update it
                        String updateWorkoutQuery = "UPDATE workout_schedule SET mon=?, tue=?, wed=?, thu=?, fri=?, sat=?, sun=? WHERE username=?";
                        PreparedStatement updateWorkoutStatement = connection.prepareStatement(updateWorkoutQuery);
                        updateWorkoutStatement.setString(1, monField.getText());
                        updateWorkoutStatement.setString(2, tueField.getText());
                        updateWorkoutStatement.setString(3, wedField.getText());
                        updateWorkoutStatement.setString(4, thuField.getText());
                        updateWorkoutStatement.setString(5, friField.getText());
                        updateWorkoutStatement.setString(6, satField.getText());
                        updateWorkoutStatement.setString(7, sunField.getText());
                        updateWorkoutStatement.setString(8, username);

                        int rowsUpdated = updateWorkoutStatement.executeUpdate();

                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(null, "Workout schedule updated successfully for " + username);
                            updateWorkoutDialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to update workout schedule for " + username);
                        }
                    } else {
                        // The record does not exist, so insert a new record
                        String insertWorkoutQuery = "INSERT INTO workout_schedule (username, mon, tue, wed, thu, fri, sat, sun) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement insertWorkoutStatement = connection.prepareStatement(insertWorkoutQuery);
                        insertWorkoutStatement.setString(1, username);
                        insertWorkoutStatement.setString(2, monField.getText());
                        insertWorkoutStatement.setString(3, tueField.getText());
                        insertWorkoutStatement.setString(4, wedField.getText());
                        insertWorkoutStatement.setString(5, thuField.getText());
                        insertWorkoutStatement.setString(6, friField.getText());
                        insertWorkoutStatement.setString(7, satField.getText());
                        insertWorkoutStatement.setString(8, sunField.getText());

                        int rowsInserted = insertWorkoutStatement.executeUpdate();

                        if (rowsInserted > 0) {
                            JOptionPane.showMessageDialog(null, "Workout schedule inserted successfully for " + username);
                            updateWorkoutDialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to insert workout schedule for " + username);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An error occurred while updating/inserting workout schedule.");
                }
            });

            // Add the input panel and button panel to the dialog
            updateWorkoutDialog.add(inputPanel, BorderLayout.CENTER);
            updateWorkoutDialog.add(buttonPanel, BorderLayout.SOUTH);

            // Make the dialog visible
            updateWorkoutDialog.setVisible(true);
        });

// Create a button to view nutrition details


// Add action listener to view nutrition details
        viewNutritionButton.addActionListener(e -> {
            // Fetch the nutrition details for the selected member
            String nutritionQuery = "SELECT calorie_intake, protein_intake, carbohydrate_intake, trainer_suggestion FROM nutrition_details WHERE username = ?";
            try {
                PreparedStatement nutritionStatement = connection.prepareStatement(nutritionQuery);
                nutritionStatement.setString(1, username);

                ResultSet nutritionResultSet = nutritionStatement.executeQuery();

                if (nutritionResultSet.next()) {
                    // Create a dialog to display the nutrition details
                    JDialog nutritionDetailsDialog = new JDialog();
                    nutritionDetailsDialog.setTitle("Nutrition Details ");
                    nutritionDetailsDialog.setSize(400, 300);
                    nutritionDetailsDialog.setLocationRelativeTo(null);
                    nutritionDetailsDialog.setLayout(new GridLayout(4, 2));

                    // Create labels and text fields for nutrition details
                    JLabel calorieLabel = new JLabel("Calorie Intake:");
                    JTextField calorieField = new JTextField(10);
                    calorieField.setText(nutritionResultSet.getString("calorie_intake"));

                    JLabel proteinLabel = new JLabel("Protein Intake:");
                    JTextField proteinField = new JTextField(10);
                    proteinField.setText(nutritionResultSet.getString("protein_intake"));

                    JLabel carbLabel = new JLabel("Carbohydrate Intake:");
                    JTextField carbField = new JTextField(10);
                    carbField.setText(nutritionResultSet.getString("carbohydrate_intake"));

                    JLabel trainingLabel = new JLabel("Training Suggestion:");
                    JTextField trainingField = new JTextField(10);
                    trainingField.setText(nutritionResultSet.getString("trainer_suggestion"));

                    // Add labels and text fields to the dialog
                    nutritionDetailsDialog.add(calorieLabel);
                    nutritionDetailsDialog.add(calorieField);
                    nutritionDetailsDialog.add(proteinLabel);
                    nutritionDetailsDialog.add(proteinField);
                    nutritionDetailsDialog.add(carbLabel);
                    nutritionDetailsDialog.add(carbField);
                    nutritionDetailsDialog.add(trainingLabel);
                    nutritionDetailsDialog.add(trainingField);

                    // Make the dialog visible
                    nutritionDetailsDialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Nutrition details not found ");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred while fetching nutrition details.");
            }
        });

        // Create a button to update nutrition details


// Add action listener to update nutrition details



// Add action listener to update nutrition details
        updateNutritionButton.addActionListener(e -> {
            // Create a dialog for staff to input the updated nutrition details
            JDialog updateNutritionDialog = new JDialog();
            updateNutritionDialog.setTitle("Update Nutrition Details");
            updateNutritionDialog.setSize(400, 300);
            updateNutritionDialog.setLocationRelativeTo(null);
            updateNutritionDialog.setLayout(new BorderLayout());

            // Create a panel for the input fields
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new GridLayout(4, 2));

            // Create text fields for nutrition details
            JTextField calorieField = new JTextField(10);
            JTextField proteinField = new JTextField(10);
            JTextField carbField = new JTextField(10);
            JTextField trainingField = new JTextField(10);

            // Add labels and text fields to the input panel
            inputPanel.add(new JLabel("Calorie Intake:"));
            inputPanel.add(calorieField);
            inputPanel.add(new JLabel("Protein Intake:"));
            inputPanel.add(proteinField);
            inputPanel.add(new JLabel("Carbohydrate Intake:"));
            inputPanel.add(carbField);
            inputPanel.add(new JLabel("Trainer Suggestion:"));
            inputPanel.add(trainingField);

            // Create a panel for the save button
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            // Create a button to save the updated nutrition details
            JButton saveButton = new JButton("Save");

            // Add the save button to the button panel
            buttonPanel.add(saveButton);

            // Add action listener to the save button
            saveButton.addActionListener(saveEvent -> {
                // Check if the record already exists
                String selectQuery = "SELECT * FROM nutrition_details WHERE username=?";
                try {
                    PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                    selectStatement.setString(1, username);
                    ResultSet resultSet = selectStatement.executeQuery();

                    if (resultSet.next()) {
                        // The record exists, so update it
                        String updateNutritionQuery = "UPDATE nutrition_details SET calorie_intake=?, protein_intake=?, carbohydrate_intake=?, trainer_suggestion=? WHERE username=?";
                        PreparedStatement updateNutritionStatement = connection.prepareStatement(updateNutritionQuery);
                        updateNutritionStatement.setString(1, calorieField.getText());
                        updateNutritionStatement.setString(2, proteinField.getText());
                        updateNutritionStatement.setString(3, carbField.getText());
                        updateNutritionStatement.setString(4, trainingField.getText());
                        updateNutritionStatement.setString(5, username);

                        int rowsUpdated = updateNutritionStatement.executeUpdate();

                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(null, "Nutrition details updated successfully for " + username);
                            updateNutritionDialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to update nutrition details for " + username);
                        }
                    } else {
                        // The record does not exist, so insert a new record
                        String insertNutritionQuery = "INSERT INTO nutrition_details (username, calorie_intake, protein_intake, carbohydrate_intake, trainer_suggestion) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement insertNutritionStatement = connection.prepareStatement(insertNutritionQuery);
                        insertNutritionStatement.setString(1, username);
                        insertNutritionStatement.setString(2, calorieField.getText());
                        insertNutritionStatement.setString(3, proteinField.getText());
                        insertNutritionStatement.setString(4, carbField.getText());
                        insertNutritionStatement.setString(5, trainingField.getText());

                        int rowsInserted = insertNutritionStatement.executeUpdate();

                        if (rowsInserted > 0) {
                            JOptionPane.showMessageDialog(null, "Nutrition details inserted successfully for " + username);
                            updateNutritionDialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to insert nutrition details for " + username);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An error occurred while updating/inserting nutrition details.");
                }
            });

            // Add the input panel and button panel to the dialog
            updateNutritionDialog.add(inputPanel, BorderLayout.CENTER);
            updateNutritionDialog.add(buttonPanel, BorderLayout.SOUTH);

            // Make the dialog visible
            updateNutritionDialog.setVisible(true);
        });



// Add the view nutrition button to the member details panel
        memberDetailsPanel.add(viewNutritionButton);


        memberDetailsPanel.add(viewWorkoutButton);
        memberDetailsPanel.add(viewNutritionButton);
        memberDetailsPanel.add(updateWorkoutButton);
        memberDetailsPanel.add(updateNutritionButton);
        memberDetailsFrame.add(memberDetailsPanel);
        memberDetailsFrame.setVisible(true);

    }


}
