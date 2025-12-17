import java.awt.*;
import java.awt.image.*;
import java.util.*;
public class Hornet extends Bee{
	private int id;
	private boolean dead=false;
	private boolean seen=false;
	private int num=0;
	private boolean isDanger=false;
	public Hornet(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
	}
	
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){
		String strVision = BeeFarming.search(id);	

		if (strVision.startsWith("*")) {
			this.num++;
			System.out.println("黄蜂看见墙："+strVision);
			Random ra = new Random();
			double deltAngle = 0.0;
			if((strVision.indexOf('S')==1) || (strVision.indexOf('N')==1)){
				angle = (360-angle+deltAngle)%360;
				System.out.println(angle);
				ratoteImage(angle);
			}
			else {
				angle = (((540-angle)%360)+deltAngle)%360;
				ratoteImage(angle);
			}
			strVision = strVision.substring(strVision.indexOf('~')+1);
		}
		else {
			this.num = 0;
		}
		
		while (strVision.startsWith("-(")) {
			strVision = strVision.substring(strVision.indexOf('~')+1);
		}
	
		if (strVision.startsWith("+(")) {
			System.out.println("黄蜂看见蜜蜂："+strVision);
			while (strVision.indexOf('+') != strVision.lastIndexOf('+')) {
				strVision = strVision.substring(strVision.indexOf('~')+1);
			}
			String beeAngle = strVision.substring(strVision.indexOf(',')+1,strVision.lastIndexOf(','));
			angle = Double.parseDouble(beeAngle);
			ratoteImage(angle);
		}
		
		setXYs(0);	
	}
	
	/**如果黄蜂抓到了蜜蜂，则boolean dead==true，黄蜂可以根据dead的值判断蜜蜂知否被杀死。
	本方法可以修改，在BeeFarming的killBee方法中当蜜蜂被黄蜂消灭后将被调用*/
	public boolean isCatched(){
	    dead = true;
	    return dead;
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
	  
}