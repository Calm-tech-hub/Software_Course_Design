import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class HoneyBee extends Bee{
	private int id;
	private boolean isDanger=false;
	private int num=0;
	public HoneyBee(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
	}
	
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){
		String strVision = BeeFarming.search(id); //返回值为result，包含了
		//System.out.println("ID="+id);
		System.out.println(strVision);
		
		//当黄蜂视野中有该蜜蜂时，蜜蜂不采蜜且遇花躲开，有一定的几率能甩掉黄蜂
		int HorBeeId = getHornetVision();
		//System.out.println("HorBeeId="+HorBeeId);
		if(HorBeeId==id){
			if(strVision.indexOf('-')!=-1){
				strVision="*";
			}
		}
		
		//如果碰到*为首的字符串，代表遇到了边，这里是随机顺时针旋转90度以内的角度
		if(strVision.indexOf('*')==0)
		{	
			Random ra = new Random();
			angle += ra.nextInt(90);
			//angle+=80;    //固定顺时针旋转80度
			ratoteImage(angle);
		}
		
		//如果碰到-为首的字符串，代表遇到了花，找准角度前往采蜜
		else if(strVision.indexOf('-')==0){
			int inde1 = strVision.indexOf(',');
			int inde2 = strVision.indexOf(')');
			if(strVision.indexOf("ON")==-1){
				angle = Double.valueOf(strVision.substring(inde1+1, inde2)).doubleValue();	//从中取出angle并将string转化为double
				ratoteImage(angle);
			}
			
		}
		
		//如果碰到+为首的字符串，代表遇到了其他蜜蜂或者黄蜂，需要躲开
		else if(strVision.indexOf('+')==0){
			int inde1 = strVision.indexOf(',');
		    int inde2 = strVision.indexOf(',',inde1+1);
			angle = Double.valueOf(strVision.substring(inde1+1, inde2)).doubleValue();
			
			int bee_id = Integer.valueOf(strVision.substring(2, inde1)).intValue();
			if(bee_id==9)  {
				isDanger=handleDanger(angle);
				}
			else {
				//Random ra = new Random();
				//angle += ra.nextInt(90);   
				angle+=90;
				ratoteImage(angle);
			}
		}
	
		setXYs(0);
	}
	
	/** 当黄蜂在蜜蜂的视野内，处理该危机*/
	public boolean handleDanger(double angle){
		isDanger = true;
		ratoteImage(angle+45);
		return false;
	}
	
	/**伪装成黄蜂查看其视野中有没有蜜蜂*/
	public int getHornetVision(){
		String HornetVision = BeeFarming.search(9);
		int beeIdHor;
		if(HornetVision.indexOf('+')!=-1)
		{
			int inde1=HornetVision.indexOf("+(");
			int inde2=HornetVision.indexOf(',',inde1+1);
			beeIdHor = Integer.valueOf(HornetVision.substring(inde1+2, inde2)).intValue();	
		}
		else beeIdHor=9;
		
		return beeIdHor;
	}
	
	
}