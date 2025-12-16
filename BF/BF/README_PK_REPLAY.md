# 蜜蜂采蜜游戏 - 多轮PK和回放系统

## 功能概述

本系统实现了蜜蜂采蜜游戏的多轮PK功能和历史回放功能，主要包括：

1. **后台快速计算**: 无GUI的游戏引擎快速模拟多轮对局（每轮约0.07秒）
2. **数据持久化**: 完整记录每一帧的游戏状态到文件
3. **历史回放**: 可视化回放任意一轮对局，支持播放控制
4. **PK管理界面**: 图形化配置和管理对局，浏览历史记录

## 文件说明

### 核心类

1. **数据结构类**
   - `BeeState.java` - 蜜蜂状态记录
   - `FlowerState.java` - 花朵状态记录
   - `GameFrame.java` - 单帧游戏状态
   - `GameRecord.java` - 完整对局记录

2. **游戏引擎类**
   - `GameEngine.java` - 无GUI游戏核心引擎
   - `AlgorithmLoader.java` - 动态算法加载器
   - `BatchSimulator.java` - 批量模拟器

3. **回放系统类**
   - `ReplayController.java` - 回放控制器
   - `BeeFarmingReplay.java` - 回放可视化界面

4. **管理界面类**
   - `BattleManager.java` - PK管理系统主界面

## 使用方法

### 方式一：使用PK管理界面（推荐）

1. **启动管理界面**
   ```bash
   cd /home/ubuntu22/Desktop/软件课设/BF/BF
   java BattleManager
   ```

2. **配置PK**
   - 选择"蜜蜂算法组"（group1）
   - 选择"大黄蜂算法组"（group2）
   - 设置对战轮数（1-100）
   - 点击"开始PK"按钮

3. **查看历史记录**
   - 记录列表显示所有已完成的对局
   - 包含轮次、组别、得分、花蜜、存活数等信息
   - 点击"刷新列表"更新记录

4. **回放对局**
   - 在列表中选中要回放的记录
   - 点击"回放选中"按钮
   - 自动打开回放界面

### 方式二：命令行批量模拟

1. **运行批量模拟**
   ```bash
   cd /home/ubuntu22/Desktop/软件课设/BF/BF
   java BatchSimulator <组1> <组2> <轮数>
   ```

   示例：
   ```bash
   java BatchSimulator BF BF 5
   ```

2. **查看结果**
   - 对局记录保存在 `BattleRecords/` 目录
   - 文件命名格式: `battle_组1vs组2_round轮次_时间戳.dat`

### 方式三：直接回放

1. **回放指定记录**
   ```bash
   cd /home/ubuntu22/Desktop/软件课设/BF/BF
   java BeeFarmingReplay <记录文件路径>
   ```

2. **回放最新记录**
   ```bash
   java BeeFarmingReplay
   ```
   自动选择最新的记录文件进行回放

## 回放控制

回放界面提供以下控制功能：

- **<<** (倒回) - 跳到第一帧
- **<** (上一帧) - 后退一帧
- **播放** - 开始自动播放
- **暂停** - 暂停播放
- **>** (下一帧) - 前进一帧
- **>>** (快进) - 跳到最后一帧
- **进度条** - 拖动快速定位到指定帧

## 目录结构

```
BF/BF/
├── 原有文件（BeeFarming.java等）
├── 数据结构类
│   ├── BeeState.java
│   ├── FlowerState.java
│   ├── GameFrame.java
│   └── GameRecord.java
├── 游戏引擎
│   ├── GameEngine.java
│   ├── AlgorithmLoader.java
│   └── BatchSimulator.java
├── 回放系统
│   ├── ReplayController.java
│   └── BeeFarmingReplay.java
├── 管理界面
│   └── BattleManager.java
└── BattleRecords/          # 对局记录目录
    └── battle_*.dat        # 记录文件

group/                       # 算法组目录
├── 001/
│   ├── HoneyBee.java
│   └── Hornet.java
├── 006/
│   └── ...
└── ...
```

## 添加新算法组

1. 在 `group/` 目录下创建新文件夹（如 `123/`）
2. 将算法类（`HoneyBee.java` 和 `Hornet.java`）放入该文件夹
3. 编译算法类：
   ```bash
   cd group/123
   javac HoneyBee.java Hornet.java
   ```
4. 在PK管理界面中会自动识别新组别

## 性能说明

- **模拟速度**: 每轮约0.07秒（无GUI）
- **记录大小**: 每轮约400KB（包含所有帧数据）
- **支持轮数**: 理论上无限制，实测100轮无问题
- **回放流畅度**: 50ms/帧，可调整

## 扩展功能

系统已预留扩展接口，可以轻松添加：

1. **更多轮数支持**: 修改 `BattleManager` 中的轮数上限
2. **算法热插拔**: 完善 `AlgorithmLoader` 实现运行时加载
3. **数据分析**: 利用 `GameRecord` 进行统计分析
4. **网络对战**: 基于记录文件实现异步对战
5. **回放速度调整**: 修改 `BeeFarmingReplay` 的定时器间隔

## 技术特点

1. **分离关注点**: 游戏逻辑与GUI完全分离
2. **数据驱动**: 基于状态记录的回放机制
3. **可扩展性**: 模块化设计，易于扩展
4. **持久化**: 使用Java序列化保存完整状态
5. **用户友好**: 提供图形化管理界面

## 故障排除

### 问题1: 找不到记录文件
- 检查 `BattleRecords/` 目录是否存在
- 确保已运行至少一次模拟

### 问题2: 算法加载失败
- 检查算法类是否已编译（.class文件）
- 确认类名为 `HoneyBee` 和 `Hornet`

### 问题3: 回放界面无法启动
- 检查图片资源文件（bee.png, flower0-2.png等）
- 确保记录文件未损坏

### 问题4: 中文显示乱码
- 编译时使用 `-encoding UTF-8` 参数
- 确保系统支持中文字符集

## 示例工作流

1. **运行10轮PK测试**
   ```bash
   java BattleManager
   # 在界面中选择组别，设置10轮，点击"开始PK"
   ```

2. **查看第5轮回放**
   - 在记录列表中找到第5轮
   - 点击该行，然后点击"回放选中"
   - 使用播放控制观看对局过程

3. **对比不同组别**
   - 分别运行 A vs B 和 A vs C
   - 在记录列表中对比得分
   - 选择感兴趣的对局进行回放分析

## 开发者信息

- **版本**: 1.0
- **开发日期**: 2025年12月16日
- **兼容性**: Java 8+
- **测试环境**: Ubuntu 22.04, OpenJDK 11

## 未来规划

- [ ] 支持更多回放速度档位（慢放、快放）
- [ ] 添加回放时的事件标记（采蜜、被捕等）
- [ ] 实现对局数据的统计图表
- [ ] 支持多组算法的循环赛
- [ ] 添加算法性能评分系统
