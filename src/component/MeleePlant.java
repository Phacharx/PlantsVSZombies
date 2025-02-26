package component;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import main.GameApp;

public class MeleePlant extends Plant {
    private boolean canAttack = true; // ควบคุมการโจมตีให้เกิดคูลดาวน์

    public MeleePlant(int x, int y) {
        super(150, x, y); // กำหนดค่าเลือดเริ่มต้นให้พืชเป็น 150 (ปรับได้ตามต้องการ)
    }

    @Override
    public void performAction() {
        if (canAttack && !isDead()) {
            // ตรวจสอบซอมบี้ในเกมว่ามีตัวไหนอยู่ในระยะโจมตี (ระยะ 50 หน่วย)
            for (Zombie zombie : GameApp.zombies) {
                if (zombie.getHealth() > 0 && isInRange(zombie)) {
                    attack(zombie);
                    break; // โจมตีแค่ตัวแรกที่เจอในระยะ
                }
            }
        }
    }

    // ฟังก์ชันตรวจสอบว่าซอมบี้อยู่ในระยะโจมตีหรือไม่
    private boolean isInRange(Zombie zombie) {
        // เปรียบเทียบระยะห่างในแกน X ระหว่างพืชกับซอมบี้ (สามารถปรับให้ละเอียดขึ้นได้)
        return Math.abs(this.x - zombie.getX()) < 50;
    }

    // ฟังก์ชันโจมตีซอมบี้ที่อยู่ในระยะ
    private void attack(Zombie zombie) {
        System.out.println("MeleePlant กำลังโจมตีซอมบี้!");
        zombie.takeDamage(20); // ลดเลือดซอมบี้ลง 20 หน่วย (ปรับค่าความเสียหายได้ตามต้องการ)
        
        // ตั้งคูลดาวน์เพื่อป้องกันการโจมตีต่อเนื่อง
        canAttack = false;
        PauseTransition pause = new PauseTransition(Duration.seconds(1)); // ตั้งเวลาคูลดาวน์ 1 วินาที
        pause.setOnFinished(event -> canAttack = true);
        pause.play();
    }

    @Override
    public void die() {
        super.die();
        canAttack = false; // ยืนยันว่าหยุดการโจมตีเมื่อพืชตาย
    }
}
