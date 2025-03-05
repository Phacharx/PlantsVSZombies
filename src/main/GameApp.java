package main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicInteger;

import component.*;

import java.util.ArrayList;
import java.util.Iterator;
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
    private static Timeline energyTimeline;
    
    private static Text scoreText;
    
    private static Timeline waveTimer;
    private int currentWave = 0;
    
    private static Timeline moveZombiesTimeline;
    public static boolean isGameOver = false;
    
    private static boolean isGameStarted = false;
    private static boolean isWaveRunning = false;
    private static boolean isZombieMoving = false;
    
    private static int difficultyLevel;
    
    public static int gameSessionId = (int) (Math.random() * 100000); // สุ่มค่า session ใหม่ทุกครั้งที่เริ่มเกม
    
    public static int totalZombiesKilled = 0; // ✅ จำนวนซอมบี้ที่ถูกฆ่า
    public final static int TOTAL_ZOMBIES_TO_WIN = 46; // ✅ จำนวนซอมบี้ที่ต้องฆ่าถึงจะชนะ

    
    public GameApp(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
    
    @Override
    public void start(Stage primaryStage) {
        gamePane = new Pane();
        gamePane.setStyle("-fx-background-color: #b6b9bf;");
        
        Scene scene = new Scene(gamePane, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Plant vs Zombie");

        // ✅ บันทึก instance ของ GameApp ใน stage
        primaryStage.setUserData(this);

        setupGameUI();
        primaryStage.show();
    }

    public static void increaseEnergy(int amount) {
        Platform.runLater(() -> {
            energy += amount;
            energyText.setText("Energy: " + energy);
        });
    }

    public static void increaseScore() {
    	totalZombiesKilled++;
        Platform.runLater(() -> {
        	scoreText.setText("Score: " + totalZombiesKilled);
        }); 
        if (totalZombiesKilled - (difficultyLevel * 4) >= TOTAL_ZOMBIES_TO_WIN) {
            // ✅ หา instance ของ GameApp ที่กำลังรันอยู่ แล้วเรียก youWinScreen()
            Platform.runLater(() -> {
                Stage stage = (Stage) GameApp.gamePane.getScene().getWindow();
                GameApp gameAppInstance = (GameApp) stage.getUserData(); 
                if (gameAppInstance != null) {
                    gameAppInstance.youWinScreen(); 
                }
            });
        }
    }

    private void placePlant(int row, int col) {
        if (!isGameStarted) {
            System.out.println("⚠ You must press 'Start' before placing plants!");
            return;
        }

        if (selectedPlantType == null) return; // ❌ ไม่ทำงานถ้ายังไม่เลือกการ์ด

        int cost;
        int xPos = 40 + col * 85;
        int yPos = 140 + row * 85;

        // ✅ ตรวจสอบว่ามีพืชอยู่ในตำแหน่งนี้หรือไม่
        for (BasePlant plant : GameApp.plants) {
            if (plant.getX() == xPos && plant.getY() == yPos) {
                System.out.println("⚠ A plant already exists here!");
                return;
            }
        }

        // ✅ ตั้งค่าราคาตามประเภทของพืช
        switch (selectedPlantType) {
            case "Shooter":
                cost = 40;
                break;
            case "Melee":
                cost = 25;
                break;
            case "Defensive":
                cost = 30;
                break;
            default:
                System.out.println("⚠ Invalid plant type selected!");
                return;
        }

        if (energy < cost) {  // ✅ ถ้าเงินไม่พอ ให้ return ออกไปเลย
            System.out.println("⚠ Not enough energy to place " + selectedPlantType);
            return;
        }

        // ✅ หักพลังงานก่อนสร้างพืช
        energy -= cost;
        energyText.setText("Energy: " + energy);

        // ✅ สร้างพืชหลังจากตรวจสอบเงื่อนไขทั้งหมดแล้ว
        BasePlant newPlant;
        switch (selectedPlantType) {
            case "Shooter":
                newPlant = new Shooter(xPos, yPos);
                break;
            case "Melee":
                newPlant = new MeleePlant(xPos, yPos);
                break;
            case "Defensive":
                newPlant = new DefensivePlant(xPos, yPos);
                break;
            default:
                return;
        }

        // ✅ ตรวจสอบว่าพืชอยู่ใน `gamePane` แล้วหรือยัง ก่อน `add()`
        if (!gamePane.getChildren().contains(newPlant.getImageView())) {
            gamePane.getChildren().add(newPlant.getImageView());
        } else {
            System.out.println("⚠ Plant already exists in gamePane! Avoiding duplicate.");
        }

        GameApp.plants.add(newPlant);
    }

    private void startWaves() {
        if (isWaveRunning) return; // ⛔ ป้องกันการรันซ้ำซ้อน

        isWaveRunning = true; // ✅ ตั้งค่าว่ากำลังมี Wave
        System.out.println("🚀 startWaves() called");

        int[] zombiesPerWave = {6 + difficultyLevel, 10 + difficultyLevel, 14 + difficultyLevel, 16 + difficultyLevel}; 
        currentWave = 1;
        spawnWave(currentWave, zombiesPerWave[currentWave - 1], () -> {
            startNextWave(zombiesPerWave);
        });
    }
    
    private void startNextWave(int[] zombiesPerWave) {
        if (isGameOver) {
            isWaveRunning = false;
            return; // ⛔ หยุดถ้าเกมจบแล้ว
        }

        if (currentWave < 4) {
            System.out.println("⏳ Waiting 25 seconds before starting wave " + (currentWave + 1));

            waveTimer = new Timeline(new KeyFrame(Duration.seconds(25), e -> {
                if (isGameOver) {
                    isWaveRunning = false;
                    return;
                }

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
            isWaveRunning = false; // ✅ ป้องกันการล็อค
        }
    }

    private void spawnWave(int waveNumber, int totalZombies, Runnable onComplete) {
        if (isGameOver) return; // ⛔ หยุดถ้าเกมจบแล้ว

        System.out.println("🚀 Spawning wave " + waveNumber + " with " + totalZombies + " zombies.");

        AtomicInteger remainingZombies = new AtomicInteger(totalZombies);
        Timeline waveTimeline = new Timeline();

        KeyFrame spawnFrame = new KeyFrame(Duration.seconds(2), e -> {
            if (isGameOver) {
                System.out.println("⚠ Stopping wave " + waveNumber + " because Game Over.");
                waveTimeline.stop();
                return;
            }

            if (remainingZombies.get() > 0) {
                int lane = (int) (Math.random() * 5);
                spawnZombie(lane, waveNumber);
                remainingZombies.decrementAndGet();
            }

            if (remainingZombies.get() <= 0) {
                System.out.println("⚠ Wave " + waveNumber + " ended!");
                waveTimeline.stop();
                if (onComplete != null) {
                    onComplete.run();              
                }
            }
        });

        waveTimeline.getKeyFrames().add(spawnFrame);
        waveTimeline.setCycleCount(totalZombies);
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
        if (isZombieMoving) return;
        isZombieMoving = true;

        System.out.println("moveZombies() started");

        moveZombiesTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            if (GameApp.isGameOver) {  // ✅ หยุดเมื่อเกมโอเวอร์
                moveZombiesTimeline.stop();
                return;
            }

            Iterator<BaseZombie> iterator = zombies.iterator();
            while (iterator.hasNext()) {
                BaseZombie zombie = iterator.next();
                zombie.move();

                if (zombie.getX() <= 0) {
                    System.out.println("⚠ Zombie escaped! Game Over!");
                    gameOverScreen();
                    resetGame();
                    return;
                }
            }
        }));
        moveZombiesTimeline.setCycleCount(Timeline.INDEFINITE);
        moveZombiesTimeline.play();
    }

    private void spawnZombie(int lane, int waveNumber) {
        int baseHealth = 40; // เลือดพื้นฐานของซอมบี้
        double baseSpeed = 0.0; // ความเร็วพื้นฐาน

        int zombieHealth = baseHealth + (difficultyLevel * 7);
        double zombieSpeed = baseSpeed + (difficultyLevel * 0.1);

        BaseZombie zombie;
        if (waveNumber == 1) {
            zombie = new Zombie(900, 140 + lane * 85, zombieHealth + 50, zombieSpeed + 0.5);
        } else if (waveNumber == 2) {
            zombie = (Math.random() < 0.5) 
                ? new FastZombie(900, 140 + lane * 85, zombieHealth, zombieSpeed + 1.5) 
                : new Zombie(900, 140 + lane * 85, zombieHealth + 50, zombieSpeed + 0.5);
        } else if (waveNumber == 3) {
            zombie = (Math.random() < 0.33) 
                ? new HeavyZombie(900, 140 + lane * 85, zombieHealth + 200, zombieSpeed) 
                : new Zombie(900, 140 + lane * 85, zombieHealth + 50, zombieSpeed + 0.5);
        } else {
            zombie = (Math.random() < 0.25) 
                ? new HeavyZombie(900, 140 + lane * 85, zombieHealth + 200, zombieSpeed) 
                : new FastZombie(900, 140 + lane * 85, zombieHealth, zombieSpeed + 1.5);
        }

        zombies.add(zombie);
        Platform.runLater(() -> {
            if (!gamePane.getChildren().contains(zombie.getImageView())) {
                gamePane.getChildren().add(zombie.getImageView());
            }
        });

    }

    public void youWinScreen() {
        if (isGameOver) return;
        isGameOver = true;

        // ✅ หยุด Timeline ทั้งหมด
        if (energyTimeline != null) energyTimeline.stop();
        if (moveZombiesTimeline != null) moveZombiesTimeline.stop();
        if (waveTimer != null) waveTimer.stop();

        // ✅ หยุดการยิงของ Shooter
        for (BasePlant plant : plants) {
            if (plant instanceof Shooter) {
                ((Shooter) plant).stopShooting();
            }
        }

        // ✅ ลบพืชและซอมบี้ทั้งหมด
        Platform.runLater(() -> {
            gamePane.getChildren().clear();
        });
        plants.clear();
        zombies.clear();
        projectiles.clear();
        
        System.out.println("🎉 YOU WIN! All zombies defeated!");

        // ✅ แสดงหน้าจอ "You Win!!"
        Rectangle overlay = new Rectangle(900, 600);
        overlay.setFill(Color.BLACK);
        overlay.setOpacity(0.5);

        Text winText = new Text("YOU WIN!!");
        winText.setFont(new Font(50));
        winText.setFill(Color.LIMEGREEN);
        winText.setLayoutX(320);
        winText.setLayoutY(250);

        javafx.scene.control.Button restartButton = new javafx.scene.control.Button("Play Again");
        restartButton.setLayoutX(400);
        restartButton.setLayoutY(300);
        restartButton.setOnAction(e -> {
        	restartGame();
        	resetGame();
        });

        Platform.runLater(() -> {
            gamePane.getChildren().addAll(overlay, winText, restartButton);
        });
    }

    
    private static void gameOverScreen() {
        if (isGameOver) return;
        isGameOver = true;

        // ✅ หยุด Timeline ทั้งหมด
        if (energyTimeline != null) energyTimeline.stop();
        if (moveZombiesTimeline != null) moveZombiesTimeline.stop();
        if (waveTimer != null) waveTimer.stop();

        // ✅ หยุดการยิงของ Shooter
        for (BasePlant plant : plants) {
            if (plant instanceof Shooter) {
                ((Shooter) plant).stopShooting();
            }
        }

        // ✅ ลบพืชและซอมบี้ทั้งหมด
        plants.clear();
        zombies.clear();
        projectiles.clear();
        
        Platform.runLater(() -> {
            gamePane.getChildren().clear(); // ล้างหน้าจอ
        });

        System.out.println("⚠ Game Over! Stopping all zombie movement and shooting.");

        // ✅ ใช้ Timeline เพื่อหน่วงเวลาก่อนแสดง Game Over UI
        new Timeline(new KeyFrame(Duration.millis(50), e -> {
            Platform.runLater(() -> {
                Rectangle overlay = new Rectangle(900, 600);
                overlay.setFill(Color.BLACK);
                overlay.setOpacity(0.5);

                Text gameOverText = new Text("Game Over");
                gameOverText.setFont(new Font(50));
                gameOverText.setFill(Color.RED);
                gameOverText.setLayoutX(320);
                gameOverText.setLayoutY(250);

                javafx.scene.control.Button restartButton = new javafx.scene.control.Button("Restart");
                restartButton.setLayoutX(400);
                restartButton.setLayoutY(300);
                restartButton.setOnAction(event -> restartGame());

                gamePane.getChildren().addAll(overlay, gameOverText, restartButton);
            });

            System.out.println("🔥 Showing Game Over UI");
        })).play();
    }

    
    public static void resetGame() {
        Platform.runLater(() -> {
            // ❌ หยุดทุก Timeline ของซอมบี้
            for (BaseZombie zombie : GameApp.zombies) {
                if (zombie.moveTimeline != null) zombie.moveTimeline.stop();
                if (zombie.walkAnimation != null) zombie.walkAnimation.stop();
                if (zombie.attackTimer != null) zombie.attackTimer.stop();
                zombie.deactivate();
            }

            // ❌ หยุดทุก Timeline ของพืช (เช่น Shooter)
            for (BasePlant plant : GameApp.plants) {
                if (plant instanceof Shooter) {
                    ((Shooter) plant).stopShooting(); // ต้องมีฟังก์ชันหยุด
                }
            }

            // ❌ หยุดทุก Timeline ของกระสุน (Projectile)
            for (Projectile projectile : GameApp.projectiles) {
                if (projectile.moveTimeline != null) projectile.moveTimeline.stop();
            }

            // 🗑 ลบทุก Object ในเกม
            GameApp.gamePane.getChildren().clear(); // ลบทุกอย่างจากหน้าจอ
            GameApp.plants.clear();  // ลบพืชทั้งหมด
            GameApp.zombies.clear(); // ลบซอมบี้ทั้งหมด
            GameApp.projectiles.clear(); // ลบกระสุนทั้งหมด
            
            Platform.runLater(() -> {
                gamePane.getChildren().clear();
//                System.out.println("🔍 Remaining Zombies: " + zombies.size()); // Debug
//                System.out.println("🔍 Remaining Plants: " + plants.size());
            });
            
            gameOverScreen();

            // 🛑 แสดง Debug Log
            System.out.println("🛑 เกมถูกรีเซ็ต! ทุก Timeline หยุดทำงาน และลบ Object ทั้งหมด");
        });
    }
    
    private static void restartGame() {
        System.out.println("🔄 Returning to Start Screen...");

        // ✅ รีเซ็ตค่าเกม
        isGameOver = false;
        isGameStarted = false;
        isWaveRunning = false;
        isZombieMoving = false;
        totalZombiesKilled = 0;
        energy = 100;

        // ✅ รีเซ็ต session ID ใหม่
        GameApp.gameSessionId = (int) (Math.random() * 100000);
        System.out.println("🆕 New Game Session ID: " + GameApp.gameSessionId);

        // ✅ ลบพืชทั้งหมด
        for (BasePlant plant : new ArrayList<>(plants)) {
            if (plant instanceof Shooter) {
                ((Shooter) plant).stopShooting();
            }
            Platform.runLater(() -> gamePane.getChildren().remove(plant.getImageView()));
        }
        plants.clear();

        // ✅ หยุดและลบซอมบี้ทั้งหมด
        for (BaseZombie zombie : new ArrayList<>(zombies)) {
            zombie.deactivate();
            Platform.runLater(() -> gamePane.getChildren().remove(zombie.getImageView()));
        }
        zombies.clear();

        // ✅ ลบกระสุนทั้งหมด
        for (Projectile projectile : new ArrayList<>(projectiles)) {
            Platform.runLater(() -> gamePane.getChildren().remove(projectile.getImageView()));
        }
        projectiles.clear();

        // ✅ ล้าง UI ออกจาก gamePane
        Platform.runLater(() -> gamePane.getChildren().clear());

        // ✅ เปลี่ยนเป็นหน้า StartScreen
        Platform.runLater(() -> {
            try {
                StartScreen startScreen = new StartScreen();
                Stage stage = (Stage) gamePane.getScene().getWindow();
                startScreen.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



    private void setupGameUI() {
        // ✅ ลบพืชที่เหลืออยู่จากเกม (ทั้งจาก gamePane และ List)
        for (BasePlant plant : new ArrayList<>(plants)) { 
            gamePane.getChildren().remove(plant.getImageView());
        }
        plants.clear(); 
        
        // ✅ ตั้งค่าสีพื้นหลังเป็นสีเทา
        gamePane.setStyle("-fx-background-color: #b6b9bf;");

        // ✅ โหลดแผนที่
        ImageView background = new ImageView(new Image(getClass().getResource("/Image/kaiju_map.png").toExternalForm()));
        background.setFitWidth(850);
        background.setFitHeight(425);
        background.setLayoutX(30);
        background.setLayoutY(130);
        gamePane.getChildren().add(background);

        // ✅ แสดงพลังงาน
        energyText = new Text("Energy: " + energy);
        energyText.setFont(new Font(20));
        energyText.setFill(Color.WHITE);
        energyText.setLayoutX(550);
        energyText.setLayoutY(50);
        gamePane.getChildren().add(energyText);
        
        // ✅ เพิ่มข้อความแสดงคะแนน
        scoreText = new Text("Score: " + totalZombiesKilled);
        scoreText.setFont(new Font(20));
        scoreText.setFill(Color.WHITE);
        scoreText.setLayoutX(700);
        scoreText.setLayoutY(50);
        gamePane.getChildren().add(scoreText);

        // ✅ 🔥 ตรวจสอบให้แน่ใจว่า energyTimeline ถูกสร้างก่อนเริ่มเกม
        if (energyTimeline == null) {
            energyTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                energy += 5 + difficultyLevel;
                energyText.setText("Energy: " + energy);
            }));
            energyTimeline.setCycleCount(Timeline.INDEFINITE);
        }

        // ✅ รีสร้าง Grid ตารางใหม่
        grid = new GridPane();
        grid.setLayoutX(30);
        grid.setLayoutY(130);

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 10; col++) {
                Pane cell = new Pane();
                cell.setPrefSize(85, 85);

                // ✅ สร้างกรอบตาราง
                Rectangle border = new Rectangle(85, 85);
                border.setFill(Color.TRANSPARENT);
                border.setStroke(Color.WHITE);
                border.setStrokeWidth(0.5);

                cell.getChildren().add(border);
                int finalRow = row, finalCol = col;
                cell.setOnMouseClicked(event -> placePlant(finalRow, finalCol));
                grid.add(cell, col, row);
            }
        }
        gamePane.getChildren().add(grid);

        // ✅ รีเซ็ตการ์ดพืชให้สามารถเลือกได้ใหม่
        setupPlantSelectionBar();

        // ✅ เพิ่มข้อความนับถอยหลัง
        Text countdownText = new Text("3");
        countdownText.setFont(new Font(50));
        countdownText.setFill(Color.RED);
        countdownText.setLayoutX(420);
        countdownText.setLayoutY(300);
        gamePane.getChildren().add(countdownText);

        // ✅ ตั้งค่า Timer เพื่อนับถอยหลัง 3-2-1 แล้วเริ่มเกม
        Timeline countdown = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> countdownText.setText("2")),
            new KeyFrame(Duration.seconds(2), e -> countdownText.setText("1")),
            new KeyFrame(Duration.seconds(3), e -> {
                gamePane.getChildren().remove(countdownText);
                startGame(); // ✅ เริ่มเกมอัตโนมัติ
            })
        );
        countdown.setCycleCount(1);
        countdown.play();
    }

    // ✅ ฟังก์ชันเริ่มเกมอัตโนมัติ
    private void startGame() {
        isGameStarted = true;
        isWaveRunning = false;
        isZombieMoving = false;

        // ✅ 🔥 ตรวจสอบว่า energyTimeline ถูกสร้างแล้ว ก่อนเรียก play()
        if (energyTimeline != null) {
            energyTimeline.play();
        } else {
            System.out.println("⚠ Warning: energyTimeline is null!");
        }

        startWaves();
        moveZombies();
    }



    private void setupPlantSelectionBar() {
        HBox plantSelectionBar = new HBox(20);
        plantSelectionBar.setLayoutX(50);
        plantSelectionBar.setLayoutY(20);

        String[] plantNames = {"Shooter", "Melee", "Defensive"};
        String[] plantImages = {"/Image/CardShooter.jpg", "/Image/CardMelee.jpg", "/Image/CardDefensive.jpg"};

        for (int i = 0; i < plantNames.length; i++) {
            ImageView plantCard = new ImageView(new Image(getClass().getResource(plantImages[i]).toExternalForm()));
            plantCard.setFitWidth(80);
            plantCard.setFitHeight(80);

            String plantType = plantNames[i];
            plantCard.setOnMouseClicked(event -> {
                if (!isGameStarted) { // ✅ ถ้ายังไม่กด Start จะไม่สามารถเลือกการ์ดได้
                    System.out.println("⚠ You must press 'Start' before selecting plants!");
                    return;
                }

                if (selectedPlantType != null && selectedPlantType.equals(plantType)) {
                    selectedPlantType = null;
                    selectedPlantCard.setOpacity(1.0);
                    selectedPlantCard = null;
                } else {
                    if (selectedPlantCard != null) {
                        selectedPlantCard.setOpacity(1.0);
                    }
                    selectedPlantType = plantType;
                    selectedPlantCard = plantCard;
                    plantCard.setOpacity(0.5);
                }
            });

            plantSelectionBar.getChildren().add(plantCard);
        }
        gamePane.getChildren().add(plantSelectionBar);
    }



    public static void main(String[]args) {
        launch(args);
    }
}