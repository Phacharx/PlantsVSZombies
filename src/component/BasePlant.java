package component;

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
        if (health <= 0) {
            die();
        }
    }

    public void die() {
        System.out.println("Plant died at X=" + this.x + ", Y=" + this.y);
        
        GameApp.gamePane.getChildren().remove(this.imageView);
        boolean removed = GameApp.plants.remove(this);
        
        if (removed) {
            System.out.println("Plant successfully removed from GameApp.plants.");
        } else {
            System.out.println("⚠ Error: Plant was not found in GameApp.plants.");
        }
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
