/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;
import java.util.Random;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author rad
 */
@ManagedBean(name = "effectsBean")
@RequestScoped
public class EffectsBean implements Serializable{
    private String effectType;

    public EffectsBean() {
        String arr[] = {"fade","slide","blind","clip","drop","explode","fold","puff","scale"};
        Random generator = new Random();
        effectType = arr[generator.nextInt(arr.length)];
    }

    public String getEffectType() {
        return effectType;
    }
 
    
    
}
