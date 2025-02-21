package component;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import main.GameApp;

public class Shooter extends Plant {
    private boolean canShoot = true; // ตัวแปรในการควบคุมการยิง

    public Shooter(int x, int y) {
        super(100, x, y);
    }

    @Override
    public void performAction() {
        if (canShoot) {
            shoot();
        }
    }

    public void shoot() {
        System.out.println("Shooter is shooting!");
        // สร้างกระสุนใหม่และเพิ่มลงในเกม
        Projectile projectile = new Projectile(x + 50, y + 20); // กระสุนออกจากพืช
        GameApp.projectiles.add(projectile);
        GameApp.gamePane.getChildren().add(projectile.getCircle()); // เพิ่มกระสุนใน gamePane

        // ใช้ PauseTransition เพื่อพักสักครู่ก่อนยิงกระสุนใหม่
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5)); // รอ 0.5 วินาที
        pause.setOnFinished(event -> canShoot = true); // เมื่อครบเวลาให้ยิงกระสุนใหม่
        pause.play();

        canShoot = false; // ห้ามยิงซ้ำจนกว่าจะพัก
    }
}
