import java.io.*;
import java.util.*;

/**
 * 批量模拟器
 * 用于运行多轮PK并保存结果
 */
public class BatchSimulator {
    private String group1;
    private String group2;
    private int totalRounds;
    private String saveDirectory;
    private List<GameRecord> allRecords;
    
    public BatchSimulator(String group1, String group2, int totalRounds) {
        this.group1 = group1;
        this.group2 = group2;
        this.totalRounds = totalRounds;
        this.saveDirectory = "BattleRecords";
        this.allRecords = new ArrayList<>();
    }
    
    /**
     * 运行所有轮次的模拟
     */
    public void runAllBattles() {
        System.out.println("========================================");
        System.out.println("开始批量模拟");
        System.out.println("组别: " + group1 + " vs " + group2);
        System.out.println("轮数: " + totalRounds);
        System.out.println("========================================");
        
        long startTime = System.currentTimeMillis();
        
        for (int round = 1; round <= totalRounds; round++) {
            System.out.println("\n===== 第 " + round + " 轮 =====");
            try {
                GameEngine engine = new GameEngine(group1, group2, round);
                GameRecord record = engine.runSimulation();
                
                // 保存记录
                record.saveToFile(saveDirectory);
                allRecords.add(record);
                
                System.out.println("第" + round + "轮完成: 得分=" + record.finalScore);
            } catch (Exception e) {
                System.err.println("第" + round + "轮模拟失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;
        
        System.out.println("\n========================================");
        System.out.println("所有模拟完成!");
        System.out.println("总耗时: " + totalTime + " 秒");
        System.out.println("平均每轮: " + (totalTime / totalRounds) + " 秒");
        printSummary();
        System.out.println("========================================");
    }
    
    /**
     * 打印统计摘要
     */
    private void printSummary() {
        if (allRecords.isEmpty()) return;
        
        int totalScore = 0;
        int totalHoney = 0;
        int totalAlive = 0;
        int maxScore = Integer.MIN_VALUE;
        int minScore = Integer.MAX_VALUE;
        
        for (GameRecord record : allRecords) {
            totalScore += record.finalScore;
            totalHoney += record.finalHoney;
            totalAlive += record.finalAliveCount;
            maxScore = Math.max(maxScore, record.finalScore);
            minScore = Math.min(minScore, record.finalScore);
        }
        
        System.out.println("\n统计摘要:");
        System.out.println("  平均得分: " + (totalScore / allRecords.size()));
        System.out.println("  最高得分: " + maxScore);
        System.out.println("  最低得分: " + minScore);
        System.out.println("  平均花蜜: " + (totalHoney / allRecords.size()));
        System.out.println("  平均存活: " + (totalAlive / allRecords.size()));
        System.out.println("  记录保存在: " + saveDirectory + "/");
    }
    
    /**
     * 列出所有已保存的对局记录
     */
    public static List<String> listBattleRecords(String directory) {
        List<String> records = new ArrayList<>();
        File dir = new File(directory);
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.startsWith("battle_") && name.endsWith(".dat"));
            if (files != null) {
                for (File file : files) {
                    records.add(file.getAbsolutePath());
                }
            }
        }
        
        Collections.sort(records);
        return records;
    }
    
    /**
     * 主函数 - 用于测试
     */
    public static void main(String[] args) {
        if (args.length >= 3) {
            String group1 = args[0];
            String group2 = args[1];
            int rounds = Integer.parseInt(args[2]);
            
            BatchSimulator simulator = new BatchSimulator(group1, group2, rounds);
            simulator.runAllBattles();
        } else {
            System.out.println("用法: java BatchSimulator <组1> <组2> <轮数>");
            System.out.println("示例: java BatchSimulator BF BF 5");
            
            // 默认运行一次测试
            System.out.println("\n使用默认参数运行测试...");
            BatchSimulator simulator = new BatchSimulator("BF", "BF", 2);
            simulator.runAllBattles();
        }
    }
}
