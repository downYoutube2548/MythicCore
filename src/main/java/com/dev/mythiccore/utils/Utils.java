package com.dev.mythiccore.utils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String colorize(String s) {
        if (s == null || s.equals(""))
            return "";
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher match = pattern.matcher(s);
        while (match.find()) {
            String hexColor = s.substring(match.start(), match.end());
            s = s.replace(hexColor, net.md_5.bungee.api.ChatColor.of(hexColor).toString());
            match = pattern.matcher(s);
        }

        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String[] splitTextAndNumber(String input) {
        String regex = "(?<=(\\d([.]\\d+)?))(?=[A-Za-z]+)";
        return input.split(regex);
    }

    public static String Format(Double input) {
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(input);
    }

    public static float getMantissa(float number, int exponent) {
        return number / (float) Math.pow(10, exponent);
    }

    public static int getExponent(double number) {
        return (int) Math.floor(Math.log10(Math.abs(number)));
    }
}
