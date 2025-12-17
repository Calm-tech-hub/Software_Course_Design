 import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.lang.*;
public class Hornet extends Bee{
 
 
    private int id;
    private boolean dead=false;
    private boolean seen=false;
    private static int dir = 1;
    
 
    private int timetotran = 0;
    private int distance_now=100;
 
    public Hornet(int id,int x, int y, double angle,boolean isAlive,Image img){
 
        super(id,x,y,angle,isAlive,img);
        this.id = id;
        
        this.timetotran=0;
     
}
     
    /**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
    public void search(){
        String strVision = BeeFarming.search(id);
       
        if(strVision.indexOf('*')==0)
        {
     
            if(timetotran==-1);
            else if(strVision.indexOf("+(")==-1){
            	Random ra = new Random();
                angle += ra.nextInt(90);
                ratoteImage(angle);     
            }
            else{     	
            	timetotran+=1;     
        		}
                 
        }
        else {
        	if(strVision.indexOf("+(")==-1){
            	Random ra = new Random();
            	angle = angle + ra.nextInt(45) * dir;
                ratoteImage(angle);
                dir = dir*(-1);			//控制左右摆动
                setXYs(0);        
    		}
                	
        	timetotran=0;
        }
 
        if(timetotran >= 20){
 
        	angle += 180;
        	ratoteImage(angle);
        	timetotran = -1;
        }

        if(strVision.indexOf("+(")!=-1)
        {
         
        	if(timetotran != -1)
        	{
        		this.angle=tran_direction(strVision);
        		ratoteImage(angle);
        	}
        }

        if(strVision.indexOf("+(") == -1)
        	distance_now = 100;
       
        
        setXYs(0);
    }
 
 
/**如果黄蜂抓到了蜜蜂，则boolean dead==true，黄蜂可以根据dead的值判断蜜蜂知否被杀死。
    本方法可以修改，在BeeFarming的killBee方法中当蜜蜂被黄蜂消灭后将被调用*/
    public boolean isCatched(){
 
        dead = true;
        return dead;
     
}
       
 
    public double tran_direction(String strVision){

    	double temp_honey = -1;
    	double between_angle = 0;
    	double fly_angle = 0;
    	double angle_tran_direction = 0;
    	int honeybee_posx_next;
    	int honeybee_posy_next;
    	int honeybee_posx_now;
    	int honeybee_posy_now;
    	int hornet_posx_next;
    	int hornet_posy_next;
    	int distance;
    	int distance1;
    	int distance2;
	
    	int hornetflag = strVision.indexOf('+');
    	int hornetflag1 = strVision.indexOf('(',hornetflag+1);
    	int hornetflag2 = strVision.indexOf(',',hornetflag1+1);
    	int hornetflag3 = strVision.indexOf(',',hornetflag2+1);
    	int hornetflag4 = strVision.indexOf(')',hornetflag3+1);
	
    	String idstring = strVision.substring(hornetflag1+1,hornetflag2);		//id
    	String astring = strVision.substring(hornetflag2+1,hornetflag3);		//a
    	String anglestring = strVision.substring(hornetflag3+1,hornetflag4);	//angle
 
    	if(strVision.indexOf("-(")!=-1){
    		int flag = strVision.indexOf('-');
    		int flag1 = strVision.indexOf('(',flag+1);
    		int flag2 = strVision.indexOf(',',flag1+1);
    		int flag3 = strVision.indexOf(')',flag2+1);
	
    		String honeybee_anglestring = strVision.substring(flag2+1,flag3);
    		if(!honeybee_anglestring.equals("ON"))
    			temp_honey = Double.valueOf(honeybee_anglestring);
    	}

    	between_angle = Double.valueOf(astring);
    	fly_angle = Double.valueOf(anglestring);
    	between_angle = between_angle%360;
    	fly_angle = (fly_angle+360)%360;
 
    	honeybee_posx_now = (int)(distance_now*Math.cos(Math.toRadians(between_angle)));
    	honeybee_posy_now = (int)(distance_now*Math.sin(Math.toRadians(between_angle)));
    	distance1 = (int)(Math.pow((0+18*Math.cos(Math.toRadians(fly_angle))+honeybee_posx_now),2)
                            +Math.pow((0+18*Math.sin(Math.toRadians(fly_angle))+honeybee_posx_now),2));
    	distance2 = (int)(Math.pow((0-18*Math.cos(Math.toRadians(fly_angle))+honeybee_posx_now),2)
                            +Math.pow((0-18*Math.sin(Math.toRadians(fly_angle))+honeybee_posx_now),2));
    	honeybee_posx_next = (int)(distance_now*Math.cos(Math.toRadians(between_angle))+18*Math.cos(Math.toRadians(fly_angle)));
    	honeybee_posy_next = (int)(distance_now*Math.sin(Math.toRadians(between_angle))+18*Math.sin(Math.toRadians(fly_angle)));
    	distance = (int)(Math.pow(honeybee_posx_now,2)+Math.pow(honeybee_posy_now,2));

    	if(distance <= Math.pow(50,2))
    	{
    		if(distance1 < distance2 ){
    			angle_tran_direction=between_angle;
 
		}
    		else;
 
    	}  
    	else if(temp_honey == between_angle) 
    		angle_tran_direction = between_angle;
 
    	angle_tran_direction = (BeeFarming.getVectorDegree(0,0,honeybee_posx_next,honeybee_posy_next))%360;


    	hornet_posx_next = (int)(18*Math.cos(Math.toRadians(angle_tran_direction)));
    	hornet_posy_next = (int)(18*Math.sin(Math.toRadians(angle_tran_direction)));
 
    	distance_now = (int)(Math.sqrt((Math.pow(honeybee_posx_next-hornet_posx_next,2))+(Math.pow(honeybee_posy_next-hornet_posy_next,2))));
    	temp_honey = -1;
    	return angle_tran_direction;
 
    }

}