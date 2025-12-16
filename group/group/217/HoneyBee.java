import java.awt.*;
import java.util.*;

public class HoneyBee extends Bee {
	private int id;
    private double rotateToAngle;
    private boolean findingAnotherBee;

    public HoneyBee(int id, int x, int y, double angle, boolean isAlive, Image img) {
        super(id, x, y, angle, isAlive, img);
        this.id=id;
        rotateToAngle = angle;
        findingAnotherBee=false;
    }
	 
	public void search()
	{ 
		 if(!isIndanger(id))
	    	{
	    		if(!isReachBound(id))
	    			searchFlower();
	    		else
	    			meetBound(id);    			
	    	}
		 else	 
	    	{
	    		if(!isReachBound(id))
	    			meetHornet(id);
	    		else
	    			meetBoundHornet(id);
	    	}
	    	
		  ratoteImage(rotateToAngle);
	      setXYs(0);
		 
	}
	
	/**
	 * 黄蜂是否发现蜜蜂
	 * @param id
	 * @return
	 */
	public  boolean isIndanger(int id)
    {
		 String strHornet = BeeFarming.search(9);
		 String strVision = BeeFarming.search(id);
		 
    	 if(strHornet.contains("+("))
         {
         		int index1=strHornet.lastIndexOf('(');
         		int index2=strHornet.lastIndexOf(')');
         		String strBee=strHornet.substring(index1+1,index2);
         		String beeMessage[]=strBee.split(",");
         		if(beeMessage.length==3)
         		{
 	        		int beeId=Integer.parseInt(beeMessage[0]);
 	        		if(beeId==id)
 	        		{
 	        			return true;
 	        		} 
 	        		else
 	        		{
 	        			findingAnotherBee=true;
 	        		}
         		}
         }
    	 return false;
    }   
	
	/**
	 * 蜜蜂是否看见黄蜂
	 * @return
	 */
	public boolean isSeeHornet()
    {
		 String strHornet = BeeFarming.search(9);
		 String strVision = BeeFarming.search(id);
    	if(strVision.contains("+("))
    	{
    		int index1=strVision.lastIndexOf('(');
			int index2=strVision.lastIndexOf(')');
			String strBee=strVision.substring(index1+1,index2);
			String beeMessage[]=strBee.split(",");
			int beeId=Integer.parseInt(beeMessage[0]);
			if(beeId==9)
				return true;
			else
				return false;
    	}
		else
			return false;
    }
	
	
	public boolean isSeeHoney()
	{
		String strVision = BeeFarming.search(id);
    	if(strVision.contains("+("))
    	{
    		int index1=strVision.lastIndexOf('(');
			int index2=strVision.lastIndexOf(')');
			String strBee=strVision.substring(index1+1,index2);
			String beeMessage[]=strBee.split(",");
			int beeId=Integer.parseInt(beeMessage[0]);
			if(beeId!=9)
				return true;
			else
				return false;
    	}
		else
			return false;
	}
	
	/**
	 * 蜜蜂是否碰边
	 * @param id
	 * @return
	 */
	public boolean isReachBound(int id)
    {
    	String strVision = BeeFarming.search(id);
    	if(strVision.contains("*"))
    		return true;
    	else
    		return false;
    }
    
	/**
	 * 蜜蜂寻找花与避免骚扰黄蜂
	 */
	public void searchFlower()
    {
		double hornetAngle=angle;;
		String strHornet = BeeFarming.search(9);
		String strVision = BeeFarming.search(id);
		
    	if(strHornet.contains("+(")&&findingAnotherBee&&(isSeeHornet()||isSeeHoney()))	
    	{
    		int index1=strHornet.lastIndexOf('(');
    		int index2=strHornet.lastIndexOf(')');
    		String strBee=strHornet.substring(index1+1,index2);
    		String beeMessage[]=strBee.split(",");
    		if(beeMessage.length==3)
    		{
    			hornetAngle=Double.parseDouble(beeMessage[2]);
    		}
    		rotateToAngle=hornetAngle+180;
    	}
    	
    	if(strVision.contains("-("))
    	{		
        		if(strVision.contains("ON"))
        		{
        			rotateToAngle=angle;
        		}
                else
            	{
        			int index1=strVision.indexOf(',');
        			int index2=strVision.indexOf(')');
        			rotateToAngle=Double.parseDouble(strVision.substring(index1+1,index2));
            	}
    	}
    	else
    		rotateToAngle=angle;
	}
        	
	
	/**
	 * 处理黄蜂追击
	 * @param id
	 */
	public void meetHornet(int id)
    {
		boolean flag=false;
		 String strHornet = BeeFarming.search(9);
		 String strVision = BeeFarming.search(id);
    	if(strVision.contains("+("))
    	{
    		int index1=strVision.lastIndexOf('(');
			int index2=strVision.lastIndexOf(')');
			String strBee=strVision.substring(index1+1,index2);
			String beeMessage[]=strBee.split(",");
			int beeId=Integer.parseInt(beeMessage[0]);
			double beeAngle=Double.parseDouble(beeMessage[2]);
			if(beeId==9)
				rotateToAngle=angle+180;		
    	}
    		
    	else
    	{
	    	int index1=strHornet.lastIndexOf('(');
			int index2=strHornet.lastIndexOf(')');
			String strBee=strHornet.substring(index1+1,index2);
			String beeMessage[]=strBee.split(",");
			int beeId=Integer.parseInt(beeMessage[0]);
			double beeA=Double.parseDouble(beeMessage[1]);
			double beeAngle=Double.parseDouble(beeMessage[2]);
			rotateToAngle=beeAngle;
			if (flag) {
				rotateToAngle = beeAngle+45;
				flag = false;
			}
			else {
				rotateToAngle = beeAngle-45;
				flag = true;
			}
				
    	}
    }
	
	
	 public void meetBound(int id)
     {
		 Random ra = new Random();
         angle += ra.nextInt(90);
         rotateToAngle=angle;
     }
	 
	 public void meetBoundHornet(int id)
	 {
		 String strVision = BeeFarming.search(id);
		 Random ra = new Random();
		 if(strVision.contains("*W"))
		 {
			 rotateToAngle=90-ra.nextInt(15);
		 }
		 if(strVision.contains("*E"))
		 {
			 rotateToAngle=270-ra.nextInt(15);
		 }
		 if(strVision.contains("*N"))
		 {
			 rotateToAngle=180-ra.nextInt(15);
		 }
		 if(strVision.contains("*S"))
		 {
			 rotateToAngle=360-ra.nextInt(15);
		 }
	 }
	 
}
    
    	
    	