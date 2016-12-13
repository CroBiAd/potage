/* 
 * Copyright 2016 University of Adelaide.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reusable;
import beans.*;
import java.io.Serializable;
import java.util.List;  
import javax.faces.model.ListDataModel;  
import org.primefaces.model.SelectableDataModel;  
  
/**
 * 
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
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