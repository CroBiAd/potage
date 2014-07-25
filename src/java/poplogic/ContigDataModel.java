/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poplogic;
import java.io.Serializable;
import java.util.List;  
import javax.faces.model.ListDataModel;  
import org.primefaces.model.SelectableDataModel;  
  
public class ContigDataModel extends ListDataModel<Contig> implements SelectableDataModel<Contig>, Serializable{    
  
//    private String queryID;
    
    public ContigDataModel() {  
    }  
  
    public ContigDataModel(List<Contig> data) {  
        super(data); 
//        queryID = data.get(0).getQueryId();
    }  
      
    @Override  
    public Contig getRowData(String rowKey) {  
        List<Contig> contigs = (List<Contig>) getWrappedData();  
        for(Contig contig : contigs) {  
            if(contig.getId().equals(rowKey))  
//            if(promoter.getUniqueIdString().equals(rowKey))  
                return contig;  
        }  
        return null;  
    }
    
  
    @Override  
    public Object getRowKey(Contig contig) {  
        return contig.getId();  
//        return promoter.getUniqueIdString();  
    }  
    
//    public Integer getRowIndex(Contig contig) {  
//        List<Contig> contigs = (List<Contig>) getWrappedData();  
//        return contigs.indexOf(contig);
//    }  

    public Integer getRowIndex(String rowKey) {  
        List<Contig> contigs = (List<Contig>) getWrappedData();  
        for(int i=0; i<contigs.size(); i++) { 
            Contig contig = contigs.get(i);
            if(contig.getId().equals(rowKey))  
//            if(promoter.getUniqueIdString().equals(rowKey))  
                return i;  
        }  
        return null;  
    } 
    
    
    public boolean isEmpty() {
        return this.getRowCount() < 1; 
    }
    
//    public String getQueryID() {
//        return queryID;
//    }
//
//    public void setQueryID(String queryID) {
//        this.queryID = queryID;
//    }
    
    
}  