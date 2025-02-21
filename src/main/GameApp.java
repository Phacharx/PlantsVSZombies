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
    private List<Plant> plants;
    private List<Zombie> zombies;

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

        Scene scene = new Scene(gamePane, 600, 400);
        primaryStage.setTitle("Plant vs Zombie");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateGame() {
        // Update ซอมบี้
        for (Zombie zombie : zombies) {
            zombie.move();
            if (zombie.getX() < 100) {
                zombie.attack();
            }
        }

        // ใช้ Iterator สำหรับลบกระสุนที่ชนกับซอมบี้
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.move();
            for (Zombie zombie : zombies) {
                if (projectile.getCircle().getBoundsInParent().intersects(zombie.getRectangle().getBoundsInParent())) {
                    zombie.takeDamage(projectile.getDamage()); // ส่งความเสียหายให้ซอมบี้
                    iterator.remove(); // ลบกระสุนจาก projectiles
                    gamePane.getChildren().remove(projectile.getCircle()); // ลบกระสุนจากหน้าจอ
                    break;
                }
            }
        }

        // ตรวจสอบการยิงของพืช
        for (Plant plant : plants) {
            plant.performAction();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
