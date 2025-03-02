package component;

public class Zombie extends BaseZombie {
    public Zombie(int x, int y) {
        super(new String[]{
            "/Image/Big_Kai_Ju_9_WalkS.png",
            "/Image/Big_Kai_Ju_9_WalkR.png",
            "/Image/Big_Kai_Ju_9_WalkS.png",
            "/Image/Big_Kai_Ju_9_WalkL.png"
        }, x, y, 50, 2.5);
    }
}
