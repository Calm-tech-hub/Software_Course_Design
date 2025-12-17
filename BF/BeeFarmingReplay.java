import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import java.io.*;

/**
 * 游戏回放界面
 * 从GameRecord读取数据并可视化展示
 */
public class BeeFarmingReplay extends JFrame {
    private static final int BG_WIDTH = 800;
    private static final int BG_HEIGHT = 600;
    
    // 图片资源
    private Image imgBee, imgBee2, imgBG;
    private Image[] imgFlw = new Image[3];
    
    // 回放控制器
    private ReplayController controller;
    
    // UI组件
    private ReplayPanel replayPanel;
    private JPanel controlPanel;
    private JButton btnPlay, btnPause, btnPrev, btnNext, btnRewind, btnFastForward;
    private JSlider progressSlider;
    private JLabel lblInfo, lblFrame, lblProgress;
    private Timer playTimer;
    
    // 当前显示的帧
    private GameFrame currentFrame;
    
    /**
     * 构造函数
     */
    public BeeFarmingReplay(String recordFilePath) {
        setTitle("蜜蜂采蜜游戏 - 回放模式");
        
        // 加载图片资源
        loadImages();
        
        // 初始化回放控制器
        controller = new ReplayController();
        if (!controller.loadRecord(recordFilePath)) {
            JOptionPane.showMessageDialog(this, "加载记录文件失败！", "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // 初始化界面
        initComponents();
        
        setSize(BG_WIDTH + 20, BG_HEIGHT + 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        
        // 显示第一帧
        updateFrame();
    }
    
    /**
     * 加载图片资源
     */
    private void loadImages() {
        try {
            imgBee = getToolkit().getImage("bee.png");
            imgBee2 = getToolkit().getImage("bee2.png");
            imgBG = getToolkit().getImage("green.jpg");
            imgFlw[0] = getToolkit().getImage("flower0.png");
            imgFlw[1] = getToolkit().getImage("flower1.png");
            imgFlw[2] = getToolkit().getImage("flower2.png");
            
            MediaTracker mt = new MediaTracker(this);
            mt.addImage(imgBee, 0);
            mt.addImage(imgBee2, 1);
            mt.addImage(imgBG, 2);
            for (int i = 0; i < 3; i++) {
                mt.addImage(imgFlw[i], 3 + i);
            }
            mt.waitForAll();
        } catch (Exception e) {
            System.err.println("加载图片失败: " + e.getMessage());
        }
    }
    
    /**
     * 初始化界面组件
     */
    private void initComponents() {
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        
        // 游戏画面面板
        replayPanel = new ReplayPanel();
        replayPanel.setPreferredSize(new Dimension(BG_WIDTH, BG_HEIGHT));
        container.add(replayPanel, BorderLayout.CENTER);
        
        // 控制面板
        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnRewind = new JButton("<<");
        btnPrev = new JButton("<");
        btnPlay = new JButton("播放");
        btnPause = new JButton("暂停");
        btnNext = new JButton(">");
        btnFastForward = new JButton(">>");
        
        btnRewind.addActionListener(e -> rewindAction());
        btnPrev.addActionListener(e -> prevFrameAction());
        btnPlay.addActionListener(e -> playAction());
        btnPause.addActionListener(e -> pauseAction());
        btnNext.addActionListener(e -> nextFrameAction());
        btnFastForward.addActionListener(e -> fastForwardAction());
        
        buttonPanel.add(btnRewind);
        buttonPanel.add(btnPrev);
        buttonPanel.add(btnPlay);
        buttonPanel.add(btnPause);
        buttonPanel.add(btnNext);
        buttonPanel.add(btnFastForward);
        
        // 进度条
        progressSlider = new JSlider(0, controller.getTotalFrames() - 1, 0);
        progressSlider.addChangeListener(e -> {
            if (!progressSlider.getValueIsAdjusting()) {
                controller.seekToFrame(progressSlider.getValue());
                updateFrame();
            }
        });
        
        // 信息标签
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        lblInfo = new JLabel(getRecordInfo());
        lblFrame = new JLabel("帧: 0 / " + controller.getTotalFrames());
        lblProgress = new JLabel("进度: 0.0%");
        infoPanel.add(lblInfo);
        infoPanel.add(lblFrame);
        infoPanel.add(lblProgress);
        
        controlPanel.add(buttonPanel, BorderLayout.NORTH);
        controlPanel.add(progressSlider, BorderLayout.CENTER);
        controlPanel.add(infoPanel, BorderLayout.SOUTH);
        
        container.add(controlPanel, BorderLayout.SOUTH);
        
        // 播放定时器
        playTimer = new Timer(50, e -> {
            if (controller.isPlaying()) {
                if (controller.nextFrame()) {
                    updateFrame();
                } else {
                    // 播放到结尾，停止
                    controller.pause();
                }
            }
        });
        playTimer.start();
    }
    
    /**
     * 获取记录信息
     */
    private String getRecordInfo() {
        GameRecord record = controller.getRecord();
        return String.format("对局: %s vs %s (第%d轮) | 得分: %d | 花蜜: %d kg | 存活: %d",
                record.group1, record.group2, record.roundNumber,
                record.finalScore, record.finalHoney, record.finalAliveCount);
    }
    
    /**
     * 更新显示帧
     */
    private void updateFrame() {
        currentFrame = controller.getCurrentFrame();
        lblFrame.setText("帧: " + controller.getCurrentFrameNumber() + " / " + controller.getTotalFrames());
        lblProgress.setText(String.format("进度: %.1f%%", controller.getProgress()));
        progressSlider.setValue(controller.getCurrentFrameNumber());
        replayPanel.repaint();
    }
    
    // 控制动作
    private void rewindAction() {
        controller.rewind();
        updateFrame();
    }
    
    private void prevFrameAction() {
        controller.previousFrame();
        updateFrame();
    }
    
    private void playAction() {
        controller.play();
    }
    
    private void pauseAction() {
        controller.pause();
    }
    
    private void nextFrameAction() {
        controller.nextFrame();
        updateFrame();
    }
    
    private void fastForwardAction() {
        controller.fastForward();
        updateFrame();
    }
    
    /**
     * 游戏画面绘制面板
     */
    private class ReplayPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // 绘制背景
            if (imgBG != null) {
                g.drawImage(imgBG, 0, 0, this);
            }
            
            if (currentFrame == null) return;
            
            // 绘制花朵
            for (FlowerState flower : currentFrame.flowerStates) {
                if (flower.visible && flower.volume > 0) {
                    drawFlower(g, flower);
                }
            }
            
            // 绘制蜜蜂
            for (BeeState bee : currentFrame.beeStates) {
                if (bee.isAlive) {
                    drawBee(g, bee);
                }
            }
            
            // 绘制信息
            drawGameInfo(g);
        }
        
        /**
         * 绘制花朵
         */
        private void drawFlower(Graphics g, FlowerState flower) {
            if (flower.imageType >= 0 && flower.imageType < imgFlw.length) {
                Image img = imgFlw[flower.imageType];
                g.drawImage(img, flower.x - 32, flower.y - 32, this);
                
                // 绘制黄点表示有蜜
                g.setColor(new Color(255, 255, 0));
                g.fillOval(flower.x - 4, flower.y - 4, 8, 8);
            }
        }
        
        /**
         * 绘制蜜蜂
         */
        private void drawBee(Graphics g, BeeState bee) {
            Image img = (bee.id == 9) ? imgBee2 : imgBee;
            if (img == null) return;
            
            // 旋转蜜蜂图片
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform old = g2d.getTransform();
            
            g2d.translate(bee.x, bee.y);
            g2d.rotate(Math.toRadians(bee.angle));
            g2d.drawImage(img, -img.getWidth(this) / 2, -img.getHeight(this) / 2, this);
            
            g2d.setTransform(old);
        }
        
        /**
         * 绘制游戏信息
         */
        private void drawGameInfo(Graphics g) {
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            
            int atime = 200 - currentFrame.time / 20;
            if (atime > 10) {
                g.setColor(new Color(128, 255, 255));
            } else {
                g.setColor(Color.RED);
            }
            g.drawString("TIME: " + atime + " S", 500, 30);
            
            g.setColor(new Color(128, 255, 255));
            g.drawString("totalHoney: " + currentFrame.totalHoney + " kg", 500, 65);
            g.drawString("still alive Bees: " + currentFrame.aliveBeesCount, 500, 100);
            
            int goals = currentFrame.totalHoney + currentFrame.aliveBeesCount * 50;
            if (currentFrame.aliveBeesCount > 0 && currentFrame.remainingFlowersCount == 0) 
                goals += atime;
            if (currentFrame.aliveBeesCount == 0 && currentFrame.remainingFlowersCount > 0) 
                goals -= atime;
            g.drawString("Goals: " + goals, 500, 135);
            
            // 如果到达结尾，显示GAME OVER
            if (controller.isAtEnd()) {
                g.setFont(new Font("Arial", Font.PLAIN, 60));
                g.setColor(Color.RED);
                g.drawString("GAME OVER", 230, 310);
            }
        }
    }
    
    /**
     * 主函数
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            // 查找最新的记录文件
            java.util.List<String> records = BatchSimulator.listBattleRecords("BattleRecords");
            if (records.isEmpty()) {
                System.out.println("未找到记录文件！");
                System.out.println("请先运行 BatchSimulator 生成对局记录");
                System.exit(1);
            }
            
            System.out.println("找到 " + records.size() + " 个记录文件:");
            for (int i = 0; i < records.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + new File(records.get(i)).getName());
            }
            
            // 使用最新的记录文件
            System.out.println("\n使用最新的记录文件进行回放...");
            SwingUtilities.invokeLater(() -> new BeeFarmingReplay(records.get(records.size() - 1)));
        } else {
            String filepath = args[0];
            SwingUtilities.invokeLater(() -> new BeeFarmingReplay(filepath));
        }
    }
}
