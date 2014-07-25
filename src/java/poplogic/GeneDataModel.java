/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poplogic;

import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

public class GeneDataModel extends ListDataModel<Gene> implements SelectableDataModel<Gene>, Serializable {

//    private double cM_corrected_min;
//    private double cM_corrected_max;
//    private double cM_original_min;
//    private double cM_original_max;
//
//    private double cM_corrected_min_user;
//    private double cM_corrected_max_user;
//    private double cM_original_min_user;
//    private double cM_original_max_user;


    
//    private String queryID;
//    public GeneDataModel() {  
//    }  
    
    public GeneDataModel(GeneDataModel originalModel, List<Gene> restrrictedList) {
        super(restrrictedList);
//        cM_corrected_min = originalModel.getcM_corrected_min();
//        cM_corrected_max = originalModel.getcM_corrected_max();
//        cM_original_min = originalModel.getcM_original_min();
//        cM_original_max = originalModel.getcM_original_max();
    }
    
    public GeneDataModel(InputProcessor ip) {
        super(ip.getGenes());

        //min/max cM values for current dataset
//        this.cM_corrected_min = ip.getcM_corrected_min();
//        this.cM_corrected_max = ip.getcM_corrected_max();
//        this.cM_original_min = ip.getcM_original_min();
//        this.cM_original_max = ip.getcM_original_max();

        //initiate user values to min max available
//        cM_corrected_min_user = cM_corrected_min;
//        cM_corrected_max_user = cM_corrected_max;
//        cM_original_min_user = cM_original_min;
//        cM_original_max_user = cM_original_max;
//        System.err.printf("cM_corrected %.2f %.2f\n", cM_corrected_min, cM_corrected_max);
//        System.err.printf("cM_corrected_user %.2f %.2f\n", cM_corrected_min_user, cM_corrected_max_user);
//        System.err.printf("cM_original %.2f %.2f\n", cM_original_min, cM_original_max);
//        System.err.printf("cM_original_user %.2f %.2f\n", cM_original_min_user, cM_original_max_user);
    }

//    public GeneDataModel(List<Gene> data) {  
//        super(data); 
//        
////        queryID = data.get(0).getQueryId();
//    }  
//    private boolean isWithinMinMax(Gene gene) {
//        double cM_corrected = gene.getContig().getcM_corrected();
//        double cM_original = gene.getContig().getcM_original();
//        if (cM_corrected >= cM_corrected_min_user && cM_corrected <= cM_corrected_max_user && cM_original >= cM_original_min_user && cM_original <= cM_original_max_user) {
//            return true;
//        }
//        return false;
//    }

    
    @Override
    public Gene getRowData(String rowKey) {
//        System.err.printf("cM_corrected %.2f %.2f\n", cM_corrected_min, cM_corrected_max);
//        System.err.printf("cM_corrected_user %.2f %.2f\n", cM_corrected_min_user, cM_corrected_max_user);
//        System.err.printf("cM_original %.2f %.2f\n", cM_original_min, cM_original_max);
//        System.err.printf("cM_original_user %.2f %.2f\n", cM_original_min_user, cM_original_max_user);

        List<Gene> genes = (List<Gene>) getWrappedData();
        for (Gene gene : genes) {
//            if (gene.getId().equals(rowKey) && isWithinMinMax(gene)) {
                return gene;
//            }
        }

        return null;
    }

    @Override
    public Object getRowKey(Gene gene) {
        return gene.getGeneId();
//        return promoter.getUniqueIdString();  
    }

//    public Integer getRowIndex(Contig contig) {  
//        List<Contig> contigs = (List<Contig>) getWrappedData();  
//        return contigs.indexOf(contig);
//    }  
    
    public Integer getRowIndex(String key) {  
        List<Gene> contigs = (List<Gene>) getWrappedData();  
        for(int i=0; i<contigs.size(); i++) { 
            Gene gene = contigs.get(i);
            if(gene.getId().equals(key) || gene.getContig().getId().equals(key))  
//            if(promoter.getUniqueIdString().equals(rowKey))  
                return i;  
        }  
        return null;  
    } 
    public boolean isEmpty() {
        return this.getRowCount() < 1;
    }

//    public double getcM_corrected_min() {
//        return cM_corrected_min;
//    }
//
//    public double getcM_corrected_max() {
//        return cM_corrected_max;
//    }
//
//    public double getcM_original_min() {
//        return cM_original_min;
//    }
//
//    public double getcM_original_max() {
//        return cM_original_max;
//    }


    
}
