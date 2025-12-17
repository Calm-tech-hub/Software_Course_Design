import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 记录游戏中某一帧的完整状态
 */
public class GameFrame implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public int frameNumber;              // 帧编号
    public int time;                     // 游戏时间
    public List<BeeState> beeStates;     // 所有蜜蜂的状态
    public List<FlowerState> flowerStates; // 所有花朵的状态
    public int totalHoney;               // 总采集花蜜量
    public int aliveBeesCount;           // 存活蜜蜂数量
    public int remainingFlowersCount;    // 剩余花朵数量（有蜜的）
    
    public GameFrame(int frameNumber, int time) {
        this.frameNumber = frameNumber;
        this.time = time;
        this.beeStates = new ArrayList<>();
        this.flowerStates = new ArrayList<>();
        this.totalHoney = 0;
        this.aliveBeesCount = 0;
        this.remainingFlowersCount = 0;
    }
    
    public void addBeeState(BeeState beeState) {
        beeStates.add(beeState);
        if (beeState.isAlive) {
            aliveBeesCount++;
        }
    }
    
    public void addFlowerState(FlowerState flowerState) {
        flowerStates.add(flowerState);
        if (flowerState.visible && flowerState.volume > 0) {
            remainingFlowersCount++;
        }
    }
    
    @Override
    public String toString() {
        return String.format("Frame[%d] time=%d, bees=%d/%d alive, flowers=%d, honey=%d",
                frameNumber, time, aliveBeesCount, beeStates.size(), 
                remainingFlowersCount, totalHoney);
    }
}
