package org.reactome.web.fireworks.util.gradient;

import net.auroris.ColorPicker.client.Color;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class TwoColorGradient {

    private Color from;
    private Color to;

    public TwoColorGradient(String hexFrom, String hexTo) throws Exception {
        this.from = new Color();
        this.from.setHex(hexFrom.replace("#", ""));
        this.to = new Color();
        this.to.setHex(hexTo.replace("#", ""));
    }

    public String getColor(double p){
        int r = getValue(this.from.getRed(), this.to.getRed(), p);
        int g = getValue(this.from.getGreen(), this.to.getGreen(), p);
        int b = getValue(this.from.getBlue(), this.to.getBlue(), p);
        Color rtn = new Color();
        try {
            rtn.setRGB(r,g,b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "#" + rtn.getHex();
    }

    public String getColor(double point, double min, double max){
        return getColor(getPercentage(point, min, max));
    }

    public static double getPercentage(double point, double min, double max){
        double length = Math.abs(max - min);
        double delta = Math.abs(point - max);
        return delta / length;
    }

    public static int getValue(int min, int max, double p){
        return (int) Math.round (min * p + max * (1 - p));
    }
}