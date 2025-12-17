import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * 777组 - 完全静止的蜜蜂算法
 * 用于验证自定义算法是否真的被执行
 * 如果这个算法被正确加载，蜜蜂应该完全不动
 */
public class HoneyBee extends Bee {
    private int id;
    
    public HoneyBee(int id, int x, int y, double angle, boolean isAlive, Image img) {
        super(id, x, y, angle, isAlive, img);
        this.id = id;
        System.out.println("🐝 [777组-静止蜜蜂] 初始化成功! ID=" + id);
    }
    
    /**
     * 完全静止的search()方法
     */
    public void search() {
        // 手动把nextX和nextY都设为0
        nextX = new int[9];
        nextY = new int[9];
        // 数组默认值就是0,所以蜜蜂不会移动
    }
    
    /**
     * 覆盖flying()方法，确保蜜蜂完全不移动
     */
    @Override
    public void flying(int i) {
        // 什么都不做，让nextX[i]和nextY[i]都是0
        // 这样posX和posY不会改变
        // 但还是要更新状态
        fs = new FlyingStatus(id, posX, posY, angle, isAlive, 0);
        BeeFarming.update(fs);
        setLocation(posX-img.getWidth(null)/2, posY-img.getHeight(null)/2);
    }
}