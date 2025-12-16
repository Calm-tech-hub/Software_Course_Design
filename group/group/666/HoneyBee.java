import java.awt.*;
import java.awt.image.*;
import java.lang.invoke.SwitchPoint;
import java.util.*;
import java.util.List;

public class HoneyBee extends Bee {
	private int id;
	private int num = 0;
	private int x; // 获得初始的坐标
	private int y;
	private double angle; // 获得初始的角度用于记录现在的角度 //方向感
	private double time; // 用来获取时间
	private int speed = 18;
	// 下面为三只蜜蜂的状态的值
	public static int FIRST_STATUS = 1; // 1表示寻找花蜜的 2表示危险
	public static int SECOND_STATUS = 1;
	public static int THIRD_STATUS = 1;
	// 用于记录危险的时间
	public static double FIRST_DANGERTIME = 0;
	public static double SECOND_DANGERTIME = 0;
	public static double THIRD_DANGERTIME = 0;
	//
	public boolean searchright = true; // 是 表示向右转 否表示向左转
	public int searchturn = 0; // 表示向转轮回的数目
	//
	public int start = 0; // 表示第几轮开始通常都值为零
	// 在墙外的时候的提示的消息
	public int timer = 15; // 被追击到墙的外面的时候的数据的操作
	public int thetimes = 0;
	public boolean mm = false;

	public HoneyBee(int id, int x, int y, double angle, boolean isAlive,
			Image img) {
		super(id, x, y, angle, isAlive, img);
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = angle;
	}

	/** 此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现 */
	public void search() {
		String str = BeeFarming.search(9);
		if (str.indexOf("+") != -1) {//
			Random ra = new Random();
			DeleteDanger(1);
			DeleteDanger(2);
			DeleteDanger(3);
			String[] strVisionStrings = str.split("~");
			for (int i = 0; i < strVisionStrings.length; i++) {
				String tempsString = strVisionStrings[i];
				if (tempsString.startsWith("+")) {// 存入其他蜜蜂的方向信息
					String[] mString = tempsString.split(","); // 发现其他蜜蜂的方向消息
					String beeId = mString[0]
							.substring(mString[0].indexOf("(") + 1); // 发现蜜蜂的ID号码
					String beeflyAngle = mString[2].substring(0,
							mString[2].indexOf(")")); // 发现蜜蜂的飞行方向
					String seeAngle = mString[1]; // 看到蜜蜂的方向
					InformInDanger(Integer.parseInt(beeId));
					// if ((this.angle - Double.parseDouble(beeflyAngle)) % 360
					// < 90) {
					// angle += 180;
					// }

				}

			}

		}

		thetimes++;
		String strVision = BeeFarming.search(id);
		switch (getStatus(id)) {
		case 1:
			runInNormal(strVision);
			break;
		case 2:
			runInDanger(strVision);
			break;
		}
		// 每次调度到最后的时候的策略调度

		caculatePosition(start);
		setXYs(start);
		start = 0;// 每一轮结束之后都要将自己的初试的位置的值为零
	}

	/**
	 * 计算当前的位置信息
	 * 
	 * @param start
	 */
	private void caculatePosition(int start) {
		this.x += (int) (speed * (9 - start) / 9.0 * Math.cos(Math
				.toRadians(angle)));
		this.y += (int) (speed * (9 - start) / 9.0 * Math.sin(Math
				.toRadians(angle)));
	}

