import java.io.File;
import java.util.*;

/**
 * 测试组加载
 */
public class TestGroupLoading {
    public static void main(String[] args) {
        List<String> availableGroups = new ArrayList<>();
        availableGroups.add("BF");
        
        System.out.println("当前工作目录: " + new File(".").getAbsolutePath());
        System.out.println();
        
        // 测试 ../../group/group
        File groupDir = new File("../../group/group");
        System.out.println("测试路径: ../../group/group");
        System.out.println("存在: " + groupDir.exists());
        System.out.println("是目录: " + groupDir.isDirectory());
        System.out.println("绝对路径: " + groupDir.getAbsolutePath());
        System.out.println();
        
        if (groupDir.exists() && groupDir.isDirectory()) {
            File[] dirs = groupDir.listFiles(File::isDirectory);
            System.out.println("找到 " + (dirs != null ? dirs.length : 0) + " 个子目录");
            
            if (dirs != null) {
                for (File dir : dirs) {
                    File honeyBee = new File(dir, "HoneyBee.class");
                    File hornet = new File(dir, "Hornet.class");
                    
                    System.out.println("  检查组: " + dir.getName());
                    System.out.println("    HoneyBee.class: " + honeyBee.exists() + " (" + honeyBee.getAbsolutePath() + ")");
                    System.out.println("    Hornet.class: " + hornet.exists() + " (" + hornet.getAbsolutePath() + ")");
                    
                    if (honeyBee.exists() && hornet.exists()) {
                        availableGroups.add(dir.getName());
                        System.out.println("    ✓ 已添加");
                    } else {
                        System.out.println("    ✗ 跳过");
                    }
                }
            }
        }
        
        Collections.sort(availableGroups);
        System.out.println();
        System.out.println("========================================");
        System.out.println("可用组列表 (" + availableGroups.size() + " 个):");
        for (String group : availableGroups) {
            System.out.println("  - " + group);
        }
        System.out.println("========================================");
    }
}
