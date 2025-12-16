import java.awt.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HoneyBee extends Bee {
    private int id;
    private double ratoteToAngle;
    private int outBoundTime = 0;

    public HoneyBee(int id, int x, int y, double angle, boolean isAlive, Image img) {
        super(id, x, y, angle, isAlive, img);
        this.id = id;
        ratoteToAngle = angle;
    }

    public void search() {

        String valueStr = BeeFarming.search(id);

        boolean isBeCatching = checkHornet();
        if (!isBeCatching) {
            boolean isSeeingFlower = checkFlower(valueStr);
            if (!isSeeingFlower) {
                boolean isOutofBound = checkBound(valueStr);
            }
        } else {
//            boolean isOutofBound = checkBound(valueStr);

        }

        if (ratoteToAngle != angle) {
            ratoteImage(ratoteToAngle);
        }

        setXYs(0);
    }

    boolean flag = false;
    int runAngle = 240;
    int runTime = 0;
    private boolean checkHornet() {
        String valueStr = BeeFarming.search(9);
        String regexp = "(?<=\\+\\()(.*?)(?=\\))";
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(valueStr);

        if (valueStr.contains("*E~") || valueStr.contains("*N~") || valueStr.contains("*W~") || valueStr.contains("*S~")) {
            outBoundTime++;
        } else {
            outBoundTime = 0;
        }


        while (m.find()) {
            System.out.println(m.group(0));
            String[] msg = m.group(1).split(",");
            int beeId = Integer.parseInt(msg[0]);
            if (beeId == id) {
                ratoteToAngle = Double.parseDouble(msg[1]);
//                ratoteToAngle = correctvalue(ratoteToAngle+230);

                if(flag){
                    ratoteToAngle = correctvalue(ratoteToAngle+15);
                    flag = false;
                }else {
                    ratoteToAngle = correctvalue(ratoteToAngle-15);
                    flag = true;
                }


                if (valueStr.contains("*E~")) {
                    ratoteToAngle = correctvalue(ratoteToAngle+runAngle);
                } else if (valueStr.contains("*S~")) {
                    ratoteToAngle = correctvalue(ratoteToAngle+runAngle);
                } else if (valueStr.contains("*W~")) {
                    ratoteToAngle = correctvalue(ratoteToAngle+runAngle);
                } else if (valueStr.contains("*N~")) {
                    ratoteToAngle = correctvalue(ratoteToAngle+runAngle);
                }

//                if (outBoundTime >= 1) {
//                    for (int i = 0; i < 8; i++)
//                        flying1(0);
//                }
                return true;
            }
        }

//        if (outBoundTime > 1) {
//            for (int i = 0; i < 15; i++)
//                flying1(0);
//        }
        return false;
    }

    private boolean checkBound(String valueStr) {
        double b = correctvalue(angle + 180);
        double a = 0;
        double absAngle = 12;
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
                if (a < 0) a = 360 + a;
            } while (Math.abs(b - a) < absAngle);

        } else if (valueStr.contains("*N~")) {
            do {
                Random ra = new Random();
                a = ra.nextDouble() * 180;
            } while (Math.abs(b - a) < absAngle);
        } else {
            return false;
        }
        ratoteToAngle = a;
        return true;
    }

    private boolean checkFlower(String valueStr) {
        int flowerNum = 0;
        String regexp = "(?<=-\\()(.*?)(?=\\))";
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(valueStr);
        int flowerHoney = 0;
        double flowerAngle = 0;
        boolean isOnflower = false;

        while (m.find()) {
            String[] msg = m.group(1).split(",");

            if (msg[1].equals("ON")) {
                isOnflower = true;
                continue;
            }

            if (flowerHoney < Integer.parseInt(msg[0])) {
                flowerHoney = Integer.parseInt(msg[0]);
                flowerAngle = correctvalue(Double.parseDouble(msg[1]));
            }

            flowerNum++;
        }

        if (flowerNum > 0) {
            System.out.println(flowerNum);
            ratoteToAngle = flowerAngle;
            return true;
        } else if (isOnflower) {
            ratoteToAngle = correctvalue(angle + 180);
        }

        return false;
    }


    public double correctvalue(double angle1) {
        if (angle1 < 0)
            while (angle1 < 0) angle1 += 360;
        if (angle1 >= 360)
            while (angle1 >= 360) angle1 -= 360;
        return angle1;
    }
}