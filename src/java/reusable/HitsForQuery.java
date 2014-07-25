/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reusable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Set of inferred / putative promoters for a given query
 *
 * @author rad
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
