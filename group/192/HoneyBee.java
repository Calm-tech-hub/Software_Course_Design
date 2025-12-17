import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class HoneyBee extends Bee{
	private int id;
	private boolean isDanger=false;
	private int num=0;
	private static int dir = 1;
	
	private static boolean dangersignal[] = {false,false,false,false};
	
	public HoneyBee(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
	}
	
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){
		String strVision = BeeFarming.search(id);
		if(strVision.indexOf('*')!=-1){
			if(strVision.indexOf('+') ==-1){
				if(strVision.indexOf('-') != -1)
					Pickhoney(strVision);
				Random ra = new Random();
				angle += ra.nextInt(90);
				ratoteImage(angle);
			}
			else
			{
				dangersignal[id] = true;
				Avoidhornet(strVision);
				new Timetochange(20,id);		//遇见黄蜂，20秒不主动采蜜，逃命
			}
		}
		
		else{
			if(strVision.indexOf("+(9") !=-1)
			{
				dangersignal[id] = true;
				Avoidhornet(strVision);
				new Timetochange(20,id);
			}
			if(strVision.indexOf("+(9") ==-1 && strVision.indexOf('-')!= -1 && dangersignal[id]==false )
			{	
				if(strVision.indexOf("+(1") != -1 ||strVision.indexOf("+(2") != -1
						||strVision.indexOf("+(2") != -1)		//避免两只蜜蜂相遇出现bug
				{
					Random ra = new Random();
		        	angle = angle + ra.nextInt(90);
		        	ratoteImage(angle);
				}
				else
					Pickhoney(strVision);
			}
			if(strVision.indexOf("+(9") ==-1 && strVision.indexOf('-')== -1)
			{
				Random ra = new Random();
	        	angle = angle + ra.nextInt(25) * dir;
				
	            ratoteImage(angle);
	            dir = dir*(-1);			//实现左右摆动，随机摆动25度以内，25为多次测试得出
			}
			
			
		}

		setXYs(0);
	}
	
	
		public void Pickhoney(String strVision){	//飞往蜂蜜处
			if(strVision.indexOf('-') != -1){
				System.out.println(strVision);
				int flag = strVision.indexOf('-');
				int flag1 = strVision.indexOf('(',flag+1);
				int flag2 = strVision.indexOf(',',flag1+1);
				int flag3 = strVision.indexOf(')',flag2+1);
				
				String anglestring = strVision.substring(flag2+1,flag3);
				if(!anglestring.equals("ON")){
				angle =  (Double.parseDouble(anglestring)+360)%360;
				ratoteImage(angle);
				}
			}
		}
	
		public void Avoidhornet(String strVision){	//撞见黄蜂时转向方案
			
		if(strVision.indexOf('+') !=-1){
			
			int flag = strVision.indexOf('+');
			int flag1 = strVision.indexOf('(',flag+1);
			int flag2 = strVision.indexOf(',',flag1+1);
			int flag3 = strVision.indexOf(',',flag2+1);
			
			String idstring = strVision.substring(flag1+1,flag2);
			String astring = strVision.substring(flag2+1,flag3);
			
			double hornet = (Double.parseDouble(astring)+360) % 360;
			if(Integer.parseInt(idstring) == 9)
			{
				
				if(hornet>=0 && hornet<90)
					angle = 180;
				if(hornet>=90 && hornet<180)
					angle = 270;
				if(hornet>=180 && hornet<270)
					angle = 0;
				if(hornet>=270 && hornet<360)
					angle = 90;
				ratoteImage(angle);
			}
			setXYs(0);
		}
		
		}
		
		class Timetochange{		//计时器
			Timer timer;
			
			public Timetochange(int seconds,int id){
				timer = new Timer();
		        timer.schedule(new RemindTask(), seconds*1000);
			}
			class RemindTask extends TimerTask {
		        public void run() {
		           
		            dangersignal[id] = false;
		            timer.cancel(); //Terminate the timer thread
		        }
		    }
			
		}
		
}