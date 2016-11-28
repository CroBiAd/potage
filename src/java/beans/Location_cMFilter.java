/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

/**
 *
 * @author rad
 */
public class Location_cMFilter implements Serializable {

    //Following fileds needed for custom filtering by cM values
    private Double cM_min;
    private Double cM_max;
    private Double cM_min_user;
    private Double cM_max_user;
    private String cM_range_user; 
    
        //for contigs without genes - good to have the range of available cM locations for autocomplete
    private final ArrayList<Double> cM_Range;

    public Location_cMFilter() {
        this.cM_Range = new ArrayList<>();
    }

    public void resetFilter() {
        cM_min_user = cM_min;
        cM_max_user = cM_max;
        cM_range_user = cM_min+"-"+cM_max;
    }
    
    public ArrayList<Double> getcM_Range() {
        return cM_Range;
    }

//    public Double[] getcM_range_user() {
//        return cM_range_user;
//    }
    public String getcM_range_user() {
        return cM_range_user;
    }

    public void setcM_range_user(Double min, Double max) {
        this.cM_range_user = min+"-"+max;
    }


    public boolean isFiltered() {
        return !Objects.equals(getcM_min(), getcM_min_user()) || !Objects.equals(getcM_max(), getcM_max_user());
//        return filteredContigs != null;
    }
    
    public boolean filterByCm(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if(filterText == null||filterText.equals("")) {
            return true;
        }         
        if(value == null) {
            return false;
        }
        String[] split = filterText.split("-");
        return ((Comparable) value).compareTo(Double.valueOf(split[0])) >= 0 && ((Comparable) value).compareTo(Double.valueOf(split[1])) <= 0;
//        return ((Comparable) value).compareTo(Double.valueOf(filterText)) >= 0;
    }
    
    
    public Double getcM_min_user() {
        if (cM_min_user == null) {
            cM_min_user = cM_min;
        }
        return cM_min_user;
    }

    public void setcM_min_user(Double cM_min_user) {
        this.cM_min_user = cM_min_user;
        setcM_range_user(cM_min_user, cM_max_user);
    }

    public Double getcM_max_user() {
        if (cM_max_user == null) {
            cM_max_user = cM_max;
        }
        return cM_max_user;
    }

    public void setcM_range_user(String cM_range_user) {
        this.cM_range_user = cM_range_user;
    }

    
    public void setcM_max_user(Double cM_max_user) {
        this.cM_max_user = cM_max_user;
        setcM_range_user(cM_min_user, cM_max_user);
    }

    public Double getcM_min() {
        return cM_min;
    }

    public Double getcM_max() {
        return cM_max;
    }


//    public boolean isWithinUserCoordinates(Double cM_corrected, Double cM_original) {
//        if(cM_corrected == null || cM_original == null) {
//            return false;
//        }
//        return cM_corrected >= getcM_corrected_min_user() && cM_corrected <= getcM_corrected_max_user() && cM_original >= getcM_original_min_user() && cM_original <= getcM_original_max_user();
//    }
    
    public boolean isWithinUserCoordinates(Double cM) {
        if(cM == null) {
            return false;
        }
        return cM >= getcM_min_user() && cM <= getcM_max_user();
    }

    /**
     * This is needed to set the min/max values for the slider which restricts
     * output table to a region of interest
     *
     * @param cM
     */
    public void add_cM_valuesToMinMaxAnd_cMRanges(Double cM) {
        if (cM_min == null) {
            cM_min = cM;
            cM_max = cM;
//            cM_original_min = cM_original;
//            cM_original_max = cM_original;
        } else {
            if (cM < cM_min) {
                cM_min = Math.floor(cM);
            }
            if (cM > cM_max) {
                cM_max = Math.ceil(cM);
            }
//            if (cM_original < cM_original_min) {
//                cM_original_min = Math.floor(cM_original);
//            }
//            if (cM_original > cM_original_max) {
//                cM_original_max = Math.ceil(cM_original);
//            }
        }
        setcM_range_user(cM_min, cM_max);
        
        Double rounded = reusable.CommonMaths.round(cM, 3);
//        Double roundedOriginal = reusable.CommonMaths.round(cM_original, 3);
        if (getcM_Range().isEmpty()) {
            getcM_Range().add(rounded);
//            getcM_originalRange().add(roundedOriginal);
        } else { //risky way of collecting all cM positions (assumes input is sorted)
            Double previous = cM_Range.get(cM_Range.size()-1);
//            Double previousOriginal = cM_originalRange.get(cM_originalRange.size()-1);    
            if (previous != rounded.doubleValue()) {
                getcM_Range().add(rounded);
            }
//            if (previousOriginal != roundedOriginal.doubleValue()) {
//                getcM_originalRange().add(roundedOriginal);
//            }
        }
    }
    
    public ArrayList<Double> rangePrefix(String prefix) {
        if(prefix.equalsIgnoreCase("")) {
            return getcM_Range();
        }
        ArrayList<Double> corrected = new ArrayList<>();
        for (Double value : getcM_Range()) {
            if (value.toString().startsWith(prefix)) {
                corrected.add(value);
            }
        }
        return corrected;
    }

//    public ArrayList<Double> originalRangePrefix(String prefix) {
//         if(prefix.equalsIgnoreCase("")) {
//            return getcM_originalRange();
//        }
//         ArrayList<Double> corrected = new ArrayList<>();
//        for (Double value : getcM_originalRange()) {
//            if (value.toString().startsWith(prefix)) {
//                corrected.add(value);
//            }
//        }
//        return corrected;
//    }

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
