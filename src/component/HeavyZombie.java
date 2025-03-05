package component;

public class HeavyZombie extends BaseZombie {
    public HeavyZombie(int x, int y, int health, double speed) {
        super(new String[]{
            "/Image/Big_Kai_Ju_9_WalkS.png",
            "/Image/Big_Kai_Ju_9_WalkR.png",
            "/Image/Big_Kai_Ju_9_WalkS.png",
            "/Image/Big_Kai_Ju_9_WalkL.png"
        }, x, y, health, speed);
    }
}
