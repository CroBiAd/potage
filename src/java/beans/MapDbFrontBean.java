/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.File;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import org.mapdb.DB;
import org.mapdb.DBMaker;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
@ManagedBean(name = "mapDbFrontBean", eager = false) //eager i.e. "to be instantiated and placed in the application scope as soon as the application is started and before any request is made"
@ApplicationScoped
public class MapDbFrontBean {

    private final String CONTIG_2_GENES_MAP_KEY = "contig2Genes";
    private final String CONTIG_2_LOCATION_MAP_KEY = "contig2Location";
    private final String GENE_2_CONTIG_MAP_KEY = "gene2Contig";
    private final String GENE_2_ANNOTATION_KEY = "annotationMips";
    private final String GENE_2_ANNOTATION_RICE_KEY = "annotationRice";
    private final String EXPRESSION_MAP_KEY = "expressionMap";
    private final String EXPRESSION_HEADER_KEY = "expressionHeader";
    private final String WITH_GENES_SFX = "_withGenes";
    private final String NO_GENES_SFX = "_noGenes";
    private final String SETTINGS_MAP_NAME = "settingsMap";
    private final String DB_FILE_NAME = "/var/tomcat/persist/potage_data/potage.db";
    private final DB dbStore;
    
    public MapDbFrontBean() {
        File dbFile = new File(DB_FILE_NAME);
        DBMaker.Maker fileDB = DBMaker.fileDB(dbFile);
        fileDB.closeOnJvmShutdown();
//        fileDB.transactionDisable();
//        fileDB.fileLockDisable(); //allows multiple potage instances to access DB
        fileDB.readOnly();
//        fileDB.
        dbStore = fileDB.make();
    }

    public DB getDbStore() {
        return dbStore;
    }
    
//    public void getChromosome
    
    

}
