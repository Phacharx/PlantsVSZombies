package main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import component.Plant;
import component.Projectile;
import component.Shooter;
import component.Zombie;

public class GameApp extends Application {
    public static Pane gamePane; // gamePane เป็น public static
    public static List<Projectile> projectiles = new ArrayList<>(); // projectiles เป็น public static
    public static List<Plant> plants;
    public static List<Zombie> zombies;

    public GameApp() {
        plants = new ArrayList<>();
        zombies = new ArrayList<>();
    }

    @Override
    public void start(Stage primaryStage) {
        gamePane = new Pane(); // สร้าง gamePane

        // สร้างพืชและซอมบี้
        Plant plant1 = new Shooter(100, 300);
        Zombie zombie1 = new Zombie(500, 300);

        // เพิ่มพืชและซอมบี้ลงในเกม
        plants.add(plant1);
        zombies.add(zombie1);

        gamePane.getChildren().add(plant1.getRectangle());
        gamePane.getChildren().add(zombie1.getRectangle());

        // การเคลื่อนไหวของเกม
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.05), e -> updateGame()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Timeline สำหรับการสร้างซอมบี้ทุกๆ 10 วินาที
        Timeline zombieSpawnTimeline = new Timeline(
            new KeyFrame(Duration.seconds(20), e -> spawnZombie())
        );
        zombieSpawnTimeline.setCycleCount(Timeline.INDEFINITE); // ทำให้มันวนซ้ำไปเรื่อย ๆ
        zombieSpawnTimeline.play(); // เริ่มต้นการสร้างซอมบี้ทุกๆ 10 วินาที

        Scene scene = new Scene(gamePane, 600, 400);
        primaryStage.setTitle("Plant vs Zombie");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ฟังก์ชันการสร้างซอมบี้ใหม่ทุกๆ 10 วินาที
    private void spawnZombie() {
        // สร้างซอมบี้ใหม่
        Zombie newZombie = new Zombie(500, 300); // ตำแหน่งเริ่มต้นของซอมบี้
        zombies.add(newZombie);
        gamePane.getChildren().add(newZombie.getRectangle()); // เพิ่มซอมบี้ลงในหน้าจอ

        System.out.println("A new zombie has appeared!");
    }

    private void updateGame() {
        // Update ซอมบี้
        for (Zombie zombie : zombies) {
            zombie.move();
            if (zombie.getX() < 100) {
                zombie.attack(); // ซอมบี้โจมตีพืช
            }

            // ตรวจสอบซอมบี้ที่กำลังกินพืช
            if (zombie.isEating()) {
                zombie.takeDamage(10); // ลดเลือดซอมบี้ทุก 1 วินาที (ซอมบี้กำลังกินพืช)
            }
        }

        // ตรวจสอบกระสุน
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.move();  // เคลื่อนที่กระสุน
            boolean isHit = false; // ตรวจสอบว่ากระสุนชนกับซอมบี้หรือไม่

            // ตรวจสอบการชนกับซอมบี้
            for (Zombie zombie : zombies) {
                if (projectile.getCircle().getBoundsInParent().intersects(zombie.getRectangle().getBoundsInParent())) {
                    zombie.takeDamage(projectile.getDamage()); // ส่งความเสียหายให้ซอมบี้
                    isHit = true; // กระสุนชนซอมบี้
                    if (zombie.getHealth() <= 0) {
                        // ลบซอมบี้จากหน้าจอหากซอมบี้ตาย
                        GameApp.gamePane.getChildren().remove(zombie.getRectangle());
                        GameApp.zombies.remove(zombie);
                    }
                    break;
                }
            }

            // ลบกระสุนออกจากหน้าจอถ้ากระสุนชนกับซอมบี้หรือกระสุนออกจากหน้าจอ
            if (isHit || projectile.getX() > gamePane.getWidth()) {
                iterator.remove(); // ลบกระสุนจาก projectiles
                gamePane.getChildren().remove(projectile.getCircle()); // ลบกระสุนจากหน้าจอ
            }
        }

        // ให้พืชทุกตัวทำการยิงกระสุน (ตรวจสอบพืชที่ยังมีชีวิตอยู่)
        for (Plant plant : plants) {
            if (!plant.isDead()) { // ให้พืชยิงกระสุนได้เฉพาะเมื่อพืชยังไม่ตาย
                plant.performAction(); // เรียกใช้ performAction() เพื่อให้พืชยิงกระสุน
            }
        }

        // ให้ซอมบี้ตั้งเป้าหมายเป็นพืช
        for (Zombie zombie : zombies) {
            if (zombie.getTargetPlant() == null && !zombie.isEating()) {
                for (Plant plant : plants) {
                    if (Math.abs(zombie.getX() - plant.getX()) < 50) { // ตรวจสอบว่าซอมบี้ไปถึงพืชแล้ว
                        zombie.setTargetPlant(plant);
                        break;
                    }
                }
            }
        }
    }






    public static void main(String[] args) {
        launch(args);
    }
}
