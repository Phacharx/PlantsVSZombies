package component;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;
import main.GameApp;

public class Zombie {
    private int health;
    private int x, y;
    private Rectangle rectangle;
    private boolean isEating = false; // ตรวจสอบว่ากำลังกินพืชหรือไม่
    private boolean isAttacking = false; // ตรวจสอบว่ากำลังกโจมตีพืชหรือไม่
    private Plant targetPlant; // เป้าหมายของซอมบี้ (พืชที่กำลังกินหรือโจมตี)
    private Timeline attackTimeline;
    

    public Zombie(int x, int y) {
        this.health = 100; // ซอมบี้เริ่มต้นด้วยเลือด 100
        this.x = x;
        this.y = y;
        this.rectangle = new Rectangle(x, y, 50, 50);
        this.rectangle.setFill(javafx.scene.paint.Color.RED);
    }

    public void move() {
        if (health > 0 && !isEating && !isAttacking) {
            x -= 5; // ซอมบี้เดินไปทางซ้าย
            rectangle.setX(x);
        }

        // หากซอมบี้มีเป้าหมายเป็นพืชที่ยังมีชีวิตอยู่ ก็ให้โจมตี
        if (targetPlant != null && targetPlant.getHealth() > 0 && Math.abs(x - targetPlant.getX()) < 10) {
            stopAndAttack(); // เริ่มโจมตีพืช
        } else if (targetPlant == null || targetPlant.getHealth() <= 0) {
            // ถ้าพืชตายแล้วให้ซอมบี้เดินต่อไปหาพืชตัวถัดไป
            findNewTargetPlant();
        }
    }

    public void findNewTargetPlant() {
        // ลูปผ่านพืชทั้งหมดและหาพืชที่ยังมีชีวิตอยู่
        for (Plant plant : GameApp.plants) {
            if (plant.getHealth() > 0) { // ถ้าพืชยังมีชีวิตอยู่
                targetPlant = plant; // ตั้งค่าพืชใหม่เป็นเป้าหมาย
                break;
            }
        }
    }

    private void stopAndAttack() {
        if (health > 0 && !isAttacking) {
            isAttacking = true; // เริ่มโจมตี
            attackTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> attack())); // สร้าง attackTimeline
            attackTimeline.setCycleCount(Timeline.INDEFINITE); // ทำให้การโจมตีเป็นแบบไม่จำกัด
            attackTimeline.play(); // เริ่มเล่น
        }
    }


    public void attack() {
        if (targetPlant != null && health > 0 && targetPlant.getHealth() > 0 && isAttacking) {
            targetPlant.takeDamage(10); // ลดเลือดพืชทีละ 10 หน่วย
            System.out.println("Zombie is attacking plant! Plant health: " + targetPlant.getHealth());
        } else {
            stopAttack(); // ถ้าพืชตายให้หยุดการโจมตี
        }
    }

    private void stopAttack() {
        isAttacking = false; // หยุดการโจมตี
    }

    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("Zombie took " + damage + " damage. Health: " + health);
        if (health <= 0) {
            die(); // เรียกใช้ฟังก์ชัน die เมื่อซอมบี้ตาย
        }
    }

    public void die() {
        // เมื่อซอมบี้ตายจะต้องลบซอมบี้จากหน้าจอ
        System.out.println("Zombie died!");
        GameApp.gamePane.getChildren().remove(this.getRectangle()); // ลบซอมบี้จากหน้าจอ
        GameApp.zombies.remove(this); // ลบซอมบี้จากรายการ zombies
        targetPlant = null; // รีเซ็ต targetPlant เมื่อซอมบี้ตาย
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getX() {
        return x;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setTargetPlant(Plant plant) {
        this.targetPlant = plant; // กำหนดพืชที่เป็นเป้าหมายให้กับซอมบี้
    }

    public Plant getTargetPlant() {
        return targetPlant; // คืนค่าพืชที่ซอมบี้กำลังกินหรือโจมตี
    }

    public boolean isEating() {
        return isEating; // ตรวจสอบว่าซอมบี้กำลังกินพืชอยู่หรือไม่
    }

    public boolean isAttacking() {
        return isAttacking; // ตรวจสอบว่าซอมบี้กำลังกโจมตีพืชอยู่หรือไม่
    }
}
