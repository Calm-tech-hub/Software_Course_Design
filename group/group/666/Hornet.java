import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.lang.*;

public class Hornet extends Bee {

	private int id;
	private boolean dead = false;
	private boolean seen = false;
	//
	public boolean searchright = true; // 是 表示向右转 否表示向左转
	public int searchturn = 0; // 表示向转轮回的数目

	private int ratote = 0;
	private int distance_now = 100;

	public Hornet(int id, int x, int y, double angle, boolean isAlive, Image img) {
		super(id, x, y, angle, isAlive, img);
		this.id = id;
		this.ratote = 0;
	}

	/** 此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现 */
	public void search() {

		String strVision = BeeFarming.search(id);
	  
		// 如果碰到*为首的字符串，代表遇到了边，这里是随机顺时针旋转90度以内的角度
		if (strVision.isEmpty()) {
			// 表明这个时候，没看到到任何东西，这个时候就进行左右旋转 增大自己的视野
			Random ra = new Random();
			if (searchright) {
				if (searchturn < 3) {
					searchturn++;

				} else {
					angle = angle + ra.nextInt(90);
					searchright = false;
					searchturn = 0; // 清零的操作
				}

			} else {
				if (searchturn < 5) {
					searchturn++;

				} else {
					angle = angle - ra.nextInt(60);
					searchright = true;
					searchturn = 0; // 清零的操作
				}

			}
			ratoteImage(angle);

		} else {

			if (strVision.indexOf('*') == 0) {
				if (ratote == -1)
					angle = angle;
				else if (strVision.indexOf("+(") == -1) {
					if (strVision.indexOf("*N") != -1) {
						angle = 90;
					} else if (strVision.indexOf("*S") != -1) {
						angle = 270;
					} else if (strVision.indexOf("*E") != -1) {
						angle = 180;
					} else {
						angle = 0;
					}
				} else {
					ratote += 1;
				}
				ratoteImage(angle);
			} else
				ratote = 0;

			if (ratote >= 20) {
				angle = angle + 180;
				ratoteImage(angle);
				ratote = -1;
			}
			if (strVision.indexOf("+(") != -1) {
				if (ratote != -1) { // 计算追击的侧向的调度
					this.angle = tracert(strVision);
					ratoteImage(angle);
				}
			} else {
				distance_now = 100;// 重新计算当前的视觉距离
			}

		}

		setXYs(0);
	}

	/**
	 * 如果黄蜂抓到了蜜蜂，则boolean dead==true，黄蜂可以根据dead的值判断蜜蜂知否被杀死。
	 * 本方法可以修改，在BeeFarming的killBee方法中当蜜蜂被黄蜂消灭后将被调用
	 */
	public boolean isCatched() {
		dead = true;
		return dead;
	}

	/**
	 * 根据视觉的距， 计算出所要转的调度
	 * 
	 * @param strVision
	 * @return
	 */
	public double tracert(String strVision) {
		double honey = -1;
		// 储存所看到的角度和飞行的角度
		double seeAngle_double = 0;
		double flyAngle_double = 0;
		String seeAngle_double_str = null;
		String seeAngle = null;
		String flyAngle = null;
		String seeAngle_double_str_honey = null;
		String seeAngle_double_str_honey_2 = null;
		double angle_tracert = 0;
		int honeybee_posx_next;
		int honeybee_posy_next;
		int honeybee_posx_now;
		int honeybee_posy_now;
		int hornet_posx_next;
		int hornet_posy_next;
		int distance_honeybee;
		int distance1_honeybee;
		int distance2_honeybee;

		/**
		 * 表示也看到花的信息
		 */
		if (strVision.indexOf("-(") != -1) {
			seeAngle_double_str_honey = strVision.substring(strVision
					.indexOf("-("));
			seeAngle_double_str_honey_2 = seeAngle_double_str_honey.substring(
					seeAngle_double_str_honey.indexOf(',') + 1,
					seeAngle_double_str_honey.indexOf(')'));
			if(!seeAngle_double_str_honey_2.equals("ON"))
 {
				honey = Double.valueOf(seeAngle_double_str_honey_2);}
		}
		seeAngle_double_str = strVision.substring(strVision.indexOf("+(") + 4);
		seeAngle = seeAngle_double_str.substring(0,
				seeAngle_double_str.indexOf(','));
		flyAngle = seeAngle_double_str.substring(
				seeAngle_double_str.indexOf(',') + 1,
				seeAngle_double_str.indexOf(')'));
		seeAngle_double = Double.valueOf(seeAngle);
		flyAngle_double = Double.valueOf(flyAngle);
		seeAngle_double = seeAngle_double % 360;
		flyAngle_double = (flyAngle_double + 360) % 360;

		honeybee_posx_now = (int) (distance_now * Math.cos(Math
				.toRadians(seeAngle_double)));
		honeybee_posy_now = (int) (distance_now * Math.sin(Math
				.toRadians(seeAngle_double)));
		distance1_honeybee = (int) (Math
				.pow((0 + 18 * Math.cos(Math.toRadians(flyAngle_double)) + honeybee_posx_now),
						2) + Math
				.pow((0 + 18 * Math.sin(Math.toRadians(flyAngle_double)) + honeybee_posx_now),
						2));
		distance2_honeybee = (int) (Math
				.pow((0 - 18 * Math.cos(Math.toRadians(flyAngle_double)) + honeybee_posx_now),
						2) + Math
				.pow((0 - 18 * Math.sin(Math.toRadians(flyAngle_double)) + honeybee_posx_now),
						2));
		honeybee_posx_next = (int) (distance_now
				* Math.cos(Math.toRadians(seeAngle_double)) + 18 * Math
				.cos(Math.toRadians(flyAngle_double)));
		honeybee_posy_next = (int) (distance_now
				* Math.sin(Math.toRadians(seeAngle_double)) + 18 * Math
				.sin(Math.toRadians(flyAngle_double)));

		distance_honeybee = (int) (Math.pow(honeybee_posx_now, 2) + Math.pow(
				honeybee_posy_now, 2));

		if (distance_honeybee <= Math.pow(50, 2)) {
			if (distance1_honeybee < distance2_honeybee) {
				angle_tracert = seeAngle_double;
			} else
				;
		} else if (honey == seeAngle_double)
			angle_tracert = seeAngle_double;

		angle_tracert = (BeeFarming.getVectorDegree(0, 0, honeybee_posx_next,
				honeybee_posy_next)) % 360;

		hornet_posx_next = (int) (18 * Math.cos(Math.toRadians(angle_tracert)));
		hornet_posy_next = (int) (18 * Math.sin(Math.toRadians(angle_tracert)));

		distance_now = (int) (Math.sqrt((Math.pow(honeybee_posx_next
				- hornet_posx_next, 2))
				+ (Math.pow(honeybee_posy_next - hornet_posy_next, 2))));

		return angle_tracert;
	}
}
