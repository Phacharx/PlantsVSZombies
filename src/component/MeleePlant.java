package component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import main.GameApp;

public class MeleePlant extends BasePlant {
    private Timeline attackTimer;
    private ImageView imageView;

    public MeleePlant(int x, int y) {
    	super(x, y, 100, "/Image/Big_Finish_PunchS1.png");
        this.imageView = new ImageView(new Image(getClass().getResource("/Image/Big_Finish_PunchS1.png").toExternalForm()));
        this.imageView.setFitWidth(65);
        this.imageView.setFitHeight(65);
        this.imageView.setLayoutX(x);
        this.imageView.setLayoutY(y);
        GameApp.gamePane.getChildren().add(this.imageView);

        startAttacking();
    }

    private void startAttacking() {
        attackTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            for (BaseZombie zombie : GameApp.zombies) {
                if (Math.abs(zombie.getX() - this.x) < 50 && zombie.getY() == this.y) {
                    zombie.takeDamage(50);
                    break;
                }
            }
        }));
        attackTimer.setCycleCount(Timeline.INDEFINITE);
        attackTimer.play();
    }

    @Override
    public void performAction() {
        // Attack is handled by the timeline
    }
}
