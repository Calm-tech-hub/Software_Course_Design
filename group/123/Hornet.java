import java.awt.*;
import java.util.*;

public class Hornet extends Bee{
 
	private static final long serialVersionUID = 1L;
	
	private int id;
    private boolean dead = false;
 
    private int honeyBeeDist = 100; 
 
  //���췽������̫��˵��
    public Hornet(int id, int x, int y, double angle, boolean isAlive, Image img){
        super(id, x, y, angle, isAlive, img);
        this.id = id;
    }
     
    /**����Ʒ�ץ�����۷䣬��boolean dead==true���Ʒ���Ը���dead��ֵ�ж��۷�֪��ɱ����
	�����������޸ģ���BeeFarming��killBee�����е��۷䱻�Ʒ�����󽫱�����*/
    public boolean isCatched(){
        dead = true;
        return dead;
    }
    
    /**�˷�������Ҫ��д�ĺ��Ĵ��룬�۷���۵���Ҫ�����ڴ�������*/
    public void search() {
 
    	//strVision��������Ұ���������Ϣ
    	//��Ұ���л���"-("��ͷ��
    	//��Ұ�����۷䣺"+("��ͷ��
    	//��Ұ����ǽ��"*"��ͷ��
        String strVision = BeeFarming.search(id);
        
        //�������*Ϊ�׵��ַ��������������˱�
        if(strVision.indexOf('*') == 0 && strVision.indexOf("+(") == -1) {
     
        	//strVision.indexOf("+(") == -1������Ұ��û���۷�
        	//�������˱�����Ұ��û���۷䣬��任���з���
        	//�˴���һ����Ҫ��������⣺��ô�ûƷ価�������߽�
        	Random ra = new Random();
        	if(strVision.indexOf('N') == 1) angle = ra.nextInt(120) + 30;			
        	else if(strVision.indexOf('S') == 1) angle = ra.nextInt(120) + 210;
        	else if(strVision.indexOf('W') == 1) angle = ra.nextInt(60);
        	else if(strVision.indexOf('E') == 1) angle = ra.nextInt(60) + 180;
        	else ;
        	ratoteImage(angle); 
        	
        }
 
        //������Ұ�����۷�
        if(strVision.indexOf("+(") != -1) {
    
       //  	System.out.println(strVision);
        	this.angle = nextAngleCalculator(strVision); 
        	ratoteImage(angle);        
        	
        }
 
        else honeyBeeDist = 100;
         
        setXYs(0);
     
    }
 
    
    public double nextAngleCalculator(String strVision){
 
    	System.out.println(strVision);
    	
    	//�������صĻƷ���һ��Ӧ�÷���׷��ķ���
    	double nextAngle;
    	
    	//�Ʒ俴���۷�ķ��򣨼��Ʒ����ĵ����۷����ĵ�ʸ���ķ���
    	double seeBeeDir;
    	String seeBeeDirStr;
    	seeBeeDirStr = strVision.substring(strVision.lastIndexOf("+(")+4, strVision.lastIndexOf(','));
    	seeBeeDir = Double.parseDouble(seeBeeDirStr);
    	
    	//�۷������Ұʱ���еķ��򣨼��۷䱾����angle��
    	double beeFlyDir;
    	String beeFlyDirStr;
    	beeFlyDirStr = strVision.substring(strVision.lastIndexOf(',')+1,strVision.lastIndexOf(')'));
    	beeFlyDir = Double.parseDouble(beeFlyDirStr);
    	
    	//�Ʒ���һ��ʱ�̷ɵ��ĵ�
    	int nextHornetX;
    	int nextHornetY;
    	
    	//�۷��ʱ����ڻƷ��λ���Լ��۷���һ��ʱ�̷ɵ��ĵ�
    	int honeyBeeX;
    	int honeyBeeY;
    	int nextHoneyBeeX;
    	int nextHoneyBeeY;
    	
    	honeyBeeX = (int)(honeyBeeDist * Math.cos(Math.toRadians(seeBeeDir)));
    	honeyBeeY = (int)(honeyBeeDist * Math.sin(Math.toRadians(seeBeeDir)));
    	nextHoneyBeeX = (int)(honeyBeeDist * Math.cos(Math.toRadians(seeBeeDir)) + 18 * Math.cos(Math.toRadians(beeFlyDir)));
    	nextHoneyBeeY = (int)(honeyBeeDist * Math.sin(Math.toRadians(seeBeeDir)) + 18 * Math.sin(Math.toRadians(beeFlyDir)));
    	
    	
    	//�ԻƷ�����Ϊԭ�㽨����̬����ϵ�����۷���һ�����е�����ڻƷ�ĽǶ�nextAngle����Ϊ�Ʒ���е���һ������
    	//ע��getVectorDegree��������Ĳ�����int�ͣ�����Լ���������ǿ������ת����
    	nextAngle = BeeFarming.getVectorDegree(0, 0, nextHoneyBeeX, nextHoneyBeeY);
    	
    	nextHornetX =  (int)(18 * Math.cos(Math.toRadians(nextAngle)));
    	nextHornetY =  (int)(18 * Math.sin(Math.toRadians(nextAngle)));
    	
    	honeyBeeDist = (int)(Math.sqrt((Math.pow(nextHoneyBeeX - nextHornetX, 2)) 
    						+ (Math.pow(nextHoneyBeeY - nextHornetY,2))));
    	
    	return nextAngle;
    	
    }
 
}