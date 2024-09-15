import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
//import java.awt.event.*;

public class BoothFloorPlan extends JFrame {
    private JPanel mainPanel;
    private JPanel shapePanel;
    private Shape selectedShape;

    public BoothFloorPlan() {
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
                if (selectedShape != null) {
                    selectedShape.draw(g);
                }
            }
        };
        Border mainPanelBorder = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
        BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setBorder(mainPanelBorder);


        add(mainPanel, BorderLayout.CENTER);

        // Add shapes to shape panel
        addShape("Rectangle", new Rectangle(50, 50, 100, 60));
        addShape("Oval", new Oval(50, 50, 100, 60));
        addShape("Triangle", new Triangle(50, 50, 100, 60));
    }

    

    private void addShape(String name, Shape shape) {
        JButton button = new JButton(name);
        button.addActionListener(e -> {
            selectedShape = shape;
            mainPanel.repaint();
        });
        shapePanel.add(button);
    }

    public Shape getSelectedShape() {
        return selectedShape;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BoothFloorPlan frame = new BoothFloorPlan();
            frame.setVisible(true);

            // Example of how to use the return type
            Timer timer = new Timer(1000, e -> {
                Shape currentShape = frame.getSelectedShape();
                if (currentShape != null) {
                    System.out.println("Currently selected shape: " + currentShape.getClass().getSimpleName());
                } else {
                    System.out.println("No shape selected");
                }
            });
            timer.start();
        });
    }
}


interface Shape {
    void draw(Graphics g);
}

class Rectangle implements Shape {
    private int x, y, width, height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics g) {
        g.drawRect(x, y, width, height);
    }
}

class Oval implements Shape {
    private int x, y, width, height;

    public Oval(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics g) {
        g.drawOval(x, y, width, height);
    }
}

class Triangle implements Shape {
    private int x, y, width, height;

    public Triangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics g) {
        int[] xPoints = {x, x + width / 2, x + width};
        int[] yPoints = {y + height, y, y + height};
        g.drawPolygon(xPoints, yPoints, 3);
    }
}