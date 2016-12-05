/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reusable;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 *
 * @author rad
 */
public class CommonMaths  implements Serializable{
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
        
    public static int round(double value) {
        return (int) round(value, 0);
    }
    

//    1 Byte = 8 Bit
//1 Kilobyte = 1,024 Bytes
//1 Megabyte = 1,048,576 Bytes
//1 Gigabyte = 1,073,741,824 Bytes
//1 Terabyte = 1,099,511,627,776 Bytes
    public static String getBytesMultiple(long bytes) {
        if (bytes < 1024) {
            return bytes + " Bytes";
        } else if (bytes < 1048576) {
            return NumberFormat.getInstance().format(CommonMaths.round((double) bytes / 1024, 2)) + " kB";
        } else if (bytes < 1073741824) {
            return NumberFormat.getInstance().format(CommonMaths.round((double) bytes / 1048576, 2)) + " MB";
        } else {
            return NumberFormat.getInstance().format(CommonMaths.round((double) bytes / 1073741824, 2)) + " GB";
        }
    }

    public static String getScientific(Object number) {
        DecimalFormat df = new DecimalFormat("0.00E00");
        return df.format(number).toLowerCase();
    }
    
}
