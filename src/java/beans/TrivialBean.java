/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package beans;

import java.io.Serializable;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author rad
 */
@ManagedBean(name = "trivialBean")
@RequestScoped
public class TrivialBean implements Serializable {
    
    //move elswhere
    public ArrayList<String> getChromosomes() {
        ArrayList<String> chromosomes = new ArrayList<>();
        String genomes[] = {"A", "B", "D"};
        for (int i = 1; i < 8; i++) {
            for (String g : genomes) {
                String chromosome = i + g;
                chromosomes.add(chromosome);
            }
        }        
        return chromosomes;
    }
    
//    public ArrayList<Double> getValues() {
//        ArrayList<Double> vals = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            vals.add(Math.random());
//        }        
//        return vals;
//    }
}
