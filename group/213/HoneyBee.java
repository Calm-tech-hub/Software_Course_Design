import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class HoneyBee extends Bee{
	private int id;
	private int idBee;
	private int count = 4;
	private Image image;
	private double angleToOther;
	private double angleOther;
	private double angleFlower;
	private double angleSafeFlower;
	private boolean isDanger = false;
	private boolean turn = true;
	private boolean flag = false;
	private boolean safe = false;
	private boolean stop = false;
	static int[] flee = new int[3];
	private boolean check = false;
	private boolean checkEnable = false;
	private boolean find = false;
	private ArrayList<Integer> xFlowers = new ArrayList<Integer>();
	private ArrayList<Integer> yFlowers = new ArrayList<Integer>();
	
	public HoneyBee(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;                              
		image = img;
	}
	
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search() {
		flag = false; 
		String strHunter = BeeFarming.search(9);
		if(strHunter.indexOf('+') == -1) {
			for(int i = 0; i < 3; i++) {
				if(flee[i] == 1) {
					flee[i] = 0;
					System.out.println("修正");
				}
			}
		} 
		else {
			int begin = strHunter.indexOf('+');
			String temp = strHunter.substring(begin);
			begin = temp.indexOf('(');
			int finish = temp.indexOf(',');
			temp = temp.substring(begin + 1, finish);
			idBee = new Integer(temp).intValue();
			flee[idBee] = 1;
		}
		if(flee[id] == 1) {
			isDanger = true;
		}
			
		if(isDanger) {
			if(flee[id] == 0) {
				isDanger = false;
			}
		}
		if(checkEnable) {
			check = true;
			checkEnable = false;
		}
		if(stop) {
			find = true;
			stop = false;
		}
		if(safe) {
			if(Math.abs(angle - angleFlower) > 1.0 ) {
				safe = false;
			}
		}		
		int[][] space = new int[8][6];
		int xPoint = getX() + image.getWidth(null) / 2;
		int yPoint = getY() + image.getHeight(null) / 2;
		for(int w = 0; w < 8; w++) {
			if((xPoint > (w * 100)) && (xPoint < ((w+1) * 100))) {
				for(int h = 0; h < 6; h++) {
					if((yPoint > (h * 100)) && (yPoint < ((w+1) * 100))) {
						space[w][h] = 1;
					}
				}
			}
		}
		String strVision = BeeFarming.search(id);
		String[] strs = strVision.split("~");
		if(strVision == "") {
			if(isDanger) {
				if(turn) {
					if(count == 4) {
						angle += 90;
						if(angle >= 360) {
							angle -= 360;
						}
						ratoteImage(angle);
					}
					count--;
					if(count == 0) {
						turn = false;
					}
				}
				else {
					if(count == 0) {
						angle -= 90;
						if(angle < 0) {
							angle += 360;
						}
						ratoteImage(angle);
					}
					count++;
					if(count == 4) {
						turn = true;
					}
				}
			}
		}		
		for(int i=0;i<strs.length;i++){
			//如果碰到*为首的字符串，代表遇到了边，这里是随机顺时针旋转90度以内的角度
			
			if(strs[i].indexOf('*') == 0)
			{		
				Random ra = new Random();
				angle += 90 + ra.nextInt(45);
				if(angle >= 360) {
					angle -= 360;
				}
				ratoteImage(angle);
				if(isDanger) {
					checkEnable = true;
				}
				/*if(!isDanger) {
					Random r = new Random();
					int x = r.nextInt(8);
					int y = r.nextInt(6);
					while(space[x][y] == 1) {
						x = r.nextInt(8);
						y = r.nextInt(6);
					}
					angle = BeeFarming.getVectorDegree(xPoint, yPoint, (50 + x * 100), (50 + y * 100));
					ratoteImage(angle);
					System.out.println("飞向:" + x + "," + y + angle);
				}*/
					
			}
				//如果碰到-为首的字符串，代表遇到了花，这里是向其中一朵花飞（即使同时看到多个花）
			if(strs[i].indexOf('-') == 0)
			{
				String strTmp = strs[i];
				int start = strTmp.indexOf('(');
				int end = strTmp.indexOf(',');
				String s = strTmp.substring(start+1,end);
				int honey = new Integer(s).intValue();
				strTmp = strTmp.substring(end+1);
				end = strTmp.indexOf(')');
				s = strTmp.substring(0,end);
				if(!s.equals("ON")){
					double a = new Double(s).doubleValue();
					int sum = 0;
					for(int k = 0; k < 3; k++) {
						sum += flee[k];
					}
					if(isDanger) {
						angle = a + 90;
						if(angle >= 360) {
							angle -= 360;
						}
						ratoteImage(angle);
						System.out.println("正被追击需要避开花");
					}
					else if(sum == 0 && !safe) {
						angleFlower = a;
						angle += 180;
						if(angle >= 360) {
							angle -= 360;
						}
						ratoteImage(angle);
						stop = true;
						System.out.println("伸头也是一刀，缩头也是一刀");
					}
					else {
						angle = a;
						ratoteImage(angle);
					}	
					//System.out.println("旋转："+angle);
				}//else
						//System.out.println("ON flower");
			}
			if(strs[i].indexOf('+') == 0) {
				String strTmp = strs[i];
				int start = strTmp.indexOf('(');
				int end = strTmp.indexOf(',');
				String s =strTmp.substring(start + 1 , end);
				idBee = new Integer(s).intValue();
				strTmp = strTmp.substring(end + 1);
				end = strTmp.indexOf(',');
				s = strTmp.substring(0, end);
				angleToOther = new Double(s).doubleValue();
				strTmp = strTmp.substring(end + 1);
				end = strTmp.indexOf(')');
				s = strTmp.substring(0, end);
				angleOther = new Double(s).doubleValue();
				if(idBee == 9) {
					flag = true;
					double left = angleToOther - 90;
					double right = angleToOther + 90;
					int sum = 0;
					for(int k = 0; k < 3; k++) {
						sum += flee[k];
					}		
					if(left < 0) {
						left += 360;
						if(angleOther > right && angleOther < left) {
							if(sum == 0) {
								isDanger = true;
								flee[id] = 1;
							}
							angle = angleToOther + 135;
							ratoteImage(angle);
							System.out.println("遇敌转向逃逸");	
						}
						else {
							angle = left;
							ratoteImage(angle);
							System.out.println("没发现");
						}
					}					
					else if(right >= 360) {
							right -= 360;
							if(angleOther > right && angleOther < left) {
								if(sum == 0) {
									isDanger = true;
									flee[id] = 1;
								}
								angle = angleToOther - 135;
								ratoteImage(angle);
								System.out.println("遇敌转向逃逸");
							}
							else {
									angle = right;
									ratoteImage(angle);
									System.out.println("没发现");
							}
					}
					else {
						if(angleOther > right || angleOther < left) {
							if(sum == 0) {
								isDanger = true;
								flee[id] = 1;
							}
							angle = angleToOther + 135;
							if(angle >= 360) {
								angle -= 360;
							}
							ratoteImage(angle);
							System.out.println("遇敌转向逃逸");
						}
						else {
							angle = angleToOther + 90;
							if(angle >= 360) {
								angle -= 360;
							}
							ratoteImage(angle);
							System.out.println("没发现");
						}
					}

				}	
				else {
					if(flee[id] == 1) {
						angle = angleToOther + 135;
						if( angle >= 360) {
							angle -= 360;
						}
						ratoteImage(angle);
					}
				}
			}
			
		}	
		if(!flag && check) {
			isDanger = false;
			flee[id] = 0;
			check = false;
		}
		if(!flag && find) {
			angle = angleFlower;
			ratoteImage(angle);
			find = false;
			safe = true;
			System.out.println("后方安全");
		}
		if(stop) {
			setXYs(9);
			System.out.println("暂留");
			System.out.println(""+flee[0]+flee[1]+flee[2]);
		}
		else {
			setXYs(0);
		}
		//String strVision = BeeFarming.search(id);
		System.out.println(strVision);		
		//setXYs(0);
	}
}