/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author rad
 */
public class Location_cMFilter implements Serializable {

    //Following fileds needed for custom filtering by cM values
    private Double cM_corrected_min;
    private Double cM_corrected_max;
    private Double cM_corrected_min_user;
    private Double cM_corrected_max_user;
    private Double cM_original_min;
    private Double cM_original_max;
    private Double cM_original_min_user;
    private Double cM_original_max_user;
    
        //for contigs without genes - good to have the range of available cM locations for autocomplete
    private final ArrayList<Double> cM_correctedRange;
    private final ArrayList<Double> cM_originalRange;

    public Location_cMFilter() {
        this.cM_correctedRange = new ArrayList<>();
        this.cM_originalRange = new ArrayList<>();
    }

    public void resetFilter() {
        cM_corrected_min_user = cM_corrected_min;
        cM_corrected_max_user = cM_corrected_max;
        cM_original_min_user = cM_original_min;
        cM_original_max_user = cM_original_max;
    }
    
    public ArrayList<Double> getcM_correctedRange() {
        return cM_correctedRange;
    }

    public ArrayList<Double> getcM_originalRange() {
        return cM_originalRange;
    }

    
    
    public Double getcM_corrected_min_user() {
        if (cM_corrected_min_user == null) {
            cM_corrected_min_user = cM_corrected_min;
        }
        return cM_corrected_min_user;
    }

    public void setcM_corrected_min_user(Double cM_corrected_min_user) {
        this.cM_corrected_min_user = cM_corrected_min_user;
    }

    public Double getcM_corrected_max_user() {
        if (cM_corrected_max_user == null) {
            cM_corrected_max_user = cM_corrected_max;
        }
        return cM_corrected_max_user;
    }

    public void setcM_corrected_max_user(Double cM_corrected_max_user) {
        this.cM_corrected_max_user = cM_corrected_max_user;
    }

    public Double getcM_corrected_min() {
        return cM_corrected_min;
    }

    public Double getcM_corrected_max() {
        return cM_corrected_max;
    }

    public Double getcM_original_min_user() {
        if (cM_original_min_user == null) {
            cM_original_min_user = cM_original_min;
        }
        return cM_original_min_user;
    }

    public Double getcM_original_max_user() {
        if (cM_original_max_user == null) {
            cM_original_max_user = cM_original_max;
        }
        return cM_original_max_user;
    }

    public Double getcM_original_min() {
        return cM_original_min;
    }

    public Double getcM_original_max() {
        return cM_original_max;
    }

    public void setcM_original_min_user(Double cM_original_min_user) {
        this.cM_original_min_user = cM_original_min_user;
    }

    public void setcM_original_max_user(Double cM_original_max_user) {
        this.cM_original_max_user = cM_original_max_user;
    }

    public boolean isWithinUserCoordinates(Double cM_corrected, Double cM_original) {
        if(cM_corrected == null || cM_original == null) {
            return false;
        }
        return cM_corrected >= getcM_corrected_min_user() && cM_corrected <= getcM_corrected_max_user() && cM_original >= getcM_original_min_user() && cM_original <= getcM_original_max_user();
    }

    /**
     * This is needed to set the min/max values for the slider which restricts
     * output table to a region of interest
     *
     * @param cM_corrected
     * @param cM_original
     */
    public void add_cM_valuesToMinMaxAnd_cMRanges(Double cM_corrected, Double cM_original) {
        if (cM_corrected_min == null) {
            cM_corrected_min = cM_corrected;
            cM_corrected_max = cM_corrected;
            cM_original_min = cM_original;
            cM_original_max = cM_original;
        } else {
            if (cM_corrected < cM_corrected_min) {
                cM_corrected_min = Math.floor(cM_corrected);
            }
            if (cM_corrected > cM_corrected_max) {
                cM_corrected_max = Math.ceil(cM_corrected);
            }
            if (cM_original < cM_original_min) {
                cM_original_min = Math.floor(cM_original);
            }
            if (cM_original > cM_original_max) {
                cM_original_max = Math.ceil(cM_original);
            }
        }
        
        
        Double roundedCorrected = reusable.CommonMaths.round(cM_corrected, 3);
        Double roundedOriginal = reusable.CommonMaths.round(cM_original, 3);
        if (getcM_correctedRange().isEmpty()) {
            getcM_correctedRange().add(roundedCorrected);
            getcM_originalRange().add(roundedOriginal);
        } else { //risky way of collecting all cM positions (assumes input is sorted)
            Double previousCorrected = cM_correctedRange.get(cM_correctedRange.size()-1);
            Double previousOriginal = cM_originalRange.get(cM_originalRange.size()-1);    
            if (previousCorrected != roundedCorrected.doubleValue()) {
                getcM_correctedRange().add(roundedCorrected);
            }
            if (previousOriginal != roundedOriginal.doubleValue()) {
                getcM_originalRange().add(roundedOriginal);
            }
        }
    }
    
    public ArrayList<Double> correctedRangePrefix(String prefix) {
        if(prefix.equalsIgnoreCase("")) {
            return getcM_correctedRange();
        }
        ArrayList<Double> corrected = new ArrayList<>();
        for (Double value : getcM_correctedRange()) {
            if (value.toString().startsWith(prefix)) {
                corrected.add(value);
            }
        }
        return corrected;
    }

    public ArrayList<Double> originalRangePrefix(String prefix) {
         if(prefix.equalsIgnoreCase("")) {
            return getcM_originalRange();
        }
         ArrayList<Double> corrected = new ArrayList<>();
        for (Double value : getcM_originalRange()) {
            if (value.toString().startsWith(prefix)) {
                corrected.add(value);
            }
        }
        return corrected;
    }

    //    public void filterByCmCorrected() {
//        ArrayList<Gene> genesWithinCoordinates = new ArrayList<>();
//        if (loadedDataModel != null) {
//            for (Gene gene : loadedDataModel) {
//                double cM_corrected = gene.getContig().getcM_corrected();
//                if (cM_corrected >= cM_corrected_min_user && cM_corrected <= cM_corrected_max_user) { // && cM_original >= cM_original_min_user && cM_original <= cM_original_max_user) {
//                    genesWithinCoordinates.add(gene);
//                }
//            }
//            selectedDataModel = new GeneDataModel(loadedDataModel, genesWithinCoordinates);
//            setcM_original_min_user(cM_original_min);
//            setcM_original_max_user(cM_original_max);
//        }
//    }
//
//    public void filterByCmOriginal() {
//        ArrayList<Gene> genesWithinCoordinates = new ArrayList<>();
//        if (loadedDataModel != null) {
//            for (Gene gene : loadedDataModel) {
//                double cM_original = gene.getContig().getcM_original();
//                if (cM_original >= cM_original_min_user && cM_original <= cM_original_max_user) {
//                    genesWithinCoordinates.add(gene);
//                }
//            }
//            selectedDataModel = new GeneDataModel(loadedDataModel, genesWithinCoordinates);
//            setcM_corrected_min_user(cM_corrected_min);
//            setcM_corrected_max_user(cM_corrected_max);
//        }
//    }
}
