package component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;
import main.GameApp;

public class HeavyZombie {
    private int health;
    private int x, y;
    private Rectangle rectangle;
    private boolean isEating = false;
    private boolean isAttacking = false;
    private Plant targetPlant;
    private Timeline attackTimeline;

    public HeavyZombie(int x, int y) {
        // ตั้งค่าเลือดให้มากขึ้น (200 หน่วย)
        this.health = 200;
        this.x = x;
        this.y = y;
        this.rectangle = new Rectangle(x, y, 50, 50);
        // เปลี่ยนสีเพื่อแยกแยะกับซอมบี้ปกติ
        this.rectangle.setFill(javafx.scene.paint.Color.DARKRED);
    }

    public void move() {
        if (health > 0 && !isEating && !isAttacking) {
            // ปรับความเร็วการเดินให้ช้าลง (เคลื่อนที่ทีละ 2 หน่วย)
            x -= 2;
            rectangle.setX(x);
        }

        // ถ้ามีพืชเป้าหมายและอยู่ใกล้พอ ให้เริ่มโจมตี
        if (targetPlant != null && targetPlant.getHealth() > 0 && Math.abs(x - targetPlant.getX()) < 10) {
            stopAndAttack();
        } else if (targetPlant == null || targetPlant.getHealth() <= 0) {
            findNewTargetPlant();
        }
    }

    // ค้นหาพืชเป้าหมายตัวใหม่จาก GameApp.plants
    public void findNewTargetPlant() {
        for (Plant plant : GameApp.plants) {
            if (plant.getHealth() > 0) {
                targetPlant = plant;
                break;
            }
        }
    }

    // หยุดการเดินและเริ่มโจมตีพืชเป้าหมาย
    private void stopAndAttack() {
        if (health > 0 && !isAttacking) {
            isAttacking = true;
            attackTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> attack()));
            attackTimeline.setCycleCount(Timeline.INDEFINITE);
            attackTimeline.play();
        }
    }

    // ฟังก์ชันโจมตีพืชเป้าหมาย (ลดเลือดพืชทีละ 10 หน่วย)
    public void attack() {
        if (targetPlant != null && health > 0 && targetPlant.getHealth() > 0 && isAttacking) {
            targetPlant.takeDamage(10);
            System.out.println("HeavyZombie กำลังโจมตีพืช! พืชเหลือเลือด: " + targetPlant.getHealth());
        } else {
            stopAttack();
        }
    }

    // หยุดการโจมตี
    private void stopAttack() {
        isAttacking = false;
    }

    // ลดเลือดของ HeavyZombie เมื่อต้องรับความเสียหาย
    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("HeavyZombie ถูกโจมตี! เลือดเหลือ: " + health);
        if (health <= 0) {
            die();
        }
    }

    // เมื่อเลือดหมดให้ลบ HeavyZombie ออกจากหน้าจอและรายการซอมบี้ใน GameApp
    public void die() {
        System.out.println("HeavyZombie ตายแล้ว!");
        GameApp.gamePane.getChildren().remove(this.getRectangle());
        GameApp.zombies.remove(this);
        targetPlant = null;
    }

    public int getHealth() {
        return health;
    }

    public int getX() {
        return x;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setTargetPlant(Plant plant) {
        this.targetPlant = plant;
    }

    public Plant getTargetPlant() {
        return targetPlant;
    }

    public boolean isEating() {
        return isEating;
    }

    public boolean isAttacking() {
        return isAttacking;
    }
}
