import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class Hornet extends Bee{
	private int id;
	private boolean dead=false;
	private boolean seen=false;
	private boolean isMissing = false;
	private int TIMER = 5;
	public Hornet(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
	}
	
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){
		
		ArrayList<FlowerSimple> flowerList = new ArrayList<>();
		ArrayList<BeeSimple> beeList = new ArrayList<>();
		
		ArrayList<FlowerSimple> flowerBeeList = new ArrayList<>();
		ArrayList<BeeSimple> beeBeeList = new ArrayList<>();
		
		ArrayList<FlowerSimple> flowerTemp = new ArrayList<>();
		ArrayList<BeeSimple> beeTemp = new ArrayList<>();
		
		BeeSimple beeSimple = null;
		
		String strVision = BeeFarming.search(id);	
		for (int i = 1; i < 3; i++) {
			String strTemp = BeeFarming.search(id);
			getInformation(flowerTemp, beeTemp, strTemp);
			for (int j = 0; j < beeTemp.size(); j++) {
				if (beeTemp.get(j).id == id) {
					isMissing = true;
				}
			}
		}
		
		//如果碰到*为首的字符串，代表遇到了边，这里是随机顺时针旋转90度以内的角度
		/*if(strVision.indexOf('*')==0)
		{			
			Random ra = new Random();
			angle += ra.nextInt(90);
			if (angle == 180 || angle == 90 || angle == 0) {
				if ((angle+30)<360) {
					angle=angle+30;
				}
			}
			ratoteImage(angle);
		}*/
		//如果是其它信息，则打印出来
		if(strVision.length()>0)
			System.out.println(strVision);	
		
		getInformation(flowerList, beeList, strVision);
		
		if (TIMER != 0) {
			TIMER--;
		}
		
		if (!beeList.isEmpty() && strVision.indexOf('*')==0) {
			//有蜜蜂的情况下撞墙检测
			super.ratoteImage(beeList.get(beeList.size() - 1).angleBetwenTwoBee);
			setXYs(0);
		}else if(strVision.indexOf('*')==0){
			//无蜜蜂时撞墙检测
			Random ra = new Random();
			angle += ra.nextInt(90);
			ratoteImage(angle);
			setXYs(0);
		}else if (!beeList.isEmpty()) {
			//普通情况下黄蜂追击算法
			isMissing = true;
			seen = false;
			String strBee = BeeFarming.search(beeList.get(beeList.size() - 1).id);
			
			getInformation(flowerBeeList, beeBeeList, strBee);
			for (int i = 0; i < beeBeeList.size(); i++) {
				if (beeBeeList.get(i).id == id) {
					//beeSimple = beeBeeList.get(i);
					seen = true;
				}
			}
			beeSimple = beeList.get(beeList.size() - 1);
			if (seen) {
				//对着蜜蜂时
				if (beeSimple.angleOfSeenbee < beeList.get(beeList.size() - 1).angleBetwenTwoBee - 90&& beeSimple.angleOfSeenbee > beeList.get(beeList.size() - 1).angleBetwenTwoBee -180 && TIMER == 0) {
					super.ratoteImage(beeList.get(beeList.size() - 1).angleBetwenTwoBee - 5);
					setXYs(0);
					TIMER = 5;
				}else if(TIMER == 0){
					super.ratoteImage(beeList.get(beeList.size() - 1).angleBetwenTwoBee + 5);
					setXYs(0);
					TIMER = 5;
				}else {
					super.ratoteImage(beeList.get(beeList.size() - 1).angleBetwenTwoBee);
					setXYs(0);
				}
				
				
			}else {
				//背后追击
				if (beeList.get(beeList.size() - 1).angleBetwenTwoBee < 270) {
					if (beeSimple.angleOfSeenbee < beeList.get(beeList.size() - 1).angleBetwenTwoBee + 90&& beeSimple.angleOfSeenbee > beeList.get(beeList.size() - 1).angleBetwenTwoBee&& TIMER == 0) {
						super.ratoteImage(beeList.get(beeList.size() - 1).angleBetwenTwoBee + 5);
						setXYs(0);
						TIMER = 5;
					}else if (TIMER == 0) {
						super.ratoteImage(beeList.get(beeList.size() - 1).angleBetwenTwoBee - 5);
						setXYs(0);
						TIMER = 5;
					}else{
						super.ratoteImage(beeList.get(beeList.size() - 1).angleBetwenTwoBee);
						setXYs(0);
					}
				}else {
					super.ratoteImage(beeList.get(beeList.size() - 1).angleBetwenTwoBee);
					setXYs(0);
				}
				
			}
			/*super.ratoteImage(beeList.get(beeList.size() - 1).angleBetwenTwoBee);
			setXYs(0);*/
		}else if (isMissing) {
			isMissing = false;
			super.ratoteImage(angle + 160);
			setXYs(0);
		}
		else{
			//正常飞行
			setXYs(0);
		}
		
		/*if (beeList.isEmpty()) {
			setXYs(0);	
		} else if (beeList.size() != 1) {
			
		} else {
			super.ratoteImage(beeList.get(0).angleBetwenTwoBee);
			setXYs(0);	
		}*/
		
		
	}
	/**如果黄蜂抓到了蜜蜂，则boolean dead==true，黄蜂可以根据dead的值判断蜜蜂知否被杀死。
	本方法可以修改，在BeeFarming的killBee方法中当蜜蜂被黄蜂消灭后将被调用*/
	public boolean isCatched(){
	    dead = true;
	    return dead;
	}
	
	/**对获得的字符串进行加工获得相应信息
	 * @param flowerList
	 * @param beeList
	 * @param strVision
	 */
	private void getInformation(ArrayList <FlowerSimple> flowerList, ArrayList <BeeSimple> beeList, String strVision) {
		
		FlowerSimple flowerSimple;
		BeeSimple beeSimple;
		
		String[] strings = strVision.split("~");
		for (int i = 0; i < strings.length; i++) {
			if (strings[i].startsWith("+")) {
				beeSimple = new BeeSimple();
				String beeString = strings[i].split("\\(")[1];
				String idString = beeString.split("\\,")[0];
				String angleBetwenTwoBeeString = beeString.split("\\,")[1];
				String angleOfSeenbee = beeString.split("\\,")[2].split("\\)")[0];
				beeSimple.id = Integer.parseInt(idString);
				beeSimple.angleBetwenTwoBee = Double.valueOf(angleBetwenTwoBeeString);
				beeSimple.angleOfSeenbee = Double.valueOf(angleOfSeenbee);
				
				beeList.add(beeSimple);
				
			}else if (strings[i].startsWith("-")) {
				flowerSimple = new FlowerSimple();
				String flowerString = strings[i].split("\\(")[1];
				String volumnString = flowerString.split("\\,")[0];
				String angleString = flowerString.split("\\,")[1].split("\\)")[0];
				if (angleString.equals("ON")) {
					flowerSimple.angleOfFlower = super.angle;
				}else {
					flowerSimple.angleOfFlower = Double.valueOf(angleString);
				}
				
				flowerSimple.volumn = Integer.parseInt(volumnString);
				
				flowerList.add(flowerSimple);
			}
		}
	}
	  
	private class FlowerSimple{
		int volumn;
		double angleOfFlower;
	}

	private class BeeSimple{
		int id;
		double angleBetwenTwoBee;
		double angleOfSeenbee;
	}
	
}

/*class FlowerSimple{
	int volumn;
	double angleOfFlower;
}

class BeeSimple{
	int id;
	double angleBetwenTwoBee;
	double angleOfSeenbee;
}*/