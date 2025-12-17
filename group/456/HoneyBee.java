import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class HoneyBee extends Bee{
	private int id;
	private boolean isDanger=false;
	private int num=0;
	public HoneyBee(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
	}

	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){
		String strVision = BeeFarming.search(id);
		if(strVision.length()>0)System.out.println(strVision);
		if(strVision.contains("*")==true)//碰到*为首的字符串，代表遇到了边
        {
            Random ra = new Random();
            angle += ra.nextInt(90);
            ratoteImage(angle);
        }
		if(strVision.contains("+")==true)//碰到+为首的字符串，代表遇到了Bee
		{
		    int i=strVision.indexOf("+");
            String result=strVision.substring(i+2,strVision.length());
            String a[]=result.split(",");//a[0]为id，a[1]为vector，a[2]为angle
            if(a[0].equals("9")==true)//看到Hornet
            {
                //蜜蜂远离
                try
				{
                    Double angle=Double.parseDouble(a[1]);
                    ratoteImage(angle+180);
			    }
			    catch(Exception ex)
			    {
			        ex.printStackTrace();  //输出出错信息
			    }
            }
            else//看到的是HoneyBee
            {
                if(strVision.indexOf("-")==0)//找花
                {
                    result=strVision.substring(2,strVision.length()-5);
                    String b[]=result.split(",");//b[0]为volumn，b[1]为ON或vector
                    if(b[1].equals("ON")==false)
                    {
                        try
                        {
                        angle=Double.valueOf(b[1]).doubleValue();
                        ratoteImage(angle);
                        }
                        catch(Exception ex)
                        {
                            ex.printStackTrace();  //输出出错信息
                        }
                    }
                }
            }
		}
		else//没有看到任何Bee
        {
            //检查是否正在被Hornet追
            String strHornet=BeeFarming.search(9);
            if(strHornet.contains("+")==true)//Hornet正在追HoneyBee
            {
                int j=strHornet.indexOf("+");
                String result=strHornet.substring(j+2,strHornet.length());
                String c=result.substring(0,1);//c[0]为id，c[1]为vector，c[2]为angle
                if(Integer.parseInt(c)==id)//Hornet正在追自己
                {
                    Random ra = new Random();
                    angle += ra.nextInt(90);
                    angle -= ra.nextInt(90);
                    ratoteImage(angle);//逃跑策略
                }
                else//Hornet在追其他HoneyBee
                {
                    if(strVision.indexOf("-")==0)//找花
                    {
                        result=strVision.substring(2,strVision.length()-2);
                        String d[]=result.split(",");//d[0]为volumn，d[1]为ON或vector
                        if(d[1].contains("-")==true) d[1]=d[1].substring(0,d[1].indexOf("-")-2);
                        if(d[1].equals("ON")==false)
                        {
                            try
                            {
                                angle=Double.valueOf(d[1]).doubleValue();
                                ratoteImage(angle);
                            }
                            catch(Exception ex)
                            {
                                ex.printStackTrace();  //输出出错信息
                            }
                        }
                    }
                }
            }
            else//Hornet没看到HoneyBee
            {
                if(strVision.contains("-")==true)//找花
                {
                    String result=strVision.substring(strVision.indexOf("-")+2,strVision.length()-2);
                    String d[]=result.split(",");//d[0]为volumn，d[1]为ON或vector
                    if(d[1].contains("-")==true) d[1]=d[1].substring(0,d[1].indexOf("-")-2);
                    if(d[1].equals("ON")==false)
                    {
                        try
                        {
                            angle=Double.valueOf(d[1]).doubleValue();
                            ratoteImage(angle);
                        }
                        catch(Exception ex)
                        {
                            ex.printStackTrace();  //输出出错信息
                        }
                    }
                }
            }
        }
        setXYs(0);
	}
}
