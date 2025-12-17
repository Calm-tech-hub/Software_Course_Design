import java.io.Serializable;

/**
 * 记录单只蜜蜂在某一帧的状态
 */
public class BeeState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public int id;           // 蜜蜂ID
    public int x;            // X坐标
    public int y;            // Y坐标
    public double angle;     // 飞行角度
    public boolean isAlive;  // 是否存活
    public int honey;        // 已采集的花蜜量（个体）
    
    public BeeState(int id, int x, int y, double angle, boolean isAlive, int honey) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.isAlive = isAlive;
        this.honey = honey;
    }
    
    @Override
    public String toString() {
        return String.format("Bee[id=%d, pos=(%d,%d), angle=%.1f, alive=%b, honey=%d]",
                id, x, y, angle, isAlive, honey);
    }
}
