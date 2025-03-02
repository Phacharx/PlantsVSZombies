package main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.BlurType;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import component.*;

import java.lang.classfile.instruction.SwitchCase;
import java.util.ArrayList;
import java.util.List;

public class GameApp extends Application {
    public static Pane gamePane;
    public static List<Projectile> projectiles = new ArrayList<>();
    public static List<BasePlant> plants = new ArrayList<>();
    public static List<BaseZombie> zombies = new ArrayList<>();
    private GridPane grid;
    private String selectedPlantType = null;
    private ImageView selectedPlantCard = null;
    
    private static int energy = 100; // พลังงานเริ่มต้น 100
    private static Text energyText;
    private Timeline energyTimeline;
    
    private Timeline waveTimer;
    private int currentWave = 0;


    @Override
    public void start(Stage primaryStage) {
        gamePane = new Pane();
        
        // โหลดแผนที่เป็นพื้นหลัง
        ImageView background = new ImageView(new Image(getClass().getResource("/Image/kaiju_map.png").toExternalForm()));
        background.setFitWidth(850);
        background.setFitHeight(425);
        background.setLayoutX(30);
        background.setLayoutY(130);
        gamePane.getChildren().add(background);
        
        // แสดงจำนวนพลังงาน
        energyText = new Text("Energy: " + energy);
        energyText.setFont(new Font(20));
        energyText.setFill(Color.WHITE);
        energyText.setLayoutX(500);
        energyText.setLayoutY(50);
        gamePane.getChildren().add(energyText);
        
        // แถบเลือกพืช
        HBox plantSelectionBar = new HBox(20);
        plantSelectionBar.setLayoutX(50);
        plantSelectionBar.setLayoutY(20);
        
        String[] plantNames = {"Shooter", "Melee"};
        String[] plantImages = {"/Image/Big_Mina.png", "/Image/Big_Finish_PunchS2.png"};
        for (int i = 0; i < plantNames.length; i++) {
            ImageView plantCard = new ImageView(new Image(getClass().getResource(plantImages[i]).toExternalForm()));
            plantCard.setFitWidth(80);
            plantCard.setFitHeight(80);
            
            String plantType = plantNames[i];
            plantCard.setOnMouseClicked(event -> {
                if (selectedPlantType != null && selectedPlantType.equals(plantType)) {
                    selectedPlantType = null;
                    selectedPlantCard.setOpacity(1.0); // คืนค่าปกติ
                    selectedPlantCard = null;
                    System.out.println("Deselected: " + plantType);
                } else {
                    if (selectedPlantCard != null) {
                        selectedPlantCard.setOpacity(1.0); // คืนค่าปกติให้พืชที่เลือกก่อนหน้า
                    }
                    selectedPlantType = plantType;
                    selectedPlantCard = plantCard;
                    plantCard.setOpacity(0.5); // ทำให้พืชที่เลือกเป็นสีเทาอ่อน
                    System.out.println("Selected: " + plantType);
                }
            });
            
            plantSelectionBar.getChildren().add(plantCard);
        }
        gamePane.getChildren().add(plantSelectionBar);
        
        // สร้างตารางเกม (7x10 ช่อง)
        grid = new GridPane();
        grid.setLayoutX(30);
        grid.setLayoutY(130);
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 10; col++) {
                Pane cell = new Pane();
                cell.setPrefSize(85, 85);
                int finalRow = row, finalCol = col;
                cell.setOnMouseClicked(event -> placePlant(finalRow, finalCol));
                grid.add(cell, col, row);
            }
        }
        gamePane.getChildren().add(grid);
        
        // ปุ่ม Start
        
        javafx.scene.control.Button startButton = new javafx.scene.control.Button("Start");
        startButton.setLayoutX(600);
        startButton.setLayoutY(30);
        gamePane.getChildren().add(startButton);
        

        // เพิ่มระบบพลังงาน: ทุก 3 วินาทีเพิ่ม 5 พลังงาน
        energyTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            energy += 5;
            energyText.setText("Energy: " + energy);
        }));
        energyTimeline.setCycleCount(Timeline.INDEFINITE);
        
        startButton.setOnAction(e -> {
            System.out.println("Start button clicked");
            energyTimeline.play();
            startWaves();
            moveZombies();
        });

        
        Scene scene = new Scene(gamePane, 910, 600);
        primaryStage.setTitle("Plant vs Zombie");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void increaseEnergy(int amount) {
        Platform.runLater(() -> {
            energy += amount;
            energyText.setText("Energy: " + energy);
        });
    }

    private void placePlant(int row, int col) {
    	if (selectedPlantType != null) {
    	    int cost = selectedPlantType.equals("Shooter") ? 40 : 25;
    	    if (energy >= cost) {
    	        for (BasePlant plant : GameApp.plants) {
    	            if (plant.getX() == (40 + col * 85) && plant.getY() == (140 + row * 85)) {
    	                System.out.println("⚠ A plant already exists here!");
    	                return;
    	            }
    	        }

    	        energy -= cost;
    	        energyText.setText("Energy: " + energy);

    	        BasePlant newPlant = selectedPlantType.equals("Shooter") ?
    	                new Shooter(40 + col * 85, 140 + row * 85) :
    	                new MeleePlant(40 + col * 85, 140 + row * 85);

    	        GameApp.plants.add(newPlant);
    	        System.out.println("✅ Plant placed at X=" + newPlant.getX() + ", Y=" + newPlant.getY());

    	        printPlantList(); // เช็คว่าพืชถูกเพิ่มลงไปจริง ๆ หรือไม่
    	    } else {
    	        System.out.println("⚠ Not enough energy to place " + selectedPlantType);
    	    }
    	}
    }

    private void startWaves() {
        System.out.println("startWaves() called");

        int[] zombiesPerWave = {8, 12, 16, 20}; // จำนวนซอมบี้ในแต่ละเวฟ

        currentWave = 1;
        spawnWave(currentWave, zombiesPerWave[currentWave - 1], () -> {
            startNextWave(zombiesPerWave);
        });
    }

    private void startNextWave(int[] zombiesPerWave) {
        if (currentWave < 4) {
            System.out.println("⏳ Waiting 45 seconds before starting wave " + (currentWave + 1));

            waveTimer = new Timeline(new KeyFrame(Duration.seconds(45), e -> {
                currentWave++;
                System.out.println("🚀 Starting wave " + currentWave);
                spawnWave(currentWave, zombiesPerWave[currentWave - 1], () -> {
                    startNextWave(zombiesPerWave);
                });
            }));
            waveTimer.setCycleCount(1);
            waveTimer.play();
        } else {
            System.out.println("🎉 All waves completed!");
        }
    }

    
    private void spawnWave(int waveNumber, int totalZombies, Runnable onComplete) {
        System.out.println("🚀 spawnWave() started for wave " + waveNumber + " with " + totalZombies + " zombies.");

        AtomicInteger remainingZombies = new AtomicInteger(totalZombies);
        Timeline waveTimeline = new Timeline();

        // ✅ เริ่มต้นการ Spawn ทันที
        KeyFrame initialSpawn = new KeyFrame(Duration.seconds(0), e -> {
            int lane = (int) (Math.random() * 5); // สุ่มเลน (0-4)
            spawnZombie(lane, waveNumber);
            remainingZombies.decrementAndGet();

            System.out.println("🔄 Spawning zombie in wave " + waveNumber + " (Remaining: " + remainingZombies.get() + " zombies)");
        });

        waveTimeline.getKeyFrames().add(initialSpawn);

        // ✅ เพิ่ม KeyFrame สำหรับการ spawn ซอมบี้ต่อเนื่องทุก 7 - 10 วินาที
        KeyFrame spawnFrame = new KeyFrame(Duration.seconds(7 + Math.random() * 3), e -> {
            if (remainingZombies.get() > 0) {
                int lane = (int) (Math.random() * 5);
                spawnZombie(lane, waveNumber);
                remainingZombies.decrementAndGet();

                double nextSpawnDelay = 7 + Math.random() * 3;
                System.out.println("⏳ Next zombie in " + nextSpawnDelay + " seconds (Remaining: " + remainingZombies.get() + " zombies)");

                // ✅ เพิ่ม KeyFrame ใหม่เข้าไปใน Timeline
                waveTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(nextSpawnDelay), e2 -> {
                    if (remainingZombies.get() > 0) {
                        spawnZombie((int) (Math.random() * 5), waveNumber);
                        remainingZombies.decrementAndGet();
                        System.out.println("🔄 Spawning zombie in wave " + waveNumber + " (Remaining: " + remainingZombies.get() + " zombies)");
                    }
                    if (remainingZombies.get() <= 0) {
                        System.out.println("⚠ Wave " + waveNumber + " ended!");
                        waveTimeline.stop();
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                }));
            }
        });

        waveTimeline.getKeyFrames().add(spawnFrame);
        waveTimeline.setCycleCount(Timeline.INDEFINITE);
        waveTimeline.play();
    }

    // ตรวจสอบว่า plants อัปเดตถูกต้องหรือไม่
    public static void printPlantList() {
        System.out.println("🔍 Current Plants in Game:");
        for (BasePlant plant : plants) {
            System.out.println("🌿 Plant at X=" + plant.getX() + ", Y=" + plant.getY() + ", Health" + plant.getHealth());
        }
    }
    
    private void moveZombies() {
        System.out.println("moveZombies() started"); // Debugging

        Timeline zombieMoveTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            for (BaseZombie zombie : zombies) {
                zombie.move(); // อัปเดตตำแหน่ง
//                System.out.println("Zombie moved to X: " + zombie.getImageView().getX());
            }
        }));
        zombieMoveTimeline.setCycleCount(Timeline.INDEFINITE);
        zombieMoveTimeline.play();
    }
    
    private void spawnZombie(int lane, int waveNumber) {
        BaseZombie zombie;

        // กำหนดประเภทของซอมบี้ตามเวฟ
        if (waveNumber == 1) {
            zombie = new Zombie(900, 140 + lane * 85);
        } else if (waveNumber == 2) {
            zombie = (Math.random() < 0.5) ? new FastZombie(900, 140 + lane * 85) : new Zombie(900, 140 + lane * 85);
        } else if (waveNumber == 3) {
            zombie = (Math.random() < 0.33) ? new HeavyZombie(900, 140 + lane * 85) : new Zombie(900, 140 + lane * 85);
        } else {
            zombie = (Math.random() < 0.25) ? new HeavyZombie(900, 140 + lane * 85) : new FastZombie(900, 140 + lane * 85);
        }

        // ✅ ตรวจสอบก่อนเพิ่มซอมบี้ลงในเกม
        if (!zombies.contains(zombie)) {
            zombies.add(zombie);
            Platform.runLater(() -> {
                if (!gamePane.getChildren().contains(zombie.getImageView())) { // ✅ ป้องกันการเพิ่มซ้ำ
                    gamePane.getChildren().add(zombie.getImageView());
                    System.out.println("Spawned zombie at lane " + lane + " position: " + zombie.getImageView().getX());
                }
            });
        } else {
            System.out.println("⚠ Zombie already exists at lane " + lane + ", skipping.");
        }
    }

    public static void main(String[]args) {
        launch(args);
    }
}