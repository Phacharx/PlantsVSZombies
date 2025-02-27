package component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.GameApp;

public abstract class BaseZombie {
    protected int health;
    protected int x, y;
    protected boolean isEating = false;
    protected boolean isAttacking = false;
    protected BasePlant targetPlant;
    protected Timeline attackTimeline;
    protected double speed;
    protected ImageView zombieImage;
    protected Timeline moveTimeline;
    
    public BaseZombie(String imagePath, int x, int y, int health, double speed) {
    	this.x = x;
        this.y = y;
        this.health = health;
        this.speed = speed;
        this.zombieImage = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        this.zombieImage.setFitWidth(80);
        this.zombieImage.setFitHeight(80);
        this.zombieImage.setX(x);
        this.zombieImage.setY(y);
        
        GameApp.gamePane.getChildren().add(zombieImage);
        
        moveTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> move()));
        moveTimeline.setCycleCount(Timeline.INDEFINITE);
        moveTimeline.play();
    }
    
    public void move() {
    	for (BasePlant plant : GameApp.plants) {
            if (Math.abs(plant.getX() - x) < 50 && Math.abs(plant.getY() - y) < 50) {
                attack(plant);
                return; // ถ้าชนต้นไม้ให้หยุดเดิน
            }
        }
        x -= speed;
        zombieImage.setX(x);
    }
    
    public void attack(BasePlant plant) {
        plant.takeDamage(10);
        System.out.println("Zombie attacking!");
    }
    
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            die();
        }
    }
    
    public void die() {
        GameApp.gamePane.getChildren().remove(zombieImage);
        GameApp.zombies.remove(this);
    }
    
    public ImageView getImageView() {
        return zombieImage;
    }
    
    protected void stopAndAttack() {
        if (health > 0 && !isAttacking) {
            isAttacking = true;
            attackTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> attack()));
            attackTimeline.setCycleCount(Timeline.INDEFINITE);
            attackTimeline.play();
        }
    }
    
    protected void attack() {
        if (targetPlant != null && health > 0 && targetPlant.getHealth() > 0 && isAttacking) {
            targetPlant.takeDamage(10);
            System.out.println("Zombie is attacking plant! Plant health: " + targetPlant.getHealth());
        } else {
            stopAttack();
        }
    }
    
    protected void stopAttack() {
        isAttacking = false;
    }
    
    protected void findNewTargetPlant() {
        for (BasePlant plant : GameApp.plants) {
            if (plant.getHealth() > 0) {
                targetPlant = plant;
                break;
            }
        }
    }
    
    public void update() {
        this.x -= speed;
        zombieImage.setLayoutX(x);

        if (x < 50) { // ถ้าซอมบี้เดินเข้าไปถึงแมพจริงๆ
            System.out.println("Zombie entered the map!");
        }

        if (x < -50) { // ถ้าซอมบี้เดินพ้นขอบจอ
            System.out.println("Zombie removed!");
            GameApp.gamePane.getChildren().remove(this.zombieImage);
            GameApp.zombies.remove(this);
        }
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

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isEating() {
		return isEating;
	}

	public void setEating(boolean isEating) {
		this.isEating = isEating;
	}

	public boolean isAttacking() {
		return isAttacking;
	}

	public void setAttacking(boolean isAttacking) {
		this.isAttacking = isAttacking;
	}

	public BasePlant getTargetPlant() {
		return targetPlant;
	}

	public void setTargetPlant(BasePlant targetPlant) {
		this.targetPlant = targetPlant;
	}

	public Timeline getAttackTimeline() {
		return attackTimeline;
	}

	public void setAttackTimeline(Timeline attackTimeline) {
		this.attackTimeline = attackTimeline;
	}

	public Timeline getMoveTimeline() {
		return moveTimeline;
	}

	public void setMoveTimeline(Timeline moveTimeline) {
		this.moveTimeline = moveTimeline;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public boolean isDead() {
		return this.health <= 0;
	}
    
}