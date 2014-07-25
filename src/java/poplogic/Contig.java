/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poplogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author rad
 */
public class Contig implements Serializable { //, Comparable<Contig> {
    //wheat contig info
    private final String contigId;
    private final String popedAtChromosome;
    private final double cM_corrected;
    private final double cM_original;

//    private long len;
   
    private final ArrayList<String> wheatGeneIdsList;
    private ArrayList<Annotation> annotations;
    private ArrayList<Annotation> annotationsRice;
    private HashMap<String, ArrayList<Double>> genesTissuesFPKMsMap;

    public Contig(String contigId, String popedAtChromosome, double cM_corrected, double cM_original, ArrayList<String> wheatGeneIdsList, HashMap<String, ArrayList<Double>> genesTissuesFPKMsMap) {
        this.contigId = contigId;
        this.popedAtChromosome = popedAtChromosome;
        this.cM_corrected = cM_corrected;
        this.cM_original = cM_original;
        this.wheatGeneIdsList = wheatGeneIdsList;
        this.genesTissuesFPKMsMap = genesTissuesFPKMsMap;
    }

//    public Contig(String contigId, String popedAtChromosome, double cM_corrected, double cM_original, ArrayList<String> wheatGeneIdsList, 
//            ArrayList<Annotation> annotations, ArrayList<Annotation> annotationsRice, HashMap<String, ArrayList<Double>> genesTissuesFPKMsMap) {
//        this.contigId = contigId;
//        this.popedAtChromosome = popedAtChromosome;
//        this.cM_corrected = cM_corrected;
//        this.cM_original = cM_original;
//        this.annotations = annotations;
//        this.annotationsRice = annotationsRice;
//        this.wheatGeneIdsList = wheatGeneIdsList;
////        this.genesTissuesFPKMsMap = genesTissuesFPKMsMap;
//    }

    public String getContigId() {
        return contigId;
    }

    public String getPopedAtChromosome() {
        return popedAtChromosome;
    }

    public double getcM_corrected() {
        return cM_corrected;
    }

    public double getcM_original() {
        return cM_original;
    }

    public ArrayList<String> getWheatGeneIdsList() {
        return wheatGeneIdsList;
    }
  
    public boolean hasGenes() {
        if(wheatGeneIdsList == null || wheatGeneIdsList.isEmpty())
            return false;
        return true;
    }
    
    

    public void setAnnotations(ArrayList<Annotation> annotations) {
        this.annotations = annotations;
    }

    public ArrayList<Annotation> getAnnotations() {
        return annotations;
    }

    public ArrayList<Annotation> getAnnotationsRice() {
        return annotationsRice;
    }

    public void setAnnotationsRice(ArrayList<Annotation> annotationsRice) {
        this.annotationsRice = annotationsRice;
    }
    
    
    
//    @Override
//    public int compareTo(Contig another ) {        
//        return (int) (getFrom()-another.getFrom());        
//    }    

    public String getId() {
        return contigId;
    }

    
    private String getColour(double value) {
        if (value >= 90) {
            return "color: green";
        } else if (value >= 75) {
            return "color: #FF7E00";
        } else {
            return "color: red";
        }
    }
}
