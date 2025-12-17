import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * 轻量级Web服务器
 * 提供REST API和静态文件服务
 */
public class WebServer {
    private static final int PORT = 8080;
    private static final String WEB_ROOT = "./web";
    private HttpServer server;
    
    public WebServer() throws IOException {
        
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.setExecutor(Executors.newFixedThreadPool(10));
        
        // 注册API路由
        server.createContext("/api/groups", new GroupsHandler());
        server.createContext("/api/battle", new BattleHandler());
        server.createContext("/api/battle/result", new BattleResultHandler());
        server.createContext("/api/records", new RecordsHandler());
        server.createContext("/api/rankings", new RankingsHandler());
        server.createContext("/api/statistics", new StatisticsHandler());
        server.createContext("/api/upload", new UploadHandler());
        server.createContext("/api/replay", new ReplayHandler());
        
        // 静态文件服务
        server.createContext("/", new StaticFileHandler());
    }
    
    public void start() {
        server.start();
        System.out.println("========================================");
        System.out.println("蜜蜂采蜜游戏 - Web管理平台");
        System.out.println("========================================");
        System.out.println("服务器已启动: http://localhost:" + PORT);
        System.out.println("请在浏览器中打开上述地址");
        System.out.println("按 Ctrl+C 停止服务器");
        System.out.println("========================================");
    }
    
    public void stop() {
        server.stop(0);
    }
    
    // ==================== API 处理器 ====================
    
    /**
     * 获取所有可用的组
     */
    class GroupsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            
            List<Map<String, Object>> groups = new ArrayList<>();
            
            // 扫描group/group目录
            File groupDir = new File("../../group/group");
            if (groupDir.exists() && groupDir.isDirectory()) {
                File[] dirs = groupDir.listFiles(File::isDirectory);
                if (dirs != null) {
                    for (File dir : dirs) {
                        File honeyBee = new File(dir, "HoneyBee.class");
                        File hornet = new File(dir, "Hornet.class");
                        if (honeyBee.exists() && hornet.exists()) {
                            Map<String, Object> group = new HashMap<>();
                            group.put("id", dir.getName());
                            group.put("name", dir.getName());
                            group.put("hasHoneyBee", true);
                            group.put("hasHornet", true);
                            groups.add(group);
                        }
                    }
                }
            }
            
            // 添加默认BF组
            Map<String, Object> bfGroup = new HashMap<>();
            bfGroup.put("id", "BF");
            bfGroup.put("name", "BF (默认)");
            bfGroup.put("hasHoneyBee", true);
            bfGroup.put("hasHornet", true);
            groups.add(0, bfGroup);
            
