public class HoneyBee {
    public static int move(BeeState thisBee, FlowerState[] flowers, BeeState[] otherBees) {
        // 测试算法 - 简单移动到最近的花
        int nearestFlower = -1;
        int minDistance = Integer.MAX_VALUE;
        
        for (int i = 0; i < flowers.length; i++) {
            if (flowers[i] != null && flowers[i].visible) {
                int distance = Math.abs(thisBee.x - flowers[i].x) + Math.abs(thisBee.y - flowers[i].y);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestFlower = i;
                }
            }
        }
        
        return nearestFlower;
    }
}