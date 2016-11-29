/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import poplogic.Contig;

/**
 *
 * @author rad
 */
@ManagedBean(name = "appDataBean")
@ApplicationScoped
public class AppDataBean {

    private final String POPSEQ = "/var/tomcat/persist/potage_data/IWGSC_CSS_POPSEQ_v2.tsv";

//    HashMap<String, Contig> popSeqContigsMap;
    HashMap<String, HashMap<Double, ArrayList<String>>> popSeqChromosomeMap;
    HashMap<String, ArrayList<Contig>> popSeqChromosomeMap1;
    HashMap<String, Location_cMFilter> chrToLocationFilter;

    public AppDataBean() {
        readPopSeq();
    }   
    
    
    
    private void readPopSeq() {
//        popSeqContigsMap = new HashMap<>();
        popSeqChromosomeMap = new HashMap<>();
        popSeqChromosomeMap1 = new HashMap<>();
        chrToLocationFilter = new HashMap<>();
        BufferedReader myData = null;
        try {
            myData = new BufferedReader(new FileReader(POPSEQ));
            Pattern p = Pattern.compile("\t");
            String inputLine; // = myData.readLine(); //SKIPPING HEADER ROW            
            while ((inputLine = myData.readLine()) != null) {
                if(inputLine.startsWith("contig")) 
                    continue;
                String[] toks = p.split(inputLine);
                Double cM = Double.parseDouble(toks[2]);
                //POPULTAE CONTIG TO POSITION 
//                ChromosomeBin chrBin = new ChromosomeBin(toks[1], cM);
//                popSeqContigsMap.put(toks[0], chrBin);

//POPULATE CHROMOSOME -> BIN -> CONTIG MAP
                HashMap<Double, ArrayList<String>> chrToBins = popSeqChromosomeMap.get(toks[1]);
                if (chrToBins == null) {
                    chrToBins = new HashMap<>();
                }
                ArrayList<String> bin = chrToBins.get(cM);
                if (bin == null) {
                    bin = new ArrayList<>();
                }
                bin.add(toks[0]);
                chrToBins.put(cM, bin);
                popSeqChromosomeMap.put(toks[1], chrToBins);
                
                

//BACKWARD COMPATIBLE STORAGE OF CONTIGS - TO BE REMOVED AFTER UPGRADE OF OTHER CODE
//                ArrayList<Contig> contigs = popSeqChromosomeMap1.getOrDefault(toks[1],new ArrayList<Contig>());
                ArrayList<Contig> contigs = popSeqChromosomeMap1.get(toks[1]);
                if(contigs == null) {
                    contigs = new ArrayList<>();
                }
                contigs.add(new Contig(toks[0], cM));
                popSeqChromosomeMap1.put(toks[1], contigs);
                

//STORE cM VALUSES FOR FILTERS
                Location_cMFilter cMFilter = chrToLocationFilter.get(toks[1]);
                if(cMFilter == null) {
                    cMFilter = new Location_cMFilter();
                }
                cMFilter.add_cM_valuesToMinMaxAnd_cMRanges(cM);
                chrToLocationFilter.put(toks[1], cMFilter);

            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
//            Reporter.report("[ERROR]", "File not found: " + newFile.getName(), getClassName());
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (myData != null) {
                    myData.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Finished reading " + POPSEQ + ", MAP size = " + popSeqChromosomeMap1.size());
    }

    public ArrayList<Contig> getContigs(String chromosome) {
        return popSeqChromosomeMap1.get(chromosome);
    }
    
    public Location_cMFilter getLocationFilter(String chromosome) {
        return chrToLocationFilter.get(chromosome);
    }
    
}
