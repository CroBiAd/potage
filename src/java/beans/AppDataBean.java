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
package beans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import poplogic.Annotation;
import poplogic.Contig;
import poplogic.ExpressionData;
import poplogic.Gene;
import reusable.InReader;
import reusable.Reporter;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
@ManagedBean(name = "appDataBean")
@ApplicationScoped
public final class AppDataBean {

    private boolean DEBUG;
    private final String DEBUG_PREFIX = "Traes_1A";
    private final String CONFIG_FILE = "/var/tomcat/persist/potage_data/potage.cfg";
//    private final String POPSEQ = "/var/tomcat/persist/potage_data/IWGSC_CSS_POPSEQ_v2.tsv";
//    private final String ANNOTATION_RICE = "/var/tomcat/persist/potage_data/HCS_2013_annotations_rice.txt";
//    private final String ANNOTATION = "/var/tomcat/persist/potage_data/ta_IWGSC_MIPSv2.0_HCS_HUMAN_READABLE_DESCS_2013Nov28_no_header_no_brackets.txt"; //tr -d '()' < ta_IWGSC_MIPSv2.0_HCS_HUMAN_READABLE_DESCS_2013Nov28_no_header.txt > ta_IWGSC_MIPSv2.0_HCS_HUMAN_READABLE_DESCS_2013Nov28_no_header_no_brackets.txt
//    private final String GENE_2_CONTIG_MAP = "/var/tomcat/persist/potage_data/Traes_to_CSS.map";
//    private final String TABLE_HEADERS = "Gene ID,From,To,Strand,Contig ID,cM,MIPS annotation Hit ID,MIPS annotation Description,MIPS annotation Interpro ID,Rice annotation Hit ID,Rice annotation Description";

    private String parentPath;
    private HashMap<String, String> staticFilesMap = new HashMap<>();
    private ArrayList<String> expressionDataConfigFiles = new ArrayList<>();

    //ALL_POPSEQed CONTIGS
//    private HashMap<String, HashMap<Double, ArrayList<String>>> popSeqChromosomeMap;
    private HashMap<String, ArrayList<Contig>> popSeqChromosomeMap1;
    private HashMap<String, Location_cMFilter> chrToContigLocationFilter;

    //ALL Contigs
    private HashMap<String, Contig> contigsMap;

    //MAIN TBALE DATA - ANNOTATION
    private HashMap<String, String[]> mipsIdToAnnotationStringToksMap;
    private HashMap<String, String[]> mipsIdToRiceAnnotationStringToksMap;
    //MAIN TABLE DATA -  GENES
    private HashMap<String, ArrayList<String>> cssToTraesIdMap;
    private HashMap<String, String> traesOnCssMap;
    private HashMap<String, ArrayList<Gene>> chrToBinnedGenesMap;
    private HashMap<String, ArrayList<Gene>> chrToAllGenesMap;
    private HashMap<String, Location_cMFilter> chrToGeneLocationFilter;
    //MAIN TABLE DATA -  EXPRESSION
//    private HashMap<String, ArrayList<Double>> genesToExpressionMap;
    private ArrayList<ExpressionData> expressionDatasets;

    private String potageCommitId;
    private String potageDataCommitId;

//    private String[] fpkmTableHeaders;
    public AppDataBean() {
        readConfigFile();
        //if running on development host, don't load all the data
        String devHostname = getDEV_HOSTNAME();
        if (devHostname != null) {
            DEBUG = devHostname.equalsIgnoreCase(getHostname());
        }
        String name = "POTAGE." + this.getClass().getSimpleName();
        Reporter.report("[INFO]", "Reading in Popseq data...", name);
        readPopSeq();
        Reporter.report("[INFO]", "Reading in expression data...", name);
        readExpressionData();
        Reporter.report("[INFO]", "Reading in annotation...", name);
        readAnnotationData();
        Reporter.report("[INFO]", "Cross-referencing genes with contigs...", name);
        buildCssToTraesAndReverseMaps(getGENE_2_CONTIG_MAP());
        Reporter.report("[INFO]", "Cross-reference all data...", name);
        integrateGeneWithPopSeqData();
        Reporter.report("[INFO]", "Finished populating Application-scoped datastore", name);
//        SearchResult quickFind = quickFind("1BL_3811941");
//        System.err.println("");
    }

    public String getBLAST_DB() {
        return staticFilesMap.get("BLAST_DB");
    }

    public String getDEV_HOSTNAME() {
        return staticFilesMap.get("DEV_HOSTNAME");
    }

