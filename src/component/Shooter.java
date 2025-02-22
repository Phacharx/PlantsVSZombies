package component;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import main.GameApp;

public class Shooter extends Plant {
    private boolean canShoot = true; // การควบคุมการยิงกระสุน
    private Zombie targetZombie; // ซอมบี้ที่เป็นเป้าหมาย

    public Shooter(int x, int y) {
        super(100, x, y); // เรียกใช้ constructor ของ Plant
    }

    @Override
    public void performAction() {
        if (canShoot && health > 0) {
            // ค้นหาซอมบี้ตัวถัดไปที่พืชจะยิง
            targetZombie = getNextTargetZombie();
            if (targetZombie != null && targetZombie.getHealth() > 0) {
                shoot(); // เมื่อพืชสามารถยิงได้ และซอมบี้ยังมีชีวิต
            }
        }
    }

    // ฟังก์ชันยิงกระสุน
    public void shoot() {
        System.out.println("Shooter is shooting!");
        // สร้างกระสุนใหม่
        Projectile projectile = new Projectile(x + 50, y + 20); // กระสุนออกจากพืช
        GameApp.projectiles.add(projectile);
        GameApp.gamePane.getChildren().add(projectile.getCircle()); // เพิ่มกระสุนลงในหน้าจอ

        // การหน่วงเวลาให้ยิงซ้ำได้ทุก 0.5 วินาที
        PauseTransition pause = new PauseTransition(Duration.seconds(10)); // รอ 0.5 วินาที
        pause.setOnFinished(event -> canShoot = true); // เมื่อครบเวลาจะให้ยิงกระสุนใหม่
        pause.play();

        canShoot = false; // ห้ามยิงซ้ำจนกว่าจะครบเวลา
    }

    // ฟังก์ชันหาซอมบี้ตัวถัดไป
    public Zombie getNextTargetZombie() {
        for (Zombie zombie : GameApp.zombies) {
            if (zombie.getHealth() > 0) { // ถ้าซอมบี้ยังมีชีวิตอยู่
                return zombie; // คืนค่าซอมบี้ตัวถัดไปที่ยังมีชีวิต
            }
        }
        return null; // ไม่มีซอมบี้ที่ยังมีชีวิต
    }

    @Override
    public void die() {
        super.die(); // เรียกใช้ die() ของพืชทั่วไป
        canShoot = false; // หยุดการยิงเมื่อพืชตาย
    }
}
