# DesignPatterns
YouTube Demonstration: https://youtu.be/mNjMNyZUi5Q?si=1c0YodUp1bDt7D5s

Here's a README for the provided code, highlighting the design patterns applied:

## Booth Floor Plan Application

This Java application allows users to create, edit, and manage booth floor plans using a graphical interface.

### Features

- Create new floor plans
- Add, move, and delete booth shapes
- Save and load floor plans
- Clear entire floor plans
- Return to landing page

### Design Patterns

This application implements several design patterns to enhance its structure and functionality:

**Controller Pattern**
The `FloorPlanController` class acts as an intermediary between the UI and the model (FloorPlan). It handles operations like adding shapes, clearing the floor plan, and managing shape selection[1].

**Expert Pattern**
The `Shape` interface and `CustomRectangle` class demonstrate the Expert pattern. These components encapsulate the behavior and properties specific to shapes, such as drawing, intersection detection, and position setting[1].

**Iterator Pattern**
Both the `Shape` interface and `FloorPlan` class implement the `Iterable` interface, allowing for easy iteration over shapes in the floor plan[1].

**Composite Pattern**
The `FloorPlan` class implements the Composite pattern by treating individual shapes and groups of shapes uniformly through the `Shape` interface. This allows for operations to be performed on both single shapes and collections of shapes[1].

**Flyweight Pattern**
The `RectangleFactory` class implements the Flyweight pattern to efficiently manage and reuse rectangle objects with similar properties, reducing memory usage[1].

### Key Classes

- `BoothFloorPlan`: Main GUI class for the floor plan editor
- `FloorPlanController`: Manages the logic between the UI and the floor plan model
- `CustomRectangle`: Represents individual booth shapes
- `FloorPlan`: Manages the collection of shapes in the floor plan
- `RectangleFactory`: Creates and manages rectangle objects efficiently

### Usage

1. Run the application
2. Use the buttons in the left panel to add shapes
3. Click and drag shapes to move them
4. Use the Delete button to remove selected shapes
5. Save your floor plan using the Save button or File menu
6. Load existing floor plans from the landing page

### Implementation Details

- The application uses Swing for the GUI
- Shapes are serializable for easy saving and loading
- The main drawing area updates in real-time as shapes are added or moved

This README provides an overview of the Booth Floor Plan application, highlighting its key features, design patterns, and usage instructions.

Citations:
[1] https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/4916938/47701d7d-756d-423f-9c3a-00b46164f40c/paste.txt
