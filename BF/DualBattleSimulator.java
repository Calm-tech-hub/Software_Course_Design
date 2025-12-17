import java.io.*;
import java.util.*;

/**
 * 双向对战模拟器
 * 实现A组蜜蜂vsB组大黄蜂 + A组大黄蜂vsB组蜜蜂的完整对战
 */
public class DualBattleSimulator {
    private String groupA;
    private String groupB;
    private int roundsPerSide;  // 每个方向的轮数
    private String saveDirectory;
    
    // 统计数据
    private List<GameRecord> roundABRecords = new ArrayList<>();  // A蜜蜂 vs B大黄蜂
    private List<GameRecord> roundBARecords = new ArrayList<>();  // B蜜蜂 vs A大黄蜂
    
    public DualBattleSimulator(String groupA, String groupB, int roundsPerSide) {
        this.groupA = groupA;
        this.groupB = groupB;
        this.roundsPerSide = roundsPerSide;
        this.saveDirectory = "BattleRecords";
    }
    
    /**
     * 运行完整的双向对战
     */
    public BattleResult runDualBattle() {
        System.out.println("==========================================");
        System.out.println("双向对战模拟");
        System.out.println("组别: " + groupA + " vs " + groupB);
        System.out.println("每方向轮数: " + roundsPerSide);
        System.out.println("总对局数: " + (roundsPerSide * 2));
        System.out.println("==========================================");
        
        long startTime = System.currentTimeMillis();
        
        // 第一轮: A的蜜蜂 vs B的大黄蜂
        System.out.println("\n【第一阶段】" + groupA + "的蜜蜂 vs " + groupB + "的大黄蜂");
        System.out.println("------------------------------------------");
        for (int round = 1; round <= roundsPerSide; round++) {
            System.out.println("回合 " + round + "/" + roundsPerSide);
            try {
                // A组蜜蜂, B组大黄蜂
                GameEngine engine = new GameEngine(
                    groupA + "(蜜蜂)", groupB + "(大黄蜂)", 
                    groupA, groupB, round
                );
                GameRecord record = engine.runSimulation();
                record.saveToFile(saveDirectory);
                roundABRecords.add(record);
                System.out.println("  得分: " + record.finalScore);
            } catch (Exception e) {
                System.err.println("  失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // 第二轮: B的蜜蜂 vs A的大黄蜂
        System.out.println("\n【第二阶段】" + groupB + "的蜜蜂 vs " + groupA + "的大黄蜂");
        System.out.println("------------------------------------------");
        for (int round = 1; round <= roundsPerSide; round++) {
            System.out.println("回合 " + round + "/" + roundsPerSide);
            try {
                // B组蜜蜂, A组大黄蜂
                GameEngine engine = new GameEngine(
                    groupB + "(蜜蜂)", groupA + "(大黄蜂)",
                    groupB, groupA, round
                );
                GameRecord record = engine.runSimulation();
                record.saveToFile(saveDirectory);
                roundBARecords.add(record);
                System.out.println("  得分: " + record.finalScore);
            } catch (Exception e) {
                System.err.println("  失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;
        
        // 计算结果
        BattleResult result = calculateResult(totalTime);
        
        // 打印结果
        printResult(result);
        
        return result;
    }
    
    /**
     * 计算对战结果
     */
    private BattleResult calculateResult(double totalTime) {
        BattleResult result = new BattleResult();
        result.groupA = groupA;
        result.groupB = groupB;
        result.roundsPerSide = roundsPerSide;
        result.totalTime = totalTime;
        
        // 统计A组作为蜜蜂的表现
        if (!roundABRecords.isEmpty()) {
            int totalScore = 0, totalHoney = 0, totalAlive = 0;
            for (GameRecord r : roundABRecords) {
                totalScore += r.finalScore;
                totalHoney += r.finalHoney;
                totalAlive += r.finalAliveCount;
            }
            result.groupA_HoneyBee_avgScore = totalScore / (double) roundABRecords.size();
            result.groupA_HoneyBee_avgHoney = totalHoney / (double) roundABRecords.size();
            result.groupA_HoneyBee_avgAlive = totalAlive / (double) roundABRecords.size();
        }
        
        // 统计B组作为蜜蜂的表现
        if (!roundBARecords.isEmpty()) {
            int totalScore = 0, totalHoney = 0, totalAlive = 0;
            for (GameRecord r : roundBARecords) {
                totalScore += r.finalScore;
                totalHoney += r.finalHoney;
                totalAlive += r.finalAliveCount;
            }
            result.groupB_HoneyBee_avgScore = totalScore / (double) roundBARecords.size();
            result.groupB_HoneyBee_avgHoney = totalHoney / (double) roundBARecords.size();
            result.groupB_HoneyBee_avgAlive = totalAlive / (double) roundBARecords.size();
        }
        
        // 计算综合评分
        result.groupA_totalAvgScore = result.groupA_HoneyBee_avgScore;
        result.groupB_totalAvgScore = result.groupB_HoneyBee_avgScore;
        
        // 判定获胜方
        if (result.groupA_totalAvgScore > result.groupB_totalAvgScore) {
            result.winner = groupA;
            result.scoreDiff = result.groupA_totalAvgScore - result.groupB_totalAvgScore;
        } else if (result.groupB_totalAvgScore > result.groupA_totalAvgScore) {
            result.winner = groupB;
            result.scoreDiff = result.groupB_totalAvgScore - result.groupA_totalAvgScore;
        } else {
            result.winner = "平局";
            result.scoreDiff = 0;
        }
        
        return result;
    }
    
    /**
     * 打印对战结果
     */
    private void printResult(BattleResult result) {
        System.out.println("\n==========================================");
        System.out.println("对战结果统计");
        System.out.println("==========================================");
        System.out.println();
        
        System.out.println("【" + groupA + "组表现】");
        System.out.println("  作为蜜蜂:");
        System.out.println("    平均得分: " + String.format("%.2f", result.groupA_HoneyBee_avgScore));
        System.out.println("    平均花蜜: " + String.format("%.2f", result.groupA_HoneyBee_avgHoney));
        System.out.println("    平均存活: " + String.format("%.2f", result.groupA_HoneyBee_avgAlive));
        System.out.println();
        
        System.out.println("【" + groupB + "组表现】");
        System.out.println("  作为蜜蜂:");
        System.out.println("    平均得分: " + String.format("%.2f", result.groupB_HoneyBee_avgScore));
        System.out.println("    平均花蜜: " + String.format("%.2f", result.groupB_HoneyBee_avgHoney));
        System.out.println("    平均存活: " + String.format("%.2f", result.groupB_HoneyBee_avgAlive));
        System.out.println();
        
        System.out.println("【综合评分】");
        System.out.println("  " + groupA + " 综合得分: " + String.format("%.2f", result.groupA_totalAvgScore));
        System.out.println("  " + groupB + " 综合得分: " + String.format("%.2f", result.groupB_totalAvgScore));
        System.out.println();
        
        System.out.println("【最终结果】");
        System.out.println("  获胜方: " + result.winner);
        System.out.println("  分差: " + String.format("%.2f", result.scoreDiff));
        System.out.println();
        
        System.out.println("总耗时: " + String.format("%.3f", result.totalTime) + " 秒");
        System.out.println("平均每局: " + String.format("%.3f", result.totalTime / (roundsPerSide * 2)) + " 秒");
        System.out.println("==========================================");
    }
    
    /**
     * 对战结果类
     */
    public static class BattleResult {
        public String groupA;
        public String groupB;
        public int roundsPerSide;
        public double totalTime;
        
        // A组作为蜜蜂的表现
        public double groupA_HoneyBee_avgScore;
        public double groupA_HoneyBee_avgHoney;
        public double groupA_HoneyBee_avgAlive;
        
        // B组作为蜜蜂的表现
        public double groupB_HoneyBee_avgScore;
        public double groupB_HoneyBee_avgHoney;
        public double groupB_HoneyBee_avgAlive;
        
        // 综合评分
        public double groupA_totalAvgScore;
        public double groupB_totalAvgScore;
        
        public String winner;
        public double scoreDiff;
    }
    
    /**
     * 主函数
     */
    public static void main(String[] args) {
        if (args.length >= 3) {
            String groupA = args[0];
            String groupB = args[1];
            int rounds = Integer.parseInt(args[2]);
            
            DualBattleSimulator simulator = new DualBattleSimulator(groupA, groupB, rounds);
            simulator.runDualBattle();
        } else {
            System.out.println("用法: java DualBattleSimulator <组A> <组B> <每方向轮数>");
            System.out.println("示例: java DualBattleSimulator 001 006 3");
            System.out.println();
            System.out.println("说明:");
            System.out.println("  将进行双向对战:");
            System.out.println("  1. 组A的蜜蜂 vs 组B的大黄蜂 (N轮)");
            System.out.println("  2. 组B的蜜蜂 vs 组A的大黄蜂 (N轮)");
            System.out.println("  最后计算平均分比较");
            System.out.println();
            
            // 默认运行测试
            System.out.println("使用默认参数运行测试 (BF vs BF, 2轮)...");
            DualBattleSimulator simulator = new DualBattleSimulator("BF", "BF", 2);
            simulator.runDualBattle();
        }
    }
}
