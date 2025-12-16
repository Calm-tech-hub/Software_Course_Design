/** 第六组 ：通信1201班 朱  冉 U201213546
             通信1201班 何元渊 U201213543
             通信1201班 鹿鵬学 U201213564
*/

import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class HoneyBee extends Bee{
	private int id;
	private boolean isDanger=false;
	private int num=0;
	private static int first = 4;
	public HoneyBee(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
	}
	
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
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){
		 //Double jiaodu = BeeFarming.getVectorDegree(210,210,467,79) ; System.out.println("jiaodu "+jiaodu);
	   //if(id == 0 && haha == 0){ratoteImage(340.9908043898101);haha = 1;System.out.println("已做好牺牲准备！！！！");}
	  
		 String strVision = BeeFarming.search(id);
		 String[] str = substringBetween(strVision,"(",")");
		    
		 //System.out.println(strVision);
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
		 	   if(ll.length == 2){
		 	   	key[i][0] = ll[0];
		 	   	key[i][1] = ll[1];
		 	   	key[i][2] = null;
		 	   	}
		 	   	else {
		 	   		key[i] = ll;
		 	   		}
		 	   	}
		 	}  //处理来自BeeFarming.search（）返回的result信息
		 	  //判断是否是遇见墙壁
		    if((strVision.indexOf('*')==0)&&strVision.length()<15){
		    	  Random ra = new Random();
            angle += ra.nextInt(90);
         //   System.out.println("蜜蜂撞墙");
            ratoteImage(angle); 
            }
      
                  
                  
                  
        if(id == first){
        	if(key[0][2] != null || key[1][2] != null || key[2][2] != null ){
               
                   
                    if(str[0] !=null && key[0][2] != null/** && key[0][0].equals("9")*/){
            	         Double a= Double.parseDouble(key[0][1]);
               	   	    
               	   	   a +=180.0;
               	   	    
               	   	   ratoteImage(a);
               	   	       
               	     }
                    if(str[1] !=null && key[1][2] != null/** && key[1][0].equals("9")*/){
            	          Double a= Double.parseDouble(key[1][1]);
               	   	    
               	   	   a +=180.0;
               	   	   ratoteImage(a);
               	   	       
               	     }
                    if(str[2] !=null && key[2][2] != null/** && key[2][0].equals("9")*/){
            	       Double a= Double.parseDouble(key[2][1]);
               	   	   
               	   	   a +=180.0;
               	   	  
               	   	   ratoteImage(a);
               	   	         
               	    
               	     }
               	}
        	
        	}
        	else{   
            //判断是否在吃蜜，若是，蜜蜂环视周围
      
        if( key[0][1] != null && key[0][1].equals("ON") 
        && ((key[0][2] == null && key[1][2] == null && key[2][2] == null) 
           || ((key[0][2] != null && key[0][0].equals("9")) || (key[1][2] != null && key[1][0].equals("9")) || (key[2][2] != null && key[2][0].equals("9") ))) ){
         // 	System.out.println("蜜蜂采蜜");
          	 Random ra = new Random();
             angle += ra.nextInt(180); 
             angle += 270;
             ratoteImage(angle); 
          	}
          	
         //判断见蜂，闪！！！
         if(key[0][2] != null || key[1][2] != null || key[2][2] != null ){
               	 
                    double dan = 0; 
                    if(str[0] !=null && key[0][2] != null){ 
                    	if( key[0][0].equals("9")){
                     
            	         Double a= Double.parseDouble(key[0][1]);
               	   	   Double b= Double.parseDouble(key[0][2]);
               	   	   
               	   	   a +=180.0;
               	   	   if(!((-60 < (a-b)) && ((a-b) < 60.0)) || !((300.0 < (a-b)) && ((a-b) < 420.0))){
               	   	   	System.out.println(id+"号蜜蜂看见黄蜂并且满足修改条件,"+id+":我已做好牺牲准备！！！");
               	   	   	first = id;
               	   	   	}
               	   	   if (((-30.0 < (a-b)) && ((a-b) < 30.0)) || ((330.0 < (a-b)) && ((a-b) < 390.0))){
               	   	   	for(int g = 0;g<3;g++){
               	   	   	    dan += 90.0;
               	   	   	    ratoteImage(dan);}
               	   	  }
               	   	   else{
               	   	   ratoteImage(a);
               	   	        }
               	     }
               	   
               	     else{
                     
            	      /**   Double a= Double.parseDouble(key[0][1]);
               	   	   Double b= Double.parseDouble(key[0][2]);
               	   	   
               	   	   a +=180.0;
               	   	   
               	   	   ratoteImage(a);
               	   	   */
               	   	    Random ra = new Random();
                        angle += ra.nextInt(180); 
                         angle += 270;
                        ratoteImage(angle);      
               	     }
               	   }
                    if(str[1] !=null && key[1][2] != null){
                    	 if( key[1][0].equals("9")){
                    	 
            	         Double a= Double.parseDouble(key[1][1]);
               	   	   Double b= Double.parseDouble(key[1][2]);
               	   	   a +=180.0;
               	   	   if(!((-60 < (a-b)) && ((a-b) < 60.0)) || !((300.0 < (a-b)) && ((a-b) < 420.0))){
               	   	   	System.out.println(id+"号蜜蜂看见黄蜂并且满足修改条件,"+id+":我已做好牺牲准备！！！");
               	   	   	first = id;
               	   	   	}
               	   	   if (((-30.0 < (a-b)) && ((a-b) < 30.0)) || ((330.0 < (a-b)) && ((a-b) < 390.0))){
               	   	   	for(int g = 0;g<3;g++){
               	   	   	    dan += 90.0;
               	   	   	    ratoteImage(dan);}
               	   	   	    }
               	   	   else{
               	   	   ratoteImage(a);
               	   	        }  
               	     }
               	     else{
                    /** 
            	         Double a= Double.parseDouble(key[1][1]);
               	   	   Double b= Double.parseDouble(key[1][2]);
               	   	   
               	   	   a +=180.0;
               	   	   
               	   	   ratoteImage(a);
               	   	   */
               	   	    Random ra = new Random();
                        angle += ra.nextInt(180); 
                        angle += 270;
                        ratoteImage(angle);      
               	     }
               	   }
                    if(str[2] !=null && key[2][2] != null){
                    	 if(key[2][0].equals("9")){
            	          
               	   	   Double a= Double.parseDouble(key[2][1]);
               	   	   Double b= Double.parseDouble(key[2][2]);
               	   	   a +=180.0;
               	   	   if(!((-60 < (a-b)) && ((a-b) < 60.0)) || !((300.0 < (a-b)) && ((a-b) < 420.0))){
               	   	   	System.out.println(id+"号蜜蜂看见黄蜂并且满足修改条件,"+id+":我已做好牺牲准备！！！");
               	   	   	first = id;
               	   	   	}
               	   	   if (((-30.0 < (a-b)) && ((a-b) < 30.0)) || ((330.0 < (a-b)) && ((a-b) < 390.0))){
               	   	   	for(int g = 0;g<3;g++){
               	   	   	    dan += 90.0;
               	   	   	    ratoteImage(dan);}
               	   	   	    }
               	   	   else{
               	   	   ratoteImage(a);
               	   	        }
               	   	      }
               	   	 else{
            	      /**    
               	   	   Double a= Double.parseDouble(key[2][1]);
               	   	   Double b= Double.parseDouble(key[2][2]);
               	   	   a +=180.0;
               	   	    
               	   	   ratoteImage(a);
               	   	   */
               	   	      Random ra = new Random();
                          angle += ra.nextInt(180); 
                          angle += 270;
                          ratoteImage(angle);    
               	    }
               	     }
          	   
          	}
            //看见花，就去  
        
        if(key[0][2] == null && key[0][1] != null && /**(!key[1][0].equals("9") && !key[2][0].equals("9"))&&*/ !key[0][1].equals("ON")){
               	   	   Double tt= Double.parseDouble(key[0][1]);
               	   	//   System.out.println("蜜蜂见花");
               	   	   ratoteImage(tt);
               	     }
        if(key[1][2] == null && key[1][1] != null && !key[1][1].equals("ON")/** &&(!key[0][0].equals("9") && !key[2][0].equals("9"))*/){
               	   	   Double tt= Double.parseDouble(key[1][1]);
               	   	//   System.out.println("蜜蜂见花");
               	   	   ratoteImage(tt);
               	     }
        if(key[2][2] == null && key[2][1] != null && !key[2][1].equals("ON")/** &&(!key[0][0].equals("9") && !key[1][0].equals("9"))*/){
               	   	   Double tt= Double.parseDouble(key[2][1]);
               	   	//   System.out.println("蜜蜂见花");
               	   	   ratoteImage(tt);
               	     }
     }          	   
		setXYs(0);
	}

}