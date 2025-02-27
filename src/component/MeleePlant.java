package component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.application.Platform;
import main.GameApp;

public class MeleePlant extends BasePlant {
    private Timeline attackTimer;

    public MeleePlant(int x, int y) {
        super(x, y, 100, "/Image/Big_Finish_PunchS1.png");

        GameApp.gamePane.getChildren().add(this.imageView); // ✅ ใช้ imageView จาก BasePlant
        startAttacking();
    }

    private void startAttacking() {
        attackTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (isDead()) { // ✅ ตรวจสอบว่าตายหรือยัง
                stopAttacking();
                return;
            }
            for (BaseZombie zombie : GameApp.zombies) {
                if (Math.abs(zombie.getX() - this.x) < 50 && zombie.getY() == this.y) {
                    zombie.takeDamage(15);
                    break;
                }
            }
        }));
        attackTimer.setCycleCount(Timeline.INDEFINITE);
        attackTimer.play();
    }

    public void stopAttacking() {
        if (attackTimer != null) {
            attackTimer.stop();
            attackTimer = null;
            System.out.println("🛑 MeleePlant stopped attacking.");
        }
    }

    @Override
    public void performAction() {
        // การโจมตีจัดการโดย Timeline แล้ว
    }

    @Override
    public void die() {
        stopAttacking(); // ✅ หยุด Timeline ก่อนตาย
        super.die(); // ✅ ลบออกจาก GameApp.plants และ gamePane
    }
}
