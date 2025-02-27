package component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import main.GameApp;

public class Projectile {
	private int x, y;
    private ImageView projectileImage;
    private int damage = 25;
    private Timeline moveTimeline;
    
    public Projectile(int x, int y) {
        this.x = x;
        this.y = y;
        this.projectileImage = new ImageView(new Image(getClass().getResource("/Image/Big_Energy_Ball.png").toExternalForm()));
        this.projectileImage.setFitWidth(20);
        this.projectileImage.setFitHeight(20);
        this.projectileImage.setX(x);
        this.projectileImage.setY(y);
        
        GameApp.gamePane.getChildren().add(projectileImage);
        
        moveTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> move()));
        moveTimeline.setCycleCount(Timeline.INDEFINITE);
        moveTimeline.play();
    }
    
    public void move() {
        x += 10;
        projectileImage.setX(x);

        for (BaseZombie zombie : GameApp.zombies) {
            if (Math.abs(zombie.getX() - x) < 30 && Math.abs(zombie.getY() - y) < 30) {
                zombie.takeDamage(damage);
                GameApp.gamePane.getChildren().remove(projectileImage);
                moveTimeline.stop();
                GameApp.projectiles.remove(this);
                break;
            }
        }
    }

    public void remove() {
        GameApp.gamePane.getChildren().remove(this.projectileImage);
    }

    public int getDamage() {
        return damage;
    }

    public ImageView getImageView() {
        return projectileImage;
    }
}
