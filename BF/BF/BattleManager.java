import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * PK管理界面
 * 用于配置和启动多轮PK，浏览历史记录，选择回放
 */
public class BattleManager extends JFrame {
    // 组件
    private JComboBox<String> cmbGroup1, cmbGroup2;
    private JSpinner spinRounds;
    private JButton btnStartBattle, btnStartDualBattle, btnRefresh, btnReplay;
    private JTable tableRecords;
    private DefaultTableModel tableModel;
    private JTextArea txtLog;
    private JProgressBar progressBar;
    private JLabel lblStatus;
    
    // 数据
    private List<String> availableGroups;
    private List<String> recordFiles;
    
    /**
     * 构造函数
     */
    public BattleManager() {
        setTitle("蜜蜂采蜜游戏 - PK管理系统");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 初始化数据
        loadAvailableGroups();
        loadRecordFiles();
        
        // 初始化界面
        initComponents();
        
        setVisible(true);
    }
    
    /**
     * 加载可用的组
     */
    private void loadAvailableGroups() {
        availableGroups = new ArrayList<>();
        availableGroups.add("BF"); // 默认组
        
        // 扫描group/group目录
        File groupDir = new File("../../group/group");
        if (groupDir.exists() && groupDir.isDirectory()) {
            File[] dirs = groupDir.listFiles(File::isDirectory);
            if (dirs != null) {
                for (File dir : dirs) {
                    // 检查是否包含HoneyBee.class和Hornet.class
                    File honeyBee = new File(dir, "HoneyBee.class");
                    File hornet = new File(dir, "Hornet.class");
                    if (honeyBee.exists() && hornet.exists()) {
                        availableGroups.add(dir.getName());
                    }
                }
            }
        }
        
        // 也扫描group目录（兼容）
        groupDir = new File("../../group");
        if (groupDir.exists() && groupDir.isDirectory()) {
            File[] dirs = groupDir.listFiles(File::isDirectory);
            if (dirs != null) {
                for (File dir : dirs) {
                    String dirName = dir.getName();
                    if (!dirName.equals("group") && !availableGroups.contains(dirName)) {
                        File honeyBee = new File(dir, "HoneyBee.class");
                        File hornet = new File(dir, "Hornet.class");
                        if (honeyBee.exists() && hornet.exists()) {
                            availableGroups.add(dirName);
                        }
                    }
                }
            }
        }
        
        Collections.sort(availableGroups);
    }
    
    /**
     * 加载记录文件
     */
    private void loadRecordFiles() {
        recordFiles = BatchSimulator.listBattleRecords("BattleRecords");
    }
    
    /**
     * 初始化界面组件
     */
    private void initComponents() {
        Container container = getContentPane();
        container.setLayout(new BorderLayout(10, 10));
        
        // 顶部配置面板
        JPanel topPanel = createConfigPanel();
        container.add(topPanel, BorderLayout.NORTH);
        
        // 中间分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // 上部：记录列表
        JPanel recordPanel = createRecordPanel();
        splitPane.setTopComponent(recordPanel);
        
        // 下部：日志
        JPanel logPanel = createLogPanel();
        splitPane.setBottomComponent(logPanel);
        
        splitPane.setDividerLocation(350);
        container.add(splitPane, BorderLayout.CENTER);
        
        // 底部状态栏
        JPanel statusPanel = createStatusPanel();
        container.add(statusPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 创建配置面板
     */
    private JPanel createConfigPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("PK配置"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 组1选择
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("组A:"), gbc);
        gbc.gridx = 1;
        cmbGroup1 = new JComboBox<>(availableGroups.toArray(new String[0]));
        panel.add(cmbGroup1, gbc);
        
        // 组2选择
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("组B:"), gbc);
        gbc.gridx = 3;
        cmbGroup2 = new JComboBox<>(availableGroups.toArray(new String[0]));
        panel.add(cmbGroup2, gbc);
        
        // 轮数选择
        gbc.gridx = 4; gbc.gridy = 0;
        panel.add(new JLabel("轮数:"), gbc);
        gbc.gridx = 5;
        spinRounds = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        panel.add(spinRounds, gbc);
        
        // 按钮
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        btnStartBattle = new JButton("单向PK (仅测试)");
        btnStartBattle.addActionListener(e -> startBattle());
        panel.add(btnStartBattle, gbc);
        
        gbc.gridx = 3; gbc.gridy = 1; gbc.gridwidth = 3;
        btnStartDualBattle = new JButton("双向对战 (推荐)");
        btnStartDualBattle.setFont(new Font(btnStartDualBattle.getFont().getName(), Font.BOLD, 14));
        btnStartDualBattle.setForeground(new Color(0, 128, 0));
        btnStartDualBattle.addActionListener(e -> startDualBattle());
        panel.add(btnStartDualBattle, gbc);
        
        return panel;
    }
    
