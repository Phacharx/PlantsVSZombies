package component;

public class FastZombie extends BaseZombie {
    public FastZombie(int x, int y, int health, double speed) {
        super("FastZombie", x, y, health, speed, 70, 70); // ✅ ขนาด 35x65
    }
}
