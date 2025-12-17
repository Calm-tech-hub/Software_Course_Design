import java.awt.*;
import java.util.*;

public class Hornet extends Bee {
	private int id;
	int x, y;
	String[] s = null;
	private boolean dead = false;
	private double beeAngle;
	private double beeAngle1;
	private double beeHeading;
	private double beeHeading1;

	public Hornet(int id, int x, int y, double angle, boolean isAlive, Image img) {
		super(id, x, y, angle, isAlive, img);
		this.id = id;
		this.x = x;
		this.y = y;
	}

	public void search() {
		String strVision = BeeFarming.search(id);
		BeeFarming.update(new FlyingStatus(id, x, y, angle + 180, true, 0));
		String[] strs = strVision.split("~");
		if (strVision.length() > 6) {
			for (int i = 0; i < strs.length; i++) {
				if (strs[i].contains("+(")) {
					int index1 = strs[i].lastIndexOf('(');
					int index2 = strs[i].lastIndexOf(')');
					String strBee = strs[i].substring(index1 + 1, index2);
					s = strBee.split(",");
					Integer.parseInt(s[0]);
					beeAngle = Double.parseDouble(s[1]);
					beeHeading = Double.parseDouble(s[2]);
					beeAngle1 = 90 - angle + beeAngle;
					beeHeading1 = 90 - angle + beeHeading;
					if ((x < 80) || (y < 80) || (x > 740) || (y > 540))
						angle = beeAngle1
								+ 60
								* Math.cos(Math.toRadians(90 - beeHeading1
										+ beeAngle1)) - 90 + angle;
					else
						angle = beeAngle1
								+ 30
								* Math.cos(Math.toRadians(90 - beeHeading1
										+ beeAngle1)) - 90 + angle;
				}
			}
			ratoteImage(angle);
			setXYs(0);
		} else {
			checkBound(strVision);
		}

	}

	private boolean checkBound(String valueStr) {
		double b = correctvalue(angle + 180);
		double a = 0;
		double absAngle = 10;
		if (valueStr.contains("*E~")) {
			do {
				Random ra = new Random();
				a = ra.nextDouble() * 180 + 90;
			} while (Math.abs(b - a) < absAngle);

		} else if (valueStr.contains("*S~")) {
			do {
				Random ra = new Random();
				a = ra.nextDouble() * 180 + 180;
			} while (Math.abs(b - a) < absAngle);

		} else if (valueStr.contains("*W~")) {
			do {
				Random ra = new Random();
				a = ra.nextDouble() * 180 - 90;
				if (a < 0)
					a = 360 + a;
			} while (Math.abs(b - a) < absAngle);

		} else if (valueStr.contains("*N~")) {
			do {
				Random ra = new Random();
				a = ra.nextDouble() * 180;
			} while (Math.abs(b - a) < absAngle);
		} else {
			// ratoteImage(angle);
			setXYs(0);
			return false;
		}
		// ratoteToAngle = a;
		ratoteImage(a);
		setXYs(0);
		return true;
	}

	public double correctvalue(double angle1) {
		if (angle1 < 0)
			while (angle1 < 0)
				angle1 += 360;
		if (angle1 >= 360)
			while (angle1 >= 360)
				angle1 -= 360;
		return angle1;
	}

	public boolean isCatched() {
		dead = true;
		return dead;
	}

}




