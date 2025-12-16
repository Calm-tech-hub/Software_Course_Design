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

	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){
		String strVision = BeeFarming.search(id);
		if(strVision.length()>0) System.out.println(strVision);
		if(strVision.contains("+")==true)//碰到+为首的字符串，代表遇到了HoneyBee
		{
		    int k=strVision.indexOf("+");
            String result=strVision.substring(k+2,strVision.length());
            String a[]=result.split(",");//a[0]为id，a[1]为vector，a[2]为angle
            //进入捕杀状态
            try
            {
                angle=Double.valueOf(a[1]).doubleValue();
                ratoteImage(angle);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();  //输出出错信息
            }

		}
        else//视野内没有HoneyBee,找附近有没有没看到的HoneyBee
        {
            for(int i=0;i<3;i++)
            {
                String strHoneyBee=BeeFarming.search(i);
                if(strHoneyBee.contains("+")==true)
                {
                    int j=strHoneyBee.indexOf("+");
                    String result=strHoneyBee.substring(j+2,strHoneyBee.length());
                    String b=result.substring(0,1);
                    if(Integer.parseInt(b)==9) ratoteImage(180);//有HoneyBee看到Hornet
                }
            }
            if(strVision.contains("*")==true)//碰到*为首的字符串，代表遇到了边
            {
                Random ra = new Random();
                angle += ra.nextInt(90);
                ratoteImage(angle);
            }
            else
            {
                Random ra = new Random();
                angle += ra.nextInt(90);
                angle -= ra.nextInt(90);
                ratoteImage(angle);
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
