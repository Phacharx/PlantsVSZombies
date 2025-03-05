package component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.util.Duration;
import javafx.application.Platform;
import main.GameApp;

public abstract class BaseZombie {
    protected int health;
    protected double speed;
    protected ImageView zombieImage;
    protected Timeline moveTimeline;
    protected boolean isAttacking = false;
    private Timeline attackTimer = null;
    private boolean isDead = false; // ✅ เพิ่มตัวแปรเช็คว่าตายหรือยัง
    
    private Image[] walkFrames; // เก็บภาพเดิน 4 รูป
    private int currentFrame = 0;
    private Timeline walkAnimation; // ใช้เปลี่ยนรูปขณะเดิน

    public BaseZombie(String[] imagePaths, int x, int y, int health, double speed) {
        this.health = health;
        this.speed = speed;
        this.zombieImage = new ImageView();
        this.zombieImage.setFitWidth(40);
        this.zombieImage.setFitHeight(70);
        this.zombieImage.setX(x);
        this.zombieImage.setY(y);

        // ✅ โหลดภาพเดิน 4 รูป
        walkFrames = new Image[4];
        for (int i = 0; i < 4; i++) {
            walkFrames[i] = new Image(getClass().getResource(imagePaths[i]).toExternalForm());
        }
        this.zombieImage.setImage(walkFrames[0]); // เริ่มที่รูปแรก

        GameApp.gamePane.getChildren().add(zombieImage);

        // ✅ ตั้งค่า Timeline เปลี่ยนรูปขณะเดิน
        walkAnimation = new Timeline(new KeyFrame(Duration.millis(200), e -> updateWalkFrame()));
        walkAnimation.setCycleCount(Timeline.INDEFINITE);
        walkAnimation.play();

        // ✅ ตั้งค่า Timeline สำหรับการเดิน
        moveTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> move()));
        moveTimeline.setCycleCount(Timeline.INDEFINITE);
        moveTimeline.play();
    }

    private void updateWalkFrame() {
        if (!isAttacking && !isDead) { // ✅ เปลี่ยนรูปเฉพาะตอนเดิน และถ้ายังไม่ตาย
            currentFrame = (currentFrame + 1) % 4;
            zombieImage.setImage(walkFrames[currentFrame]);
        }
    }

    public void move() {
        if (isDead) return; // ✅ ถ้าตายแล้ว ไม่ต้องเคลื่อนที่
        if (!isAttacking) {
            if (!walkAnimation.getStatus().equals(Timeline.Status.RUNNING)) {
                walkAnimation.play(); // ✅ เดิน = วนรูปภาพ
            }

            if (checkCollisionWithPlants()) {
                isAttacking = true;
                walkAnimation.stop(); // ❌ หยุดเปลี่ยนรูปขณะโจมตี
                attackPlant();
            } else {
                zombieImage.setX(zombieImage.getX() - speed);
            }
        }
    }

    private boolean checkCollisionWithPlants() {
        for (BasePlant plant : GameApp.plants) {
            double dx = Math.abs(zombieImage.getX() - plant.getImageView().getX());
            double dy = Math.abs(zombieImage.getY() - plant.getImageView().getY());

            if (dx < 50 && dy < 50) {
                return true;
            }
        }
        return false;
    }

    private void attackPlant() {
        if (isDead) return; // ✅ ถ้าตายแล้วไม่ต้องโจมตี

        if (attackTimer == null) {
            attackTimer = new Timeline(new KeyFrame(Duration.seconds(0.6), e -> {
                if (isDead) return; // ✅ เช็คอีกครั้งว่าตายหรือยัง
                BasePlant targetPlant = null;

                for (BasePlant plant : GameApp.plants) {
                    double dx = Math.abs(zombieImage.getX() - plant.getImageView().getX());
                    double dy = Math.abs(zombieImage.getY() - plant.getImageView().getY());

                    if (dx < 50 && dy < 50) {
                        targetPlant = plant;
                        break;
                    }
                }

                if (targetPlant != null) {
                    // ✅ เปลี่ยนภาพไปที่เฟรม 0 ตอนโจมตี
                    zombieImage.setImage(walkFrames[0]); 

                    targetPlant.takeDamage(15);

                    if (targetPlant.getHealth() <= 0) {
                        GameApp.gamePane.getChildren().remove(targetPlant.getImageView());
                        GameApp.plants.remove(targetPlant);
                        isAttacking = false;
                        
                        // ✅ กลับไปเดินหลังจากพืชที่โจมตีตาย
                        walkAnimation.play();
                        attackTimer.stop();
                        attackTimer = null;
                    }
                } else {
                    isAttacking = false;
                    
                    // ✅ กลับไปเดินถ้าไม่มีพืชให้โจมตีแล้ว
                    walkAnimation.play();
                    attackTimer.stop();
                    attackTimer = null;
                }
            }));
            attackTimer.setCycleCount(Timeline.INDEFINITE);
            attackTimer.play();
        }
    }


    public void takeDamage(int damage) {
        if (isDead) return; // ✅ ถ้าตายแล้วไม่ต้องรับดาเมจ
        this.health -= damage;
//        System.out.println("💥 Zombie took " + damage + " damage, HP left: " + this.health);

        if (this.health <= 0) {
            die(); // ✅ เรียกให้ตาย
        }
    }

    public void die() {
        if (isDead) return;
        isDead = true;

        Platform.runLater(() -> {
            GameApp.gamePane.getChildren().remove(zombieImage);
        });

        GameApp.zombies.remove(this);

        System.out.println("💀 Zombie defeated! +10 Energy");
        GameApp.increaseEnergy(10);

        // ✅ นับจำนวนซอมบี้ที่ตาย
        GameApp.totalZombiesKilled++;

        // ✅ ตรวจสอบว่าฆ่าครบตามที่กำหนดหรือยัง
        if (GameApp.totalZombiesKilled >= GameApp.TOTAL_ZOMBIES_TO_WIN) {
            // ✅ หา instance ของ GameApp ที่กำลังรันอยู่ แล้วเรียก youWinScreen()
            Platform.runLater(() -> {
                Stage stage = (Stage) GameApp.gamePane.getScene().getWindow();
                GameApp gameAppInstance = (GameApp) stage.getUserData(); 
                if (gameAppInstance != null) {
                    gameAppInstance.youWinScreen(); 
                }
            });
        }
    }


    public ImageView getImageView() {
        return zombieImage;
    }

    public int getX() {
        return (int) zombieImage.getX();
    }

    public int getY() {
        return (int) zombieImage.getY();
    }
}