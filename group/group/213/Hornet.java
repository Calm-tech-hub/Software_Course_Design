import java.awt.*;
import java.awt.image.*;
import java.util.*;
public class Hornet extends Bee{
	private int id;
	private boolean dead=false;
	private boolean seen=false;
	private boolean locked = false;
	private ArrayList<Integer> xFlowers = new ArrayList<Integer>();
	private ArrayList<Integer> yFlowers = new ArrayList<Integer>();
	private Image image;
	private int count = 3;
	private int idPrey;
	private int idOld;
	private int currentPrey;
	private double anglePrey;
	private double angleToPrey;
	private boolean stop = false;
	private boolean flowerExist;
	private boolean miss = false;
	private boolean flag = false;
	public Hornet(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
		image=img;
	}
	
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){	
		//System.out.println(""+(getX() +	image.getWidth(null) / 2) + "," + (getY() + image.getHeight(null) / 2));
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
		oldAngle = angle;
		String strVision = BeeFarming.search(id);
		String[] strs = strVision.split("~");
		/*if(strVision.indexOf('+') == -1) {
			angle += 180;
			if(angle >= 360) {
				angle -= 360;
			}
			setXYs(9);
			flying(0);
			String strVisionBehind = BeeFarming.search(id);
			String[] strsBehind = strVisionBehind.split("~");
			if(strVisionBehind.indexOf('+') == -1) {
				angle = oldAngle;
				setXYs(9);
				flying(0);
			}
			else {
				strVision = strVisionBehind;
				strs = strVision.split("~");
				System.out.println("转向后方蜜蜂");
			}
		}*/
		if(dead) {
			locked  = false;
			dead = false;
		}
		/*if(locked && strVision.indexOf('+') == -1) {
			angle += 180;
			if(angle >= 360) {
				angle -= 360;
			}
			ratoteImage(angle);
			miss = true;	
			System.out.println("错过重寻");
		}*/
		/*while(strVision.indexOf('+') == -1) {
			if(strVision.indexOf('*') == -1) {
				setXYs(0);
				for(int k = 0; k < 9; k++) {
					flying(k);
				}
			}
			else {
				Random r = new Random();
				angle += r.nextInt(90);
				ratoteImage(angle);
				setXYs(0);
				for(int k = 0; k < 9; k++) {
					flying(k);
				}
			}
			strVision = BeeFarming.search(id);
		}*/
		flag = false;
		for(int i = 0;i < strs.length; i++) {
			if(strs[i].indexOf("+") == 0) {
				System.out.println("当前角度" + angle);                                                                                             
				String strTmp = strs[i];
				int start = strTmp.indexOf('(');
				int end = strTmp.indexOf(',');
				String s = strTmp.substring(start+1,end);
				idPrey = new Integer(s).intValue();
				if(locked) {
					if(idPrey != idOld) {
						continue;
					}
				}
				flag = true;
				count = 3;
				strTmp = strTmp.substring(end+1);
				end = strTmp.indexOf(',');
				s = strTmp.substring(0,end);
				angleToPrey = new Double(s).doubleValue();
				strTmp = strTmp.substring(end+1);
				end = strTmp.indexOf(')');
				s = strTmp.substring(0,end);
				anglePrey = new Double(s).doubleValue();
				System.out.println("测试");
				angle = angleToPrey;
				System.out.println("转向蜜蜂"+idPrey);
				ratoteImage(angle);
				/*while(!dead) {
					String strStepVision = BeeFarming.search(id);
					if(strStepVision.indexOf('+') == -1) {
						angle += 180;
						if(angle >= 360) {
							angle -= 360;
						}
						ratoteImage(angle);
						setXYs(0);
						System.out.println("错过重寻");
					}
					for(int k = 0; k < 9; k++) {
						flying(k);
						if(dead) {
							break;
						}
					}
					System.out.println("作弊飞行");
				}*/
				idOld = idPrey;
				locked = true;
			}
			if(!locked) {
				if(strs[i].indexOf("*") == 0){
					/*if(xFlowers.size() > 2) {
						Random r = new Random();
						int index = r.nextInt(xFlowers.size());
						double a = BeeFarming.getVectorDegree(getX() + image.getWidth(null) / 2, getY() + image.getHeight(null) / 2, xFlowers.get(index), yFlowers.get(index));
						angle = a;
						ratoteImage(angle);
						System.out.println("转向已知花位置");
					}				
					angle += 90;
					if(angle >= 360) {
						angle -= 360;
					}
					ratoteImage(angle);	*/
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
				}
				if(strs[i].indexOf("-")==0){
					String strTmp = strs[i];
					int start = strTmp.indexOf('(');
					int end = strTmp.indexOf(',');
					String s = strTmp.substring(start+1,end);
					int honey = new Integer(s).intValue();
					strTmp = strTmp.substring(end+1);
					end = strTmp.indexOf(')');
					s = strTmp.substring(0,end);
					if(s.equals("ON")) {
						/*if(stop) {
							stop = false;
						}
						else {
							Random ra = new Random();
							angle += 90 + ra.nextInt(90);
							if(angle >= 360) {
								angle -= 360;
							}
							System.out.println("暂留并转角");
							ratoteImage(angle);
							stop = true;
							flowerExist = false;
							for(int j = 0; j < xFlowers.size(); j++) {
								int distance = (int)(Math.pow(getX() + image.getWidth(null) / 2 - xFlowers.get(j), 2) + Math.pow(getY() + image.getHeight(null) / 2 - yFlowers.get(j), 2));
								if(distance <= 4) {
									flowerExist = true;
									break;
								}
							}
							if(!flowerExist) {
								xFlowers.add(getX() + image.getWidth(null) / 2);
								yFlowers.add(getY() + image.getHeight(null) / 2);
								System.out.println("记录花位置：(" + (getX() + image.getWidth(null) / 2) +" , " + (getY() + image.getHeight(null) / 2) +")");
							}
						}*/
					}
					else{
						double a = new Double(s).doubleValue();
						angle = a + 75;
						if(a >= 360) {
							angle -= 360;
						}
						ratoteImage(angle);
					}
				}
			}		
		}
		if(locked && !flag) {
			if(count > 0) {
				angle += 90;
				if(angle >= 360) {
					angle -= 360;
				}
				ratoteImage(angle);
				miss = true;	
				System.out.println("错过重寻");
				count--;
			}
			else {
				locked = false;
				count = 3;
			}
		}
		System.out.println(strVision);
		if(miss) {
			setXYs(9);
			miss = false;
		}
		else {
			setXYs(0);
		}
	}
	/**如果黄蜂抓到了蜜蜂，则boolean dead==true，黄蜂可以根据dead的值判断蜜蜂知否被杀死。
	本方法可以修改，在BeeFarming的killBee方法中当蜜蜂被黄蜂消灭后将被调用*/
	public boolean isCatched(){
	    dead = true;
		System.out.println("dead");
	    return dead;
	}
	  
}