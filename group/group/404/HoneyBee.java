import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.math.*;


public class HoneyBee extends Bee{
	private int id;
	private static boolean isDanger[]=new boolean[10];//记录该蜜蜂是否可能正在被追逐
	private static int dangerTime[]=new int[10];//记录从开始被追逐后经历的时间
	private int num=0;
	static int mTime=1100;
	private int isOutofrange = 0;
	
	/*尝试坐标记录系统，此系统需要默认速度不变，编写时速度为18，视距为50*/
	private int dx=-1,dy=-1,sx=-1,sy=-1;//记录蜜蜂绝对坐标，尝试通过触碰边界的方法获得,-1表示未知，预计有一个视距的误差
	
	private void getXY(String fx,int speed,int RANGE,double tangle)
	{
		double angle=Math.toRadians(tangle);
		if (fx.equals("E")) {
			this.sx=(int) Math.abs(RANGE*Math.cos(angle));
		}
		if (fx.equals("W")) {
			this.dx=(int) Math.abs(RANGE*Math.cos(angle));
		}
		if (fx.equals("N")) {
			this.dy=(int) Math.abs(RANGE*Math.sin(angle));
		}
		if (fx.equals("S")) {
			this.sy=(int) Math.abs(RANGE*Math.sin(angle));
		}
	}
	private void fXY(int flwV,double tangle,int speed) {
		double angle=Math.toRadians(tangle);
		int dist=speed*(10-flwV);
		
		this.dx+=dist*Math.cos(angle);
		this.dy+=dist*Math.sin(angle);
		this.sx-=dist*Math.cos(angle);
		this.sy-=dist*Math.sin(angle);		
	}
	
	/**
	 * 
	 * @author Nanxu
	 * 如果不存在花，则volume = -1
	 * 如果没碰到边界则range = 'y'
	 * 
	 */		

	public  Hashtable getState(String state) {
		String[] input = state.split("~");
		//System.out.println(input[1].charAt(1));
		ArrayList<String> range = new ArrayList<String>();
		//check range
		//此处需要修改，不然无法处理同时遇到两个边界的情况
		//BeeFarming.search()中对的边界检测是正前方视距远处是否超界。
//		if (input[0].startsWith("*")) {
//		       range = input[0].substring(1,2);
//		}
		//check flower
		int Flower = 0;
		int isOnFlower = 0;
		ArrayList<Integer> volume = new ArrayList();
		ArrayList<Double> angleofFlower = new ArrayList();
		for (String element : input) {
			//System.out.println(element);
			if (element.startsWith("-")){
				Flower += 1;
				String[] temp = element.split(",");
				volume.add(Integer.parseInt(temp[0].substring(2)));
				if (element.endsWith("ON)"))
				   isOnFlower = 1;
				else 
					angleofFlower.add(Double.parseDouble(temp[1].replace(')', ' ')));
					
			}
			else if(element.startsWith("*")){
				range.add(element.substring(1, 2));
			}
		}
		//check bees
		int isExistBee = 0;
		ArrayList<Integer> idOfbee = new ArrayList();
		ArrayList<Double> angleOfbeeR = new ArrayList();
		ArrayList<Double> angleOfbee = new ArrayList();
		
		for (String element : input) {
			if (element.startsWith("+")) {
				isExistBee = 1;
				String[] temp = element.split(",");
				idOfbee.add(Integer.parseInt(temp[0].substring(2)));
				angleOfbeeR.add(Double.parseDouble(temp[1]));
				angleOfbee.add(Double.parseDouble(temp[2].replace(')', ' ')));
			}
		}
		Hashtable result = new Hashtable(8);
		result.put("isOnFlower", isOnFlower);
		result.put("range", range);
		result.put("volume", volume);
		result.put("angleofflower", angleofFlower);
		result.put("isExistBee", isExistBee);
		result.put("idOfbee", idOfbee);
		result.put("angleOfbeeR", angleOfbeeR);
		result.put("angleOfbee", angleOfbee);
	    return result;
	}	 
		   
