import java.io.*;
import java.util.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.*;

/**
 * 无GUI的游戏核心引擎,用于快速模拟游戏
 * 从BeeFarming提取核心逻辑,移除所有GUI相关代码
 */
public class GameEngine {
    // 游戏常量
    private static final int BG_WIDTH = 800;
    private static final int BG_HEIGHT = 600;
    private static final int[] RANGE = {50,50,50,50,50,50,50,50,50,100};
    private static final int MAX_TIME = 4000; // 最大游戏时间
    
    // 游戏状态
    private int time = 0;
    private int totalHoney = 0;
    private int aliveBeesCount = 3;
    private int remainingFlowersCount = 20;
    
    // 蜜蜂和花的数据 - 改用Bee类型
    private Bee[] bees = new Bee[10];
    private FlowerSimple[] flowers = new FlowerSimple[20];
    private static FlyingStatus[] status = new FlyingStatus[10]; // 改为static以便Bee类访问
    
    // 静态引擎实例,用于Bee类回调
    private static GameEngine currentEngine = null;
    
    // 记录器
    private GameRecord gameRecord;
    private int frameCounter = 0;
    
    // 算法类(需要动态加载)
    private Class<?> honeyBeeClass;
    private Class<?> hornetClass;
    
    private String honeyBeeGroup;  // 蜜蜂算法组
    private String hornetGroup;    // 大黄蜂算法组
    
    /**
     * 初始化游戏引擎（兼容旧接口）
     */
    public GameEngine(String group1, String group2, int roundNumber) throws Exception {
        this(group1, group2, group1, group2, roundNumber);
    }
    
    /**
     * 初始化游戏引擎（新接口，指定蜜蜂组和大黄蜂组）
     * @param displayGroup1 显示用的组1名称
     * @param displayGroup2 显示用的组2名称
     * @param honeyBeeGroup 蜜蜂算法所在组
     * @param hornetGroup 大黄蜂算法所在组
     * @param roundNumber 轮次
     */
    public GameEngine(String displayGroup1, String displayGroup2, 
                      String honeyBeeGroup, String hornetGroup, int roundNumber) throws Exception {
        this(displayGroup1, displayGroup2, honeyBeeGroup, hornetGroup, roundNumber, true);
    }
    
    /**
     * 初始化游戏引擎（完整接口）
     * @param displayGroup1 显示用的组1名称
     * @param displayGroup2 显示用的组2名称
     * @param honeyBeeGroup 蜜蜂算法所在组
     * @param hornetGroup 大黄蜂算法所在组
     * @param roundNumber 轮次
     * @param loadAlgorithms 是否加载算法（false表示使用当前classpath中的类）
     */
    public GameEngine(String displayGroup1, String displayGroup2, 
                      String honeyBeeGroup, String hornetGroup, int roundNumber,
                      boolean loadAlgorithmsFlag) throws Exception {
        this.gameRecord = new GameRecord(displayGroup1, displayGroup2, roundNumber);
        this.honeyBeeGroup = honeyBeeGroup;
        this.hornetGroup = hornetGroup;
        
        // 设置当前引擎实例,供Bee类回调
        currentEngine = this;
        
        // 加载算法类
        if (loadAlgorithmsFlag) {
            loadAlgorithms(honeyBeeGroup, hornetGroup);
        } else {
            // 直接使用当前类路径中的类
            honeyBeeClass = Class.forName("HoneyBee");
            hornetClass = Class.forName("Hornet");
            System.out.println("使用当前目录的算法: HoneyBee (" + honeyBeeGroup + ") 和 Hornet (" + hornetGroup + ")");
        }
        
        // 初始化蜜蜂
        initBees();
        
        // 初始化花朵
        initFlowers();
    }
    
    // ============= 静态方法供Bee类回调 =============
    
    /**
     * Bee类通过该方法更新自己的状态
     */
    public static void update(FlyingStatus fs) {
        status[fs.id] = fs;
    }
    
    /**
     * Bee类通过该方法查询视距范围内的物体
     */
    public static String search(int id) {
        if (currentEngine == null) return "";
        return currentEngine.searchEnvironment(id);
    }
    
    /**
     * Bee类通过该方法采集花蜜
     */
    public static int pickFlowerHoney(int id) {
        if (currentEngine == null) return -1;
        return currentEngine.pickFlower(id);
    }
    
    /**
     * 大黄蜂通过该方法捕杀蜜蜂
     */
    public static void killBee(int id) {
        if (currentEngine == null) return;
        currentEngine.killBeeInternal(id);
    }
    
