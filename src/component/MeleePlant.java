package component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import main.GameApp;

public class MeleePlant extends BasePlant {
    private Timeline attackTimer;
    private Timeline attackAnimation;
    private Image idleFrame;
    private Image[] attackFrames;
    private int currentFrame = 0;
    private boolean isAttacking = false;

    public MeleePlant(int x, int y) {
        super(x, y, 100, "/Image/Big_Finish_PunchS1.png");

        // โหลดภาพ idle และภาพโจมตี (4 เฟรม)
        idleFrame = new Image(getClass().getResource("/Image/Big_Finish_PunchS1.png").toExternalForm());
        attackFrames = new Image[4];
        for (int i = 0; i < 4; i++) {
            attackFrames[i] = new Image(getClass().getResource("/Image/Big_Finish_PunchA" + (i + 1) + ".png").toExternalForm());
        }

        GameApp.gamePane.getChildren().add(this.imageView);
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
                    playAttackAnimation();
                    zombie.takeDamage(15);
                    return;
                }
            }
            // ไม่มีซอมบี้ → กลับเป็นภาพ idle
            if (isAttacking) {
                isAttacking = false;
                this.imageView.setImage(idleFrame);
            }
        }));
        attackTimer.setCycleCount(Timeline.INDEFINITE);
        attackTimer.play();
    }

    private void playAttackAnimation() {
        if (isAttacking) return; // ✅ ป้องกันการเริ่มซ้ำ
        isAttacking = true;
        currentFrame = 0;

        if (attackAnimation != null) {
            attackAnimation.stop();
        }

        attackAnimation = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            if (currentFrame < attackFrames.length) {
                this.imageView.setImage(attackFrames[currentFrame]);
                currentFrame++;
            } else {
                this.imageView.setImage(idleFrame);
                isAttacking = false;
                attackAnimation.stop();
            }
        }));
        attackAnimation.setCycleCount(attackFrames.length + 1);
        attackAnimation.play();
    }

    public void stopAttacking() {
        if (attackTimer != null) {
            attackTimer.stop();
            attackTimer = null;
        }
        if (attackAnimation != null) {
            attackAnimation.stop();
            attackAnimation = null;
        }
        System.out.println("🛑 MeleePlant stopped attacking.");
    }

    @Override
    public void performAction() {
        // การโจมตีจัดการโดย Timeline แล้ว
    }

    @Override
    public void die() {
        stopAttacking();
        super.die();
    }
}