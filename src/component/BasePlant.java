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
        GameApp.gamePane.getChildren().add(this.imageView);
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
        GameApp.gamePane.getChildren().remove(this.imageView);
        GameApp.plants.remove(this);
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
}
