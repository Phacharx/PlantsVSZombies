package main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
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

import component.BasePlant;
import component.Projectile;
import component.Shooter;
import component.MeleePlant;
import component.Zombie;

import java.util.ArrayList;
import java.util.List;

public class GameApp extends Application {
    public static Pane gamePane;
    public static List<Projectile> projectiles = new ArrayList<>();
    public static List<BasePlant> plants = new ArrayList<>();
    public static List<Zombie> zombies = new ArrayList<>();
    private GridPane grid;
    private String selectedPlantType = null;
    private ImageView selectedPlantCard = null;
    private Timeline gameTimer;
    
    private int energy = 100; // พลังงานเริ่มต้น 100
    private Text energyText;
    private Timeline energyTimeline;

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
        
        // ปุ่ม Start และตัวจับเวลา
        Text timerText = new Text("3:00");
        timerText.setFont(new Font(20));
        timerText.setFill(Color.WHITE);
        timerText.setLayoutX(750);
        timerText.setLayoutY(50);
        gamePane.getChildren().add(timerText);
        
        javafx.scene.control.Button startButton = new javafx.scene.control.Button("Start");
        startButton.setLayoutX(600);
        startButton.setLayoutY(30);
        gamePane.getChildren().add(startButton);
        
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            String[] timeParts = timerText.getText().split(":");
            int minutes = Integer.parseInt(timeParts[0]);
            int seconds = Integer.parseInt(timeParts[1]);
            if (minutes == 0 && seconds == 0) return;
            if (seconds == 0) {
                minutes--;
                seconds = 59;
            } else {
                seconds--;
            }
            timerText.setText(String.format("%d:%02d", minutes, seconds));
        }));
        gameTimer.setCycleCount(180);

        // เพิ่มระบบพลังงาน: ทุก 3 วินาทีเพิ่ม 5 พลังงาน
        energyTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            energy += 5;
            energyText.setText("Energy: " + energy);
        }));
        energyTimeline.setCycleCount(Timeline.INDEFINITE);
        
        startButton.setOnAction(e -> {
            gameTimer.play();
            energyTimeline.play(); // เริ่มเพิ่มพลังงานเมื่อกด Start
        });
        
        Scene scene = new Scene(gamePane, 910, 600);
        primaryStage.setTitle("Plant vs Zombie");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void placePlant(int row, int col) {
        if (selectedPlantType != null) {
            int cost = selectedPlantType.equals("Shooter") ? 40 : 25;
            if (energy >= cost) {
                energy -= cost;
                energyText.setText("Energy: " + energy);
                
                String plantImagePath = selectedPlantType.equals("Shooter") ? "/Image/Big_Mina.png" : "/Image/Big_Finish_PunchS2.png";
                ImageView plantImage = new ImageView(new Image(getClass().getResource(plantImagePath).toExternalForm()));
                plantImage.setFitWidth(65);
                plantImage.setFitHeight(65);
                plantImage.setLayoutX(40 + col * 85);
                plantImage.setLayoutY(140 + row * 85);
                
                gamePane.getChildren().add(plantImage);
                selectedPlantCard.setOpacity(1.0); // คืนค่าเดิมเมื่อวางพืช
                selectedPlantType = null;
            } else {
            	selectedPlantCard.setOpacity(1.0); // คืนค่าเดิมเมื่อวางพืช
                selectedPlantType = null;
                System.out.println("Not enough energy to place " + selectedPlantType);
            }
        }
    }

    public static void main(String[]args) {
        launch(args);
    }
}
