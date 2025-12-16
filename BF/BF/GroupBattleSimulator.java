import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * 真正的组别对战模拟器
 * 通过复制和编译group目录的算法来实现不同组的PK
 */
public class GroupBattleSimulator {
    
    private String groupA;
    private String groupB;
    private int roundsPerSide;
    private String groupBasePath = "../group/group";
    private String workDir = "battle_temp";
    
    public GroupBattleSimulator(String groupA, String groupB, int roundsPerSide) {
        this.groupA = groupA;
        this.groupB = groupB;
        this.roundsPerSide = roundsPerSide;
    }
    
    /**
     * 运行完整的双向对战
     */
    public void runFullBattle() throws Exception {
        System.out.println("==========================================");
        System.out.println("组别对战系统");
        System.out.println("组A: " + groupA + " vs 组B: " + groupB);
        System.out.println("每方向轮数: " + roundsPerSide);
        System.out.println("==========================================\n");
        
        // 创建临时工作目录
        File workDirFile = new File(workDir);
        if (workDirFile.exists()) {
            deleteDirectory(workDirFile);
        }
        workDirFile.mkdirs();
        
        long startTime = System.currentTimeMillis();
        
        // 第一阶段: A组蜜蜂 vs B组大黄蜂
        System.out.println("【第一阶段】" + groupA + "的蜜蜂 vs " + groupB + "的大黄蜂");
        System.out.println("------------------------------------------");
        List<Integer> scoresAB = new ArrayList<>();
        for (int round = 1; round <= roundsPerSide; round++) {
            System.out.println("回合 " + round + "/" + roundsPerSide);
            int score = runSingleBattle(groupA, groupB, round, true);
            scoresAB.add(score);
            System.out.println("  得分: " + score);
        }
        
        // 第二阶段: B组蜜蜂 vs A组大黄蜂
        System.out.println("\n【第二阶段】" + groupB + "的蜜蜂 vs " + groupA + "的大黄蜂");
        System.out.println("------------------------------------------");
        List<Integer> scoresBA = new ArrayList<>();
        for (int round = 1; round <= roundsPerSide; round++) {
            System.out.println("回合 " + round + "/" + roundsPerSide);
            int score = runSingleBattle(groupB, groupA, round, false);
            scoresBA.add(score);
            System.out.println("  得分: " + score);
        }
        
        // 清理临时目录
        deleteDirectory(workDirFile);
        
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;
        
        // 打印结果
        printResults(scoresAB, scoresBA, totalTime);
    }
    
    /**
     * 运行单场对战
     */
    private int runSingleBattle(String honeyBeeGroup, String hornetGroup, 
                                int round, boolean isFirstPhase) throws Exception {
        // 准备算法文件
        prepareAlgorithms(honeyBeeGroup, hornetGroup);
        
        // 编译算法
        compileAlgorithms();
        
        // 运行游戏引擎
        ProcessBuilder pb = new ProcessBuilder(
            "java", "-cp", workDir + ":.",
            "BattleRunner",
            honeyBeeGroup, hornetGroup, String.valueOf(round)
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        
        // 读取输出
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()));
        String line;
        int finalScore = 0;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("SCORE:")) {
                finalScore = Integer.parseInt(line.substring(6).trim());
            }
        }
        
        process.waitFor();
        return finalScore;
    }
    
    /**
     * 准备算法文件（复制到临时目录）
     */
    private void prepareAlgorithms(String honeyBeeGroup, String hornetGroup) throws IOException {
        // 复制HoneyBee
        File honeyBeeSource = new File(groupBasePath + "/" + honeyBeeGroup + "/HoneyBee.java");
        File honeyBeeDest = new File(workDir + "/HoneyBee.java");
        if (honeyBeeSource.exists()) {
            Files.copy(honeyBeeSource.toPath(), honeyBeeDest.toPath(), 
                      StandardCopyOption.REPLACE_EXISTING);
        }
        
        // 复制Hornet
        File hornetSource = new File(groupBasePath + "/" + hornetGroup + "/Hornet.java");
        File hornetDest = new File(workDir + "/Hornet.java");
        if (hornetSource.exists()) {
            Files.copy(hornetSource.toPath(), hornetDest.toPath(),
                      StandardCopyOption.REPLACE_EXISTING);
        }
        
        // 复制必要的基类文件
        copyBaseFiles();
    }
    
    /**
     * 复制基类文件
     */
    private void copyBaseFiles() throws IOException {
        String[] baseFiles = {
            "Bee.java", "Bee.class",
            "FlyingStatus.java", "FlyingStatus.class",
            "BeeFarming.class",
            "BeeState.class", "FlowerState.class", "GameFrame.class", "GameRecord.class"
        };
        
        for (String file : baseFiles) {
            File source = new File(file);
            if (source.exists()) {
                File dest = new File(workDir + "/" + file);
                Files.copy(source.toPath(), dest.toPath(),
                          StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
    
    /**
     * 编译算法
     */
    private void compileAlgorithms() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
            "javac", "-encoding", "UTF-8", "-cp", ".",
            workDir + "/HoneyBee.java",
            workDir + "/Hornet.java"
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            // 忽略警告
            if (line.contains("错误")) {
                System.err.println("编译错误: " + line);
            }
        }
        
        process.waitFor();
        if (process.exitValue() != 0) {
            throw new Exception("编译失败");
        }
    }
    
    /**
     * 打印结果
     */
    private void printResults(List<Integer> scoresAB, List<Integer> scoresBA, double totalTime) {
        double avgA = scoresAB.stream().mapToInt(Integer::intValue).average().orElse(0);
        double avgB = scoresBA.stream().mapToInt(Integer::intValue).average().orElse(0);
        
        System.out.println("\n==========================================");
        System.out.println("对战结果统计");
        System.out.println("==========================================\n");
        
        System.out.println("【" + groupA + "组表现】");
        System.out.println("  作为蜜蜂平均得分: " + String.format("%.2f", avgA));
        
        System.out.println("\n【" + groupB + "组表现】");
        System.out.println("  作为蜜蜂平均得分: " + String.format("%.2f", avgB));
        
        System.out.println("\n【最终结果】");
        if (avgA > avgB) {
            System.out.println("  获胜方: " + groupA);
            System.out.println("  分差: " + String.format("%.2f", avgA - avgB));
        } else if (avgB > avgA) {
            System.out.println("  获胜方: " + groupB);
            System.out.println("  分差: " + String.format("%.2f", avgB - avgA));
        } else {
            System.out.println("  结果: 平局");
        }
        
        System.out.println("\n总耗时: " + String.format("%.3f", totalTime) + " 秒");
        System.out.println("==========================================");
    }
    
    /**
     * 删除目录
     */
    private void deleteDirectory(File dir) throws IOException {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            dir.delete();
        }
    }
    
    public static void main(String[] args) {
        if (args.length >= 3) {
            String groupA = args[0];
            String groupB = args[1];
            int rounds = Integer.parseInt(args[2]);
            
            try {
                GroupBattleSimulator simulator = new GroupBattleSimulator(groupA, groupB, rounds);
                simulator.runFullBattle();
            } catch (Exception e) {
                System.err.println("运行失败: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("用法: java GroupBattleSimulator <组A> <组B> <每方向轮数>");
            System.out.println("示例: java GroupBattleSimulator 001 006 3");
            System.out.println("\n注意: 组名对应 group/group/ 目录下的文件夹");
        }
    }
}
