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
  
public class HitsForQueryDataModel extends ListDataModel<HitsForQuery> implements SelectableDataModel<HitsForQuery>, Serializable{    
  
  
    public HitsForQueryDataModel(List<HitsForQuery> data) {  
        super(data);  
    }  
      
    @Override  
    public HitsForQuery getRowData(String rowKey) {  
        List<HitsForQuery> hitsForQuery = (List<HitsForQuery>) getWrappedData();  
        for(HitsForQuery hits : hitsForQuery) {  
            if(hits.getQueryId().equals(rowKey))  
                return hits;  
        }  
        return null;  
    }  
  
    @Override  
    public Object getRowKey(HitsForQuery hits) {  
        return hits.getQueryId();  
    }  
    
    public HitsForQuery getRow(int index) {
        List<HitsForQuery> hitsForQuery = (List<HitsForQuery>) getWrappedData();  
        if(hitsForQuery != null && !hitsForQuery.isEmpty())
            return hitsForQuery.get(index);
        return null;
    }
}  