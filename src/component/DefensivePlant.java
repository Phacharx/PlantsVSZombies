package component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import main.GameApp;

public class DefensivePlant extends BasePlant {
    private Image[] shieldFrames;
    private int currentFrame = 0;
    private Timeline shieldAnimation;
    private boolean isShielded = false;

    public DefensivePlant(int x, int y) {
        super(x, y, 150, "/Image/Big_Defensive_Plant_1.png");

        // โหลดภาพโล่ทั้งหมด 6 เฟรม
        shieldFrames = new Image[6];
        for (int i = 0; i < 6; i++) {
            shieldFrames[i] = new Image(getClass().getResource("/Image/Big_Defensive_Plant_" + (i + 1) + ".png").toExternalForm());
        }

        GameApp.gamePane.getChildren().add(this.imageView);

        // ตั้งค่า Timeline เพื่อตรวจจับซอมบี้และเปิด/ปิดโล่
        Timeline detectionLoop = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> detectZombie()));
        detectionLoop.setCycleCount(Timeline.INDEFINITE);
        detectionLoop.play();
    }

    private void detectZombie() {
        boolean zombieNearby = false;
        for (BaseZombie zombie : GameApp.zombies) {
            if (Math.abs(zombie.getX() - this.x) < 85 && zombie.getY() == this.y) {
                zombieNearby = true;
                break;
            }
        }

        if (zombieNearby && !isShielded) {
            expandShield();
        } else if (!zombieNearby && isShielded) {
            retractShield();
        }
    }

    private void expandShield() {
        isShielded = true;
        playAnimation(0, 5); // เล่นจากเฟรม 1 → 6
    }

    private void retractShield() {
        isShielded = false;
        playAnimation(5, 0); // เล่นจากเฟรม 6 → 1
    }

    private void playAnimation(int startFrame, int endFrame) {
        int step = (startFrame < endFrame) ? 1 : -1;
        int frameCount = Math.abs(endFrame - startFrame) + 1; // คำนวณจำนวนเฟรมที่ต้องเล่น

        if (shieldAnimation != null) {
            shieldAnimation.stop();
        }

        shieldAnimation = new Timeline();
        
        for (int i = 0; i < frameCount; i++) {
            int frameIndex = startFrame + (i * step);
            double frameTime = 75 * (i + 1); // ใช้ 200ms ต่อเฟรม
        
            // ป้องกันไม่ให้ Duration เป็น 0 หรือติดลบ
            if (frameTime < 1) frameTime = 1;

            shieldAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(frameTime), e -> {
                this.imageView.setImage(shieldFrames[frameIndex]);
            }));
        }

        shieldAnimation.setCycleCount(1);
        shieldAnimation.play();
    }


    @Override
    public void performAction() {
        // ไม่มีการโจมตี
    }
}
