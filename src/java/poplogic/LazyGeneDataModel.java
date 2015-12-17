/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poplogic;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class LazyGeneDataModel extends LazyDataModel<Gene> implements Serializable {

    List<Gene> datasource;
//    private final DB dbStore;
//    private final NavigableSet<Gene> dbStoreFace;

    public LazyGeneDataModel(List<Gene> data) {
////        File dbFile = new File("/var/tomcat/persist/potage_data/tmp.db");
//        File dbFile = new File("tmp.db");
//        if (dbFile.exists()) {
//            dbFile.delete();
//        }
//
//        DBMaker.Maker fileDB = DBMaker.fileDB(dbFile);
////        fileDB.commitFileSyncDisable();
////        fileDB.mmapFileEnableIfSupported();
//        fileDB.deleteFilesAfterClose();
//        fileDB.closeOnJvmShutdown();
//        fileDB.transactionDisable();
//        
//        dbStore = fileDB.make();
//        dbStore.treeSetCreate("treesSet");//.createTreeSet("treesSet");
//        dbStoreFace = dbStore.treeSet("treesSet");
//        
//        
//        for (Gene gene : data) {
//            try{
//                dbStoreFace.add(gene);
//            } catch (IndexOutOfBoundsException e) {
//                System.err.println(e.getLocalizedMessage());
//                System.err.println(e.getCause());
//                System.err.println("Stored: "+dbStoreFace.size());
//                System.err.println("Inmput: "+data.size());
//                System.exit(1);
//            }
//        }
        datasource = data;
    }

    @Override
    public Gene getRowData(String rowKey) {
        for (Gene gene : datasource) {
            if (gene.getGeneId().equals(rowKey)) {
                return gene;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(Gene gene) {
        return gene.getGeneId();
    }

    @Override
    public List<Gene> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<Gene> data = new ArrayList<>();

        //filter
        for (Gene gene : datasource) {
            boolean match = true;

            if (filters != null) {
                for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                    try {
                        String filterProperty = it.next();
                        Object filterValue = filters.get(filterProperty);
                        String fieldValue = String.valueOf(gene.getClass().getField(filterProperty).get(gene));

                        if (filterValue == null || fieldValue.startsWith(filterValue.toString())) {
                            match = true;
                        } else {
                            match = false;
                            break;
                        }
                    } catch (Exception e) {
                        match = false;
                    }
                }
            }

            if (match) {
                data.add(gene);
            }
        }

//        dbStore.close();
        
        //sort
        if (sortField != null) {
//            Collections.sort(data, new LazySorter(sortField, sortOrder));
        }

        //rowCount
        int dataSize = data.size();
        this.setRowCount(dataSize);

        //paginate
        if (dataSize > pageSize) {
            try {
                return data.subList(first, first + pageSize);
            } catch (IndexOutOfBoundsException e) {
                return data.subList(first, first + (dataSize % pageSize));
            }
        } else {
            return data;
        }
    }

//    public Integer getRowIndex(Contig contig) {  
//        List<Contig> contigs = (List<Contig>) getWrappedData();  
//        return contigs.indexOf(contig);
//    }  
//    public Integer getRowIndex(String key) {  
//        List<Gene> contigs = (List<Gene>) getWrappedData();  
//        for(int i=0; i<contigs.size(); i++) { 
//            Gene gene = contigs.get(i);
//            if(gene.getGeneId().equals(key) || gene.getContig().getId().equals(key))  
////            if(promoter.getUniqueIdString().equals(rowKey))  
//                return i;  
//        }  
//        return null;  
//    } 
//    public boolean isEmpty() {
//        return this.getRowCount() < 1;
//    }
//
////    public double getcM_corrected_min() {
////        return cM_corrected_min;
////    }
////
////    public double getcM_corrected_max() {
////        return cM_corrected_max;
////    }
////
////    public double getcM_original_min() {
////        return cM_original_min;
////    }
////
////    public double getcM_original_max() {
////        return cM_original_max;
////    }
}
