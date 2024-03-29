import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminFrame {
    private JFrame adminFrame;
    private Connection connection;
    private JFrame staffRemovalFrame;
    private JButton removeButton;


    public AdminFrame(Connection connection) {
        this.connection = connection;
        initialize();
    }

    private void initialize() {
        adminFrame = new JFrame("Admin Dashboard");
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminFrame.setSize(400, 300);
        adminFrame.setLocationRelativeTo(null);

        JPanel adminPanel = new JPanel(new GridLayout(6, 1, 10, 10));

        JButton registerStaffButton = new JButton("Register Staff");
        JButton allotTrainersButton = new JButton("Allot Trainers");
        JButton viewMembersButton = new JButton("View Gym Members");
        JButton viewStaffButton = new JButton("View Staff");
        JButton removeStaffButton = new JButton("Remove Staff"); // Add the "Remove Staff" button

        adminPanel.add(registerStaffButton);
        adminPanel.add(allotTrainersButton);
        adminPanel.add(viewMembersButton);
        adminPanel.add(viewStaffButton);
        adminPanel.add(removeStaffButton); // Add the "Remove Staff" button

        adminFrame.add(adminPanel);

        // Register Staff Button Action
        registerStaffButton.addActionListener(e -> registerStaff());

        // Allot Trainers Button Action
        allotTrainersButton.addActionListener(e -> allotTrainers());

        // View Members Button Action
        viewMembersButton.addActionListener(e -> fetchGymMembers());

        // View Staff Button Action
        viewStaffButton.addActionListener(e -> fetchStaffData());

        // Remove Staff Button Action
        removeStaffButton.addActionListener(e -> removeStaff());
        // Add "View Member-Trainer Allotment" button
        JButton viewMemberTrainerAllotmentButton = new JButton("View Member-Trainer Allotment");
        adminPanel.add(viewMemberTrainerAllotmentButton);

// Add an action for the new button
        viewMemberTrainerAllotmentButton.addActionListener(e -> viewMemberTrainerAllotment());
// Add the action for the "Remove Staff" button

        adminFrame.setVisible(true);
    }
    private void registerStaff() {
        JFrame staffRegistrationFrame = new JFrame("Register Staff");
        staffRegistrationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        staffRegistrationFrame.setSize(400, 300);
        staffRegistrationFrame.setLocationRelativeTo(null);

        JPanel staffRegistrationPanel = new JPanel(new GridLayout(8, 2, 10, 10));

        JTextField staffNameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField ageField = new JTextField(5);
        JRadioButton maleRadio = new JRadioButton("Male");
        JRadioButton femaleRadio = new JRadioButton("Female");
        JTextField contactField = new JTextField(20);

        JComboBox<String> jobRoleDropdown = new JComboBox<>(new String[]{"Trainer", "Non-Trainer"});

        JButton registerButton = new JButton("Register");

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);

        staffRegistrationPanel.add(new JLabel("Staff Name:"));
        staffRegistrationPanel.add(staffNameField);
        staffRegistrationPanel.add(new JLabel("Email:"));
        staffRegistrationPanel.add(emailField);
        staffRegistrationPanel.add(new JLabel("Age:"));
        staffRegistrationPanel.add(ageField);
        staffRegistrationPanel.add(new JLabel("Gender:"));
        staffRegistrationPanel.add(maleRadio);
        staffRegistrationPanel.add(new JLabel());
        staffRegistrationPanel.add(femaleRadio);
        staffRegistrationPanel.add(new JLabel("Contact:"));
        staffRegistrationPanel.add(contactField);
        staffRegistrationPanel.add(new JLabel("Job Role:"));
        staffRegistrationPanel.add(jobRoleDropdown);
        staffRegistrationPanel.add(new JLabel()); // Empty label for spacing
        staffRegistrationPanel.add(registerButton);

        registerButton.addActionListener(e -> {
            String staffName = staffNameField.getText();
            String email = emailField.getText();
            int age = Integer.parseInt(ageField.getText());
            String gender = maleRadio.isSelected() ? "Male" : "Female";
            String contact = contactField.getText();
            String jobRole = (String) jobRoleDropdown.getSelectedItem();

            try {
                String query = "INSERT INTO staff_details (staff_name, email, age, gender, contact, jobRole) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, staffName);
                preparedStatement.setString(2, email);
                preparedStatement.setInt(3, age);
                preparedStatement.setString(4, gender);
                preparedStatement.setString(5, contact);
                preparedStatement.setString(6, jobRole);

                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "Staff registration successful.");
                    staffRegistrationFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Staff registration failed. Please try again.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred during staff registration.");
            }
        });

        staffRegistrationFrame.add(staffRegistrationPanel);
        staffRegistrationFrame.setVisible(true);
    }

    private void allotTrainers() {
        JFrame trainerAllotmentFrame = new JFrame("Allot Trainers");
        trainerAllotmentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        trainerAllotmentFrame.setSize(600, 400);
        trainerAllotmentFrame.setLocationRelativeTo(null);

        JPanel trainerAllotmentPanel = new JPanel(new BorderLayout());

        try {
            String query = "SELECT name, age, plan, gender, current_trainer FROM users WHERE plan = 'Gold' OR plan = 'Platinum'";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create a table model with the fetched data
            DefaultTableModel tableModel = new DefaultTableModel();
            JTable userTable = new JTable(tableModel);

            // Add columns to the table
            tableModel.addColumn("Name");
            tableModel.addColumn("Age");
            tableModel.addColumn("Plan");
            tableModel.addColumn("Gender");
            tableModel.addColumn("Current Trainer"); // Add the current_trainer column

            // Populate the table with data
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String plan = resultSet.getString("plan");
                String gender = resultSet.getString("gender");
                String currentTrainer = resultSet.getString("current_trainer"); // Retrieve current trainer

                tableModel.addRow(new Object[]{name, age, plan, gender, currentTrainer});
            }

            JScrollPane scrollPane = new JScrollPane(userTable);

            JPanel buttonPanel = new JPanel();
            JButton updateTrainerButton = new JButton("Update Trainer"); // Add the button
            buttonPanel.add(updateTrainerButton);

            trainerAllotmentPanel.add(scrollPane, BorderLayout.CENTER);
            trainerAllotmentPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Add an action listener to the "Update Trainer" button
            updateTrainerButton.addActionListener(e -> {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow >= 0) {
                    // Retrieve data for the selected user
                    String name = (String) tableModel.getValueAt(selectedRow, 0);

                    // Now, you can show a dialog or dropdown for selecting the trainer
                    showTrainerSelectionDialog(name);
                } else {
                    JOptionPane.showMessageDialog(trainerAllotmentFrame, "Please select a user to update their trainer.");
                }
            });

            trainerAllotmentFrame.add(trainerAllotmentPanel);
            trainerAllotmentFrame.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while fetching user data.");
        }
    }
    private void showTrainerSelectionDialog(String name) {
        JFrame trainerSelectionFrame = new JFrame("Select Trainer");
        trainerSelectionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        trainerSelectionFrame.setSize(400, 200);
        trainerSelectionFrame.setLocationRelativeTo(null);

        JPanel trainerSelectionPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JComboBox<String> trainerDropdown = new JComboBox<>(); // Populate this dropdown with trainers from staff_details

        // Query the database to populate the trainer dropdown with staff members who are trainers
        try {
            String trainerQuery = "SELECT staff_name FROM staff_details WHERE jobRole = 'Trainer'";
            PreparedStatement trainerStatement = connection.prepareStatement(trainerQuery);
            ResultSet trainerResultSet = trainerStatement.executeQuery();

            while (trainerResultSet.next()) {
                String trainerName = trainerResultSet.getString("staff_name");
                trainerDropdown.addItem(trainerName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while fetching trainers.");
        }

        JButton selectTrainerButton = new JButton("Select Trainer");

        trainerSelectionPanel.add(new JLabel("Select Trainer:"));
        trainerSelectionPanel.add(trainerDropdown);
        trainerSelectionPanel.add(new JLabel()); // Empty label for spacing
        trainerSelectionPanel.add(selectTrainerButton);

        selectTrainerButton.addActionListener(e -> {
            String selectedTrainer = (String) trainerDropdown.getSelectedItem();
            if (selectedTrainer != null) {
                // Update the user's trainer in the database
                updateTrainerForUser(name, selectedTrainer);
                JOptionPane.showMessageDialog(null, "Trainer updated successfully.");
                trainerSelectionFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Please select a trainer.");
            }
        });

        trainerSelectionFrame.add(trainerSelectionPanel);
        trainerSelectionFrame.setVisible(true);
    }

    private void updateTrainerForUser(String userName, String selectedTrainer) {
        try {
            String updateQuery = "UPDATE users SET current_trainer = ? WHERE name = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, selectedTrainer);
            updateStatement.setString(2, userName);

            int rowsUpdated = updateStatement.executeUpdate();

            if (rowsUpdated > 0) {
                // Trainer updated successfully
            } else {
                // Update failed
                JOptionPane.showMessageDialog(null, "Failed to update the trainer.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while updating the trainer.");
        }
    }


    private void fetchGymMembers() {
        try {
            String query = "SELECT users.username, users.name, users.age, users.gender, users.plan, gym_plans.price " +
                    "FROM users " +
                    "INNER JOIN gym_plans ON users.plan = gym_plans.plan";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create a JTable to display the data
            JTable memberTable = new JTable(buildTableModel(resultSet));

            // Create a scroll pane for the table
            JScrollPane scrollPane = new JScrollPane(memberTable);

            // Create a new frame to display the table
            JFrame tableFrame = new JFrame("Gym Members");
            tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            tableFrame.add(scrollPane);
            tableFrame.pack();
            tableFrame.setLocationRelativeTo(null);
            tableFrame.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while fetching gym member data.");
        }
    }

    private void removeStaff() {
        JFrame staffRemovalFrame = new JFrame("Remove Staff");
        staffRemovalFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        staffRemovalFrame.setSize(600, 400);
        staffRemovalFrame.setLocationRelativeTo(null);

        JPanel staffRemovalPanel = new JPanel(new BorderLayout());

        try {
            String query = "SELECT staff_name, age, email, jobRole FROM staff_details";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create a table model with an additional column for buttons
            DefaultTableModel tableModel = new DefaultTableModel();
            JTable staffTable = new JTable(tableModel);

            // Add columns to the table
            tableModel.addColumn("Staff Name");
            tableModel.addColumn("Age");
            tableModel.addColumn("Email");
            tableModel.addColumn("Job Role");
            tableModel.addColumn("Remove"); // Add a column for "Remove" buttons

            // Populate the table with data and null values for the "Remove" column
            while (resultSet.next()) {
                String staffName = resultSet.getString("staff_name");
                int age = resultSet.getInt("age");
                String email = resultSet.getString("email");
                String jobRole = resultSet.getString("jobRole");

                tableModel.addRow(new Object[]{staffName, age, email, jobRole, null}); // Add null for the "Remove" column
            }

            JScrollPane scrollPane = new JScrollPane(staffTable);

            staffRemovalPanel.add(scrollPane, BorderLayout.CENTER);
            removeButton = new JButton("Remove");

            // Add an action listener to the table to show the "Remove" button for the selected staff
            staffTable.getSelectionModel().addListSelectionListener(e -> {
                int selectedRow = staffTable.getSelectedRow();
                if (selectedRow >= 0) {
                    staffRemovalPanel.add(removeButton, BorderLayout.SOUTH); // Add "Remove" button at the bottom of the frame
                    staffRemovalFrame.validate();
                    staffRemovalFrame.repaint();
                }
            });
            removeButton.addActionListener(e -> {
                int option = JOptionPane.showConfirmDialog(staffRemovalFrame, "Are you sure you want to remove this staff member?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    int selectedRow = staffTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        String staffName = (String) staffTable.getValueAt(selectedRow, 0);
                        removeStaffMember(staffName);
                        ((DefaultTableModel) staffTable.getModel()).removeRow(selectedRow);
                    }
                }
            });


            staffRemovalFrame.add(staffRemovalPanel);
            staffRemovalFrame.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while fetching staff data for removal.");
        }
    }




    private void removeStaffMember(String staffName) {
        try {
            // Remove the staff member's data from the staff_details table
            String removeQuery = "DELETE FROM staff_details WHERE staff_name = ?";
            PreparedStatement removeStatement = connection.prepareStatement(removeQuery);
            removeStatement.setString(1, staffName);
            removeStatement.executeUpdate();

            // Set the current_trainer to null in the users table where this staff member's name exists
            String updateUsersQuery = "UPDATE users SET current_trainer = null WHERE current_trainer = ?";
            PreparedStatement updateUsersStatement = connection.prepareStatement(updateUsersQuery);
            updateUsersStatement.setString(1, staffName);
            updateUsersStatement.executeUpdate();

            JOptionPane.showMessageDialog(null, "Staff member removed successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while removing the staff member.");
        }
    }
    private void fetchStaffData() {
        try {
            String query = "SELECT staff_name, age, gender, contact, email, jobRole FROM staff_details";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create a JTable to display the data
            JTable staffTable = new JTable(buildTableModel(resultSet));

            // Create a scroll pane for the table
            JScrollPane scrollPane = new JScrollPane(staffTable);

            // Create a new frame to display the staff data
            JFrame staffFrame = new JFrame("Staff Data");
            staffFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            staffFrame.add(scrollPane);
            staffFrame.pack();
            staffFrame.setLocationRelativeTo(null);
            staffFrame.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while fetching staff data.");
        }
    }

    // Helper method to build a table model from a ResultSet
    public static DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {
        java.sql.ResultSetMetaData metaData = resultSet.getMetaData();

        // Create the column names based on the ResultSet metadata
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int column = 1; column <= columnCount; column++) {
            columnNames[column - 1] = metaData.getColumnName(column);
        }

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        // Populate the table model with data from the ResultSet
        while (resultSet.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = resultSet.getObject(i);
            }
            tableModel.addRow(row);
        }

        return tableModel;
    }

    private void viewMemberTrainerAllotment() {
        try {
            // Fetch data from the "member_trainer_allotment" view
            String query = "SELECT name, current_trainer FROM member_trainer_allotment";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create a JTable to display the data
            JTable memberTrainerTable = new JTable(buildTableModel(resultSet));

            // Create a scroll pane for the table
            JScrollPane scrollPane = new JScrollPane(memberTrainerTable);

            // Create a new frame to display the member-trainer allotment data
            JFrame memberTrainerFrame = new JFrame("Member-Trainer Allotment");
            memberTrainerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            memberTrainerFrame.add(scrollPane);
            memberTrainerFrame.pack();
            memberTrainerFrame.setLocationRelativeTo(null);
            memberTrainerFrame.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while fetching member-trainer allotment data.");
        }
    }

}
