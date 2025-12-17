import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * 777组 - 完全静止的大黄蜂算法
 * 用于验证自定义算法是否真的被执行
 * 如果这个算法被正确加载，大黄蜂应该完全不动
 */
public class Hornet extends Bee {
    private int id;
    
    public Hornet(int id, int x, int y, double angle, boolean isAlive, Image img) {
        super(id, x, y, angle, isAlive, img);
        this.id = id;
        System.out.println("🦟 [777组-静止大黄蜂] 初始化成功! ID=" + id);
    }
    
    /**
     * 完全静止的search()方法
     */
    public void search() {
        // 手动把nextX和nextY都设为0
        nextX = new int[9];
        nextY = new int[9];
    }
    
    /**
     * 覆盖flying()方法，确保大黄蜂完全不移动
     */
    @Override
    public void flying(int i) {
        // 大黄蜂完全不移动
        fs = new FlyingStatus(id, posX, posY, angle, isAlive, 0);
        BeeFarming.update(fs);
        setLocation(posX-img.getWidth(null)/2, posY-img.getHeight(null)/2);
    }
    
    /**
     * 黄蜂抓到蜜蜂的回调
     */
    public boolean isCatched() {
        return true;
    }
}