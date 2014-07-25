/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poplogic;
import java.io.Serializable;
import java.util.List;  
import javax.faces.model.ListDataModel;  
import org.primefaces.model.SelectableDataModel;  
  
public class AnnotationDataModel extends ListDataModel<Annotation> implements SelectableDataModel<Annotation>, Serializable{    
  
//    private String queryID;
    
    public AnnotationDataModel() {  
    }  
  
    public AnnotationDataModel(List<Annotation> data) {  
        super(data); 
//        queryID = data.get(0).getQueryId();
    }  
      
    @Override  
    public Annotation getRowData(String rowKey) {  
        List<Annotation> annots = (List<Annotation>) getWrappedData();  
        for(Annotation a : annots) {  
            if(a.getGeneId().equals(rowKey))  
//            if(promoter.getUniqueIdString().equals(rowKey))  
                return a;  
        }  
        return null;  
    }
    
  
    @Override  
    public Object getRowKey(Annotation a) {  
        return a.getGeneId();  
//        return promoter.getUniqueIdString();  
    }  
    
//    public Integer getRowIndex(Contig contig) {  
//        List<Contig> contigs = (List<Contig>) getWrappedData();  
//        return contigs.indexOf(contig);
//    }  

    public Integer getRowIndex(String rowKey) {  
        List<Annotation> annots = (List<Annotation>) getWrappedData();  
        for(int i=0; i<annots.size(); i++) { 
            Annotation a = annots.get(i);
            if(a.getGeneId().equals(rowKey))  
//            if(promoter.getUniqueIdString().equals(rowKey))  
                return i;  
        }  
        return null;  
    } 
    
    
//    public String getQueryID() {
//        return queryID;
//    }
//
//    public void setQueryID(String queryID) {
//        this.queryID = queryID;
//    }
    
    
}  