    public String getTABLE_HEADERS() {
        return staticFilesMap.get("TABLE_HEADERS");
    }

    private String getPOPSEQ() {
        return staticFilesMap.get("POPSEQ");
    }

    private String getANNOTATION() {
        return staticFilesMap.get("ANNOTATION");
    }

    private String getANNOTATION_RICE() {
        return staticFilesMap.get("ANNOTATION_RICE");
    }

    private String getGENE_2_CONTIG_MAP() {
        return staticFilesMap.get("GENE_2_CONTIG_MAP");
    }

    private void readConfigFile() {
        InReader in = new InReader(CONFIG_FILE);
        parentPath = in.getParentPath();
        ArrayList<String> input = in.returnInput();
        for (String line : input) {
            if (!line.trim().isEmpty() && !line.startsWith("[") && !line.startsWith("#")) {
                String toks[] = line.split("[ \t]+");
                if (toks[0].equalsIgnoreCase("include")) {
//                    expressionDataConfigFiles.add(line.replaceFirst("[^ \t]+[ \t]+", ""));
                    String rootPath = line.replaceFirst("[^ \t]+[ \t]+", "");
                    if (!rootPath.startsWith("/")) {
                        rootPath = parentPath + "/" + rootPath;
                    }
                    getFiles(rootPath, ".cfg");

                } else {
                    String fileName = line.replaceFirst("[^ \t]+[ \t]+", "").trim();
                    if (fileName.startsWith("/") || toks[0].equalsIgnoreCase("TABLE_HEADERS") || toks[0].equalsIgnoreCase("DEV_HOSTNAME")) {
                        staticFilesMap.put(toks[0], fileName);
                    } else {
                        staticFilesMap.put(toks[0], parentPath + "/" + fileName);
                    }
                }
            }
        }
    }

