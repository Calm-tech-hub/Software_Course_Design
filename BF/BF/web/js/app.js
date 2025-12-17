// ==================== 全局变量 ====================
// 自动检测：如果是从外部访问，使用当前主机地址；如果是localhost访问，使用localhost
const API_BASE = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1' 
    ? 'http://localhost:8080/api' 
    : `http://${window.location.hostname}:8080/api`;
let currentTab = 'battle';
let battleChart = null;

console.log('🚀 app.js 已加载，API_BASE:', API_BASE);

// ==================== 页面加载 ====================
document.addEventListener('DOMContentLoaded', () => {
    console.log('📄 DOM 加载完成，开始初始化');
    initTabs();
    initBattleForm();
    initUploadForm();
    loadGroups();
    loadRecords();
    loadRankings();
    loadStatistics();
    console.log('✅ 所有初始化函数已调用');
    
    // 设置定时刷新
    setInterval(loadRecords, 10000); // 每10秒刷新记录
});

// ==================== 标签页切换 ====================
function initTabs() {
    const tabBtns = document.querySelectorAll('.tab-btn');
    tabBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const tabName = btn.dataset.tab;
            switchTab(tabName);
        });
    });
}

function switchTab(tabName) {
    // 更新按钮状态
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.tab === tabName);
    });
    
    // 更新内容显示
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.toggle('active', content.id === `${tabName}-tab`);
    });
    
    currentTab = tabName;
    
    // 切换到对应tab时加载数据
    if (tabName === 'rankings') {
        loadRankings();
    } else if (tabName === 'statistics') {
        loadStatistics();
    }
}

// ==================== 加载组列表 ====================
async function loadGroups() {
    try {
        const response = await fetch(`${API_BASE}/groups`);
        const groups = await response.json();
        
        const groupA = document.getElementById('groupA');
        const groupB = document.getElementById('groupB');
        
        groupA.innerHTML = '<option value="">请选择...</option>';
        groupB.innerHTML = '<option value="">请选择...</option>';
        
        groups.forEach(group => {
            groupA.innerHTML += `<option value="${group.id}">${group.name}</option>`;
            groupB.innerHTML += `<option value="${group.id}">${group.name}</option>`;
        });
    } catch (error) {
        console.error('加载组列表失败:', error);
    }
}

// ==================== 对战表单 ====================
function initBattleForm() {
    const form = document.getElementById('battleForm');
    const roundsInput = document.getElementById('rounds');
    const totalRoundsSpan = document.getElementById('totalRounds');
    
    // 更新总轮数显示
    roundsInput.addEventListener('input', () => {
        totalRoundsSpan.textContent = roundsInput.value * 2;
    });
    
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        await startBattle();
    });
    
    // 刷新按钮
    document.getElementById('refreshRecords').addEventListener('click', loadRecords);
}

async function startBattle() {
    const groupA = document.getElementById('groupA').value;
    const groupB = document.getElementById('groupB').value;
    const rounds = parseInt(document.getElementById('rounds').value);
    
    if (!groupA || !groupB) {
        showStatus('battleStatus', 'error', '请选择对战组！');
        return;
    }
    
    if (groupA === groupB) {
        showStatus('battleStatus', 'error', '请选择不同的组进行对战！');
        return;
    }
    
    showStatus('battleStatus', 'info', `正在启动对战: ${groupA} vs ${groupB} (${rounds}轮)...`);
    
    try {
        const response = await fetch(`${API_BASE}/battle`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ groupA, groupB, rounds })
        });
        
        const result = await response.json();
        
        if (result.success) {
            showStatus('battleStatus', 'success', '对战已启动！请等待完成...');
            // 轮询检查对战结果
            if (result.battleId) {
                pollBattleResult(result.battleId, groupA, groupB);
            } else {
                setTimeout(loadRecords, 3000);
            }
        } else {
            showStatus('battleStatus', 'error', '启动失败: ' + result.message);
        }
    } catch (error) {
        showStatus('battleStatus', 'error', '启动失败: ' + error.message);
    }
}

// 轮询对战结果
async function pollBattleResult(battleId, groupA, groupB) {
    const maxAttempts = 60; // 最多轮询60次 (60秒)
    let attempts = 0;
    
    const checkResult = async () => {
        attempts++;
        
        try {
            const response = await fetch(`${API_BASE}/battle/result?battleId=${battleId}`);
            const result = await response.json();
            
            if (result.completed) {
                // 显示结果对话框
                showBattleResult(result);
                // 刷新记录列表
                loadRecords();
            } else if (attempts < maxAttempts) {
                // 继续轮询
                setTimeout(checkResult, 1000);
            } else {
                showStatus('battleStatus', 'warning', '对战超时，请刷新查看记录');
                loadRecords();
            }
        } catch (error) {
            console.error('检查对战结果失败:', error);
            if (attempts < maxAttempts) {
                setTimeout(checkResult, 1000);
            }
        }
    };
    
    checkResult();
}

