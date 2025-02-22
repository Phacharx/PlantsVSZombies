package component;

import javafx.scene.shape.Rectangle;
import main.GameApp;

public class Plant {
    protected int health;
    protected int x, y;
    protected Rectangle rectangle;
    private boolean isDead = false; // ตัวแปรตรวจสอบสถานะของพืชว่าตายหรือไม่

    public Plant(int health, int x, int y) {
        this.health = health;
        this.x = x;
        this.y = y;
        this.rectangle = new Rectangle(x, y, 50, 50);
        this.rectangle.setFill(javafx.scene.paint.Color.GREEN);
    }

    public void performAction() {
        // สำหรับพืชที่สามารถยิงกระสุนจะต้องมีฟังก์ชัน shoot() ในคลาสย่อย
    }

    public void takeDamage(int damage) {
        this.health -= damage; // ลดเลือดพืช
        System.out.println("Plant took " + damage + " damage. Health: " + health);
        if (health <= 0) {
            die(); // หากเลือดหมดให้พืชตาย
        }
    }

    public void die() {
        this.isDead = true; // ตั้งสถานะพืชให้ตาย
        System.out.println("Plant died!");
        GameApp.gamePane.getChildren().remove(rectangle); // ลบพืชจากหน้าจอ
    }

    public boolean isDead() {
        return isDead; // คืนค่าสถานะว่าพืชตายหรือไม่
    }

    public int getX() {
        return x;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
    
}
