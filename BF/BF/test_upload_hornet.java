public class Hornet {
    public static int move(BeeState thisBee, BeeState[] enemyBees, FlowerState[] flowers) {
        // 测试算法 - 攻击最近的蜜蜂
        int nearestBee = -1;
        int minDistance = Integer.MAX_VALUE;
        
        for (int i = 0; i < enemyBees.length; i++) {
            if (enemyBees[i] != null && enemyBees[i].isAlive) {
                int distance = Math.abs(thisBee.x - enemyBees[i].x) + Math.abs(thisBee.y - enemyBees[i].y);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestBee = i;
                }
            }
        }
        
        return nearestBee;
    }
}
