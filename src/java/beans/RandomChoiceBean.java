/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;
import java.util.Random;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

/**
 *
 * @author rad
 */
@ManagedBean(name = "randomBean")
@ViewScoped
public class RandomChoiceBean  implements Serializable{
    Random generator;

    public RandomChoiceBean() {        
        generator = new Random();
    }

    public Integer getRandom(int max) {
        return generator.nextInt(max);
    }
 
    public String getRandomFilename() {
        StringBuilder sb = new StringBuilder("");
        
//        for (int j = 0; j < 10000; j++) {
//            System.out.print((char)(48+generator.nextInt(10)));
//            System.out.print((char)(65+generator.nextInt(26)));
//            if(j % 50 == 0)
//                System.out.println();
//                
//        }
        for (int i = 0; i < 4; i++) {            
            sb.append((char)(48+generator.nextInt(10)));
            sb.append((char)(65+generator.nextInt(26)));
        }
        return sb.toString();
        
    }
    
}