	public HoneyBee(int id,int x, int y, double angle, boolean isAlive, Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
	}
	static {
		for (int i=0;i<0;i++){
			isDanger[i]=false;
			dangerTime[i]=0;
		}
	}
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){
		mTime--;
		//System.out.println("mTime"+mTime);
		String strVision = BeeFarming.search(id);
		
		Random ra = new Random();
		Hashtable state=getState(strVision);
		if (isDanger[id]) dangerTime[id]++;
		ArrayList<String> tRange=(ArrayList)state.get("range");
		int nR=0;
		//边界判断
		if (!(tRange.isEmpty())) //同时碰到两个边界时会出现错误！！！
			for (String fx:tRange) {
				getXY(fx,18,50,angle);
				if (dangerTime[id]>1500) {
					dangerTime[id] = 0;
					isDanger[id] = false;
				}
				nR++;
				if (nR>1) {
					angle=angle+180+2;//+2是因为转角的时候保留了1度的空余，所以多转一度防止出界（其实不多转也不会出界
					while (angle>360) angle-=360;
					break;
				}
				double jg=-1,tmp=0;
				boolean jia=true;
				while (angle<0) angle+=360;
				while (angle>360) angle-=360;
				if (fx.equals("E")) {
					jg=0;
					jia=(angle<90);
				}
				if (fx.equals("N")) {
					jg=270;
					jia=((angle-270)>0);
				}
				if (fx.equals("W")) {
					jg=180;
					jia=(angle-180)>0;
				}
				if (fx.equals("S")) {
					jg=90;
					jia=(angle-90)>0;
				}
			
				int tr=ra.nextInt(91);
				
				double ta=angle;
				while (Math.abs(ta+(jia?tr:-tr)-jg)<90) {
					tr=ra.nextInt(91);
				}
				if (jia) angle=ta+tr;
					else angle=ta-tr;
			
				ratoteImage(angle);
				setXYs(0);
				/*
				if (!strVision.isEmpty()) {
					System.out.println("HoneyBee<"+ id + ">: " + strVision);
					System.out.println("dx="+dx+";dy="+dy+";sx="+sx+";sy="+sy);
				}
				*/
				//return;
			}
		//无边界情况，tAngle为待定角度。
		Double tAngle=null;
		//记录下即将采蜜的花蜜容量，用来计算飞行距离及坐标
		int flwV=0;
		//随机选取一朵视野中的花去采蜜，因为采蜜时间无法控制，所以花蜜的容量多少不管
		if (!((ArrayList)state.get("volume")).isEmpty())
		{
			Iterator it= ((ArrayList)state.get("angleofflower")).iterator();
			Iterator itV=((ArrayList)state.get("volume")).iterator();;
			while (it.hasNext()) {
				if (isDanger[id]) break;
				int dn=0,al=0;
				for (int i=0;i<9;i++)
					{ 
						if (isDanger[i]) dn++;				
					}
				if (dn!=1)
					if (ra.nextInt(2000)<mTime) break;	
				tAngle=(Double)it.next();
				flwV=(int)itV.next();
			}
		}
		if (tAngle!=null) {
			angle=tAngle;
		}
		
		if ((int)state.get("isExistBee")>0){		
			Iterator itb=((ArrayList)state.get("angleOfbee")).iterator();
			Iterator itr=((ArrayList)state.get("angleOfbeeR")).iterator(); 
			Iterator iti=((ArrayList)state.get("idOfbee")).iterator(); 
			while (itb.hasNext()&&iti.hasNext()&&itr.hasNext()) {
				Integer id=(Integer)iti.next();
				Double ba=(Double)itb.next();
				Double ar=(Double)itr.next();
				if (id<9) {
				//	angle=ba+ra.nextInt(90)+90;
					if (Math.abs(ar-ba)>90&&flwV==0) {
						angle=ar+ra.nextInt(90);
					}
				}
				else {//发现一只黄蜂
					//先看看自己在不在黄蜂的视野里
					boolean safe=false;
					if ((Math.abs(((int)(ar+180)%360)-ba))>90) safe=true;
					if (safe) {
						angle=ar+((ra.nextInt(10)>5)?90:-90);
						
					}
					else {
						angle=ar+89.99D;
						while (angle>360) angle-=360;
						//angle=ar+90;
						for (int i=0;i<9;i++) isDanger[i]=false;
						isDanger[id]=true;						
						dangerTime[id]=0;						
					}
					flwV=0;
				}				
			}
		}
		fXY(flwV,angle,18);
		ratoteImage(angle);
		setXYs(0);
	}
}

