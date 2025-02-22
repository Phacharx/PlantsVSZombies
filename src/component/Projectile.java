package component;

import javafx.scene.shape.Circle;

public class Projectile {
    private int x, y;
    private Circle circle;
    private int damage = 25; // กระสุนทำลาย 25 หน่วย

    public Projectile(int x, int y) {
        this.x = x;
        this.y = y;
        this.circle = new Circle(x, y, 5); // ขนาดของวงกลม 5 สำหรับเม็ดกระสุน
        this.circle.setFill(javafx.scene.paint.Color.YELLOW); // สีของกระสุน
    }

    public void move() {
        x += 5; // กระสุนเคลื่อนที่ไปทางขวา
        circle.setCenterX(x); // อัพเดตตำแหน่ง X ของวงกลม
    }

    public Circle getCircle() {
        return circle;
    }

    public int getDamage() {
        return damage; // คืนค่าความเสียหายของกระสุน
    }

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
    
    
}
