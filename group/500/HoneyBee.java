import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class HoneyBee extends Bee{
	private int id;
	//视野中是否有黄峰
	private boolean isDanger=false;
	private boolean imDanger=false;
	//是否已经反转过一次（丢失了一次花）
	//蜜蜂看到的黄蜂的运行方向
	private double angleHornet = 360;
	//黄蜂看到的相对于蜜蜂的角度
	private double angleBettHornet = 360;
	//表示定时器，为0表示遇到花需要翻转操作，否则正常采蜜
	public int isMissing=0;
	public final int TIMER = 10;
	public  int TIMER_DEC = 3;
	private int num=0;
	private boolean shuipin = false;
	public HoneyBee(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
	}
	
	
	
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){
		//保存看到的花
		ArrayList<FlowerSimple> flowerList = new ArrayList<>();
		ArrayList<BeeSimple> beeList = new ArrayList<>();
		
		//保存黄峰的信息
		ArrayList<FlowerSimple> flowerListHornet = new ArrayList<>();
		ArrayList<BeeSimple> beeListHornet = new ArrayList<>();
		
		String strHornet= BeeFarming.search(9);
		
		String strVision = BeeFarming.search(this.id);
		//System.out.println(strVision);
		//如果碰到*为首的字符串，代表遇到了边，这里是向原方向的30度偏离转向
		//转边的角度需要继续调整
		/*if(strVision.indexOf('*')==0)
		{			
			Random ra = new Random();
			//angle += ra.nextInt(180);
			angle = angle + 180 -45;
			if (angle<10) {
				angle = 10;
			}else if (angle>80 && angle <90) {
				angle = 80;
			}else if (angle>90 && angle<100) {
				angle = 100;
			}else if (angle >170 && angle <180) {
				angle = 170;
			}else if (angle>180 && angle<190) {
				angle = 190;
			}else if (angle>260 && angle<270) {
				angle = 260;
			}else if (angle>270&&angle<280) {
				angle=280;
			}else if (angle>350) {
				angle = 350;
			}
			if (angle > 360) {
				angle = angle - 360;
			}
			ratoteImage(angle);
		}*/
		
		isDanger = false;
		imDanger = false;
		
		if (TIMER_DEC != 0) {
			TIMER_DEC--;
		}
		//如果是其它信息，则打印出来
		if(strVision.length()>0)
			System.out.println(strVision);	
				
		getInformation(flowerList, beeList, strVision);
		
		getInformation(flowerListHornet, beeListHornet, strHornet);
		
		for (int i = 0; i < beeListHornet.size(); i++) {
			if (beeListHornet.get(i).id == id) {
				angleBettHornet = beeListHornet.get(i).angleBetwenTwoBee;
				isDanger = true;
			}
		}
		
		for (int i = 0; i < beeList.size(); i++) {
			if (beeList.get(i).id == 9) {
				angleHornet = beeList.get(i).angleOfSeenbee;
				imDanger = true;
			}
		}
		
		if (!isDanger && flowerList.size() != 0 && strVision.indexOf('*')==0) {
			//采花
			super.ratoteImage(flowerList.get(0).angleOfFlower);
			setXYs(0);
		} else if (strVision.indexOf('*')==0 && isDanger) {
			//有危险的撞墙检测
			angle = angle + 180 - 45;
			if (angle > 360) {
				angle = angle - 360;
			}
			ratoteImage(angle);
			setXYs(0);
		}else if (strVision.indexOf('*')==0) {
			//无危险的撞墙检测
			Random ra = new Random();
			angle += ra.nextInt(180);
			//angle = angle + 180 -45;
			if (angle > 360) {
				angle = angle - 360;
			}
			ratoteImage(angle);
			setXYs(0);
		}else if (isDanger) {
			//躲避算法
			if (!imDanger && TIMER_DEC == 0) {
				//反对着黄蜂，需要翻转
				/*if (angle<180) {
					super.ratoteImage(angle + 90);
				}else {
					super.ratoteImage(angle - 90);
				}*/
				if (angleHornet<180)
					super.ratoteImage(angle + 90);
				else {
					super.ratoteImage(angle - 90);
				}
				/*if (shuipin) {
					super.ratoteImage(angle + 90);
					shuipin = false;
				}else {
					super.ratoteImage(angle - 90);
					shuipin = true;
				}*/
				setXYs(0);
				TIMER_DEC = 3;
			}else if(TIMER_DEC == 0) {
				//正对着黄蜂，不能怂
				if (angleHornet<180) {
					super.ratoteImage(angleHornet + 180 - 90);
				}else {
					super.ratoteImage(angleHornet -180 + 90);
				}
				//super.ratoteImage(angleHornet + 180 - 45);
				TIMER_DEC = 3;
				setXYs(0);
			}else {
				setXYs(0);
			}
			
		}else if (flowerList.size() != 0) {
			//采花
			FlowerSimple flowerSimple = flowerList.get(0);
			for(FlowerSimple flower : flowerList){
				if (flower.angleOfFlower < flowerSimple.angleOfFlower) {
					flowerSimple = flower;
				}
			}
			super.ratoteImage(flowerSimple.angleOfFlower);
			setXYs(0);
		}else {
			//正常飞行
			setXYs(0);
		}
		
		/*if(strVision.indexOf('*')==0)
		{
			Random ra = new Random();
			//angle += ra.nextInt(180);
			angle = angle + 180 -45;
			if (angle<10) {
				angle = 10;
			}else if (angle>80 && angle <90) {
				angle = 80;
			}else if (angle>90 && angle<100) {
				angle = 100;
			}else if (angle >170 && angle <180) {
				angle = 170;
			}else if (angle>180 && angle<190) {
				angle = 190;
			}else if (angle>260 && angle<270) {
				angle = 260;
			}else if (angle>270&&angle<280) {
				angle=280;
			}else if (angle>350) {
				angle = 350;
			}
			if (angle > 360) {
				angle = angle - 360;
			}
			ratoteImage(angle);
			setXYs(0);
		}else if (isDanger) {
			if (TIMER_DEC == 0 && !imDanger) {
				if (angle<180) {
					super.ratoteImage(angle + 180);
				}else {
					super.ratoteImage(angle -180);
				}
				TIMER_DEC = 10;
				setXYs(0);
			}
			else if (angleHornet<180) {
				super.ratoteImage(angleHornet + 180 - 45);
			}else {
				super.ratoteImage(angleHornet -180 + 45);
			}
			setXYs(0);
			
		}else if (!flowerList.isEmpty()) {
			super.ratoteImage(flowerList.get(0).angleOfFlower);
			setXYs(0);
		}else {
			setXYs(0);
		}*/
		
/*		for (int i = 0; i < beeList.size(); i++) {
			if (beeList.get(i).id == 9) {
				isDanger = true;
			}
		}*/
		
		/*if (isMissing != 0) {
			isMissing--;
		}
		
		if (TIMER_DEC != 0) {
			TIMER_DEC--;
		}
		
		isDanger = false;
		for(int i=0; i<beeList.size(); i++){
			if (beeList.get(i).id == 9) {
				angleHornet = beeList.get(i).angleOfSeenbee;
				isDanger = true;
			}
		}
		
		if (isDanger) {
			if (angleHornet<180) {
				super.ratoteImage(angleHornet + 180 - 45);
			}else {
				super.ratoteImage(angleHornet -180 + 45);
			}
			setXYs(0);
		}
		
		else if (isMissing == 0 && !flowerList.isEmpty()) {
			if (super.angle<180) {
				super.ratoteImage(super.angle + 180 -30);
			} else {
				super.ratoteImage(super.angle - 180 +30);
			}
			angle = angle - 180 +30;
			super.ratoteImage(angle);
			//super.ratoteImage(super.angle + 180 +);
			setXYs(0);
			isMissing = TIMER;
		}
		
		else if (isMissing == TIMER - 1) {
			if (super.angle<180) {
				super.ratoteImage(super.angle + 180 -30);
			} else {
				super.ratoteImage(super.angle - 180 + 30);
			}
			angle = angle + 180 -30;
			super.ratoteImage(angle);
			//super.ratoteImage(super.angle + 180 +);
			setXYs(0);
		}
		else if (TIMER_DEC == 0) {
			TIMER_DEC =50;
			angle = angle - 180 +30;
			super.ratoteImage(angle);
			//super.ratoteImage(super.angle + 180 +);
			setXYs(0);
		}
		else if (TIMER_DEC == 49) {
			angle = angle + 180 -30;
			super.ratoteImage(angle);
			//super.ratoteImage(super.angle + 180 +);
			setXYs(0);
		}
		else if (flowerList.isEmpty()) {
			setXYs(0);
		}
		else if (flowerList.size() != 0) {
			if (super.angle<180) {
				super.ratoteImage(super.angle + 180 -30);
			} else {
				super.ratoteImage(super.angle - 180 +30);
			}
			super.ratoteImage(flowerList.get(0).angleOfFlower);
			//super.ratoteImage(super.angle + 180 +);
			setXYs(0);
			//isMissing = true;
*/		}
		
		
		/*else if (flowerList.size() != 1) {
			super.ratoteImage(flowerList.get(0).angleOfFlower);
			setXYs(0);
		}else {
				super.ratoteImage(flowerList.get(0).angleOfFlower);
				setXYs(0);
			}*/

	//}



	/**对获得的字符串进行加工获得相应信息
	 * @param flowerList
	 * @param beeList
	 * @param strVision
	 */
	private void getInformation(ArrayList<FlowerSimple> flowerList,
			ArrayList<BeeSimple> beeList, String strVision) {
		
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

