/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reusable;

import java.util.Random;

/**
 *
 * @author rad
 */
public class CommonMaths {
    public static double round(double value, int decimalPoints) {
        double scaleFactor = Math.pow(10, decimalPoints);
        double scaledUp = value*scaleFactor;
        double rounded = Math.round(scaledUp);
        double scaledDown = rounded/scaleFactor;
//        System.out.println("\nAveraged = "+scaledDown);
        return scaledDown;
    }
    
    public static String getRandomString() {
        StringBuilder sb = new StringBuilder("");
        Random generator = new Random();
        for (int i = 0; i < 4; i++) {            
            sb.append((char)(48+generator.nextInt(10)));
            sb.append((char)(65+generator.nextInt(26)));
        }
        return sb.toString();
        
    }
}
