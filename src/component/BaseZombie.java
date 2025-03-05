package component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.stage.Stage;
import main.GameApp;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseZombie {
    protected int health;
    protected double speed;
    protected ImageView zombieImage;
    public Timeline moveTimeline;
    protected boolean isAttacking = false;
    public Timeline attackTimer = null;
    private boolean isDead = false;

    private static final Map<String, Image[]> zombieImagesMap = new HashMap<>(); // ✅ Shared Images สำหรับแต่ละประเภท
    private Image[] walkFrames;
    private int currentFrame = 0;
    public Timeline walkAnimation;
    
    private boolean isActive = true;
    private int zombieSessionId;

    static {
        try {
            // ✅ โหลดภาพของซอมบี้แบบปกติ (Zombie)
            zombieImagesMap.put("Zombie", new Image[]{
                new Image(BaseZombie.class.getResource("/Image/Big_Kai_Ju_9_WalkS.png").toExternalForm()),
                new Image(BaseZombie.class.getResource("/Image/Big_Kai_Ju_9_WalkR.png").toExternalForm()),
                new Image(BaseZombie.class.getResource("/Image/Big_Kai_Ju_9_WalkS.png").toExternalForm()),
                new Image(BaseZombie.class.getResource("/Image/Big_Kai_Ju_9_WalkL.png").toExternalForm())
            });

            // ✅ โหลดภาพของซอมบี้เร็ว (FastZombie)
            zombieImagesMap.put("FastZombie", new Image[]{
                new Image(BaseZombie.class.getResource("/Image/FastZombieS.png").toExternalForm()),
                new Image(BaseZombie.class.getResource("/Image/FastZombieR.png").toExternalForm()),
                new Image(BaseZombie.class.getResource("/Image/FastZombieS.png").toExternalForm()),
                new Image(BaseZombie.class.getResource("/Image/FastZombieL.png").toExternalForm())
            });

            // ✅ โหลดภาพของซอมบี้หนัก (HeavyZombie)
            zombieImagesMap.put("HeavyZombie", new Image[]{
            	new Image(BaseZombie.class.getResource("/Image/Big_Kai_Ju_9_WalkS.png").toExternalForm()),
                new Image(BaseZombie.class.getResource("/Image/Big_Kai_Ju_9_WalkR.png").toExternalForm()),
                new Image(BaseZombie.class.getResource("/Image/Big_Kai_Ju_9_WalkS.png").toExternalForm()),
                new Image(BaseZombie.class.getResource("/Image/Big_Kai_Ju_9_WalkL.png").toExternalForm())
            });

        } catch (Exception e) {
            System.err.println("⚠ Failed to load Zombie images: " + e.getMessage());
        }
    }
    public BaseZombie(String zombieType, int x, int y, int health, double speed, double width, double height) {
        this.health = health;
        this.speed = speed;
        this.zombieSessionId = GameApp.gameSessionId;
        this.zombieImage = new ImageView();
        this.zombieImage.setFitWidth(width);  // ✅ กำหนดขนาดเฉพาะตัว
        this.zombieImage.setFitHeight(height);
        this.zombieImage.setX(x);
        this.zombieImage.setY(y);
        
        if (zombieImagesMap.containsKey(zombieType)) {
            this.walkFrames = zombieImagesMap.get(zombieType);
        } else {
            System.err.println("⚠ Invalid zombie type: " + zombieType);
            this.walkFrames = zombieImagesMap.get("Zombie"); // ใช้ Default เป็น Zombie
        }

        this.zombieImage.setImage(walkFrames[0]);
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
        if (isDead || !isActive) return; // ✅ ถ้าตายแล้วหรือไม่ Active จะไม่ขยับ
        if (!isAttacking) {
            if (!walkAnimation.getStatus().equals(Timeline.Status.RUNNING)) {
                walkAnimation.play();
            }
            if (checkCollisionWithPlants()) {
                isAttacking = true;
                walkAnimation.stop();
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

                    // ✅ เช็คว่าเป็นซอมบี้จากเกมปัจจุบันหรือไม่
                    if (dx < 50 && dy < 50 && this.zombieSessionId == GameApp.gameSessionId) {
                        targetPlant = plant;
                        break;
                    }
                }

                if (targetPlant != null) {
                    // ✅ เปลี่ยนภาพไปที่เฟรม 0 ตอนโจมตี
                    zombieImage.setImage(walkFrames[0]); 

                    targetPlant.takeDamage(15, this.zombieSessionId);

                    if (targetPlant.getHealth() <= 0) {
                        GameApp.gamePane.getChildren().remove(targetPlant.getImageView());
                        GameApp.plants.remove(targetPlant);
                        isAttacking = false;

                        if (attackTimer != null) {
                            attackTimer.stop();
                            attackTimer = null;
                        }

                        walkAnimation.play(); // ✅ กลับมาเดินต่อ
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

//        System.out.println("💀 Zombie defeated! +10 Energy");
        GameApp.increaseEnergy(10);

        // ✅ นับจำนวนซอมบี้ที่ตาย
        GameApp.increaseScore();
//        System.out.println("Zombie : " + GameApp.totalZombiesKilled);

        // ✅ ตรวจสอบว่าฆ่าครบตามที่กำหนดหรือยัง
        
    }
    
    public void deactivate() {
        isDead = true;
        isAttacking = false;
        if (moveTimeline != null) moveTimeline.stop();
        if (attackTimer != null) attackTimer.stop();
        if (walkAnimation != null) walkAnimation.stop();
    }
    
    public ImageView getImageView() {
        return zombieImage;
    }

    public int getX() {
        return (int) zombieImage.getX();
    }

    public Timeline getMoveTimeline() {
		return moveTimeline;
	}

	public void setMoveTimeline(Timeline moveTimeline) {
		this.moveTimeline = moveTimeline;
	}

	public int getY() {
        return (int) zombieImage.getY();
    }
}