    /**
     * 创建记录面板
     */
    private JPanel createRecordPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("历史对局记录"));
        
        // 创建表格
        String[] columns = {"轮次", "组1", "组2", "得分", "花蜜", "存活", "时间", "文件"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableRecords = new JTable(tableModel);
        tableRecords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 设置列宽
        tableRecords.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableRecords.getColumnModel().getColumn(1).setPreferredWidth(60);
        tableRecords.getColumnModel().getColumn(2).setPreferredWidth(60);
        tableRecords.getColumnModel().getColumn(7).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(tableRecords);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRefresh = new JButton("刷新列表");
        btnRefresh.addActionListener(e -> refreshRecordList());
        btnReplay = new JButton("回放选中");
        btnReplay.addActionListener(e -> replaySelected());
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnReplay);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 加载记录
        refreshRecordList();
        
        return panel;
    }
    
    /**
     * 创建日志面板
     */
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("运行日志"));
        
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtLog);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 创建状态面板
     */
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        lblStatus = new JLabel("就绪");
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(lblStatus, BorderLayout.WEST);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(200, 20));
        panel.add(progressBar, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * 刷新记录列表
     */
    private void refreshRecordList() {
        tableModel.setRowCount(0);
        loadRecordFiles();
        
        int successCount = 0;
        for (String filepath : recordFiles) {
            try {
                GameRecord record = GameRecord.loadFromFile(filepath);
                Object[] row = {
                    record.roundNumber,
                    record.group1,
                    record.group2,
                    record.finalScore,
                    record.finalHoney,
                    record.finalAliveCount,
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(record.timestamp)),
                    new File(filepath).getName()
                };
                tableModel.addRow(row);
                successCount++;
            } catch (Exception e) {
                System.err.println("加载记录失败: " + filepath);
            }
        }
        
        if (txtLog != null) {
            log("已加载 " + successCount + " 条记录");
        }
    }
    
    /**
     * 开始对战
     */
    private void startBattle() {
        String group1 = (String) cmbGroup1.getSelectedItem();
        String group2 = (String) cmbGroup2.getSelectedItem();
        int rounds = (Integer) spinRounds.getValue();
        
        if (group1 == null || group2 == null) {
            JOptionPane.showMessageDialog(this, "请选择对战组！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        log("========================================");
        log("开始PK: " + group1 + " vs " + group2 + " (" + rounds + "轮)");
        log("========================================");
        
        btnStartBattle.setEnabled(false);
        lblStatus.setText("正在运行...");
        progressBar.setValue(0);
        
        // 在后台线程运行
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                BatchSimulator simulator = new BatchSimulator(group1, group2, rounds);
                
                for (int round = 1; round <= rounds; round++) {
                    publish("===== 第 " + round + " 轮 =====");
                    try {
                        GameEngine engine = new GameEngine(group1, group2, round);
                        GameRecord record = engine.runSimulation();
                        record.saveToFile("BattleRecords");
                        
                        publish("第" + round + "轮完成: 得分=" + record.finalScore);
                        setProgress((int) ((double) round / rounds * 100));
                    } catch (Exception e) {
                        publish("第" + round + "轮失败: " + e.getMessage());
                    }
                }
                
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    log(message);
                }
            }
            
            @Override
            protected void done() {
                log("========================================");
                log("PK完成！");
                log("========================================");
                btnStartBattle.setEnabled(true);
                lblStatus.setText("就绪");
                progressBar.setValue(100);
                refreshRecordList();
            }
        };
        
        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                progressBar.setValue((Integer) evt.getNewValue());
            }
        });
        
        worker.execute();
    }
    
    /**
     * 开始双向对战（推荐方式）- 使用SimpleBattle的文件复制方式
     */
    private void startDualBattle() {
        String groupA = (String) cmbGroup1.getSelectedItem();
        String groupB = (String) cmbGroup2.getSelectedItem();
        int rounds = (Integer) spinRounds.getValue();
        
        if (groupA == null || groupB == null) {
            JOptionPane.showMessageDialog(this, "请选择对战组！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (groupA.equals(groupB)) {
            JOptionPane.showMessageDialog(this, "请选择不同的组进行对战！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        log("==========================================");
        log("双向对战: " + groupA + " vs " + groupB);
        log("每方向轮数: " + rounds + " (总计 " + (rounds * 2) + " 局)");
        log("==========================================");
        
        btnStartBattle.setEnabled(false);
        btnStartDualBattle.setEnabled(false);
        lblStatus.setText("正在运行双向对战...");
        progressBar.setValue(0);
        
        // 在后台线程运行
        SwingWorker<BattleResult, String> worker = 
            new SwingWorker<BattleResult, String>() {
            
            @Override
            protected BattleResult doInBackground() throws Exception {
                long startTime = System.currentTimeMillis();
                
                // 备份原始文件
                publish("备份原始算法文件...");
                SimpleBattle.backupOriginalFiles();
                
                try {
                    int totalRounds = rounds * 2;
                    int currentRound = 0;
                    
                    // 第一阶段: A蜜蜂 vs B大黄蜂
                    publish("");
                    publish("【第一阶段】" + groupA + " 的蜜蜂 vs " + groupB + " 的大黄蜂");
                    publish("------------------------------------------");
                    double[] scoresA = new double[rounds];
                    
                    for (int round = 1; round <= rounds; round++) {
                        publish("回合 " + round + "/" + rounds);
                        
                        // 复制组算法
                        SimpleBattle.copyGroupAlgorithms(groupA, groupB);
                        
                        // 运行游戏引擎 (false = 不加载算法，直接使用当前目录的.class)
                        GameEngine engine = new GameEngine(
                            groupA + "(蜜蜂)", 
                            groupB + "(大黄蜂)",
                            groupA,
                            groupB,
                            round,
                            false  // 不加载算法，使用已复制到当前目录的.class文件
                        );
                        GameRecord record = engine.runSimulation();
                        scoresA[round - 1] = record.finalScore;
                        
                        // 保存记录到目录
                        record.saveToFile("BattleRecords");
                        
                        publish("  得分: " + (int)record.finalScore);
                        currentRound++;
                        setProgress((int) ((double) currentRound / totalRounds * 100));
                    }
                    
                    double avgA = SimpleBattle.average(scoresA);
                    publish(groupA + " 平均得分: " + String.format("%.2f", avgA));
                    
                    // 第二阶段: B蜜蜂 vs A大黄蜂
                    publish("");
                    publish("【第二阶段】" + groupB + " 的蜜蜂 vs " + groupA + " 的大黄蜂");
                    publish("------------------------------------------");
                    double[] scoresB = new double[rounds];
                    
                    for (int round = 1; round <= rounds; round++) {
                        publish("回合 " + round + "/" + rounds);
                        
                        // 复制组算法
                        SimpleBattle.copyGroupAlgorithms(groupB, groupA);
                        
                        // 运行游戏引擎
                        GameEngine engine = new GameEngine(
                            groupB + "(蜜蜂)", 
                            groupA + "(大黄蜂)",
                            groupB,
                            groupA,
                            round,
                            false
                        );
                        GameRecord record = engine.runSimulation();
                        scoresB[round - 1] = record.finalScore;
                        
                        // 保存记录到目录
                        record.saveToFile("BattleRecords");
                        
                        publish("  得分: " + (int)record.finalScore);
                        currentRound++;
                        setProgress((int) ((double) currentRound / totalRounds * 100));
                    }
                    
                    double avgB = SimpleBattle.average(scoresB);
                    publish(groupB + " 平均得分: " + String.format("%.2f", avgB));
                    
                    // 计算结果
                    long duration = System.currentTimeMillis() - startTime;
                    String winner;
                    double diff = Math.abs(avgA - avgB);
                    
                    if (avgA > avgB) {
                        winner = groupA + " (领先 " + String.format("%.2f", diff) + " 分)";
                    } else if (avgB > avgA) {
                        winner = groupB + " (领先 " + String.format("%.2f", diff) + " 分)";
                    } else {
                        winner = "平局";
                    }
                    
                    return new BattleResult(groupA, groupB, avgA, avgB, winner, duration);
                    
                } finally {
                    // 恢复原始文件
                    publish("恢复原始算法文件...");
                    SimpleBattle.restoreOriginalFiles();
                }
            }
            
            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    log(message);
                }
            }
            
            @Override
            protected void done() {
                try {
                    BattleResult result = get();
                    log("");
                    log("==========================================");
                    log("对战结果");
                    log("==========================================");
                    log(result.groupA + " 组作为蜜蜂平均得分: " + String.format("%.2f", result.avgScoreA));
                    log(result.groupB + " 组作为蜜蜂平均得分: " + String.format("%.2f", result.avgScoreB));
                    log("");
                    log("获胜方: " + result.winner);
                    log("总耗时: " + String.format("%.3f", result.duration / 1000.0) + " 秒");
                    log("==========================================");
                } catch (Exception e) {
                    log("获取结果失败: " + e.getMessage());
                    e.printStackTrace();
                }
                
                btnStartBattle.setEnabled(true);
                btnStartDualBattle.setEnabled(true);
                lblStatus.setText("就绪");
                progressBar.setValue(100);
                refreshRecordList();
            }
        };
        
        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                progressBar.setValue((Integer) evt.getNewValue());
            }
        });
        
        worker.execute();
    }
    
    /**
     * 回放选中的记录
     */
    private void replaySelected() {
        int selectedRow = tableRecords.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要回放的记录！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String filepath = recordFiles.get(selectedRow);
        log("启动回放: " + new File(filepath).getName());
        
        // 启动回放界面
        SwingUtilities.invokeLater(() -> new BeeFarmingReplay(filepath));
    }
    
    /**
     * 日志输出
     */
    private void log(String message) {
        txtLog.append(message + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
    
    /**
     * 对战结果数据类
     */
    private static class BattleResult {
        String groupA;
        String groupB;
        double avgScoreA;
        double avgScoreB;
        String winner;
        long duration;
        
        BattleResult(String groupA, String groupB, double avgScoreA, double avgScoreB, String winner, long duration) {
            this.groupA = groupA;
            this.groupB = groupB;
            this.avgScoreA = avgScoreA;
            this.avgScoreB = avgScoreB;
            this.winner = winner;
            this.duration = duration;
        }
    }
    
    /**
     * 主函数
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BattleManager());
    }
}
