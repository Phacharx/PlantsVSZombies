package main;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StartScreen extends Application {
    @Override
    public void start(Stage primaryStage) {
        // ✅ โหลดรูปภาพพื้นหลัง (ต้องมีไฟล์อยู่ใน /Image/)
        Image backgroundImage = new Image(getClass().getResource("/Image/BackGround1.png").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(
            backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
            BackgroundPosition.CENTER, new BackgroundSize(900, 600, false, false, false, false)
        );

        // ✅ พื้นหลังของ VBox
        VBox root = new VBox(20);
        root.setBackground(new Background(bgImage));
        root.setAlignment(Pos.CENTER);

        // ✅ ข้อความแสดงชื่อเกม
        Text titleText = new Text("Plant vs Zombie");
        titleText.setFont(new Font(50));
        titleText.setFill(Color.WHITE);

        // ✅ เลือกระดับความยาก
        Text difficultyLabel = new Text("Select Difficulty:");
        difficultyLabel.setFont(new Font(30));
        difficultyLabel.setFill(Color.WHITE);

        ComboBox<String> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll("Easy (1)", "Normal (2)", "Hard (3)", "Very Hard (4)", "Insane (5)");
        difficultyBox.setValue("Normal (2)"); // ค่าเริ่มต้น

        // ✅ ปุ่ม Start
        Button startButton = new Button("Start Game");
        startButton.setFont(new Font(20));
        startButton.setOnAction(e -> {
            int difficultyLevel = difficultyBox.getSelectionModel().getSelectedIndex() + 1; // ดึงค่าระดับที่เลือก
            GameApp gameApp = new GameApp(difficultyLevel); // ✅ ส่งระดับความยากไปที่ GameApp
            try {
                gameApp.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // ✅ เพิ่มองค์ประกอบ UI ลงใน VBox
        root.getChildren().addAll(titleText, difficultyLabel, difficultyBox, startButton);

        // ✅ ตั้งค่า Scene และล็อกขนาด
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Plant vs Zombie - Start");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // ปิดการขยายหน้าต่าง
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
