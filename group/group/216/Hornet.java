import java.awt.*;
import java.awt.image.*;
import java.util.*;
public class Hornet extends Bee{
    /**蜜蜂当前的坐标*/
	private int id;
	private boolean dead=false;
	public Hornet(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
	}
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){
		String strVision = BeeFarming.search(id);
		String[] strs = strVision.split("~");
		for(int i=0;i<strs.length;i++){
			//如果碰到*为首的字符串，代表遇到了边，这里是随机顺时针旋转90度以内的角度
			if(strs[i].indexOf('*')==0)
			{			
				Random ra = new Random();
				angle += ra.nextInt(45)+45;
				ratoteImage(angle);
			}
			if(strs[i].indexOf('+')==0){
				String strTmp = strs[i];
				int start = strTmp.indexOf('(');
				int end = strTmp.indexOf(',');
				String s = strTmp.substring(start+1,end);
				int id = new Integer(s).intValue();
				strTmp = strTmp.substring(end+1);
				end = strTmp.indexOf(',');
				s = strTmp.substring(0,end);
				{
					double a = new Double(s).doubleValue();
					angle = a;
					ratoteImage(angle);
					break;
					//System.out.println("旋转："+angle);
				}//else
					//System.out.println("ON flower");

			}			
			else if(strs[i].indexOf('-')==0)
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
					angle = a;
					ratoteImage(angle);
					
					//System.out.println("旋转："+angle);
				}
			}
		}
		//System.out.println(strVision);
		setXYs(0);
		//System.out.println("vx="+fs1.x+",vy="+fs1.y);		
	}
public boolean isCatched(){
	    dead = true;
	    return dead;
	}
	  
}