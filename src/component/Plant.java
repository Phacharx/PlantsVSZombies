package component;

import javafx.scene.shape.Rectangle;

public abstract class Plant {
    protected int health;
    protected int x, y;
    protected Rectangle rectangle;

    public Plant(int health, int x, int y) {
        this.health = health;
        this.x = x;
        this.y = y;
        this.rectangle = new Rectangle(x, y, 50, 50);
        this.rectangle.setFill(javafx.scene.paint.Color.GREEN);
    }

    public abstract void performAction();

    public Rectangle getRectangle() {
        return rectangle;
    }
}