            sendJsonResponse(exchange, 200, groups);
        }
    }
    
    /**
     * 启动对战
     */
    class BattleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"POST".equals(exchange.getRequestMethod())) {
                    sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                    return;
                }
                
                // 解析请求体
                String body = new String(exchange.getRequestBody().readAllBytes());
                System.out.println("收到对战请求: " + body);
                
                Map<String, Object> request = SimpleJSON.parseJSON(body);
                System.out.println("解析结果: " + request);
                
                String groupA = (String) request.get("groupA");
                String groupB = (String) request.get("groupB");
                
                // 处理rounds可能是Integer或Double
                Object roundsObj = request.get("rounds");
                int rounds;
                if (roundsObj instanceof Integer) {
                    rounds = (Integer) roundsObj;
                } else if (roundsObj instanceof Double) {
                    rounds = ((Double) roundsObj).intValue();
                } else {
                    rounds = Integer.parseInt(roundsObj.toString());
                }
                
                System.out.println("参数: groupA=" + groupA + ", groupB=" + groupB + ", rounds=" + rounds);
                
                // 在新线程中运行对战
                final int finalRounds = rounds;
                final String battleId = groupA + "_vs_" + groupB + "_" + System.currentTimeMillis();
                
                new Thread(() -> {
                    try {
                        System.out.println("开始对战: " + groupA + " vs " + groupB + " (" + finalRounds + "轮)");
                        DualBattleSimulator.BattleResult result = runBattle(groupA, groupB, finalRounds);
                        System.out.println("对战完成: " + groupA + " vs " + groupB);
                        
                        // 保存结果到缓存
                        synchronized (battleResults) {
                            battleResults.put(battleId, result);
                        }
                    } catch (Exception e) {
                        System.err.println("对战失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                }).start();
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "对战已启动，正在后台运行");
                response.put("battleId", battleId);
                sendJsonResponse(exchange, 200, response);
            } catch (Exception e) {
                System.err.println("处理对战请求失败: " + e.getMessage());
                e.printStackTrace();
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", e.getMessage());
                sendJsonResponse(exchange, 500, errorResponse);
            }
        }
        
        private DualBattleSimulator.BattleResult runBattle(String groupA, String groupB, int rounds) throws Exception {
            // 直接调用DualBattleSimulator
            DualBattleSimulator simulator = new DualBattleSimulator(groupA, groupB, rounds);
            return simulator.runDualBattle();
        }
    }
    
    // 存储对战结果的缓存
    private static Map<String, DualBattleSimulator.BattleResult> battleResults = new HashMap<>();
    
    /**
     * 获取对战结果
     */
    class BattleResultHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            
            String query = exchange.getRequestURI().getQuery();
            String battleId = null;
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=");
                    if (kv.length == 2 && "battleId".equals(kv[0])) {
                        battleId = kv[1];
                        break;
                    }
                }
            }
            
            if (battleId == null) {
                sendResponse(exchange, 400, "{\"error\":\"Battle ID required\"}");
                return;
            }
            
            DualBattleSimulator.BattleResult result;
            synchronized (battleResults) {
                result = battleResults.get(battleId);
            }
            
            if (result == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("completed", false);
                sendJsonResponse(exchange, 200, response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("completed", true);
                response.put("groupA", result.groupA);
                response.put("groupB", result.groupB);
                response.put("winner", result.winner);
                response.put("scoreDiff", result.scoreDiff);
                response.put("groupA_avgScore", result.groupA_totalAvgScore);
                response.put("groupB_avgScore", result.groupB_totalAvgScore);
                response.put("groupA_honey", result.groupA_HoneyBee_avgHoney);
                response.put("groupB_honey", result.groupB_HoneyBee_avgHoney);
                response.put("groupA_alive", result.groupA_HoneyBee_avgAlive);
                response.put("groupB_alive", result.groupB_HoneyBee_avgAlive);
                response.put("totalTime", result.totalTime);
                sendJsonResponse(exchange, 200, response);
                
                // 返回后移除结果（避免内存泄漏）
                synchronized (battleResults) {
                    battleResults.remove(battleId);
                }
            }
        }
    }
    
    /**
     * 获取对战记录
     */
    class RecordsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            
            List<Map<String, Object>> records = new ArrayList<>();
            File recordDir = new File("BattleRecords");
            
            if (recordDir.exists() && recordDir.isDirectory()) {
                File[] files = recordDir.listFiles((dir, name) -> name.endsWith(".dat"));
                if (files != null) {
                    Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
                    
                    for (File file : files) {
                        try {
                            GameRecord record = GameRecord.loadFromFile(file.getAbsolutePath());
                            Map<String, Object> recordMap = new HashMap<>();
                            recordMap.put("filename", file.getName());
                            recordMap.put("group1", record.group1);
                            recordMap.put("group2", record.group2);
                            recordMap.put("round", record.roundNumber);
                            recordMap.put("score", record.finalScore);
                            recordMap.put("honey", record.finalHoney);
                            recordMap.put("alive", record.finalAliveCount);
                            recordMap.put("timestamp", record.timestamp);
                            records.add(recordMap);
                        } catch (Exception e) {
                            // 跳过损坏的文件
                        }
                    }
                }
            }
            
            sendJsonResponse(exchange, 200, records);
        }
    }
    
    /**
     * 获取排名榜
     */
    class RankingsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            
            Map<String, GroupStats> stats = new HashMap<>();
            File recordDir = new File("BattleRecords");
            
            if (recordDir.exists() && recordDir.isDirectory()) {
                File[] files = recordDir.listFiles((dir, name) -> name.endsWith(".dat"));
                if (files != null) {
                    for (File file : files) {
                        try {
                            GameRecord record = GameRecord.loadFromFile(file.getAbsolutePath());
                            
                            // 统计group1（蜜蜂方）
                            String group = extractGroupName(record.group1);
                            stats.putIfAbsent(group, new GroupStats(group));
                            stats.get(group).addScore(record.finalScore);
                            stats.get(group).totalBattles++;
                            
                        } catch (Exception e) {
                            // 跳过损坏的文件
                        }
                    }
                }
            }
            
            // 转换为列表并排序
            List<Map<String, Object>> rankings = new ArrayList<>();
            for (GroupStats stat : stats.values()) {
                Map<String, Object> rank = new HashMap<>();
                rank.put("group", stat.groupName);
                rank.put("totalScore", stat.totalScore);
                rank.put("avgScore", stat.getAvgScore());
                rank.put("battles", stat.totalBattles);
                rank.put("maxScore", stat.maxScore);
                rank.put("minScore", stat.minScore);
                rankings.add(rank);
            }
            
            rankings.sort((a, b) -> Double.compare(
                (Double) b.get("avgScore"), 
                (Double) a.get("avgScore")
            ));
            
            sendJsonResponse(exchange, 200, rankings);
        }
        
        private String extractGroupName(String displayName) {
            // 从"001(蜜蜂)"中提取"001"
            int idx = displayName.indexOf('(');
            if (idx > 0) {
                return displayName.substring(0, idx);
            }
            return displayName;
        }
    }
    
    /**
     * 组统计数据
     */
    static class GroupStats {
        String groupName;
        int totalScore = 0;
        int totalBattles = 0;
        int maxScore = 0;
        int minScore = Integer.MAX_VALUE;
        
        GroupStats(String name) {
            this.groupName = name;
        }
        
        void addScore(int score) {
            totalScore += score;
            maxScore = Math.max(maxScore, score);
            minScore = Math.min(minScore, score);
        }
        
        double getAvgScore() {
            return totalBattles > 0 ? (double) totalScore / totalBattles : 0;
        }
    }
    
    /**
     * 获取统计数据
     */
    class StatisticsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalBattles", countTotalBattles());
            statistics.put("totalGroups", countTotalGroups());
            statistics.put("recentBattles", getRecentBattlesData());
            
            sendJsonResponse(exchange, 200, statistics);
        }
        
        private int countTotalBattles() {
            File recordDir = new File("BattleRecords");
            if (recordDir.exists() && recordDir.isDirectory()) {
                File[] files = recordDir.listFiles((dir, name) -> name.endsWith(".dat"));
                return files != null ? files.length : 0;
            }
            return 0;
        }
        
        private int countTotalGroups() {
            // 统计实际参与过对战的组数
            Set<String> groups = new HashSet<>();
            File recordDir = new File("BattleRecords");
            
            if (recordDir.exists() && recordDir.isDirectory()) {
                File[] files = recordDir.listFiles((dir, name) -> name.endsWith(".dat"));
                if (files != null) {
                    for (File file : files) {
                        try {
                            GameRecord record = GameRecord.loadFromFile(file.getAbsolutePath());
                            // 提取组名
                            String group = extractGroupName(record.group1);
                            groups.add(group);
                        } catch (Exception e) {
                            // 跳过损坏的文件
                        }
                    }
                }
            }
            
            return groups.size();
        }
        
        private List<Map<String, Object>> getRecentBattlesData() {
            List<Map<String, Object>> recent = new ArrayList<>();
            File recordDir = new File("BattleRecords");
            
            if (recordDir.exists() && recordDir.isDirectory()) {
                File[] files = recordDir.listFiles((dir, name) -> name.endsWith(".dat"));
                if (files != null) {
                    Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
                    
                    int count = Math.min(10, files.length);
                    for (int i = 0; i < count; i++) {
                        try {
                            GameRecord record = GameRecord.loadFromFile(files[i].getAbsolutePath());
                            Map<String, Object> data = new HashMap<>();
                            data.put("group", extractGroupName(record.group1));
                            data.put("score", record.finalScore);
                            recent.add(data);
                        } catch (Exception e) {
                            // 跳过
                        }
                    }
                }
            }
            
            return recent;
        }
        
        private String extractGroupName(String displayName) {
            int idx = displayName.indexOf('(');
            if (idx > 0) {
                return displayName.substring(0, idx);
            }
            return displayName;
        }
    }
    
    /**
     * 上传算法文件
     */
    class UploadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            
            try {
                System.out.println("收到上传请求");
                
                // 解析multipart/form-data
                String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
                if (contentType == null || !contentType.startsWith("multipart/form-data")) {
                    System.out.println("错误: Content-Type不正确 - " + contentType);
                    sendResponse(exchange, 400, "{\"error\":\"Invalid content type\"}");
                    return;
                }
                
                // 简单实现：读取请求体
                byte[] data = exchange.getRequestBody().readAllBytes();
                String body = new String(data, "UTF-8");
                
                // 提取表单数据（简化版本）
                String groupId = extractFormValue(body, "groupId");
                String honeyBeeCode = extractFormValue(body, "honeyBeeFile");
                String hornetCode = extractFormValue(body, "hornetFile");
                
                System.out.println("提取的参数: groupId=" + groupId);
                System.out.println("HoneyBee代码长度: " + (honeyBeeCode != null ? honeyBeeCode.length() : 0));
                System.out.println("Hornet代码长度: " + (hornetCode != null ? hornetCode.length() : 0));
                
                if (groupId == null || groupId.isEmpty()) {
                    sendResponse(exchange, 400, "{\"error\":\"Group ID required\"}");
                    return;
                }
                
                // 创建组目录
                File groupDir = new File("../../group/group/" + groupId);
                if (!groupDir.exists()) {
                    groupDir.mkdirs();
                    System.out.println("创建目录: " + groupDir.getAbsolutePath());
                }
                
                // 保存文件
                if (honeyBeeCode != null && !honeyBeeCode.isEmpty()) {
                    File honeyBeeFile = new File(groupDir, "HoneyBee.java");
                    Files.writeString(honeyBeeFile.toPath(), honeyBeeCode);
                    System.out.println("保存文件: " + honeyBeeFile.getAbsolutePath());
                }
                if (hornetCode != null && !hornetCode.isEmpty()) {
                    File hornetFile = new File(groupDir, "Hornet.java");
                    Files.writeString(hornetFile.toPath(), hornetCode);
                    System.out.println("保存文件: " + hornetFile.getAbsolutePath());
                }
                
                // 编译
                System.out.println("开始编译组: " + groupId);
                String compileResult = compileGroup(groupId);
                System.out.println("编译完成: " + compileResult);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "算法上传成功并编译完成");
                response.put("compileOutput", compileResult);
                sendJsonResponse(exchange, 200, response);
                
            } catch (Exception e) {
                System.err.println("上传处理失败: " + e.getMessage());
                e.printStackTrace();
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", e.getMessage());
                sendJsonResponse(exchange, 500, response);
            }
        }
        
        private String extractFormValue(String body, String name) {
            // 提取multipart/form-data中的字段值
            String marker = "name=\"" + name + "\"";
            int start = body.indexOf(marker);
            if (start < 0) return null;
            
            // 跳过header,找到\r\n\r\n或\n\n
            start = body.indexOf("\r\n\r\n", start);
            if (start < 0) {
                start = body.indexOf("\n\n", start);
                if (start < 0) return null;
                start += 2;
            } else {
                start += 4;
            }
            
            // 找到下一个boundary
            int end = body.indexOf("\r\n--", start);
            if (end < 0) {
                end = body.indexOf("\n--", start);
                if (end < 0) end = body.length();
            }
            
            String value = body.substring(start, end);
            // 移除可能的尾部空白和\r\n
            value = value.replaceAll("[\r\n]+$", "");
            return value;
        }
        
        private String compileGroup(String groupId) throws Exception {
            File groupDir = new File("../../group/group/" + groupId);
            File bfDir = new File(".").getAbsoluteFile();
            
            // 构建编译命令 - 使用绝对路径
            ProcessBuilder pb = new ProcessBuilder(
                "javac", "-encoding", "UTF-8", 
                "-cp", bfDir.getAbsolutePath(),
                "HoneyBee.java", "Hornet.java"
            );
            pb.directory(groupDir);
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            // 读取编译输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                throw new Exception("编译失败 (退出码: " + exitCode + "): " + output.toString());
            }
            
            // 验证class文件是否生成
            File honeyBeeClass = new File(groupDir, "HoneyBee.class");
            File hornetClass = new File(groupDir, "Hornet.class");
            
            if (!honeyBeeClass.exists() || !hornetClass.exists()) {
                throw new Exception("编译完成但class文件未生成");
            }
            
            return "编译成功! 生成了 HoneyBee.class 和 Hornet.class";
        }
    }
    
    /**
     * 启动回放
     */
    class ReplayHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            
            String body = new String(exchange.getRequestBody().readAllBytes());
            Map<String, Object> request = SimpleJSON.parseJSON(body);
            String filename = (String) request.get("filename");
            
            // 在新线程中启动回放
            new Thread(() -> {
                try {
                    ProcessBuilder pb = new ProcessBuilder(
                        "java", "-cp", ".:bin", "BeeFarmingReplay", 
                        "BattleRecords/" + filename
                    );
                    pb.directory(new File("."));
                    pb.inheritIO();
                    Process process = pb.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "回放已启动");
            sendJsonResponse(exchange, 200, response);
        }
    }
    
    /**
     * 静态文件服务器
     */
    class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if ("/".equals(path)) {
                path = "/index.html";
            }
            
            File file = new File(WEB_ROOT + path);
            if (!file.exists() || !file.isFile()) {
                sendResponse(exchange, 404, "File not found");
                return;
            }
            
            String contentType = getContentType(path);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            
            byte[] data = Files.readAllBytes(file.toPath());
            exchange.sendResponseHeaders(200, data.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(data);
            }
        }
        
        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html; charset=utf-8";
            if (path.endsWith(".css")) return "text/css; charset=utf-8";
            if (path.endsWith(".js")) return "application/javascript; charset=utf-8";
            if (path.endsWith(".json")) return "application/json; charset=utf-8";
            if (path.endsWith(".png")) return "image/png";
            if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
            return "text/plain; charset=utf-8";
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
    
    private void sendJsonResponse(HttpExchange exchange, int code, Object data) throws IOException {
        String json = SimpleJSON.toJSON(data);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] bytes = json.getBytes("UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
    
    // ==================== 主函数 ====================
    
    public static void main(String[] args) {
        try {
            WebServer server = new WebServer();
            server.start();
            
            // 保持运行
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
