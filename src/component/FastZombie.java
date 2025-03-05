package component;

public class FastZombie extends BaseZombie {
    public FastZombie(int x, int y, int health, double speed) {
        super(new String[]{
            "/Image/FastZombieS.png",
            "/Image/FastZombieR.png",
            "/Image/FastZombieS.png",
            "/Image/FastZombieL.png"
        }, x, y, health, speed);
    }
}
