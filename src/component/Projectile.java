package component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import main.GameApp;

public class Projectile {
    private int x, y;
    private ImageView projectileImage;
    private int damage = 10;
    public Timeline moveTimeline;
    private boolean isRemoved = false; // ✅ ป้องกันการ remove ซ้ำ

    // ✅ ใช้ static image เพื่อลดการโหลดซ้ำ
    private static final Image PROJECTILE_IMAGE = new Image(
        Projectile.class.getResource("/Image/Big_Energy_Ball.png").toExternalForm()
    );

    public Projectile(int x, int y) {
        this.x = x;
        this.y = y;
        this.projectileImage = new ImageView(PROJECTILE_IMAGE);
        this.projectileImage.setFitWidth(15);
        this.projectileImage.setFitHeight(15);
        this.projectileImage.setX(x);
        this.projectileImage.setY(y);

        // ✅ ตรวจสอบก่อนเพิ่มเข้า gamePane
        Platform.runLater(() -> {
            if (!GameApp.gamePane.getChildren().contains(this.projectileImage)) {
                GameApp.gamePane.getChildren().add(this.projectileImage);
            }
        });

        moveTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> move()));
        moveTimeline.setCycleCount(Timeline.INDEFINITE);
        moveTimeline.play();
    }

    public void move() {
        if (isRemoved) return; // ✅ ป้องกันการ move หลังจาก remove

        x += 5;
        projectileImage.setX(x);

        // ✅ ลบกระสุนเมื่อมันออกจากหน้าจอ
        if (x > 900) {
            remove();
            return;
        }

        // ✅ ตรวจสอบการชนกับซอมบี้
        for (BaseZombie zombie : GameApp.zombies) {
            if (Math.abs(zombie.getX() - x) < 30 && Math.abs(zombie.getY() - y) < 30) {
//            	System.out.println("🧟 Zombie at X=" + zombie.getX() + ", Y=" + zombie.getY());
                zombie.takeDamage(damage);
                remove();
                break;
            }
        }
    }

    public void remove() {
        if (isRemoved) return; // ✅ ป้องกันการ remove ซ้ำ
        isRemoved = true;

        Platform.runLater(() -> {
            if (GameApp.gamePane.getChildren().contains(this.projectileImage)) {
                GameApp.gamePane.getChildren().remove(this.projectileImage);
            }
        });

        if (moveTimeline != null) {
            moveTimeline.stop();
            moveTimeline = null; // ✅ ล้าง Timeline เพื่อลดการใช้หน่วยความจำ
        }

        GameApp.projectiles.remove(this);
    }

    public int getDamage() {
        return damage;
    }

    public ImageView getImageView() {
        return projectileImage;
    }
}