// 显示对战结果
function showBattleResult(result) {
    const modal = document.createElement('div');
    modal.className = 'battle-result-modal';
    modal.innerHTML = `
        <div class="battle-result-content">
            <div class="battle-result-header">
                <h2>🏆 对战结果</h2>
                <button class="close-btn" onclick="this.closest('.battle-result-modal').remove()">×</button>
            </div>
            <div class="battle-result-body">
                <div class="winner-section">
                    <div class="winner-label">获胜者</div>
                    <div class="winner-name">${result.winner}</div>
                    <div class="winner-score">分差: ${result.scoreDiff.toFixed(2)}</div>
                </div>
                
                <div class="battle-stats">
                    <div class="stats-column">
                        <h3>${result.groupA} 组表现</h3>
                        <div class="stat-item">
                            <span class="stat-label">平均分数:</span>
                            <span class="stat-value">${result.groupA_avgScore.toFixed(2)}</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-label">平均蜂蜜:</span>
                            <span class="stat-value">${result.groupA_honey.toFixed(2)}</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-label">平均存活:</span>
                            <span class="stat-value">${result.groupA_alive.toFixed(2)}</span>
                        </div>
                    </div>
                    
                    <div class="vs-divider">VS</div>
                    
                    <div class="stats-column">
                        <h3>${result.groupB} 组表现</h3>
                        <div class="stat-item">
                            <span class="stat-label">平均分数:</span>
                            <span class="stat-value">${result.groupB_avgScore.toFixed(2)}</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-label">平均蜂蜜:</span>
                            <span class="stat-value">${result.groupB_honey.toFixed(2)}</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-label">平均存活:</span>
                            <span class="stat-value">${result.groupB_alive.toFixed(2)}</span>
                        </div>
                    </div>
                </div>
                
                <div class="battle-time">
                    总用时: ${result.totalTime.toFixed(2)} 秒
                </div>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // 更新状态
    showStatus('battleStatus', 'success', `对战完成！${result.winner} 获胜！`);
}

// ==================== 加载对战记录 ====================
async function loadRecords() {
    console.log('📥 loadRecords 被调用');
    try {
        const response = await fetch(`${API_BASE}/records`);
        console.log('📊 API响应状态:', response.status);
        const records = await response.json();
        console.log(`✅ 获取到 ${records.length} 条记录`, records[0]);
        
        // 更新最近记录
        updateRecentRecords(records.slice(0, 5));
        
        // 更新表格
        updateRecordsTable(records);
    } catch (error) {
        console.error('❌ 加载记录失败:', error);
    }
}

function updateRecentRecords(records) {
    const container = document.getElementById('recentRecords');
    
    if (records.length === 0) {
        container.innerHTML = '<div class="loading">暂无记录</div>';
        return;
    }
    
    container.innerHTML = records.map(record => `
        <div class="record-item">
            <div class="record-item-header">
                <span class="record-item-title">${record.group1} vs ${record.group2}</span>
                <span class="record-item-score">${record.score}</span>
            </div>
            <div class="record-item-details">
                第${record.round}轮 | 花蜜: ${record.honey} | 存活: ${record.alive}
            </div>
        </div>
    `).join('');
}

function updateRecordsTable(records) {
    const tbody = document.getElementById('recordsBody');
    
    if (records.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="loading">暂无记录</td></tr>';
        return;
    }
    
    tbody.innerHTML = records.map(record => `
        <tr>
            <td>${record.group1}</td>
            <td>${record.group2}</td>
            <td>${record.round}</td>
            <td><strong>${record.score}</strong></td>
            <td>${record.honey}</td>
            <td>${record.alive}</td>
            <td>${formatTime(record.timestamp)}</td>
            <td>
                <button class="btn btn-success" onclick="replayBattle('${record.filename}')">
                    🎬 回放
                </button>
            </td>
        </tr>
    `).join('');
}

function formatTime(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleString('zh-CN');
}

async function replayBattle(filename) {
    try {
        const response = await fetch(`${API_BASE}/replay`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ filename })
        });
        
        const result = await response.json();
        
        if (result.success) {
            alert('回放窗口已启动！请查看弹出的Java窗口。');
        } else {
            alert('启动回放失败: ' + result.message);
        }
    } catch (error) {
        alert('启动回放失败: ' + error.message);
    }
}

// ==================== 排名榜 ====================
async function loadRankings() {
    try {
        const response = await fetch(`${API_BASE}/rankings`);
        const rankings = await response.json();
        
        const container = document.getElementById('rankingsGrid');
        
        if (rankings.length === 0) {
            container.innerHTML = '<div class="loading">暂无排名数据</div>';
            return;
        }
        
        container.innerHTML = rankings.map((rank, index) => {
            const rankClass = index < 3 ? `rank-${index + 1}` : '';
            const medal = index === 0 ? '🥇' : index === 1 ? '🥈' : index === 2 ? '🥉' : index + 1;
            
            return `
                <div class="rank-card ${rankClass}">
                    <div class="rank-badge">${medal}</div>
                    <div class="rank-group">组 ${rank.group}</div>
                    <div class="rank-stats">
                        <div class="rank-stat">
                            <span class="rank-stat-label">平均得分</span>
                            <span class="rank-stat-value">${rank.avgScore.toFixed(2)}</span>
                        </div>
                        <div class="rank-stat">
                            <span class="rank-stat-label">总得分</span>
                            <span class="rank-stat-value">${rank.totalScore}</span>
                        </div>
                        <div class="rank-stat">
                            <span class="rank-stat-label">对局数</span>
                            <span class="rank-stat-value">${rank.battles}</span>
                        </div>
                        <div class="rank-stat">
                            <span class="rank-stat-label">最高分</span>
                            <span class="rank-stat-value">${rank.maxScore}</span>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    } catch (error) {
        console.error('加载排名失败:', error);
    }
}

