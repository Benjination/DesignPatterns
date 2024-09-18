import javax.swing.border.Border;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class BoothFloorPlan extends JFrame {
    private JPanel mainPanel;
    private JPanel shapePanel;
    private FloorPlanController controller;
    private String planName;

    public BoothFloorPlan() {
        this(null);
    }

    public BoothFloorPlan(String planName) {
        this.planName = planName;
        controller = new FloorPlanController();

        setTitle("Booth Floor Plan" + (planName != null ? " - " + planName : ""));
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setupMenuBar();
        setupShapePanel();
        setupMainPanel();
        
        addShapeButtons();
        addClearButton();
        addSaveButton();
        addReturnToLandingPageButton();

        if (planName != null) {
            loadFloorPlan(planName);
        }

    
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");
        saveItem.addActionListener(e -> saveFloorPlan());
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void setupShapePanel() {
        shapePanel = new JPanel();
        shapePanel.setPreferredSize(new Dimension(150, 400));
        shapePanel.setLayout(new BoxLayout(shapePanel, BoxLayout.Y_AXIS));
        add(shapePanel, BorderLayout.WEST);
        Border shapePanelBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5));
        shapePanel.setBorder(shapePanelBorder);
    }

    private void setupMainPanel() {
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                controller.drawFloorPlan(g);
            }
        };
        Border mainPanelBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setBorder(mainPanelBorder);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void addShapeButtons() {
        addShapeButton("Large", 100, 60, Color.BLUE);
        addShapeButton("Medium", 50, 30, Color.GREEN);
        addShapeButton("Small", 30, 30, new Color(128, 0, 128));
    }

    private void addShapeButton(String name, int width, int height, Color color) {
        JButton button = new JButton(name);
        button.addActionListener(e -> {
            boolean added = controller.addShape(width, height, color, mainPanel.getWidth(), mainPanel.getHeight());
            if (!added) {
                JOptionPane.showMessageDialog(this, "No space available for new shape", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            mainPanel.repaint();
        });
        shapePanel.add(button);
    }

    private void addClearButton() {
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            controller.clearFloorPlan();
            mainPanel.repaint();
        });
        shapePanel.add(Box.createVerticalStrut(20));
        shapePanel.add(clearButton);
    }

    private void addSaveButton() {
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveFloorPlan());
        shapePanel.add(Box.createVerticalStrut(20));
        shapePanel.add(saveButton);
    }

    private void saveFloorPlan() {
        if (planName == null) {
            planName = JOptionPane.showInputDialog(this, "Enter a name for the floor plan:");
        }
        if (planName != null && !planName.isEmpty()) {
            try {
                File folder = new File("saved_plans");
                if (!folder.exists()) {
                    folder.mkdir();
                }
                FileOutputStream fileOut = new FileOutputStream("saved_plans/" + planName + ".ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(controller.getFloorPlan());
                out.close();
                fileOut.close();
                JOptionPane.showMessageDialog(this, "Floor plan saved successfully.", "Save Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException i) {
                JOptionPane.showMessageDialog(this, "Error saving floor plan.", "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addReturnToLandingPageButton() {
        JButton returnButton = new JButton("Home");
        returnButton.addActionListener(e -> promptSaveAndReturn());
        shapePanel.add(Box.createVerticalStrut(20));
        shapePanel.add(returnButton);
    }

    private void returnToLandingPage() {
        SwingUtilities.invokeLater(() -> {
            LandingPage landingPage = new LandingPage();
            landingPage.setVisible(true);
            this.dispose(); // Close the current BoothFloorPlan window
        });
    }

    private void promptSaveAndReturn() {
        int option = JOptionPane.showConfirmDialog(this,
                "Do you want to save the current floor plan before returning?",
                "Save Floor Plan",
                JOptionPane.YES_NO_CANCEL_OPTION);
    
        if (option == JOptionPane.YES_OPTION) {
            saveFloorPlan();
            returnToLandingPage();
        } else if (option == JOptionPane.NO_OPTION) {
            returnToLandingPage();
        }
        // If CANCEL_OPTION, do nothing and stay on the current page
    }

    private void loadFloorPlan(String planName) {
        try {
            FileInputStream fileIn = new FileInputStream("saved_plans/" + planName + ".ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            FloorPlan loadedPlan = (FloorPlan) in.readObject();
            in.close();
            fileIn.close();
            controller.setFloorPlan(loadedPlan);
            mainPanel.repaint();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading floor plan.", "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LandingPage landingPage = new LandingPage();
            landingPage.setVisible(true);
        });
    }
}

// Controller Pattern
class FloorPlanController {
    private FloorPlan floorPlan;
    private RectangleFactory rectangleFactory;
    private Random random;

    public FloorPlanController() {
        this.floorPlan = new FloorPlan();
        this.rectangleFactory = new RectangleFactory();
        this.random = new Random();
    }

    public boolean addShape(int width, int height, Color color, int maxWidth, int maxHeight) {
        CustomRectangle newShape = generateNonOverlappingShape(width, height, color, maxWidth, maxHeight);
        if (newShape != null) {
            floorPlan.add(newShape);
            return true;
        }
        return false;
    }

    public void clearFloorPlan() {
        floorPlan.clear();
    }

    public void drawFloorPlan(Graphics g) {
        floorPlan.draw(g);
    }

    private CustomRectangle generateNonOverlappingShape(int width, int height, Color color, int maxWidth, int maxHeight) {
        int maxAttempts = 100;
        for (int i = 0; i < maxAttempts; i++) {
            int x = random.nextInt(maxWidth - width + 1);
            int y = random.nextInt(maxHeight - height + 1);
            CustomRectangle newShape = rectangleFactory.getRectangle(width, height, color);
            newShape.setPosition(x, y);
            if (!floorPlan.overlapsWithExistingShapes(newShape)) {
                return newShape;
            }
        }
        return null;
    }

    public FloorPlan getFloorPlan() {
        return floorPlan;
    }

    public void setFloorPlan(FloorPlan floorPlan) {
        this.floorPlan = floorPlan;
    }
}

// Expert Pattern
interface Shape extends Iterable<Shape> {
    void draw(Graphics g);
    boolean intersects(Shape other);
    void setPosition(int x, int y);
}

class CustomRectangle implements Shape, Serializable {
    private static final long serialVersionUID = 1L;
    private int x, y;
    int width;
    int height;
    Color color;

    public CustomRectangle(int width, int height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }

    @Override
    public boolean intersects(Shape other) {
        if (other instanceof CustomRectangle) {
            CustomRectangle otherRect = (CustomRectangle) other;
            return !(this.x + this.width <= otherRect.x || 
                     otherRect.x + otherRect.width <= this.x || 
                     this.y + this.height <= otherRect.y || 
                     otherRect.y + otherRect.height <= this.y);
        }
        return false;
    }

    //Iterator pattern
    @Override
    public Iterator<Shape> iterator() {
        return Collections.emptyIterator();
    }
}

//Composite Pattern
class FloorPlan implements Shape, Serializable {
    private static final long serialVersionUID = 1L;
    private final ArrayList<Shape> components = new ArrayList<>();

    @Override
    public void draw(Graphics g) {
        for (Shape component : components) {
            component.draw(g);
        }
    }

    @Override
    public boolean intersects(Shape other) {
        for (Shape component : components) {
            if (component.intersects(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setPosition(int x, int y) {
        // Not applicable for FloorPlan
    }

    public void add(Shape component) {
        components.add(component);
    }

    public void clear() {
        components.clear();
    }

    public boolean overlapsWithExistingShapes(Shape newShape) {
        return intersects(newShape);
    }

    // Iterator Pattern
    @Override
    public Iterator<Shape> iterator() {
        return components.iterator();
    }
}

// Flyweight pattern
class RectangleFactory {
    private final HashMap<String, CustomRectangle> rectangles = new HashMap<>();

    public CustomRectangle getRectangle(int width, int height, Color color) {
        String key = width + "x" + height + color.getRGB();
        if (!rectangles.containsKey(key)) {
            rectangles.put(key, new CustomRectangle(width, height, color));
        }
        CustomRectangle prototype = rectangles.get(key);
        return new CustomRectangle(prototype.width, prototype.height, prototype.color);
    }
}