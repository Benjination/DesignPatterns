import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LandingPage extends JFrame {
    private JList<String> savedPlansList;
    private DefaultListModel<String> listModel;

    public LandingPage() {
        setTitle("Floor Plan Manager");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        JButton newPlanButton = new JButton("Create New Floor Plan");
        JButton loadPlanButton = new JButton("Load Selected Floor Plan");
        JButton deletePlanButton = new JButton("Delete Selected Floor Plan");

        newPlanButton.addActionListener(e -> createNewFloorPlan());
        loadPlanButton.addActionListener(e -> loadSelectedFloorPlan());
        deletePlanButton.addActionListener(e -> deleteSelectedFloorPlan());

        buttonPanel.add(newPlanButton);
        buttonPanel.add(loadPlanButton);
        buttonPanel.add(deletePlanButton);

        listModel = new DefaultListModel<>();
        savedPlansList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(savedPlansList);
        add(listScrollPane, BorderLayout.CENTER);

        add(buttonPanel, BorderLayout.NORTH);
        add(listScrollPane, BorderLayout.CENTER);

        loadSavedPlans();
    }

    private void createNewFloorPlan() {
        SwingUtilities.invokeLater(() -> {
            BoothFloorPlan frame = new BoothFloorPlan();
            frame.setVisible(true);
        });
        this.dispose();
    }

    private void loadSelectedFloorPlan() {
        String selectedPlan = savedPlansList.getSelectedValue();
        if (selectedPlan != null) {
            SwingUtilities.invokeLater(() -> {
                BoothFloorPlan frame = new BoothFloorPlan(selectedPlan);
                frame.setVisible(true);
            });
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a floor plan to load.", "No Plan Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadSavedPlans() {
        listModel.clear();
        File folder = new File("saved_plans");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".ser"));
            if (files != null) {
                for (File file : files) {
                    listModel.addElement(file.getName().replace(".ser", ""));
                }
            }
        }
    }

    private void deleteSelectedFloorPlan() {
        String selectedPlan = savedPlansList.getSelectedValue();
        if (selectedPlan != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the floor plan '" + selectedPlan + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                File fileToDelete = new File("saved_plans/" + selectedPlan + ".ser");
                if (fileToDelete.delete()) {
                    listModel.removeElement(selectedPlan);
                    JOptionPane.showMessageDialog(this, "Floor plan deleted successfully.", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting floor plan.", "Deletion Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a floor plan to delete.", "No Plan Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LandingPage landingPage = new LandingPage();
            landingPage.setVisible(true);
        });
    }
}