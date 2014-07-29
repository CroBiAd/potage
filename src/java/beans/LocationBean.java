/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package beans;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

/**
 *
 * @author rad
 */
@ManagedBean(name = "locationBean")
@ViewScoped
public class LocationBean implements Serializable{
    private double cM_original;

    public double getcM_original() {
        return cM_original;
    }

    public void setcM_original(double cM_original) {
        this.cM_original = cM_original;
    }
    
    
}
