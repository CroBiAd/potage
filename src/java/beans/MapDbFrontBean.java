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
@ManagedBean(name = "mapDbFrontBean", eager = true) //eager i.e. "to be instantiated and placed in the application scope as soon as the application is started and before any request is made"
@ApplicationScoped
public class MapDbFrontBean {

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
    
    
    
    

}
