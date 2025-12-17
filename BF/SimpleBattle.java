import java.io.*;
import java.nio.file.*;

/**
 * 最简单的组别对战实现 - 直接复制.class文件运行
 */
public class SimpleBattle {
    
    private static final String GROUP_BASE = "../group";
    private static final String BF_BASE = "./";
    
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("用法: java SimpleBattle <组A> <组B> <每方向轮数>");
            System.out.println("示例: java SimpleBattle 001 006 3");
            return;
        }
        
        String groupA = args[0];
        String groupB = args[1];
        int rounds = Integer.parseInt(args[2]);
        
        System.out.println("==========================================");
        System.out.println("组别PK系统");
        System.out.println("组A: " + groupA + " vs 组B: " + groupB);
        System.out.println("每方向轮数: " + rounds);
        System.out.println("==========================================\n");
        
        // 备份原始文件
        backupOriginalFiles();
        
        try {
            // 第一阶段: A的蜜蜂 vs B的大黄蜂
            System.out.println("【第一阶段】" + groupA + " 的蜜蜂 vs " + groupB + " 的大黄蜂");
            System.out.println("------------------------------------------");
            double[] scoresA = runBattles(groupA, groupB, true, rounds);
            double avgA = average(scoresA);
            System.out.println(groupA + " 平均得分: " + String.format("%.2f", avgA) + "\n");
            
            // 第二阶段: B的蜜蜂 vs A的大黄蜂
            System.out.println("【第二阶段】" + groupB + " 的蜜蜂 vs " + groupA + " 的大黄蜂");
            System.out.println("------------------------------------------");
            double[] scoresB = runBattles(groupB, groupA, true, rounds);
            double avgB = average(scoresB);
            System.out.println(groupB + " 平均得分: " + String.format("%.2f", avgB) + "\n");
            
            // 结果
            System.out.println("==========================================");
            System.out.println("对战结果");
            System.out.println("==========================================");
            System.out.println(groupA + " 组作为蜜蜂平均得分: " + String.format("%.2f", avgA));
            System.out.println(groupB + " 组作为蜜蜂平均得分: " + String.format("%.2f", avgB));
            
            double diff = Math.abs(avgA - avgB);
            if (avgA > avgB) {
                System.out.println("\n获胜方: " + groupA + " (领先 " + String.format("%.2f", diff) + " 分)");
            } else if (avgB > avgA) {
                System.out.println("\n获胜方: " + groupB + " (领先 " + String.format("%.2f", diff) + " 分)");
            } else {
                System.out.println("\n结果: 平局");
            }
            System.out.println("==========================================");
            
        } finally {
            // 恢复原始文件
            restoreOriginalFiles();
        }
    }
    
    /**
     * 运行多轮对战
     * @param honeyBeeGroup 蜜蜂组
     * @param hornetGroup 大黄蜂组
     * @param useGroupAlgorithms 是否使用组算法
     * @param rounds 轮数
     * @return 每轮得分
     */
    private static double[] runBattles(String honeyBeeGroup, String hornetGroup, 
                                       boolean useGroupAlgorithms, int rounds) throws Exception {
        double[] scores = new double[rounds];
        
        for (int i = 0; i < rounds; i++) {
            System.out.println("回合 " + (i+1) + "/" + rounds);
            
            // 复制组算法
            if (useGroupAlgorithms) {
                copyGroupAlgorithms(honeyBeeGroup, hornetGroup);
            }
            
            // 运行游戏引擎 (false = 不加载算法，直接使用当前目录的.class)
            GameEngine engine = new GameEngine(
                honeyBeeGroup + "(蜜蜂)", 
                hornetGroup + "(大黄蜂)",
                honeyBeeGroup,
                hornetGroup,
                i + 1,
                false  // 不加载算法，使用已复制到当前目录的.class文件
            );
            
            GameRecord record = engine.runSimulation();
            scores[i] = record.finalScore;
            
            System.out.println("  得分: " + (int)scores[i]);
            
            // 保存记录到目录
            record.saveToFile("BattleRecords");
        }
        
        return scores;
    }
    
    /**
     * 复制组算法到当前目录
     */
    public static void copyGroupAlgorithms(String honeyBeeGroup, String hornetGroup) throws IOException {
        String honeyBeeSrc = GROUP_BASE + honeyBeeGroup + "/HoneyBee.class";
        String hornetSrc = GROUP_BASE + hornetGroup + "/Hornet.class";
        
        Files.copy(
            Paths.get(honeyBeeSrc), 
            Paths.get(BF_BASE + "HoneyBee.class"),
            StandardCopyOption.REPLACE_EXISTING
        );
        
        Files.copy(
            Paths.get(hornetSrc),
            Paths.get(BF_BASE + "Hornet.class"),
            StandardCopyOption.REPLACE_EXISTING
        );
    }
    
    /**
     * 备份原始文件
     */
    public static void backupOriginalFiles() throws IOException {
        Files.copy(
            Paths.get(BF_BASE + "HoneyBee.class"),
            Paths.get(BF_BASE + "HoneyBee.class.backup"),
            StandardCopyOption.REPLACE_EXISTING
        );
        
        Files.copy(
            Paths.get(BF_BASE + "Hornet.class"),
            Paths.get(BF_BASE + "Hornet.class.backup"),
            StandardCopyOption.REPLACE_EXISTING
        );
    }
    
    /**
     * 恢复原始文件
     */
    public static void restoreOriginalFiles() throws IOException {
        Files.copy(
            Paths.get(BF_BASE + "HoneyBee.class.backup"),
            Paths.get(BF_BASE + "HoneyBee.class"),
            StandardCopyOption.REPLACE_EXISTING
        );
        
        Files.copy(
            Paths.get(BF_BASE + "Hornet.class.backup"),
            Paths.get(BF_BASE + "Hornet.class"),
            StandardCopyOption.REPLACE_EXISTING
        );
        
        // 删除备份
        Files.deleteIfExists(Paths.get(BF_BASE + "HoneyBee.class.backup"));
        Files.deleteIfExists(Paths.get(BF_BASE + "Hornet.class.backup"));
    }
    
    /**
     * 计算平均值
     */
    public static double average(double[] values) {
        double sum = 0;
        for (double v : values) {
            sum += v;
        }
        return sum / values.length;
    }
}
