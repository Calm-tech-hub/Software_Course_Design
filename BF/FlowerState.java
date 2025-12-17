import java.io.Serializable;

/**
 * 记录单朵花在某一帧的状态
 */
public class FlowerState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public int id;          // 花的ID（索引）
    public int x;           // X坐标
    public int y;           // Y坐标
    public int volume;      // 剩余花蜜量
    public int maxVolume;   // 初始花蜜量
    public int appearTime;  // 出现时间
    public int imageType;   // 图片类型（0,1,2）
    public boolean visible; // 是否可见（已出现且未采完）
    
    public FlowerState(int id, int x, int y, int volume, int maxVolume, 
                       int appearTime, int imageType, boolean visible) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.volume = volume;
        this.maxVolume = maxVolume;
        this.appearTime = appearTime;
        this.imageType = imageType;
        this.visible = visible;
    }
    
    @Override
    public String toString() {
        return String.format("Flower[id=%d, pos=(%d,%d), volume=%d/%d, visible=%b]",
                id, x, y, volume, maxVolume, visible);
    }
}
