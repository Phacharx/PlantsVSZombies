package component;

import javafx.scene.shape.Rectangle;
import main.GameApp;

public class Zombie {
    private int health;
    private int x, y;
    private Rectangle rectangle;

    public Zombie(int x, int y) {
        this.health = 100; // ซอมบี้เริ่มต้นด้วยเลือด 100
        this.x = x;
        this.y = y;
        this.rectangle = new Rectangle(x, y, 50, 50);
        this.rectangle.setFill(javafx.scene.paint.Color.RED);
    }

    public void move() {
        x -= 5; // ซอมบี้เดินไปทางซ้าย
        rectangle.setX(x);
    }

    public void attack() {
        System.out.println("Zombie is attacking!");
    }

    public void takeDamage(int damage) {
        this.health -= damage; // ลดเลือดซอมบี้
        System.out.println("Zombie took " + damage + " damage. Health: " + health);
        if (health <= 0) {
            die();
        }
    }

    public void die() {
        GameApp.gamePane.getChildren().remove(rectangle); // ลบซอมบี้จากหน้าจอ
        System.out.println("Zombie died!");
    }

    public int getX() {
        return x;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}
