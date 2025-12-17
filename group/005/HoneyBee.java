//angle的具体含义：右是0，下是90，左是180，上是270。
//利用ratoteImage(angle)方法进行方向的调整。
//result变量是关键，其包含了视野内物体的信息。可以通过判断result第一个字符的值来判断视野内究竟有什么。
//利用result截取角度的时候需要注意位数的选择，要考虑所有的情况，故先找到.的位置，前面截取整数，后面截取一位小数
//要考虑视野里有多朵花的情况

import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class HoneyBee extends Bee{
	
	private int id;
	private int dangerBeeId = -1;
	private boolean danger = false;
	private int num = 0;
	
	int turnCount = 0;
	
	public HoneyBee(int id ,int x, int y, double angle, boolean isAlive, Image img) {
		super(id, x, y, angle, isAlive, img);
		this.id = id;
	}


	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){ 
		
		//蜜蜂获取黄蜂的视野
		String hornetVision = BeeFarming.search(9); 
		//蜜蜂自己的视野
		String honeyBeeVision = BeeFarming.search(this.id); 
		
		//若黄蜂的视野中出现了蜜蜂，让对应id的蜜蜂做出逃脱反应
		if(hornetVision.indexOf("+(") != -1) {

			String dangerBeeStr = hornetVision.substring(hornetVision.lastIndexOf("+(")); 
			dangerBeeId = Integer.parseInt(dangerBeeStr.substring(2, 3));
			
			if(this.id == dangerBeeId) {
				this.danger = true;
			}
			else {
				this.danger = false;
				turnCount = 0;
			}
			
			if(danger == true) {		
				if(turnCount == 0){
					angle = (angle + 100) % 360;
					turnCount = 1;
				}
				if(honeyBeeVision.indexOf('-') == 0) {
					angle = (angle + 120) % 360;
				}
			}
			
		} 
		else {
			this.danger = false;
		}
		
		//若蜜蜂的视野里出现了黄蜂立即掉头
		if(honeyBeeVision.indexOf("+(9") != -1 && danger == false) {
			angle = (angle + 180) % 360;	
		}
		
		//若蜜蜂不危险且视野里有花
		if(danger == false && honeyBeeVision.indexOf('-') != -1 && honeyBeeVision.contains("ON") == false) {
			
			String getFlowerStr = honeyBeeVision.substring(honeyBeeVision.indexOf("-("));
			int pointLocation = getFlowerStr.indexOf('.');
			String flowerStr = getFlowerStr.substring(5, pointLocation + 2);
			angle = Double.parseDouble(flowerStr) % 360;
				
		}
		
		//碰边情况的判断（第二个条件表示可采微出界的花）
        if(honeyBeeVision.indexOf('*') == 0 && honeyBeeVision.indexOf('-') == -1) {

        	Random ra = new Random();
        	
        	if(honeyBeeVision.indexOf('N') != -1) {	
        		if(angle <= 360 && angle > 270) angle = (angle + 70 + ra.nextInt(40)) % 360;
        		else angle = (angle - 70 - ra.nextInt(40)) % 360;
        	}
        	if(honeyBeeVision.indexOf('S') != -1) {	
        		if(angle < 90 && angle >= 0) angle = (angle + 250 + ra.nextInt(20)) % 360;
        		else angle = (angle + 70 + ra.nextInt(40)) % 360;
        	}
        	if(honeyBeeVision.indexOf('W') != -1) {	
        		if(angle < 180 && angle > 90) angle = (angle - 70 - ra.nextInt(20)) % 360;
        		else angle = (angle + 70 + ra.nextInt(40)) % 360;
        	}
        	if(honeyBeeVision.indexOf('E') != -1) {	
        		if(angle < 90 && angle >= 0) angle = (angle + 70 + ra.nextInt(40)) % 360;
        		else angle = (angle - 70 - ra.nextInt(20)) % 360;
        	}
        		
        	
        }
		
        ratoteImage(angle); 
		setXYs(0);

	}
}	
		
		