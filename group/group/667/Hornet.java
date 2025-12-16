import java.awt.*;
import java.awt.image.*;
import java.util.*;
public class Hornet extends Bee{
	private int id;
	private boolean dead=false;
	private boolean seen=false;
	public Hornet(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
	}
	int time = 0;
	int ftime = 0;
	boolean choose = false;
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search()
	{
		String strVision = BeeFarming.search(id);
		
		ftime ++;
		//遇到蜜蜂，确定角度并跟随
		if(strVision.indexOf('+')!=-1)
		{
			time ++;
			int inde1 = strVision.indexOf('+');
			int inde2 = strVision.lastIndexOf('~');
			String str1 = new String();
			str1 = strVision.substring(inde1+1, inde2);
			    
			//防止出现同时看到两只蜜蜂无法选择角度情况
			if(str1.contains("+"))
			{
				int ind1 = str1.indexOf('(');
	            int ind2 = str1.indexOf('~');
	            str1 = str1.substring(ind1, ind2);
			}
			
			//调整角度准备出击
			int inde3 = str1.indexOf(',');
			int inde4 = str1.lastIndexOf(',');
			angle = Double.valueOf(str1.substring(inde3+1, inde4)).doubleValue();
			
			//黄蜂不能放弃
			/*if(time == 20)
			{
				angle = - angle ;
				time = 0;
			}*/
			ratoteImage(angle);
		}
   
		//看到花时黄蜂向花飞，以寻找蜜蜂
		else if((strVision.indexOf('-')!=-1)&&(ftime ==5))
		{
			ftime =0;
			String[] str2 = strVision.split("~");
			for(int i = 0; i<str2.length;i++)
			{
				if(str2[i].contains("-"))
				{
					if(str2[i].indexOf("ON")==-1)
					{
					int inde1 = str2[i].indexOf(',');
				    int inde2 = str2[i].indexOf(')');
					angle = Double.valueOf(str2[i].substring(inde1+1, inde2)).doubleValue();
					}
				}
				ratoteImage(angle);
			}
		}
		//没有蜜蜂,没有花，但遇到墙
		else if(strVision.indexOf("*")!=-1)
		{
			Random ra = new Random();
			angle += ra.nextInt(90);
			ratoteImage(angle);
		}
		//无蜜蜂，无墙情况
	
	 else 
		{
			
				if(choose==false)
				{
					Random ra = new Random();
					angle += ra.nextInt(90);
					ratoteImage(angle);
					choose = true;
				}
				else
				{
					Random ra = new Random();
					angle -= ra.nextInt(90);
					ratoteImage(angle);
					choose = false;
				}
		
		}
		setXYs(0);	
	}
		

	/**如果黄蜂抓到了蜜蜂，则boolean dead==true，黄蜂可以根据dead的值判断蜜蜂知否被杀死。
	本方法可以修改，在BeeFarming的killBee方法中当蜜蜂被黄蜂消灭后将被调用*/
	public boolean isCatched(){
	    dead = true;
	    return dead;
	}
}