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
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import component.Plant;
import component.Projectile;
import component.Shooter;
import component.MeleePlant;
import component.Zombie;

import java.util.ArrayList;
import java.util.List;

public class GameApp extends Application {
    public static Pane gamePane;
    public static List<Projectile> projectiles = new ArrayList<>();
    public static List<Plant> plants = new ArrayList<>();
    public static List<Zombie> zombies = new ArrayList<>();
    private GridPane grid;
    private String selectedPlantType = null;
    private Timeline gameTimer;

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
        
        // สร้างตารางเกม (7x10 ช่อง)
        grid = new GridPane();
        grid.setLayoutX(30);
        grid.setLayoutY(130);
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 10; col++) {
                Pane cell = new Pane();
                cell.setPrefSize(85, 85);
                int finalRow = row, finalCol = col;
                cell.setOnMouseDragReleased(event -> placePlant(finalRow, finalCol));
                grid.add(cell, col, row);
            }
        }
        gamePane.getChildren().add(grid);
        
        // เพิ่มเส้นตารางให้ชัดขึ้น
        for (int row = 0; row <= 7; row++) {
            Line line = new Line(30, 130 + row * 85, 880, 130 + row * 85);
            line.setStroke(Color.WHITE);
            gamePane.getChildren().add(line);
        }
        for (int col = 0; col <= 10; col++) {
            Line line = new Line(30 + col * 85, 130, 30 + col * 85, 130 + 7 * 85);
            line.setStroke(Color.WHITE);
            gamePane.getChildren().add(line);
        }
        
        // เพิ่มแถบเลือกพืชเป็นกล่องสี่เหลี่ยมที่ลากได้
        HBox plantBoxBar = new HBox(20);
        plantBoxBar.setLayoutX(50);
        plantBoxBar.setLayoutY(20);
        
        String[] plantNames = {"Shooter", "Melee"};
        String[] plantImages = {"/Image/Big_Mina.png", "/Image/Big_Finish_PunchS2.png"};
        for (int i = 0; i < plantNames.length; i++) {
            Pane plantBox = new Pane();
            plantBox.setPrefSize(80, 50);
            plantBox.setStyle("-fx-background-image: url('" + plantImages[i] + "'); -fx-background-size: cover; -fx-border-color: black;");
            
            String plantType = plantNames[i];
            plantBox.setOnDragDetected(event -> {
                selectedPlantType = plantType;
                plantBox.startFullDrag();
                System.out.println("Dragging " + plantType);
            });
            
            plantBoxBar.getChildren().add(plantBox);
        }
        gamePane.getChildren().add(plantBoxBar);
        
        // ปุ่ม Start และตัวจับเวลา
        Text timerText = new Text("3:00");
        timerText.setFont(new Font(20));
        timerText.setFill(Color.WHITE);
        timerText.setLayoutX(750);
        timerText.setLayoutY(50);
        gamePane.getChildren().add(timerText);
        
        javafx.scene.control.Button startButton = new javafx.scene.control.Button("Start");
        startButton.setLayoutX(680);
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
        
        startButton.setOnAction(e -> gameTimer.play());
        
        Scene scene = new Scene(gamePane, 910, 600);
        primaryStage.setTitle("Plant vs Zombie");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void placePlant(int row, int col) {
        if (selectedPlantType != null) {
            String plantImagePath = selectedPlantType.equals("Shooter") ? "/Image/Big_Mina.png" : "/Image/Big_Finish_PunchS2.png";
            ImageView plantImage = new ImageView(new Image(getClass().getResource(plantImagePath).toExternalForm()));
            plantImage.setFitWidth(65);
            plantImage.setFitHeight(65);
            plantImage.setLayoutX(40 + col * 85);
            plantImage.setLayoutY(140 + row * 85);
            
            gamePane.getChildren().add(plantImage);
            selectedPlantType = null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
