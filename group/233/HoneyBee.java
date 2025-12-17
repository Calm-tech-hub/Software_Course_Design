import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class HoneyBee extends Bee{
	private int id;
	private boolean isDanger=false;
	private int num=0;
	private int count=0;
	public HoneyBee(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
	}
	
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
public void search(){
	
		String strVision = BeeFarming.search(id);
		System.out.println(strVision);
		
		
		if (this.isDanger == true) {
			System.out.println("啦啦啦"+this.isDanger + "啦啦啦" + this.num);
			if (strVision.startsWith("*")){
				if((strVision.indexOf('S')==1) || (strVision.indexOf('N')==1) || (strVision.indexOf('W')==1) || (strVision.indexOf('E')==1)){
					this.num++;
				}
			}
			
			if ( this.num >= 1 ) {
				if (strVision.indexOf('S')!=-1) angle = 0.0;
				if (strVision.indexOf('N')!=-1) angle = 180.0;
				if (strVision.indexOf('W')!=-1)angle = 90.0;
				if (strVision.indexOf('E')!=-1) angle = 270.0;
				if ((strVision.indexOf('S')!=-1) && (strVision.indexOf('W')!=-1)) angle=315.0;
				if ((strVision.indexOf('S')!=-1) && (strVision.indexOf('E')!=-1)) angle=225.0;
				if ((strVision.indexOf('N')!=-1) && (strVision.indexOf('W')!=-1)) angle=45.0;
				if ((strVision.indexOf('N')!=-1) && (strVision.indexOf('E')!=-1)) angle=135.0;
				if (!strVision.startsWith("*")) {this.isDanger = false;this.num = 0;}
				
				ratoteImage(angle);
				
			}
		}
		else if (strVision.startsWith("*")) {
				String subString1 = strVision.substring(strVision.indexOf('~')+1);
				//只有墙壁
				if (subString1.equals("")) {
					Random ra = new Random();
					double deltAngle = ra.nextInt(30);
					if((strVision.indexOf('S')==1) || (strVision.indexOf('N')==1)){
						angle = (360-angle+deltAngle)%360;
						//angle = adjustAngle(angle);
						ratoteImage(angle);
					}
					else {
						angle = (((540-angle)%360)+deltAngle)%360;
						//angle = adjustAngle(angle);
						ratoteImage(angle);
					}
				}
				
				//有墙壁又有花
				else if (subString1.startsWith("-")) {
					String subString2 = subString1.substring(subString1.indexOf('~')+1);
					//有花又没有黄蜂就先采花
					if (subString2.equals("")) {
						String flowerAngle = subString1.substring(subString1.indexOf(',')+1,subString1.indexOf(')'));
						if (!flowerAngle.equals("ON")) {
							angle = Double.parseDouble(flowerAngle);
							ratoteImage(angle);
						}
					}
					else if (subString2.startsWith("+")){
						int honeyBeeId = Integer.parseInt(subString2.substring(subString2.indexOf('(')+1,subString2.indexOf('(')+2));
						//看见黄蜂就先逃命
						if (honeyBeeId == 9){
							System.out.println("有危险啦！");
							this.isDanger = true;
							String hornetAngle = subString2.substring(subString2.lastIndexOf(',')+1,subString2.indexOf(')'));
							angle = Double.parseDouble(hornetAngle);
							angle = angle+90;//采取垂直于黄蜂的方向逃走
							System.out.println("Hornet: "+angle);
							ratoteImage(angle);
						}
						else {
					
						}
					}
					else {
					
					}

				}
				else if (subString1.startsWith("+")) {
					int honeyBeeId = Integer.parseInt(subString1.substring(subString1.indexOf('(')+1,subString1.indexOf('(')+2));
					//看见黄蜂就先逃命
					if (honeyBeeId == 9){
					//	System.out.println("有危险啦！");
						this.isDanger = true;
						String hornetAngle = subString1.substring(subString1.lastIndexOf(',')+1,subString1.indexOf(')'));
						angle = Double.parseDouble(hornetAngle);
						angle = angle+90;//采取垂直于黄蜂的方向逃走
						System.out.println("Hornet: "+angle);
						ratoteImage(angle);
					}
					else {
					//	System.out.println("是小伙伴啦！");
						String honeyBeeAngle = subString1.substring(subString1.lastIndexOf(',')+1,subString1.indexOf(')'));
						angle = Double.parseDouble(honeyBeeAngle) + 120;
						ratoteImage(angle);
					}
				}
			}
		else if (strVision.startsWith("-(")) {
			String subString1 = strVision.substring(strVision.indexOf('~')+1);
			//只有花就采花
			if (subString1.equals("")) {
				String flowerAngle = strVision.substring(strVision.indexOf(',')+1,strVision.indexOf(')'));
				if (!flowerAngle.equals("ON")) {
					angle = Double.parseDouble(flowerAngle);
					ratoteImage(angle);
				}
			}
			else if (subString1.startsWith("+")) {
				int honeyBeeId = Integer.parseInt(subString1.substring(subString1.indexOf('(')+1,subString1.indexOf('(')+2));
				//看见黄蜂就先逃命
				if (honeyBeeId == 9){
					System.out.println("有危险啦！");
					this.isDanger = true;
					String hornetAngle = subString1.substring(subString1.lastIndexOf(',')+1,subString1.indexOf(')'));
					angle = Double.parseDouble(hornetAngle);
					angle = angle+90;//采取垂直于黄蜂的方向逃走
					System.out.println("Hornet: "+angle);
					ratoteImage(angle);
				}
				else {
				
				}
			}
			
		}
		else if (strVision.startsWith("+(")) {
			while (strVision.startsWith("+(")) {
				int beeId = Integer.parseInt(strVision.substring(strVision.indexOf('(')+1,strVision.indexOf('(')+2));
				String subString1 = strVision.substring(strVision.indexOf('+'), strVision.indexOf('~'));
				if (beeId == 9) {
					this.isDanger = true;
					String hornetAngle = subString1.substring(subString1.lastIndexOf(',')+1,subString1.indexOf(')'));
					angle = Double.parseDouble(hornetAngle);
					angle = angle+90;//采取垂直于黄蜂的方向逃走
					ratoteImage(angle);
					break;
				}
				else {
					String honeyBeeAngle = subString1.substring(subString1.lastIndexOf(',')+1,subString1.indexOf(')'));
					angle = Double.parseDouble(honeyBeeAngle);
					angle = angle+120;//采取120°角策略分开寻找花
					ratoteImage(angle);
				}
				strVision = strVision.substring(strVision.indexOf('~')+1);
				
			}
		}
		else{
			this.count++;
			if (count%2==0) {angle+=30;}
			else {angle=(angle+360-30)%360;}
			ratoteImage(angle);
		}
		
		setXYs(0);
	}
	
	/**调整角度：避免垂直反弹**/
	public double adjustAngle(double angle){
		if(angle<10 && angle>=0){
			angle = angle + 30;
		}
		else if(angle<95 && angle>85){
			angle = angle + 40;
		}
		else if(angle<185 && angle>175){
			angle = angle - 40;
		}
		else if(angle<275 && angle>265){
			angle = angle - 40;
		}
		
		return angle;
	}
	
	public boolean getIsDanger()
	{	
		return this.isDanger;
	}
}