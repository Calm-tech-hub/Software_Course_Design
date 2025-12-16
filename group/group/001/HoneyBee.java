import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class HoneyBee extends Bee
{
	private int id;
	private int num = 0;
	
	private char first_char = ' ';
	private char last_char = ' ';
	
	private boolean isDanger = false;
	
	public HoneyBee(int id, int x, int y, double angle, boolean isAlive, Image img)
	{
		super(id, x, y, angle, isAlive, img);
		this.id = id;
	}
	
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search()
	{
		String strVision = BeeFarming.search(id);
		
		if (strVision.length() != 0)
		{
			first_char = strVision.charAt(0);
			
			if(last_char == '+' && (first_char == '-' || first_char == '+'))
			{
				first_char = '*';
			}
			
			last_char = strVision.charAt(0);
		}
		
		// meet wall
		if(strVision.indexOf('*') == 0)
		{
			angle += 150;
			ratoteImage(angle);
		}
		// meet bee
		if(strVision.indexOf('+') == 0)
		{	
			angle += 90;
			ratoteImage(angle);
		}
		else
		{
			// meet flowers
			int dou = strVision.indexOf(',');
			if((dou == 4 || dou == 5) && (strVision.indexOf('-') == 0) && (strVision.charAt(dou + 1) != 'O'))
			{
				String str = strVision;
				str = str.trim();
				
				String f_angle = "";
				
				dou ++;
				
				while(strVision.charAt(dou) != ',' && strVision.charAt(dou) != ')')
				{
					f_angle += str.charAt(dou);
					dou ++;
				}
				
				double flower_angle = Double.parseDouble(f_angle);
				
				angle = flower_angle;
				ratoteImage(angle);
			}
		}
		
		System.out.println("strVision = " + strVision);
		
		setXYs(0);
	}
}