package component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.util.Duration;
import main.GameApp;

public abstract class BaseZombie {
    protected int health;
    protected double speed;
    protected ImageView zombieImage;
    protected Timeline moveTimeline;
    protected boolean isAttacking = false; // ซอมบี้กำลังโจมตีอยู่หรือไม่
    private Timeline attackTimer = null; // ตัวจับเวลาโจมตีพืช

    public BaseZombie(String imagePath, int x, int y, int health, double speed) {
        this.health = health;
        this.speed = speed;
        this.zombieImage = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        this.zombieImage.setFitWidth(80);
        this.zombieImage.setFitHeight(80);
        this.zombieImage.setX(x);
        this.zombieImage.setY(y);

        GameApp.gamePane.getChildren().add(zombieImage);

        moveTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> move()));
        moveTimeline.setCycleCount(Timeline.INDEFINITE);
        moveTimeline.play();
    }

    public void move() {
    	System.out.println("Total Plants in game: " + GameApp.plants.size());
        if (!isAttacking) { // ซอมบี้จะเดินต่อเมื่อไม่ได้โจมตี
            System.out.println("Zombie moving: X=" + zombieImage.getX() + ", Y=" + zombieImage.getY());

            if (checkCollisionWithPlants()) {
                isAttacking = true;
                System.out.println("Zombie stopped at X=" + zombieImage.getX() + " to attack.");
                attackPlant();
            } else {
                zombieImage.setX(zombieImage.getX() - 2);
                System.out.println("Zombie moved to X: " + zombieImage.getX());
            }

        } else {
            System.out.println("Zombie is attacking, not moving.");
        }
    }

    // ตรวจจับว่ามีพืชขวางอยู่ข้างหน้าหรือไม่
    private boolean checkCollisionWithPlants() {
        System.out.println("Checking collision... Total Plants: " + GameApp.plants.size());

        for (BasePlant plant : GameApp.plants) {
            double dx = Math.abs(zombieImage.getX() - plant.getImageView().getX());
            double dy = Math.abs(zombieImage.getY() - plant.getImageView().getY());

            System.out.println("Zombie (" + zombieImage.getX() + ", " + zombieImage.getY() + 
                               ") vs Plant (" + plant.getImageView().getX() + ", " + plant.getImageView().getY() + 
                               ") → dx=" + dx + ", dy=" + dy);

            // ปรับค่า dx และ dy ให้อยู่ในระยะที่ซอมบี้ควรจะหยุด
            if (dx < 50 && dy < 50) {  // 🔥 เพิ่มขอบเขตการตรวจจับ
                System.out.println("Collision detected! Zombie will attack.");
                return true;
            }
        }
        return false;
    }



    private void attackPlant() {
        System.out.println("Zombie is attacking a plant!");

        if (attackTimer == null) { // ป้องกันการสร้าง Timer ซ้ำซ้อน
            attackTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                BasePlant targetPlant = null;

                for (BasePlant plant : GameApp.plants) {
                    double dx = Math.abs(zombieImage.getX() - plant.getImageView().getX());
                    double dy = Math.abs(zombieImage.getY() - plant.getImageView().getY());

                    if (dx < 50 && dy < 50) { // 🔥 ตรวจสอบพืชอีกครั้งก่อนโจมตี
                        targetPlant = plant;
                        break;
                    }
                }

                if (targetPlant != null) {
                    targetPlant.takeDamage(15);
                    System.out.println("Plant HP: " + targetPlant.getHealth());

                    if (targetPlant.getHealth() <= 0) {
                        System.out.println("Plant destroyed!");
                        GameApp.gamePane.getChildren().remove(targetPlant.getImageView());
                        GameApp.plants.remove(targetPlant);
                        
                        // ✅ ปล่อยให้ซอมบี้เดินต่อหลังจากพืชตาย
                        isAttacking = false;
                        attackTimer.stop();
                        attackTimer = null;
                    }
                } else {
                    System.out.println("No plant found. Zombie will resume walking.");
                    isAttacking = false; // ไม่มีพืชให้โจมตี เดินต่อ
                    attackTimer.stop();
                    attackTimer = null;
                }
            }));
            attackTimer.setCycleCount(Timeline.INDEFINITE);
            attackTimer.play();
        }
    }




    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("Zombie took damage: " + damage + " | HP left: " + health);
        
        if (health <= 0) {
            System.out.println("Zombie died at X=" + zombieImage.getX() + ", Y=" + zombieImage.getY());
            die();
        }
    }


    public void die() {
        GameApp.gamePane.getChildren().remove(zombieImage);
        GameApp.zombies.remove(this);
        if (attackTimer != null) {
            attackTimer.stop();
            attackTimer = null;
        }
        System.out.println("Zombie removed from game.");
    }


    public ImageView getImageView() {
        return zombieImage;
    }

    public boolean isDead() {
        return this.health <= 0;
    }
    public int getX() {
        return (int) zombieImage.getX(); // คืนค่า X ตำแหน่งของซอมบี้
    }

    public int getY() {
        return (int) zombieImage.getY(); // คืนค่า Y ตำแหน่งของซอมบี้
    }

}
