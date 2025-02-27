package component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import main.GameApp;

public class Shooter extends BasePlant {
    private Timeline shootTimer;
    private ImageView imageView;

    public Shooter(int x, int y) {
    	super(x, y, 100, "/Image/Big_Mina.png"); // ส่ง path ของรูป
        this.imageView = new ImageView(new Image(getClass().getResource("/Image/Big_Mina.png").toExternalForm()));
        this.imageView.setFitWidth(65);
        this.imageView.setFitHeight(65);
        this.imageView.setLayoutX(x);
        this.imageView.setLayoutY(y);
        GameApp.gamePane.getChildren().add(this.imageView);

        startShooting();
    }

    private void startShooting() {
        shootTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            for (BaseZombie zombie : GameApp.zombies) {
                if (zombie.getY() == this.y && zombie.getX() < 900) {
                    shoot();
                    break;
                }
            }
        }));
        shootTimer.setCycleCount(Timeline.INDEFINITE);
        shootTimer.play();
    }

    private void shoot() {
        Projectile projectile = new Projectile(this.x + 60, this.y + 20);
        GameApp.projectiles.add(projectile);
        GameApp.gamePane.getChildren().add(projectile.getImageView());
    }

    @Override
    public void performAction() {
        // Shooting is handled by the timeline
    }
}
