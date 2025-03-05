package component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import main.GameApp;

public class Shooter extends BasePlant {
    public Timeline shootTimer;

    public Shooter(int x, int y) {
        super(x, y, 100, "/Image/Big_Mina.png");
        if (!GameApp.gamePane.getChildren().contains(this.imageView)) { // ✅ ป้องกันเพิ่มซ้ำ
            GameApp.gamePane.getChildren().add(this.imageView);
        } else {
            System.out.println("⚠ Shooter already exists in gamePane! Avoiding duplicate.");
        }
        startShooting();
    }

    private void startShooting() {
        shootTimer = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
            if (isDead()) { // หยุดยิงถ้าพืชตาย
                stopShooting();
                return;
            }

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
        if (isDead()) return; // ❌ ไม่ยิงถ้าพืชตายแล้ว

        Projectile projectile = new Projectile(this.x + 60, this.y + 20);
        GameApp.projectiles.add(projectile);

        // ✅ ตรวจสอบก่อนเพิ่มเข้า gamePane
        if (!GameApp.gamePane.getChildren().contains(projectile.getImageView())) {
            GameApp.gamePane.getChildren().add(projectile.getImageView());
        }
    }


    public void stopShooting() {
        if (shootTimer != null) {
            shootTimer.stop();
            shootTimer = null;
//            System.out.println("🛑 Shooter stopped shooting.");
        }
    }

    @Override
    public void performAction() {
        // Shooting is handled by the timeline
    }

    @Override
    public void die() {
        stopShooting(); // ✅ หยุดยิงก่อนตาย
        super.die();    // ✅ เรียก die() ใน BasePlant
    }
}