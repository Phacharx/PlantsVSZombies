package component;

public class Zombie extends BaseZombie {
    public Zombie(int x, int y, int health, double speed) {
        super("Zombie", x, y, health, speed, 40, 70); // ✅ ขนาด 40x70
    }
}