// ==================== 统计分析 ====================
async function loadStatistics() {
    try {
        const response = await fetch(`${API_BASE}/statistics`);
        const stats = await response.json();
        
        // 更新统计卡片
        document.getElementById('totalBattles').textContent = stats.totalBattles || 0;
        document.getElementById('totalGroups').textContent = stats.totalGroups || 0;
        
        // 计算平均分
        if (stats.recentBattles && stats.recentBattles.length > 0) {
            const avgScore = stats.recentBattles.reduce((sum, b) => sum + b.score, 0) / stats.recentBattles.length;
            document.getElementById('avgScore').textContent = avgScore.toFixed(0);
        } else {
            document.getElementById('avgScore').textContent = '0';
        }
        
        // 绘制图表
        drawScoreChart(stats.recentBattles || []);
    } catch (error) {
        console.error('加载统计失败:', error);
    }
}

function drawScoreChart(battles) {
    const ctx = document.getElementById('scoreChart');
    
    if (!ctx) return;
    
    // 销毁旧图表
    if (battleChart) {
        battleChart.destroy();
    }
    
    // 准备数据
    const labels = battles.map((b, i) => `第${i + 1}局`);
    const data = battles.map(b => b.score);
    const groups = battles.map(b => b.group);
    
    // 创建图表
    battleChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: '得分',
                data: data,
                borderColor: '#4a90e2',
                backgroundColor: 'rgba(74, 144, 226, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: true
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const index = context.dataIndex;
                            return `${groups[index]}: ${context.parsed.y} 分`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

// ==================== 算法上传 ====================
function initUploadForm() {
    const form = document.getElementById('uploadForm');
    const honeyBeeInput = document.getElementById('honeyBeeFile');
    const hornetInput = document.getElementById('hornetFile');
    
    // 文件选择显示
    honeyBeeInput.addEventListener('change', (e) => {
        const fileName = e.target.files[0]?.name || '未选择文件';
        e.target.parentElement.querySelector('.file-name').textContent = fileName;
    });
    
    hornetInput.addEventListener('change', (e) => {
        const fileName = e.target.files[0]?.name || '未选择文件';
        e.target.parentElement.querySelector('.file-name').textContent = fileName;
    });
    
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        await uploadAlgorithm();
    });
}

async function uploadAlgorithm() {
    const groupId = document.getElementById('groupId').value.trim();
    const honeyBeeFile = document.getElementById('honeyBeeFile').files[0];
    const hornetFile = document.getElementById('hornetFile').files[0];
    
    if (!groupId || !/^\d{3}$/.test(groupId)) {
        showStatus('uploadStatus', 'error', '组别ID必须是3位数字！');
        return;
    }
    
    if (!honeyBeeFile || !hornetFile) {
        showStatus('uploadStatus', 'error', '请选择HoneyBee.java和Hornet.java两个文件！');
        return;
    }
    
    showStatus('uploadStatus', 'info', '正在上传并编译...');
    
    try {
        // 读取文件内容
        const honeyBeeCode = await readFileAsText(honeyBeeFile);
        const hornetCode = await readFileAsText(hornetFile);
        
        // 构造表单数据
        const formData = new FormData();
        formData.append('groupId', groupId);
        formData.append('honeyBeeFile', honeyBeeCode);
        formData.append('hornetFile', hornetCode);
        
        const response = await fetch(`${API_BASE}/upload`, {
            method: 'POST',
            body: formData
        });
        
        const result = await response.json();
        
        if (result.success) {
            showStatus('uploadStatus', 'success', '上传成功！已自动编译，可以在对战中使用了。');
            document.getElementById('uploadForm').reset();
            document.querySelectorAll('.file-name').forEach(el => el.textContent = '未选择文件');
            // 重新加载组列表
            setTimeout(loadGroups, 1000);
        } else {
            showStatus('uploadStatus', 'error', '上传失败: ' + result.error);
        }
    } catch (error) {
        showStatus('uploadStatus', 'error', '上传失败: ' + error.message);
    }
}

function readFileAsText(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = (e) => resolve(e.target.result);
        reader.onerror = (e) => reject(e);
        reader.readAsText(file);
    });
}

// ==================== 工具函数 ====================
function showStatus(elementId, type, message) {
    const element = document.getElementById(elementId);
    element.className = `status-message ${type}`;
    element.textContent = message;
    element.style.display = 'block';
    
    // 3秒后自动隐藏成功消息
    if (type === 'success') {
        setTimeout(() => {
            element.style.display = 'none';
        }, 3000);
    }
}