	/*
	 * 正常的情况下的策略调度
	 */
	private void runInNormal(String strVion) {
		//

		if (strVion.isEmpty()) {
			// 表明这个时候，没看到到任何东西，这个时候就进行左右旋转 增大自己的视野
			Random ra = new Random();
			if (searchright) {
				if (searchturn < 5) {
					searchturn++;

				} else {
					angle = angle + ra.nextInt(60);
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

			String[] strVisionStrings = strVion.split("~");
			for (int i = strVisionStrings.length - 1; i >= 0; i--) { // 如果存在其他的地方。表明先看蜜粉，再看花，再看墙
				String tempsString = strVisionStrings[i];
				if (tempsString.startsWith("*")) { // 存入墙的方向信息
					Random ra = new Random();
					angle += ra.nextInt(30) + 15; // h
					ratoteImage(angle);
				} else if (tempsString.startsWith("-")) { // 存入花的方向消息
					String[] mString = tempsString.split(",");

					mString[1] = mString[1].substring(0,
							mString[1].indexOf(")"));
					String honeyleft = mString[0].substring(mString[0]
							.indexOf("(") + 1); // 获得剩余的分泌的数量
					if (mString[1].equals("ON")) {
						start = Integer.parseInt(honeyleft);
						if (start >= 10) { // 表明蜂蜜的量还是大于10
							start = 0;
						}
						ratoteImage(angle);
					} else if (mString[1] != null) {
						double angle = Double.parseDouble(mString[1]);
						ratoteImage(angle);
					}
				} else if (tempsString.startsWith("+")) {// 存入其他蜜蜂的方向信息
					String[] mString = tempsString.split(","); // 发现其他蜜蜂的方向消息
					String beeId = mString[0]
							.substring(mString[0].indexOf("(") + 1); // 发现蜜蜂的ID号码
					String beeflyAngle = mString[2].substring(0,
							mString[2].indexOf(")")); // 发现蜜蜂的飞行方向
					String seeAngle = mString[1]; // 看到蜜蜂的方向
					if (beeId.equals("9")) { // 表示发现 黄蜂
						double beeflyAngle_double = Double
								.parseDouble(beeflyAngle);
						angle += 180;
						InformInDanger(id); // 将自己的置于危险的状态
						ratoteImage(angle);
					} else { // 发现其他的黄蜂，首先检测是否是处于危险的状态，
						if (getStatus(id) == 2) {// 告诉对方自己的
							InformInDanger(Integer.parseInt(beeId));
						}
					}

				}
			}
		}
	}

	/*
	 * 危险的情况下的策略调度
	 */
	private void runInDanger(String strVison) {
		String[] strVisionStrings = strVison.split("~");
		// 取出所看到的数据 存到对应的map之中
		Random ra = new Random();
		for (int i = strVisionStrings.length - 1; i >= 0; i--) {
			String tempsString = strVisionStrings[i];
			if (tempsString.startsWith("*")) { // 存入墙的信息
				// 现在处于危险的状态，或者飞过 赶快逃避
				// if(angle%360<450){
				angle += ra.nextInt(30) + 15;
				// }else if(angle%360>90)

				ratoteImage(angle);
				// 为了避免过了不会来 。设置一个定时器 每一轮都减1，5轮过后，检查周围的环境的状况，安全的情况之下，，进入到桌面的quyu
				timer--;
				if (timer < 0) { // 表明这个时候已经五轮之外了
					// ratoteImage(angle + 180);
					timer = 15;
					DeleteDanger(id); // 解除自己的危险的状态
				}

			} else if (tempsString.startsWith("-")) { // 存入花的消息
				String[] mString = tempsString.split(",");
				mString[1] = mString[1].substring(0, mString[1].indexOf(")"));
				if (mString[1].equals("ON")) {
					ratoteImage(angle);
				} else {
					double flowerangle = Double.parseDouble(mString[1]);
					if (this.angle - flowerangle < 10) { // 为了避免菜花
						angle += 10;
					}
					ratoteImage(angle);
				}
			} else if (tempsString.startsWith("+")) {// 存入其他蜜蜂的信息
				String[] mString = tempsString.split(","); // 发现其他蜜蜂的方向消息
				String beeId = mString[0]
						.substring(mString[0].indexOf("(") + 1); // 发现蜜蜂的ID号码
				String beeflyAngle = mString[2].substring(0,
						mString[2].indexOf(")")); // 发现蜜蜂的飞行方向
				String seeAngle = mString[1]; // 看到蜜蜂的方向
				if (beeId.equals("9")) { // 表示发现 黄蜂
					double beeflyAngle_double = Double
							.parseDouble(beeflyAngle);
					angle += 180;
					InformInDanger(id); // 将自己的置于危险的状态
					ratoteImage(angle);
				}
				if (getStatus(Integer.parseInt(beeId)) == 1) {// 告诉对方自己的
					// InformInDanger(Integer.parseInt(beeId));
				}
			}
		}

	}

	/**
	 * 告诉某只蜜蜂处于危险的状态
	 * 
	 * @param id
	 */
	public static void InformInDanger(int id) {
		switch (id) {
		case 1:
			FIRST_STATUS = 2;
			FIRST_DANGERTIME = System.currentTimeMillis(); // 记录危险的状态的时间
			break;
		case 2:
			SECOND_STATUS = 2;
			SECOND_DANGERTIME = System.currentTimeMillis();
			break;
		case 3:
			THIRD_STATUS = 2;
			THIRD_DANGERTIME = System.currentTimeMillis();
			break;

		default:
			System.out.println("不存在这只蜜蜂");
			break;
		}

	}

	/**
	 * 解除某只蜜蜂的危险的状态
	 * 
	 * @param id
	 */
	public static void DeleteDanger(int id) {
		switch (id) {
		case 1:
			FIRST_STATUS = 1;
			break;
		case 2:
			SECOND_STATUS = 1;
			break;
		case 3:
			THIRD_STATUS = 1;
			break;

		default:
			System.out.println("不存在这只蜜蜂");
			break;
		}

	}

	/**
	 * 获取状态
	 * 
	 * @param id
	 * @return
	 */
	public static int getStatus(int id) {

		switch (id) {
		case 1:
			return FIRST_STATUS;
		case 2:
			return SECOND_STATUS;
		case 3:
			return THIRD_STATUS;

		}
		// 如果都不是 就返回正常的状态
		return 1;

	}
}