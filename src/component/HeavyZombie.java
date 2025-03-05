package component;

public class HeavyZombie extends BaseZombie {
    public HeavyZombie(int x, int y, int health, double speed) {
        super("HeavyZombie", x, y, health, speed, 45, 90); // ✅ ขนาด 50x80
    }
}
