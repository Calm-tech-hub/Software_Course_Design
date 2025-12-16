import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 记录完整的一局游戏的所有帧数据
 */
public class GameRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String group1;                // 组1号（蜜蜂算法）
    public String group2;                // 组2号（大黄蜂算法）
    public int roundNumber;              // 第几轮对局
    public long timestamp;               // 时间戳
    public List<GameFrame> frames;       // 所有帧数据
    public int finalHoney;               // 最终采集花蜜量
    public int finalAliveCount;          // 最终存活蜜蜂数
    public int finalScore;               // 最终得分
    public String winner;                // 获胜方（或平局）
    
    public GameRecord(String group1, String group2, int roundNumber) {
        this.group1 = group1;
        this.group2 = group2;
        this.roundNumber = roundNumber;
        this.timestamp = System.currentTimeMillis();
        this.frames = new ArrayList<>();
    }
    
    public void addFrame(GameFrame frame) {
        frames.add(frame);
    }
    
    public int getFrameCount() {
        return frames.size();
    }
    
    public GameFrame getFrame(int index) {
        if (index >= 0 && index < frames.size()) {
            return frames.get(index);
        }
        return null;
    }
    
    /**
     * 保存记录到文件
     */
    public void saveToFile(String directory) throws IOException {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String filename = String.format("%s/battle_%svs%s_round%d_%d.dat",
                directory, group1, group2, roundNumber, timestamp);
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(this);
        }
    }
    
    /**
     * 从文件加载记录
     */
    public static GameRecord loadFromFile(String filepath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filepath))) {
            return (GameRecord) ois.readObject();
        }
    }
    
    @Override
    public String toString() {
        return String.format("GameRecord[%s vs %s, Round %d, Frames=%d, Score=%d]",
                group1, group2, roundNumber, frames.size(), finalScore);
    }
}
