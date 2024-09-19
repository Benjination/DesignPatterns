import javax.swing.border.Border;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class BoothFloorPlan extends JFrame {
    private JPanel mainPanel;
    private JPanel shapePanel;
    private FloorPlanController controller;
    private String planName;
    private CustomRectangle draggingShape;
    private int offsetX, offsetY;

    public BoothFloorPlan() {
        this(null);
    }

    public BoothFloorPlan(String planName) {
        this.planName = planName;
        controller = new FloorPlanController();

        //Main Frame
        setTitle("Booth Floor Plan" + (planName != null ? " - " + planName : ""));
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //Creates GUI sections
        setupMenuBar();
        setupShapePanel();
        setupMainPanel();
        
        //buttons
        addShapeButtons();
        addDeleteButton();
        addClearButton();
        addSaveButton();
        addReturnToLandingPageButton();

        //generates data from saved file if information was loaded
        if (planName != null) {
            loadFloorPlan(planName);
        }

        //Listens for user to select Shape in drawing area
        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                CustomRectangle clickedShape = controller.getShapeAt(e.getX(), e.getY());
                controller.selectShape(clickedShape);
                if (clickedShape != null) {
                    draggingShape = clickedShape;
                    offsetX = e.getX() - draggingShape.x;//Updates x,y when dragged
                    offsetY = e.getY() - draggingShape.y;
                    draggingShape.startDragging(draggingShape.x, draggingShape.y);
                } else if (draggingShape != null) {
                    draggingShape.setSelected(false); // Deselect if no shape is clicked
                    draggingShape = null;
                }
                mainPanel.repaint(); // Update the panel to reflect changes
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (draggingShape != null) {
                    draggingShape.stopDragging();
                    draggingShape = null;
                }
            }
        });

        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggingShape != null) {
                    int newX = e.getX() - offsetX;
                    int newY = e.getY() - offsetY;
                    draggingShape.dragTo(newX, newY);
                    mainPanel.repaint();
                }
            }
        });
    }
    //Creates menu bar with File Menu
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

    //Creates shape panel
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

    //Creates Drawing Area and paints object images in collection
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

    //Adds shape selction options to shape panel
    private void addShapeButtons() {
        addShapeButton("Large", 100, 60, Color.BLUE);
        addShapeButton("Medium", 50, 30, Color.GREEN);
        addShapeButton("Small", 30, 30, new Color(128, 0, 128));
    }

    //Finds space in drawing area to insert new shape image, and adds shape image if space is found
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

    //Creates delete button in shape panel and allows deletion of single booth
    private void addDeleteButton() {
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            CustomRectangle selectedShape = controller.getSelectedShape();
            if (selectedShape != null) {
                controller.getFloorPlan().remove(selectedShape); // Remove the selected shape
                controller.selectShape(null); // Deselect shape after deletion
                mainPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "No shape selected for deletion.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        shapePanel.add(Box.createVerticalStrut(20));
        shapePanel.add(deleteButton);
    }

    //Adds Clear button to shape panel to delete all booths at once
    private void addClearButton() {
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            controller.clearFloorPlan();
            mainPanel.repaint();
        });
        shapePanel.add(Box.createVerticalStrut(20));
        shapePanel.add(clearButton);
    }

    //Creates save button in shape panel and allows user to save file
    private void addSaveButton() {
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveFloorPlan());
        shapePanel.add(Box.createVerticalStrut(20));
        shapePanel.add(saveButton);
    }

    //Method to save file
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

    //Adds Home button to shape panel, prompts save message and returns user to landing page
    private void addReturnToLandingPageButton() {
        JButton returnButton = new JButton("Home");
        returnButton.addActionListener(e -> promptSaveAndReturn());
        shapePanel.add(Box.createVerticalStrut(20));
        shapePanel.add(returnButton);
    }

    //Method to navigate back to Landing Page
    private void returnToLandingPage() {
        SwingUtilities.invokeLater(() -> {
            LandingPage landingPage = new LandingPage();
            landingPage.setVisible(true);
            this.dispose(); // Close the current BoothFloorPlan window
        });
    }

    //Gives user the option to save file before navigating away from design page
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

    //Method to load existing file and paint shape images on drawing area
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

    //MAIN
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
    private CustomRectangle selectedShape; // Track the currently selected shape

    public FloorPlanController() {
        this.floorPlan = new FloorPlan();
        this.rectangleFactory = new RectangleFactory();
        this.random = new Random();
    }

    //Calls nonoverlapping to find space and adds new shape image to drawing area
    public boolean addShape(int width, int height, Color color, int maxWidth, int maxHeight) {
        CustomRectangle newShape = generateNonOverlappingShape(width, height, color, maxWidth, maxHeight);
        if (newShape != null) {
            floorPlan.add(newShape);
            return true;
        }
        return false;
    }

    //Method to clear all images from drawing area
    public void clearFloorPlan() {
        floorPlan.clear();
        selectedShape = null; 
    }

    //Sends Floorplan to GUI
    public void drawFloorPlan(Graphics g) {
        floorPlan.draw(g);
    }

    //Method to find unoccupied space in drawing area
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

    //gets Floorplan
    public FloorPlan getFloorPlan() {
        return floorPlan;
    }

    //sets Floorplan
    public void setFloorPlan(FloorPlan floorPlan) {
        this.floorPlan = floorPlan;
    }

    //Selects shape at certain coordinate
    public CustomRectangle getShapeAt(int x, int y) {
        for (Shape shape : floorPlan) {
            if (shape instanceof CustomRectangle) {
                CustomRectangle rect = (CustomRectangle) shape;
                if (x >= rect.x && x <= rect.x + rect.width && y >= rect.y && y <= rect.y + rect.height) {
                    return rect;
                }
            }
        }
        return null;
    }

    //Method to change status of selected shape
    public void selectShape(CustomRectangle shape) {
        if (selectedShape != null) {
            selectedShape.setSelected(false);
        }
        selectedShape = shape;
        if (selectedShape != null) {
            selectedShape.setSelected(true);
        }
    }

    //getter
    public CustomRectangle getSelectedShape() {
        return selectedShape;
    }
}

