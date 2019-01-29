package com.xiao.example.project20190122.util;

public class CheckCodeUtil {
    private static final String PREFIX = "$CMM";
    private static final Integer RADIUS = 2048;

    private CheckCodeUtil() {
    }

    /**
     * 根据nema-0183协议字符串生成校验码
     *
     * @param content 字符串内容
     * @return 校验码
     */
    public static String stringToConvert(String content) {
        int sum = 0;
        for (int i = 0; i < content.length(); i++) {
            sum ^= content.charAt(i);
        }
        sum %= 65536;
        String suffix = Integer.toHexString(sum);
        StringBuilder builder = new StringBuilder(PREFIX);
        builder.append(content);
        if (suffix.length() < 2) {
            suffix += "0";
        }
        builder.append("*");
        builder.append(suffix);
        return builder.toString().toUpperCase();
    }

    public static String getCommand(int level, int angle) {
        String x = "";
        String y = "";
        if (level >= 2048) {
            level = 2047;
        }
        if (level <= 100) {
            x = "800";
            y = "800";
        }
        if (level == 2047) {
            if (angle >= 355 || angle <= 5) {
                x = Integer.toHexString(RADIUS + level);
                y = "800";
            } else if (angle >= 85 && angle <= 95) {
                x = "800";
                y = Integer.toHexString(RADIUS + level);
            } else if (angle >= 175 && angle <= 185) {
                x = Integer.toHexString(RADIUS - level - 1);
                y = "800";
            } else if (angle >= 265 && angle <= 275) {
                x = "800";
                y = Integer.toHexString(RADIUS - level - 1);
            } else  {
                double temp_x = RADIUS + Math.cos(angle * (Math.PI / 180)) * RADIUS;
                double temp_y = RADIUS + Math.sin(angle * (Math.PI / 180)) * RADIUS;
                x = Integer.toHexString((int)temp_x);
                y = Integer.toHexString((int)temp_y);
            }
        }
        if (level > 100 && level < 2047) {
            double temp_x = RADIUS + Math.cos(angle * (Math.PI / 180)) * level;
            double temp_y = RADIUS + Math.sin(angle * (Math.PI / 180)) * level;
            x = Integer.toHexString((int)temp_x);
            y = Integer.toHexString((int)temp_y);
        }
        if (x.length() == 1) {
            x = "00" + x;
        }
        if (x.length() == 2) {
            x = "0" + x;
        }
        if (y.length() == 1) {
            y = "00" + y;
        }
        if (y.length() == 2) {
            y = "0" + y;
        }

        StringBuffer prefix = new StringBuffer(PREFIX);
        String content = x + y;
        int sum = 0;
        for (int i = 0; i < content.length(); i++) {
            sum ^= content.charAt(i);
        }
        sum %= 65536;
        String suffix = Integer.toHexString(sum);
        prefix.append(content);
        prefix.append("*");
        if (suffix.length() < 2) {
            suffix = "0" + suffix;
        }
        prefix.append(suffix);
        return prefix.toString().toUpperCase();
    }
}