    /**
     * 计算矢量角度
     */
    public static double getVectorDegree(int x1, int y1, int x2, int y2) {
        int deltaY = y2 - y1;
        int deltaX = x2 - x1;
        if (deltaX == 0) {
            if (deltaY > 0) return 90;
            if (deltaY < 0) return 270;
        } else {
            double k = (double)deltaY / deltaX;
            if (deltaX > 0 && deltaY >= 0) return Math.toDegrees(Math.atan(k));
            if (deltaX > 0 && deltaY < 0) return 360 + Math.toDegrees(Math.atan(k));
            if (deltaX < 0) return 180 + Math.toDegrees(Math.atan(k));
        }
        return 360;
    }
    
    // ============= 算法加载 =============
    
    /**
     * 加载算法类
     * @param honeyBeeGroup 蜜蜂算法组
     * @param hornetGroup 大黄蜂算法组
     */
    private void loadAlgorithms(String honeyBeeGroup, String hornetGroup) throws Exception {
        // 加载蜜蜂算法
        try {
            // 尝试多个可能的路径
            File groupDir = new File("../group/group/" + honeyBeeGroup);
            if (!groupDir.exists()) {
                groupDir = new File("../group/" + honeyBeeGroup);
            }
            
            if (groupDir.exists() && !honeyBeeGroup.equals("BF")) {
                // 使用自定义类加载器加载
                AlgorithmLoader loader = new AlgorithmLoader(groupDir.getAbsolutePath());
                honeyBeeClass = loader.loadClass("HoneyBee");
                System.out.println("加载蜜蜂算法: " + honeyBeeGroup + "/HoneyBee");
            } else {
                // 使用当前目录的默认类
                honeyBeeClass = Class.forName("HoneyBee");
                System.out.println("使用默认HoneyBee算法 (BF)");
            }
        } catch (Exception e) {
            System.out.println("使用默认HoneyBee算法: " + e.getMessage());
            honeyBeeClass = Class.forName("HoneyBee");
        }
        
        // 加载大黄蜂算法
        try {
            // 尝试多个可能的路径
            File groupDir = new File("../group/group/" + hornetGroup);
            if (!groupDir.exists()) {
                groupDir = new File("../group/" + hornetGroup);
            }
            
            if (groupDir.exists() && !hornetGroup.equals("BF")) {
                AlgorithmLoader loader = new AlgorithmLoader(groupDir.getAbsolutePath());
                hornetClass = loader.loadClass("Hornet");
                System.out.println("加载大黄蜂算法: " + hornetGroup + "/Hornet");
            } else {
                hornetClass = Class.forName("Hornet");
                System.out.println("使用默认Hornet算法 (BF)");
            }
        } catch (Exception e) {
            System.out.println("使用默认Hornet算法: " + e.getMessage());
            hornetClass = Class.forName("Hornet");
        }
    }
    
    /**
     * 初始化蜜蜂 - 使用反射创建真实的Bee对象
     */
    private void initBees() throws Exception {
        // 创建虚拟图片(Bee类需要,但在无GUI模式下不实际使用)
        Image dummyImg = new BufferedImage(36, 36, BufferedImage.TYPE_INT_ARGB);
        
        // 创建3只蜜蜂
        bees[1] = (Bee) honeyBeeClass.getConstructor(int.class, int.class, int.class, double.class, boolean.class, Image.class)
                .newInstance(1, 200, 200, 76.0, true, dummyImg);
        bees[2] = (Bee) honeyBeeClass.getConstructor(int.class, int.class, int.class, double.class, boolean.class, Image.class)
                .newInstance(2, 100, 100, 145.0, true, dummyImg);
        bees[3] = (Bee) honeyBeeClass.getConstructor(int.class, int.class, int.class, double.class, boolean.class, Image.class)
                .newInstance(3, 20, 300, 240.0, true, dummyImg);
        
        // 创建1只大黄蜂
        bees[9] = (Bee) hornetClass.getConstructor(int.class, int.class, int.class, double.class, boolean.class, Image.class)
                .newInstance(9, 690, 500, 240.0, true, dummyImg);
        
        // 注意: status 数组会通过 Bee 构造函数中的 BeeFarming.update() 自动初始化
        // 但在headless模式下,BeeFarming.update()会转发到GameEngine.update()
        // 所以这里手动初始化 status
        for (int i = 1; i <= 3; i++) {
            if (status[i] == null) {
                status[i] = new FlyingStatus(i, 200, 200, 76, true, 0);
            }
        }
        if (status[9] == null) {
            status[9] = new FlyingStatus(9, 690, 500, 240, true, 0);
        }
        
        // 设置BeeFarming的static bees数组,以便Bee.flying()调用BeeFarming.killBee()时能访问
        try {
            java.lang.reflect.Field beesField = Class.forName("BeeFarming").getDeclaredField("bees");
            beesField.setAccessible(true);
            beesField.set(null, bees);  // static字段,第一个参数为null
            
            // 同样设置status字段
            java.lang.reflect.Field statusField = Class.forName("BeeFarming").getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(null, status);  // static字段,第一个参数为null
        } catch (Exception e) {
            System.out.println("Warning: 无法设置BeeFarming字段: " + e.getMessage());
        }
    }
    
