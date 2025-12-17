import java.awt.*;
import java.awt.image.*;
import java.util.*;
public class Hornet extends Bee{
	private int id;
	private boolean dead=false;
	private boolean seen=false;
	private static FlyingStatus[] status = new FlyingStatus[10];//每只蜜蜂当前飞行状态数据
		private final static int[] RANGE = {50,50,50,50,50,50,50,50,50,100};
	public Hornet(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
	}
	  
	  //从strVision中取出有效数据
	   public static String[] substringBetween(String str, String open, String close)
  {
  	int i=0,k=0,start,end;
  	String[] strs = new String[10];
  	String op = open;
  	String cl = close;
  	if ((str == null) || (open == null) || (close == null)) {
      return null;
       }
  	for(i=0;((str!=null)||(str.length()==1))&&(str.indexOf(op)!=-1)&&(str.indexOf(cl)!=-1);i++){
        start = str.indexOf(op);
        end = str.indexOf(cl, start + op.length());
        strs[i] = str.substring(start + op.length(), end);
        str = str.substring(end + 1);
   }
    return strs;
 }
 
    //归一化观测到的蜜蜂的角度
    public  double finalAngle (double x1,double x2)
    {       double x3,x4,x5;
            x3=90-x2+x1;
            x4=90-x1+x2;    
           while(x3<0)
           {
            x3=x3+360;
           }
            while(x3>360)
           {
            x3=x3-360;
           }      
            while(x4<0)
           {
            x4=x4+360;
           }
            while(x4>360)
           {
            x4=x4-360; 
           }
           while(x2>360)
           {
            x2=x2-360;
           }
           x5=x3+60*Math.cos(Math.toRadians(90-x4+x3))-90+x2;
           return x5;
    }

	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){
     boolean seen1=false;
     boolean seen2=false;
     boolean seen3=false;
		 String strVision = BeeFarming.search(id);
		 String[] str = substringBetween(strVision,"(",")");
		
		 int k=0;
		 String[][] key = new String[10][3] ;
		 if(str[0] == null && str[1] == null &&str[2] == null){
		 	 for (int i = 0; i < 10; i++)
       {
          for (int j = 0; j < 3; j++)
           {
              key[i][j]= null;
           }
       }
		 	}
		 for(int i = 0;i<10/**str[i] != null*/;i++){
		 	 if(str[i] == null){
		 	 	for(int n=0;n<3;n++){
		 	 		key[i][n] = null;
		 	 	   	}
		 	 		}
		 	 		else{
		 	   String[] ll  = str[i].split(",");
		 	   if(ll.length == 2){                //蜜蜂遇到花。蜜蜂在花上
		 	   	key[i][0] = ll[0];
		 	   	key[i][1] = ll[1];
		 	   	key[i][2] = null; 
		 	   	}
		 	   	else {
		 	   		key[i] = ll;
		 	   		}
		 	   	}
		 	}  
      /////根据所得数据判断黄蜂是否是遇见蜜蜂，若是，开始捕捉
     if(key[0][2] != null || key[1][2] != null || key[2][2] != null ){
          //System.out.println("遇见蜜蜂，捉捕ing");
          if(key[0][2] != null){
                    seen1=true;
                    if((seen2!=true)&&(seen3!=true)){
                    int num = Integer.parseInt(key[0][0]);                                   //取出id
             	   	  Double tt= Double.parseDouble(key[0][1]);
                    Double kk= Double.parseDouble(key[0][2]);
                    Double ff=finalAngle(tt,kk);
             	   	  //System.out.println(num+"号小蜜蜂，莫跑！！！↖(^ω^)↗");
             	   	  ratoteImage(tt);
                   }
          }
          else if(key[1][2] != null){
                    seen2=true;
                    if((seen1!=true)&&(seen3!=true)){
             	   	  Double tt= Double.parseDouble(key[1][1]);
                    Double kk= Double.parseDouble(key[1][2]);
             	   	  int num = Integer.parseInt(key[1][0]);
                    Double ff=finalAngle(tt,kk);
             	   	  //System.out.println(num+"号小蜜蜂，莫跑！！！↖(^ω^)↗");
             	   	  ratoteImage(tt);
                    }
          }
          else if(key[2][2] != null){
                    seen3=true;
                    if((seen2!=true)&&(seen1!=true)){
             	   	  int num = Integer.parseInt(key[2][0]);
             	   	  //System.out.println(num+"号小蜜蜂，莫跑！！！↖(^ω^)↗");
             	   	  Double tt= Double.parseDouble(key[2][1]);
                    Double kk= Double.parseDouble(key[2][2]);
                    Double ff=finalAngle(tt,kk);
             	   	  ratoteImage(tt);
                }
          }
      }
             	   ///// 判断是否是遇见墙壁，若是，随机以一个角度闪开
	    else if((strVision.indexOf('*')==0)&&strVision.length()<15){
	    	              Random ra = new Random();
                      angle += ra.nextInt(90);
                      //System.out.println("黄蜂撞墙");
                      angle += 180;
                      ratoteImage(angle);
      }
      //黄蜂在花上的行动 
      else if((key[0][1] != null && key[0][1].equals("ON"))||(key[1][1] != null && key[1][1].equals("ON"))||(key[2][1] != null && key[2][1].equals("ON"))){
            /** Random ra = new Random();
             angle += ra.nextInt(180);
             ratoteImage(angle);**/ 
      }
      //黄蜂见到花的行动
	    /**else if ((seen1!=true)&&(seen2!=true)&&(seen3!=true)) {                   //且黄蜂不在追击蜜蜂时
          if(key[0][2] == null && key[0][1] != null){
                         Double tt= Double.parseDouble(key[0][1]);
                         //System.out.println("蜜蜂见花");
                         ratoteImage(tt);
          }
          else if(key[1][2] == null && key[1][1] != null ){
                         Double tt= Double.parseDouble(key[1][1]);
                         //System.out.println("蜜蜂见花");
                         ratoteImage(tt);
          }
          else if(key[2][2] == null && key[2][1] != null){
                         Double tt= Double.parseDouble(key[2][1]);
                         //System.out.println("蜜蜂见花");
                         ratoteImage(tt);
          }
    } 
    **/
		setXYs(0);	
	}
	/**如果黄蜂抓到了蜜蜂，则boolean dead==true，黄蜂可以根据dead的值判断蜜蜂知否被杀死。
	本方法可以修改，在BeeFarming的killBee方法中当蜜蜂被黄蜂消灭后将被调用*/
	   public boolean isCatched(){                                                                                                                                     
       dead = true;                  
       return dead;                  
   }   
 }
                   