    private void getFiles(String rootName, String suffix) {
        File directory = new File(rootName);
        File fileList[] = directory.listFiles();
        Arrays.sort(fileList);
        for (File file : fileList) {
            if (file.isFile() && file.getAbsolutePath().endsWith(suffix)) {
                expressionDataConfigFiles.add(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                getFiles(file.getAbsolutePath(), suffix);
            }
        }
    }

    public SearchResult quickFind(String id) {
        Contig contig;
        if (traesOnCssMap.containsKey(id)) { //HABEMUS GENE
            contig = contigsMap.get(traesOnCssMap.get(id).split(",")[1]);
        } else {
            contig = contigsMap.get(id);
        }

        if (contig != null) {
            String chromosome = contig.getChromosome();

            if (contig.hasGenes()) {
                ArrayList<Gene> genes = chrToAllGenesMap.get(chromosome);
                for (int i = 0; i < genes.size(); i++) {
                    Gene gene = genes.get(i);
                    if (gene.getGeneId().equals(id)) {
//                        System.out.println("Found gene "+gene.getGeneId());
                        return new SearchResult(gene, gene.getContig(), chromosome, i);
                    } else if (gene.getContig().getId().equals(id)) {
//                        System.out.println("Found contig "+contig.getId());
                        return new SearchResult(null, contig, chromosome, i);
                    }
                }

            } else {
                ArrayList<Contig> contigs = popSeqChromosomeMap1.get(chromosome);
                for (int i = 0; i < contigs.size(); i++) {
                    if (contigs.get(i).getContigId().equals(contig.getId())) {
                        System.out.println("Found contig " + contigs.get(i).getId());
                        return new SearchResult(null, contig, chromosome, i);
                    }
                }
            }
        }
        System.out.println("Found nothing ");
        return null;
    }

    private void readAnnotationData() {
//        if (DEBUG) {
//            mipsIdToAnnotationStringToksMap = new HashMap<>();
//            mipsIdToRiceAnnotationStringToksMap = new HashMap<>();
//        } else {
        mipsIdToAnnotationStringToksMap = buildAnnotationMap(getANNOTATION(), false);
        mipsIdToRiceAnnotationStringToksMap = buildAnnotationMap(getANNOTATION_RICE(), true);
//        }
    }

    private void readExpressionData() {
//        if (DEBUG) {
////            genesToExpressionMap = new HashMap<>();
////            genesToExpressionMap = new HashMap<>();
//        } else {

        expressionDatasets = new ArrayList<>(expressionDataConfigFiles.size());
        for (String expressionDataConfigFile : expressionDataConfigFiles) {
            if (!expressionDataConfigFile.startsWith("/")) {
                expressionDataConfigFile = parentPath + "/" + expressionDataConfigFile;
            }
            expressionDatasets.add(new ExpressionData(expressionDataConfigFile, DEBUG));
        }
//            genesToExpressionMap = getGenesTissuesFPKMs(FPKMS);
//            genesToExpressionMap = getGenesTissuesFPKMs(FPKMS_UNORDERED_GENES);

//        }
    }

    private void integrateGeneWithPopSeqData() {

        //file specifies that first chart takes 15 FPKM cols (CS), next takes 2 cols (Chris Anther, Seedling)...
        //this file could also be used in the future as a more generic settings file
        //GENES ON POPSeq-ed CONTIGS ONLY
        chrToBinnedGenesMap = new HashMap<>();
        //ALL GENES
        chrToAllGenesMap = new HashMap<>();
        chrToGeneLocationFilter = new HashMap<>();

        for (Map.Entry<String, ArrayList<Contig>> entry : popSeqChromosomeMap1.entrySet()) {
            String chromosome = entry.getKey();
            ArrayList<Gene> binnedGenes = chrToBinnedGenesMap.get(chromosome);
            ArrayList<Gene> allGenes = chrToAllGenesMap.get(chromosome);
            if (binnedGenes == null) {
                binnedGenes = new ArrayList<>();
            }
            if (allGenes == null) {
                allGenes = new ArrayList<>();
            }
            ArrayList<Contig> contigs = entry.getValue();
            for (Contig contig : contigs) {
                ArrayList<String> geneIds = cssToTraesIdMap.get(contig.getId());
                if (geneIds != null) {
                    Location_cMFilter cMFilter = chrToGeneLocationFilter.get(chromosome);
                    if (cMFilter == null) {
                        cMFilter = new Location_cMFilter();
                    }

                    for (String geneId : geneIds) {
                        Gene g = new Gene(geneId, contig, getAnnotation(geneId, mipsIdToAnnotationStringToksMap, false),
                                getAnnotation(geneId, mipsIdToRiceAnnotationStringToksMap, true),
                                expressionDatasets, traesOnCssMap.get(geneId));
                        binnedGenes.add(g);
                        allGenes.add(g);
                        cMFilter.add_cM_valuesToMinMaxAnd_cMRanges(g.getContig().getcM());

                    }
                    chrToGeneLocationFilter.put(chromosome, cMFilter);
                }
            }
            chrToBinnedGenesMap.put(chromosome, binnedGenes);
            chrToAllGenesMap.put(chromosome, allGenes);
        }

        //add un-binned genes
        Pattern p = Pattern.compile(",");
        for (Map.Entry<String, Contig> contigsEntry : contigsMap.entrySet()) {
            Contig c = contigsEntry.getValue();
            if (c.getcM() == null && c.getWheatGeneIdsList() != null) {
                ArrayList<Gene> genes = chrToAllGenesMap.get(c.getChromosome());
                ArrayList<String> geneIds = cssToTraesIdMap.get(c.getId());
                if (genes == null && geneIds != null) {
                    genes = new ArrayList<>();
                }
                for (String geneId : geneIds) {
                    Gene g = new Gene(geneId, c, getAnnotation(geneId, mipsIdToAnnotationStringToksMap, false),
                            getAnnotation(geneId, mipsIdToRiceAnnotationStringToksMap, true),
                            expressionDatasets, traesOnCssMap.get(geneId));
                    genes.add(g);
                }
                chrToAllGenesMap.put(c.getChromosome(), genes);
            }
//            for(String geneId: traesOnCssMap.keySet()) {
//                String[] toks = p.split(traesOnCssMap.get(geneId));
//                if(c)
//            }
        }

//        processInput(inputFileName, mipsIdToAnnotationStringToksMap, mipsIdToRiceAnnotationStringToksMap, cssToTraesIdMap, genesTissuesFPKMsMap,
//                fpkmSettings);
//        //IF we want the unordered genes to be included as well
//        if (FPKMS_UNORDERED_GENES != null) {
//            HashMap<String, ArrayList<Double>> unorderedGenesTissuesFPKMsMap = getGenesTissuesFPKMs(FPKMS_UNORDERED_GENES);
//            addUnorderedGenes(mipsIdToAnnotationStringToksMap, mipsIdToRiceAnnotationStringToksMap, unorderedGenesTissuesFPKMsMap, fpkmSettings);
//        }
    }

    private void readPopSeq() {
//        popSeqContigsMap = new HashMap<>();
//        popSeqChromosomeMap = new HashMap<>();
        popSeqChromosomeMap1 = new HashMap<>();
        chrToContigLocationFilter = new HashMap<>();
        contigsMap = new HashMap<>();

        BufferedReader myData = null;
        try {
            myData = new BufferedReader(new FileReader(getPOPSEQ()));
            Pattern p = Pattern.compile("\t");
            Pattern p1 = Pattern.compile("_");
            String inputLine; // = myData.readLine(); //SKIPPING HEADER ROW            
            while ((inputLine = myData.readLine()) != null) {
                if (inputLine.startsWith("contig")) {
                    continue;
                }
                if (DEBUG && !inputLine.startsWith(p1.split(DEBUG_PREFIX)[1])) {
                    continue;
                }
                String[] toks = p.split(inputLine);
                Double cM = reusable.CommonMaths.round(Double.parseDouble(toks[2]), 3);

                //POPULTAE CONTIG TO POSITION 
//POPULATE CHROMOSOME -> BIN -> CONTIG MAP
//                HashMap<Double, ArrayList<String>> chrToBins = popSeqChromosomeMap.get(toks[1]);
//                if (chrToBins == null) {
//                    chrToBins = new HashMap<>();
//                }
//                ArrayList<String> bin = chrToBins.get(cM);
//                if (bin == null) {
//                    bin = new ArrayList<>();
//                }
//                bin.add(toks[0]);
//                chrToBins.put(cM, bin);
//                popSeqChromosomeMap.put(toks[1], chrToBins);
//BACKWARD COMPATIBLE STORAGE OF CONTIGS - TO BE REMOVED AFTER UPGRADE OF OTHER CODE
//                ArrayList<Contig> contigs = popSeqChromosomeMap1.getOrDefault(toks[1],new ArrayList<Contig>());
                ArrayList<Contig> contigs = popSeqChromosomeMap1.get(toks[1]);
                if (contigs == null) {
                    contigs = new ArrayList<>();
                }
                Contig c = new Contig(toks[0], toks[1], cM);
                contigs.add(c);
                popSeqChromosomeMap1.put(toks[1], contigs);
                contigsMap.put(toks[0], c);

//STORE cM VALUSES FOR FILTERS
                Location_cMFilter cMFilter = chrToContigLocationFilter.get(toks[1]);
                if (cMFilter == null) {
                    cMFilter = new Location_cMFilter();
                }
                cMFilter.add_cM_valuesToMinMaxAnd_cMRanges(cM);
                chrToContigLocationFilter.put(toks[1], cMFilter);

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
//        System.out.println("Finished reading " + POPSEQ + ", MAP size = " + popSeqChromosomeMap1.size());
    }

    public ArrayList<Contig> getContigs(String chromosome) {
        return popSeqChromosomeMap1.get(chromosome);
    }

    /**
     * returnning a clone
     *
     * @param chromosome
     * @return
     */
    public Location_cMFilter getLocationFilterContigs(String chromosome) {
        return new Location_cMFilter(chrToContigLocationFilter.get(chromosome));
    }

    /**
     * returnning a clone
     *
     * @param chromosome
     * @return
     */
    public Location_cMFilter getLocationFilterGenes(String chromosome) {
        return new Location_cMFilter(chrToGeneLocationFilter.get(chromosome));
    }

    private HashMap<String, String[]> buildAnnotationMap(String inputFileName, boolean isRiceAnnotation) {
        HashMap<String, String[]> annotationMap = new HashMap<>(125000, 1);
        File file = new File(inputFileName);
        BufferedReader myData = null;
        try {
            String inputLine;
            myData = new BufferedReader(new FileReader(file));
            Pattern p = Pattern.compile("\t");
            while ((inputLine = myData.readLine()) != null) {
                String toks[] = p.split(inputLine);
                if (DEBUG && !toks[0].startsWith(DEBUG_PREFIX)) {
                    continue;
                }
                if (toks != null && toks.length > 1) {
                    if (isRiceAnnotation) {
                        annotationMap.put(toks[0], toks);
                    } else {
                        annotationMap.put(toks[0].substring(0, toks[0].length() - 2), toks);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println("File not found exception!\t" + file.getName());
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
        return annotationMap;
    }

    private HashMap<String, ArrayList<String>> buildCssToTraesAndReverseMaps(String inputFileName) {
        traesOnCssMap = new HashMap<>();
        cssToTraesIdMap = new HashMap<>(125000, 1);
//        System.out.println("Reading annotations file: " + inputFileName);

        File file = new File(inputFileName);
        BufferedReader myData = null;
        try {
            String inputLine;
            myData = new BufferedReader(new FileReader(file));
            Pattern p = Pattern.compile(",");
            Pattern p1 = Pattern.compile("_");
            while ((inputLine = myData.readLine()) != null) {
                if (DEBUG && !inputLine.startsWith(DEBUG_PREFIX)) {
                    continue;
                }
                String toks[] = p.split(inputLine);
                if (toks != null && toks.length > 1) {
                    traesOnCssMap.put(toks[0], inputLine);
                    ArrayList<String> geneIds = cssToTraesIdMap.get(toks[1]);
                    if (geneIds == null) {
                        geneIds = new ArrayList<>();
                        geneIds.add(toks[0]);
                        cssToTraesIdMap.put(toks[1], geneIds);
                    } else {
                        geneIds.add(toks[0]);
                    }
                    //Should Already have POPSEQ info in contigsMap, now add geneIds
                    Contig c = contigsMap.get(toks[1]);
                    if (c == null) {
                        c = new Contig(toks[1]);
                    }
                    ArrayList<String> wheatGeneIdsList = c.getWheatGeneIdsList();
                    if (wheatGeneIdsList == null) {
                        c.setWheatGeneIdsList(geneIds);
                    }
                    contigsMap.put(toks[1], c);
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println("File not found exception!\t" + file.getName());
//            ex.printStackTrace();
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
        return cssToTraesIdMap;
    }

    private Annotation getAnnotation(String wheatGeneId, HashMap<String, String[]> mipsIdToAnnotationStringToksMap, boolean isRice) {
        return new Annotation(wheatGeneId, mipsIdToAnnotationStringToksMap, isRice);
    }

    public ArrayList<Gene> getGenesBinned(String chromosome) {
        return chrToBinnedGenesMap.get(chromosome);
    }

    public ArrayList<Gene> getGenesAll(String chromosome) {
        return chrToAllGenesMap.get(chromosome);
    }

    public ArrayList<ExpressionData> getExpressionDatasets() {
        return expressionDatasets;
    }

    private String getHostname() {
        Process p;
        try {
            ProcessBuilder pb = new ProcessBuilder("hostname");
            p = pb.start();
            try {
                p.waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(AppDataBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (p.exitValue() == 0) {
                return inStreamToString(p.getInputStream(), false);
            }
        } catch (IOException ex) {
            Logger.getLogger(AppDataBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    private String inStreamToString(InputStream inStream, boolean newlines) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inStream))) {
            String read;
            while ((read = br.readLine()) != null) {
                if (read.startsWith(">lcl|")) {
                    sb.append(read.replaceFirst("lcl\\|", ""));
                } else {
                    sb.append(read);
                }
                if (newlines) {
                    sb.append("<br />");
                }
            }
            br.close();
            inStream.close();
        }

        return sb.toString();
    }

    public static void main(String[] args) {
//       String fasta = InReader.readInputToString("/home/rad/example.seq");
//        System.err.println(fasta.matches("[ACTGWSMKRYBDHVNactgwsmkrybdhvn\n]+"));
        new AppDataBean();
    }

    public String getPotageCommitId() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String realPath = externalContext.getRealPath("//potage.commit.id");
        if (realPath != null) {
            File file = new File(realPath);
            if (potageCommitId == null) {
                BufferedReader myData = null;
                try {
                    String inputLine;
                    myData = new BufferedReader(new FileReader(file));
                    Pattern p = Pattern.compile("\t");
                    while ((inputLine = myData.readLine()) != null) {
                        setPotageCommitId(inputLine.trim());
                    }
                } catch (FileNotFoundException ex) {
//                    System.err.println("File not found exception!\t" + file.getName());
                    potageCommitId = "";
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
            }      
        }
        return potageCommitId;
    }

    public void setPotageCommitId(String potageCommitId) {
        this.potageCommitId = potageCommitId;
    }

    public String getPotageDataCommitId() {
        if (potageDataCommitId == null) {
            File file = new File(parentPath + "/potage_data.commit.id");
            BufferedReader myData = null;
            try {
                String inputLine;
                myData = new BufferedReader(new FileReader(file));
                Pattern p = Pattern.compile("\t");
                while ((inputLine = myData.readLine()) != null) {
                    setPotageDataCommitId(inputLine.trim());
                }
            } catch (FileNotFoundException ex) {
//                System.err.println("File not found exception!\t" + file.getName());
                potageDataCommitId = "";
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
        } else if (potageDataCommitId.isEmpty()) {
            return null;
        }
        return potageDataCommitId;
    }

    public void setPotageDataCommitId(String potageDataCommitId) {
        this.potageDataCommitId = potageDataCommitId;
    }

}