    /**
     * 初始化花朵
     */
    private void initFlowers() {
        try {
            int i = 0;
            Scanner input = new Scanner(new File("flower.txt"));
            while (input.hasNext() && i < 20) {
                int appearTime = Integer.parseInt(input.next());
                int x = 30 + Integer.parseInt(input.next());
                int y = 30 + Integer.parseInt(input.next());
                int volume = 10 + Integer.parseInt(input.next());
                int imageType = Integer.parseInt(input.next());
                
                flowers[i] = new FlowerSimple(i, appearTime, x, y, volume, imageType);
                i++;
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: Cannot find flower.txt");
        }
    }
    
    /**
     * 运行完整的游戏模拟
     */
    public GameRecord runSimulation() {
        System.out.println("开始模拟游戏: " + gameRecord.group1 + " vs " + gameRecord.group2 + " Round " + gameRecord.roundNumber);
        
        int angle_damp = 0;
        
        while (time < MAX_TIME && aliveBeesCount > 0 && remainingFlowersCount > 0) {
            // 记录当前帧
            if (time % 9 == 0) { // 每9个时间单位记录一帧
                recordFrame();
            }
            
            // 生成花朵
            for (int i = 0; i < 20; i++) {
                if (flowers[i] != null && flowers[i].appearTime == time && !flowers[i].visible) {
                    flowers[i].visible = true;
                }
            }
            
            // 蜜蜂搜索和飞行
            if (angle_damp == 0) {
                for (int i = 1; i < 10; i++) {
                    if (bees[i] != null && status[i] != null && status[i].isAlive) {
                        bees[i].search(); // 调用真实的算法
                    }
                }
            }
            
            // 飞行
            for (int i = 1; i < 10; i++) {
                if (bees[i] != null && status[i] != null && status[i].isAlive) {
                    bees[i].flying(angle_damp); // 调用真实的飞行方法
                }
            }
            
            angle_damp++;
            if (angle_damp == 9) angle_damp = 0;
            
            time++;
        }
        
        // 记录最后一帧
        recordFrame();
        
        // 计算最终分数
        calculateFinalScore();
        
        System.out.println("模拟完成: 时间=" + time + ", 花蜜=" + totalHoney + ", 存活=" + aliveBeesCount);
        
        return gameRecord;
    }
    
    /**
     * 记录当前帧状态
     */
    private void recordFrame() {
        GameFrame frame = new GameFrame(frameCounter++, time);
        
        // 记录蜜蜂状态 - 从status获取
        for (int i = 1; i < 10; i++) {
            if (status[i] != null) {
                BeeState bs = new BeeState(i, status[i].x, status[i].y, 
                        status[i].angle, status[i].isAlive, 0);
                frame.addBeeState(bs);
            }
        }
        
        // 记录花朵状态
        for (int i = 0; i < 20; i++) {
            if (flowers[i] != null) {
                FlowerState fs = new FlowerState(i, flowers[i].x, flowers[i].y,
                        flowers[i].volume, flowers[i].maxVolume, flowers[i].appearTime,
                        flowers[i].imageType, flowers[i].visible);
                frame.addFlowerState(fs);
            }
        }
        
        frame.totalHoney = totalHoney;
        frame.aliveBeesCount = aliveBeesCount;
        frame.remainingFlowersCount = remainingFlowersCount;
        
        gameRecord.addFrame(frame);
    }
    
    /**
     * 搜索环境(从BeeFarming.search提取)
     */
    private String searchEnvironment(int id) {
        FlyingStatus fs = status[id];
        String result = "";
        
        // 判断与边界的关系
        int visionX = fs.x + (int)(Math.cos(Math.toRadians(fs.angle)) * RANGE[fs.id]) - 18;
        int visionY = fs.y + (int)(Math.sin(Math.toRadians(fs.angle)) * RANGE[fs.id]) - 18;
        
        if (visionX < 0) result += "*W~";
        else if (visionX > BG_WIDTH) result += "*E~";
        if (visionY < 0) result += "*N~";
        else if (visionY > BG_HEIGHT) result += "*S~";
        
        // 判断视角范围内是否有花
        for (FlowerSimple f : flowers) {
            if (f != null && f.visible && f.volume > 0) {
                int distance = (int)(Math.pow(fs.x - f.x, 2) + Math.pow(fs.y - f.y, 2));
                if (distance <= 4) {
                    result += "-("+f.volume+",ON)~";
                } else {
                    int distance1 = (int)(Math.pow((fs.x + 18*Math.cos(Math.toRadians(fs.angle)) - f.x), 2)
                            + Math.pow((fs.y + 18*Math.sin(Math.toRadians(fs.angle)) - f.y), 2));
                    if (distance1 <= RANGE[fs.id] * RANGE[fs.id]) {
                        double a = getVectorDegree(fs.x, fs.y, f.x, f.y);
                        result += "-("+f.volume+","+a+")~";
                    }
                }
            }
        }
        
        // 判断视角范围内是否有其他蜜蜂
        for (int i = 0; i < 10; i++) {
            if (fs.getisAlive() && i != fs.id && status[i] != null && status[i].isAlive) {
                FlyingStatus fs1 = status[i];
                int distance = (int)(Math.pow(fs.x - fs1.x, 2) + Math.pow(fs.y - fs1.y, 2));
                if (distance <= Math.pow(RANGE[fs.id], 2)) {
                    int distance1 = (int)(Math.pow((fs.x + 18*Math.cos(Math.toRadians(fs.angle)) - fs1.x), 2)
                            + Math.pow((fs.y + 18*Math.sin(Math.toRadians(fs.angle)) - fs1.y), 2));
                    int distance2 = (int)(Math.pow((fs.x - 18*Math.cos(Math.toRadians(fs.angle)) - fs1.x), 2)
                            + Math.pow((fs.y - 18*Math.sin(Math.toRadians(fs.angle)) - fs1.y), 2));
                    if (distance1 < distance2) {
                        double a = getVectorDegree(fs.x, fs.y, fs1.x, fs1.y);
                        result += "+("+fs1.id+","+a+","+fs1.angle+")~";
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * 采集花蜜(内部方法,供pickFlowerHoney静态方法调用)
     */
    private int pickFlower(int id) {
        FlyingStatus fs = status[id];
        if (fs == null) return -1;
        
        for (FlowerSimple f : flowers) {
            if (f != null && f.visible && f.volume > 0) {
                int distance = (int)(Math.pow(fs.x - f.x, 2) + Math.pow(fs.y - f.y, 2));
                if (distance <= 4) {
                    f.volume--;
                    totalHoney++;
                    if (f.volume <= 0) {
                        f.visible = false;
                        remainingFlowersCount--;
                        return 0;
                    }
                    return 1;
                }
            }
        }
        return -1;
    }
    
    /**
     * 大黄蜂捕杀蜜蜂(内部方法,供killBee静态方法调用)
     */
    private void killBeeInternal(int hornetId) {
        FlyingStatus hornetStatus = status[hornetId];
        if (hornetStatus == null) return;
        
        for (int i = 1; i <= 3; i++) {
            if (status[i] != null && status[i].isAlive) {
                int distance = (int)(Math.pow(hornetStatus.x - status[i].x, 2) + Math.pow(hornetStatus.y - status[i].y, 2));
                if (distance <= 16) {
                    status[i].isAlive = false;
                    aliveBeesCount--;
                    
                    // 调用大黄蜂的isCatched方法(如果存在)
                    try {
                        if (bees[hornetId] != null) {
                            bees[hornetId].getClass().getMethod("isCatched").invoke(bees[hornetId]);
                        }
                    } catch (Exception e) {
                        // 忽略,可能不是Hornet类或方法不存在
                    }
                }
            }
        }
    }
    
    /**
     * 计算最终分数
     */
    private void calculateFinalScore() {
        gameRecord.finalHoney = totalHoney;
        gameRecord.finalAliveCount = aliveBeesCount;
        
        int atime = 200 - time / 20;
        int goals = totalHoney + aliveBeesCount * 50;
        if (aliveBeesCount > 0 && remainingFlowersCount == 0) goals += atime;
        if (aliveBeesCount == 0 && remainingFlowersCount > 0) goals -= atime;
        
        gameRecord.finalScore = goals;
        gameRecord.winner = "Group " + gameRecord.group1;
    }
    
    /**
     * 简化的花朵类(无GUI)
     */
    private class FlowerSimple {
        int id, x, y, volume, maxVolume, appearTime, imageType;
        boolean visible;
        
        FlowerSimple(int id, int appearTime, int x, int y, int volume, int imageType) {
            this.id = id;
            this.appearTime = appearTime;
            this.x = x;
            this.y = y;
            this.volume = volume;
            this.maxVolume = volume;
            this.imageType = imageType;
            this.visible = false;
        }
    }
}
