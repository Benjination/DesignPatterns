import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class BoothFloorPlan extends JFrame {
    private JPanel mainPanel;
    private JPanel shapePanel;
    private ArrayList<Shape> shapes;
    private Random random;

    //Floor Plan Manager
    //Begin GUI Display screen
    //Begins Code for Building all aesthetics
    public BoothFloorPlan() {
        random = new Random();
        shapes = new ArrayList<>();

        setTitle("Booth Floor Plan");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);


        // Create shape panel
        shapePanel = new JPanel();
        shapePanel.setPreferredSize(new Dimension(150, 400));
        shapePanel.setLayout(new BoxLayout(shapePanel, BoxLayout.Y_AXIS));
        add(shapePanel, BorderLayout.WEST);
        Border shapePanelBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5));
        shapePanel.setBorder(shapePanelBorder);

        // Create main panel
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Shape shape : shapes) {
                    shape.draw(g);
                }
            }
        };
        //Creates Border on Drawing area
        Border mainPanelBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setBorder(mainPanelBorder);

        add(mainPanel, BorderLayout.CENTER);
        
        // Add shapes to shape panel
        addShape("Large", 100, 60, Color.BLUE);
        addShape("Medium", 50, 30, Color.GREEN);
        addShape("Small", 30, 30, new Color(128, 0, 128)); // Purple

        // Add clear button
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            shapes.clear();
            mainPanel.repaint();
        });
        shapePanel.add(Box.createVerticalStrut(20));
        shapePanel.add(clearButton);
    }
    //End GUI Build Aesthetics Complete

    //Generates New Shape
    private void addShape(String name, int width, int height, Color color) {
        JButton button = new JButton(name);  //Creates shape based on name (Small Medium Large)
        button.addActionListener(e -> {
            CustomRectangle newShape = generateNonOverlappingShape(width, height, color);
            if (newShape != null) {
                shapes.add(newShape);  //Adds shape to array of selected shapes
                mainPanel.repaint();   //Recreates Floor Plan
            } else {                   //Breaks when there is no more space for the new shape
                JOptionPane.showMessageDialog(this, "No space available for new shape", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        shapePanel.add(button); //Adds button to Shape panel for user to select
    }

    //Attemps to find unoccupied space to add the shape 100 times and then sends No space available message
    private CustomRectangle generateNonOverlappingShape(int width, int height, Color color) {
        int maxAttempts = 100;
        for (int i = 0; i < maxAttempts; i++) {
            int x = getRandomX(width);
            int y = getRandomY(height);
            CustomRectangle newShape = new CustomRectangle(x, y, width, height, color);
            if (!overlapsWithExistingShapes(newShape)) {
                return newShape;
            }
        }
        return null; //null will trigger message
    }

    //Searches for empty space
    private boolean overlapsWithExistingShapes(CustomRectangle newShape) {
        for (Shape shape : shapes) {
            if (shape instanceof CustomRectangle) {
                CustomRectangle existingShape = (CustomRectangle) shape;
                if (newShape.intersects(existingShape)) {
                    return true;
                }
            }
        }
        return false;
    }

    //Generates Random x value
    private int getRandomX(int width) {
        return random.nextInt(mainPanel.getWidth() - width + 1);
    }

    //Generates Random y value
    private int getRandomY(int height) {
        return random.nextInt(mainPanel.getHeight() - height + 1);
    }

    //Generates each shape in Drawing window
    interface Shape {
        void draw(Graphics g);
    }


    class CustomRectangle implements Shape {
        private int x, y, width, height;
        private Color color;

        //Creates new Booth Object
        public CustomRectangle(int x, int y, int width, int height, Color color) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
        }

        //Draws booth on Drawing area
        @Override
        public void draw(Graphics g) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
        }

        //Tests if any part of new object overlaps with any other object
        public boolean intersects(CustomRectangle other) {
            return !(this.x + this.width <= other.x || 
                     other.x + other.width <= this.x || 
                     this.y + this.height <= other.y || 
                     other.y + other.height <= this.y);
        }
    }

    //Main function
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BoothFloorPlan frame = new BoothFloorPlan();
            frame.setVisible(true);
        });
    }
}