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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Set of inferred / putative promoters for a given query
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
public class HitsForQuery implements Serializable {

    private ArrayList<Hit> hits;
    private String queryId;

    public HitsForQuery(ArrayList<Hit> hits, String queryId) {
        this.hits = hits;
        this.queryId = queryId;
    }


//    public PromotersForQuery(ArrayList<Promoter> promoters, String queryId) {
//        this.promoters = promoters;
//        this.queryId = queryId;
//    }
    public void addPromoter(Hit p) {
        hits.add(p);
    }

    public ArrayList<Hit> getHits() {
        return hits;
    }
    
    public Hit[] getPromotersArray(double alignedCapidThreshold) {
        Hit[] arr = new Hit[hits.size()];
        for (int j = 0; j < hits.size(); j++) {
            if(hits.get(j).getPercIdOverAlignedQlenDouble()> alignedCapidThreshold)
                arr[j] = hits.get(j);
            else 
                arr[j] = null;
        }
        return arr;
    }
    
    public ArrayList<Hit> getPromotersList(double alignedCapidThreshold) {
        ArrayList<Hit> list = new ArrayList<>(hits.size());
        for (int j = 0; j < hits.size(); j++) {
            if(hits.get(j).getPercIdOverAlignedQlenDouble()> alignedCapidThreshold)
                list.add(hits.get(j));
        }
        return list;
    }

    public void setHits(ArrayList<Hit> hits) {
        this.hits = hits;
    }

    public String getQueryId() {
        return queryId;
    }

    public String getColoredQueryId() {
        int num = hits.size();
        if (num == 0) {
            return formatString(queryId, "red");
        } else {
            return formatString(queryId, hits.get(0).getTraficLightColor());
        }
    }
    
    public String getFormatedQueryId() {
        int num = hits.size();
        String query = queryId;
        if(queryId.length() > 50) {
            query = queryId.substring(0,50).concat("...");
        }
        
//        if (num == 0) {
//            return formatString(query, "red");
//        } else {           
////            return formatString(query, promoters.get(0).getTraficLightColor());
            return query;
//        }
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getNumbPromotersA() {
        int num = hits.size();
        if (num == 0) {
            return formatString("no reasonable hits", "red");
        } else {
            return formatString("" + num, hits.get(0).getTraficLightColor());
        }
    }

    public String getNumbPromotersB() {
        int num = hits.size();
        if (num == 0) {
            return formatString("0", "red");
        } else {
            return formatString("" + num, hits.get(0).getTraficLightColor());
        }
    }

    private String formatString(String s, String color) {
        return "<font color=\"" + color + "\">" + s + "</font>";
    }
}
