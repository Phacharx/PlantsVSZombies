package component;

import java.util.Iterator;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.GameApp;

public abstract class BasePlant {
    protected int health;
    protected int x, y;
    protected ImageView imageView;

    public BasePlant(int x, int y, int health, String imagePath) {
        this.health = health;
        this.x = x;
        this.y = y;
        this.imageView = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        this.imageView.setFitWidth(70);
        this.imageView.setFitHeight(70);
        this.imageView.setX(x);
        this.imageView.setY(y);
//        if (!GameApp.gamePane.getChildren().contains(this.imageView)) {
//            GameApp.gamePane.getChildren().add(this.imageView);
//        }
//        GameApp.plants.add(this);
    }

    public void performAction() {
    	
    }

    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("Plant took damage: " + damage + " | HP left: " + health);

        if (health <= 0) {
            System.out.println("Plant destroyed!");
            die();
        }
    }


    public void die() {
        System.out.println("🔥 Plant died at X=" + this.x + ", Y=" + this.y);

        // ✅ ใช้ Platform.runLater() เพื่อลบ UI อย่างปลอดภัย
        Platform.runLater(() -> {
            boolean removedFromGamePane = GameApp.gamePane.getChildren().remove(this.imageView);
            System.out.println("📌 Removed from gamePane: " + removedFromGamePane);
        });

        // ✅ ลบจาก List
        boolean removedFromPlants = GameApp.plants.remove(this);
        System.out.println("✅ Plant removed from GameApp.plants: " + removedFromPlants);

        GameApp.printPlantList(); // ตรวจสอบรายการพืชที่เหลือ
    }




    public ImageView getImageView() {
        return imageView;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        this.imageView.setX(x);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        this.imageView.setY(y);
    }
    public boolean isDead() {
		return this.health <= 0;
	}
}
