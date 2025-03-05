package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StartScreen extends Application {
    @Override
    public void start(Stage primaryStage) {
        // ✅ ข้อความแสดงชื่อเกม
        Text titleText = new Text("Plant vs Zombie");
        titleText.setFont(new Font(40));

        // ✅ เลือกระดับความยาก
        Text difficultyLabel = new Text("Select Difficulty:");
        difficultyLabel.setFont(new Font(20));

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

        // ✅ Layout หลักของ Start Screen
        VBox root = new VBox(20, titleText, difficultyLabel, difficultyBox, startButton);
        root.setAlignment(javafx.geometry.Pos.CENTER);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Plant vs Zombie - Start");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
