import java.io.*;

/**
 * 回放控制器
 * 管理游戏记录的回放，提供播放控制功能
 */
public class ReplayController {
    private GameRecord record;
    private int currentFrame;
    private boolean isPlaying;
    private int playSpeed; // 播放速度倍数 (1=正常, 2=2倍速, etc.)
    
    public ReplayController() {
        this.currentFrame = 0;
        this.isPlaying = false;
        this.playSpeed = 1;
    }
    
    /**
     * 加载游戏记录文件
     */
    public boolean loadRecord(String filepath) {
        try {
            record = GameRecord.loadFromFile(filepath);
            currentFrame = 0;
            System.out.println("成功加载记录: " + record);
            System.out.println("总帧数: " + record.getFrameCount());
            return true;
        } catch (Exception e) {
            System.err.println("加载记录失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取当前记录
     */
    public GameRecord getRecord() {
        return record;
    }
    
    /**
     * 获取当前帧
     */
    public GameFrame getCurrentFrame() {
        if (record != null && currentFrame >= 0 && currentFrame < record.getFrameCount()) {
            return record.getFrame(currentFrame);
        }
        return null;
    }
    
    /**
     * 获取当前帧编号
     */
    public int getCurrentFrameNumber() {
        return currentFrame;
    }
    
    /**
     * 获取总帧数
     */
    public int getTotalFrames() {
        return record != null ? record.getFrameCount() : 0;
    }
    
    /**
     * 跳转到指定帧
     */
    public boolean seekToFrame(int frameNumber) {
        if (record != null && frameNumber >= 0 && frameNumber < record.getFrameCount()) {
            currentFrame = frameNumber;
            return true;
        }
        return false;
    }
    
    /**
     * 下一帧
     */
    public boolean nextFrame() {
        if (record != null && currentFrame < record.getFrameCount() - 1) {
            currentFrame++;
            return true;
        }
        return false;
    }
    
    /**
     * 上一帧
     */
    public boolean previousFrame() {
        if (record != null && currentFrame > 0) {
            currentFrame--;
            return true;
        }
        return false;
    }
    
    /**
     * 跳到第一帧
     */
    public void rewind() {
        currentFrame = 0;
    }
    
    /**
     * 跳到最后一帧
     */
    public void fastForward() {
        if (record != null) {
            currentFrame = record.getFrameCount() - 1;
        }
    }
    
    /**
     * 开始播放
     */
    public void play() {
        isPlaying = true;
    }
    
    /**
     * 暂停播放
     */
    public void pause() {
        isPlaying = false;
    }
    
    /**
     * 切换播放/暂停
     */
    public void togglePlay() {
        isPlaying = !isPlaying;
    }
    
    /**
     * 是否正在播放
     */
    public boolean isPlaying() {
        return isPlaying;
    }
    
    /**
     * 设置播放速度
     */
    public void setPlaySpeed(int speed) {
        if (speed > 0) {
            this.playSpeed = speed;
        }
    }
    
    /**
     * 获取播放速度
     */
    public int getPlaySpeed() {
        return playSpeed;
    }
    
    /**
     * 获取进度百分比
     */
    public double getProgress() {
        if (record == null || record.getFrameCount() == 0) {
            return 0.0;
        }
        return (double) currentFrame / record.getFrameCount() * 100;
    }
    
    /**
     * 是否到达结尾
     */
    public boolean isAtEnd() {
        return record != null && currentFrame >= record.getFrameCount() - 1;
    }
    
    /**
     * 获取记录摘要信息
     */
    public String getSummary() {
        if (record == null) {
            return "未加载记录";
        }
        
        return String.format(
            "对局: %s vs %s (第%d轮)\n" +
            "总帧数: %d\n" +
            "最终得分: %d\n" +
            "最终花蜜: %d kg\n" +
            "存活蜜蜂: %d\n" +
            "当前帧: %d (%.1f%%)",
            record.group1, record.group2, record.roundNumber,
            record.getFrameCount(),
            record.finalScore,
            record.finalHoney,
            record.finalAliveCount,
            currentFrame, getProgress()
        );
    }
    
    /**
     * 打印当前帧信息
     */
    public void printCurrentFrame() {
        GameFrame frame = getCurrentFrame();
        if (frame != null) {
            System.out.println(frame);
            System.out.println("  蜜蜂状态:");
            for (BeeState bee : frame.beeStates) {
                System.out.println("    " + bee);
            }
        }
    }
    
    /**
     * 测试主函数
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("用法: java ReplayController <记录文件路径>");
            
            // 查找最新的记录文件
            java.util.List<String> records = BatchSimulator.listBattleRecords("BattleRecords");
            if (!records.isEmpty()) {
                System.out.println("\n找到的记录文件:");
                for (int i = 0; i < records.size(); i++) {
                    System.out.println("  " + (i+1) + ". " + records.get(i));
                }
                
                // 使用第一个记录文件进行测试
                System.out.println("\n使用第一个记录文件进行测试...");
                ReplayController controller = new ReplayController();
                if (controller.loadRecord(records.get(0))) {
                    System.out.println("\n" + controller.getSummary());
                    
                    // 测试播放控制
                    System.out.println("\n测试: 显示前5帧...");
                    for (int i = 0; i < 5 && i < controller.getTotalFrames(); i++) {
                        controller.seekToFrame(i);
                        controller.printCurrentFrame();
                        System.out.println();
                    }
                }
            } else {
                System.out.println("未找到记录文件，请先运行 BatchSimulator");
            }
            return;
        }
        
        ReplayController controller = new ReplayController();
        if (controller.loadRecord(args[0])) {
            System.out.println(controller.getSummary());
        }
    }
}
