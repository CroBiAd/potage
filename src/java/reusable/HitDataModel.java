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