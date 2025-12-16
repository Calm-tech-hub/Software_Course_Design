import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class HoneyBee extends Bee{
    /**蜜蜂当前的坐标*/
	private int id;
	private boolean isDanger=false;
	public HoneyBee(int id,int x, int y, double angle,boolean isAlive,Image img){
	super(id,x,y,angle,isAlive,img);
		this.id = id;
	}
	
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){
		String strVision = BeeFarming.search(id);
		String[] strs = strVision.split("~");
		String strVision1 = BeeFarming.search(9);
		String[] strs1 = strVision1.split("~");
		//System.out.println(strVision1);
		int id1=3;
		for(int i=0;i<strs1.length;i++)
		{
			if(strs1[i].indexOf('-')==0&&isDanger)
			{
				isDanger=true;
				id1 = 3;
			}
			if(strs1[i].indexOf('+')==0)
			{
				isDanger=true;
				String strTmp = strs1[i];
				int start = strTmp.indexOf('(');
				int end = strTmp.indexOf(',');
				String s = strTmp.substring(start+1,end);
				id1 = new Integer(s).intValue();
			}

			//System.out.println(strVision1.indexOf('+'));
		}
		for(int i=0;i<strs1.length;i++)
		{
			if(strs1[i].indexOf('-')==0)
			{
				String strTmp1 = strs1[i];
				int end1 = strTmp1.indexOf(',');
				strTmp1 = strTmp1.substring(end1+1);
				end1 = strTmp1.indexOf(')');
				String s1 = strTmp1.substring(0,end1);
				 if(s1.equals("ON")){
					isDanger=true;
					//System.out.println("旋转："+angle);
				}//else
			}
		}
		//System.out.println(isDanger);
		for(int i=0;i<strs.length;i++){
			//System.out.println("是否危险"+isDanger+"id:"+id);
			//如果碰到*为首的字符串，代表遇到了边，这里是随机顺时针旋转90度以内的角度
			if(strs[i].indexOf('*')==0)
			{
				Random ra = new Random();
				angle += ra.nextInt(20)+100;
				ratoteImage(angle);
			}
			//如果碰到-为首的字符串，代表遇到了花，这里是向其中一朵花飞（即使同时看到多个花）
			if(strs[i].indexOf('-')==0&&!(isDanger&&id1==id))
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
				}//else
					//System.out.println("ON flower");
			}else if(strs[i].indexOf('+')==0){
				String strTmp = strs[i];
				int start = strTmp.indexOf('(');
				int end = strTmp.indexOf(',');
				String s = strTmp.substring(start+1,end);
				int id2 = new Integer(s).intValue();
				strTmp = strTmp.substring(end+1);
				end = strTmp.indexOf(',');
				s = strTmp.substring(0,end);
				double a = new Double(s).doubleValue();
				angle =a+90;
				ratoteImage(angle);
				break;
			}
			if(isDanger&&id1==id)
			{
				Random ra = new Random();
				angle += ra.nextInt(45)+60;
				ratoteImage(angle);
				
			}
		}
		setXYs(0);
	}
	
}