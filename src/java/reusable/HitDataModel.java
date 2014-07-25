/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reusable;
import beans.*;
import java.io.Serializable;
import java.util.List;  
import javax.faces.model.ListDataModel;  
import org.primefaces.model.SelectableDataModel;  
  
public class HitDataModel extends ListDataModel<Hit> implements SelectableDataModel<Hit>, Serializable{    
  
//    private String queryID;
    
    public HitDataModel() {  
    }  
  
    public HitDataModel(List<Hit> data) {  
        super(data); 
//        queryID = data.get(0).getQueryId();
    }  
      
    @Override  
    public Hit getRowData(String rowKey) {  
        List<Hit> promotersForQuery = (List<Hit>) getWrappedData();  
        for(Hit promoter : promotersForQuery) {  
            if(promoter.getHitId().equals(rowKey))  
//            if(promoter.getUniqueIdString().equals(rowKey))  
                return promoter;  
        }  
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Hit promoter) {  
        return promoter.getHitId();  
//        return promoter.getUniqueIdString();  
    }  

//    public String getQueryID() {
//        return queryID;
//    }
//
//    public void setQueryID(String queryID) {
//        this.queryID = queryID;
//    }
    
    
}  