//////////////////////////////////////////////////////////////// Expert Pattern
interface Shape extends Iterable<Shape> {
    void draw(Graphics g);
    boolean intersects(Shape other);
    void setPosition(int x, int y);
}

//Makes shape serializeable for saving and loading data
class CustomRectangle implements Shape, Serializable {
    private static final long serialVersionUID = 1L;
    public int x, y;
    int width;
    int height;
    Color color;
    private boolean dragging;
    private boolean selected = false; // Add a selected flag

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

    public void startDragging(int startX, int startY) {
        dragging = true;
        this.x = startX;
        this.y = startY;
    }

    public void dragTo(int newX, int newY) {
        if (dragging) {
            this.x = newX;
            this.y = newY;
        }
    }

    public void stopDragging() {
        dragging = false;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    //Draws a highlighted border when image is selected
    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        if (selected) {
            g.setColor(Color.RED);
            g.drawRect(x - 2, y - 2, width + 4, height + 4); 
        }
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }

    //Checks if objects intersect
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

    ////////////////////////////////////////////////////////////////////Iterator pattern
    @Override
    public Iterator<Shape> iterator() {
        return Collections.emptyIterator();
    }
}

////////////////////////////////////////////////////////////////////////Composite Pattern
class FloorPlan implements Shape, Serializable {
    private static final long serialVersionUID = 1L;
    private final ArrayList<Shape> components = new ArrayList<>();

    @Override
    public void draw(Graphics g) {
        for (Shape component : components) {
            component.draw(g);
        }
    }

    public void remove(CustomRectangle shape) {
        components.remove(shape);
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

    /////////////////////////////////////////////////////////////////////////////////Iterator Pattern
    @Override
    public Iterator<Shape> iterator() {
        return components.iterator();
    }
}

///////////////////////////////////////////////////////////////////////////////////// Flyweight pattern
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