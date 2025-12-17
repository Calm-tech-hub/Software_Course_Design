import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.lang.*;

public class Hornet extends Bee
{

	private int id;
	private int ratote = 0;
	private int distance_now = 100;
	
	private boolean dead = false;
	private boolean seen = false;

	public Hornet(int id, int x, int y, double angle, boolean isAlive, Image img)
	{
		super(id, x, y, angle, isAlive, img);
		this.id = id;
		this.ratote = 0;
	}
	
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	int count = 0;
	
	public void search()
	{
		// ============ 验证日志：证明001组算法被调用 ============
		System.out.println("🦟 [001组Hornet] search()方法被调用! ID=" + id);
		// ========================================================
		
		count++;
		String strVision = BeeFarming.search(id);

		if(strVision.indexOf('*') == 0)
		{	
	        if(ratote == -1)
				angle = angle;
	        else if(strVision.indexOf("+(") == -1)
			{
	        	Random ra = new Random();
	        	angle += ra.nextInt(90);
	        	ratoteImage(angle);
	        }
	        else
		        ratote += 1;
			
			count = 0;
        }
		else
			ratote = 0;

		if(ratote >= 20)
		{
			angle = angle + 180;
			ratoteImage(angle);
			ratote = -1;
			count = 0;
		}

		if(strVision.indexOf("+(") != -1)
		{		
			if(ratote != -1)
			{
				this.angle = tracert(strVision);
				ratoteImage(angle);
			}	
		}

		if(strVision.indexOf("+(") == -1)
			distance_now = 100;

		if(count > 8)
		{
			angle += -4;
			ratoteImage(angle);
		}
		
		setXYs(0);
	}


/**如果黄蜂抓到了蜜蜂，则boolean dead==true，黄蜂可以根据dead的值判断蜜蜂知否被杀死。
	本方法可以修改，在BeeFarming的killBee方法中当蜜蜂被黄蜂消灭后将被调用*/
	public boolean isCatched()
	{
	    dead = true;
	    return dead;
	}
	  

	public double tracert(String strVision)
	{
		
		int honeybee_posx_next;
		int honeybee_posy_next;
		int honeybee_posx_now;
		int honeybee_posy_now;
		int hornet_posx_next;
		int hornet_posy_next;
		int distance_honeybee;
		int distance1_honeybee;
		int distance2_honeybee;
		
		double temp_honey = -1;
		double temp = 0;
		double temp_2 = 0;
		double angle_tracert = 0;
		
		String temp_str = null;
		String temp_str_2 = null;
		String temp_str_3 = null;
		String temp_str_honey = null;
		String temp_str_honey_2 = null;
		
		if(strVision.indexOf("-(") != -1)
		{
			temp_str_honey = strVision.substring(strVision.indexOf("-("));
			temp_str_honey_2 = temp_str_honey.substring(temp_str_honey.indexOf(',') + 1, temp_str_honey.indexOf(')'));
			temp_honey = Double.valueOf(temp_str_honey_2);
		}
		
		temp_str = strVision.substring(strVision.indexOf("+(") + 4);
		temp_str_2 = temp_str.substring(0, temp_str.indexOf(','));
		temp_str_3 = temp_str.substring(temp_str.indexOf(',') + 1, temp_str.indexOf(')'));
		
		temp = Double.valueOf(temp_str_2);
		temp_2 = Double.valueOf(temp_str_3);
		
		temp = temp % 360;
		temp_2 = (temp_2 + 360) % 360;
		
		honeybee_posx_now = (int)(distance_now * Math.cos(Math.toRadians(temp)));
		honeybee_posy_now = (int)(distance_now * Math.sin(Math.toRadians(temp)));

		distance1_honeybee = (int)( Math.pow((0 + 18 * Math.cos(Math.toRadians(temp_2)) + honeybee_posx_now), 2) +
									Math.pow((0 + 18 * Math.sin(Math.toRadians(temp_2)) + honeybee_posx_now), 2));
		distance2_honeybee = (int)( Math.pow((0 - 18 * Math.cos(Math.toRadians(temp_2)) + honeybee_posx_now), 2) +
									Math.pow((0 - 18 * Math.sin(Math.toRadians(temp_2)) + honeybee_posx_now), 2));
									
		honeybee_posx_next = (int)(distance_now * Math.cos(Math.toRadians(temp)) + 18 * Math.cos(Math.toRadians(temp_2)));
		honeybee_posy_next = (int)(distance_now * Math.sin(Math.toRadians(temp)) + 18 * Math.sin(Math.toRadians(temp_2)));
		
		distance_honeybee = (int)(Math.pow(honeybee_posx_now, 2) + Math.pow(honeybee_posy_now, 2));
		
		if(distance_honeybee <= Math.pow(50, 2))
		{
			if(distance1_honeybee < distance2_honeybee )
				angle_tracert = temp;
		}	
		else if(temp_honey == temp)
			angle_tracert = temp;
		
		angle_tracert = (BeeFarming.getVectorDegree(0, 0, honeybee_posx_next, honeybee_posy_next)) % 360;
		
		hornet_posx_next = (int)(18 * Math.cos(Math.toRadians(angle_tracert)));
		hornet_posy_next = (int)(18 * Math.sin(Math.toRadians(angle_tracert)));
		
		distance_now = (int)(Math.sqrt((Math.pow(honeybee_posx_next-hornet_posx_next, 2)) +
							(Math.pow(honeybee_posy_next-hornet_posy_next, 2))));
		temp_honey = -1;

		return angle_tracert;
	}
}



