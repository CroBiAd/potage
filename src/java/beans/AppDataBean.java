/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import poplogic.Annotation;
import poplogic.Contig;
import poplogic.Gene;
import reusable.InReader;

/**
 *
 * @author rad
 */
@ManagedBean(name = "appDataBean")
@ApplicationScoped
public class AppDataBean {

    private final boolean DEBUG = false;
    private final String POPSEQ = "/var/tomcat/persist/potage_data/IWGSC_CSS_POPSEQ_v2.tsv";
    private final String ANNOTATION_RICE = "/var/tomcat/persist/potage_data/HCS_2013_annotations_rice.txt";
    private final String ANNOTATION = "/var/tomcat/persist/potage_data/ta_IWGSC_MIPSv2.0_HCS_HUMAN_READABLE_DESCS_2013Nov28_no_header_no_brackets.txt"; //tr -d '()' < ta_IWGSC_MIPSv2.0_HCS_HUMAN_READABLE_DESCS_2013Nov28_no_header.txt > ta_IWGSC_MIPSv2.0_HCS_HUMAN_READABLE_DESCS_2013Nov28_no_header_no_brackets.txt
    private final String TRAES_CSS_MAP = "/var/tomcat/persist/potage_data/Traes_to_CSS.map";
    private final String FPKMS = "/var/tomcat/persist/potage_data/FPKMs/reordered/popseqed_genes_on_with_header2016.fpkms";
    private final String FPKMS_UNORDERED_GENES = "/var/tomcat/persist/potage_data/FPKMs/reordered/unordered_genes_with_header2016.fpkms";
    private final String FPKM_SETTINGS = "/var/tomcat/persist/potage_data/FPKMs/reordered/fpkm_data_settings2016.txt";
    private final String TABLE_HEADERS = "Gene ID,From,To,Strand,Contig ID,cM,MIPS annotation Hit ID,MIPS annotation Description,MIPS annotation Interpro ID,Rice annotation Hit ID,Rice annotation Description";

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
    private HashMap<String, ArrayList<Double>> genesToExpressionMap;
    private String[] fpkmTableHeaders;

    public AppDataBean() {
        readPopSeq();
        readMainTableData();
        integrateGeneWithPopSeqData();
//        SearchResult quickFind = quickFind("1BL_3811941");
//        System.err.println("");
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
                    if (genes.get(i).getGeneId().equals(id)) {
                        Gene gene = genes.get(i);
                        System.out.println("Found gene "+gene.getGeneId());
                        return new SearchResult(gene, gene.getContig(), chromosome, i);
                    }
                }
            } else {
                ArrayList<Contig> contigs = popSeqChromosomeMap1.get(chromosome);
                for (int i = 0; i < contigs.size(); i++) {
                    if (contigs.get(i).getContigId().equals(contig.getId())) {
                        System.out.println("Found contig "+contigs.get(i).getId());
                        return new SearchResult(null, contig, chromosome, i);
                    }
                }
            }
        }
        System.out.println("Found nothing ");
        return null;
    }

    private void readMainTableData() {
        if (DEBUG) {
            mipsIdToAnnotationStringToksMap = new HashMap<>();
            mipsIdToRiceAnnotationStringToksMap = new HashMap<>();
            genesToExpressionMap = new HashMap<>();
            genesToExpressionMap = new HashMap<>();
        } else {
            mipsIdToAnnotationStringToksMap = buildAnnotationMap(ANNOTATION, false);
            mipsIdToRiceAnnotationStringToksMap = buildAnnotationMap(ANNOTATION_RICE, true);
            genesToExpressionMap = getGenesTissuesFPKMs(FPKMS);
            genesToExpressionMap = getGenesTissuesFPKMs(FPKMS_UNORDERED_GENES);
        }
        buildCssToTraesAndReverseMaps(TRAES_CSS_MAP);
    }

    private void integrateGeneWithPopSeqData() {

        //file specifies that first chart takes 15 FPKM cols (CS), next takes 2 cols (Chris Anther, Seedling)...
        //this file could also be used in the future as a more generic settings file
        InReader ir = new InReader(FPKM_SETTINGS);
        ArrayList<String> fpkmSettings = ir.returnInput();

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
                                genesToExpressionMap.get(geneId), fpkmTableHeaders, traesOnCssMap.get(geneId), fpkmSettings);
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
                            genesToExpressionMap.get(geneId), fpkmTableHeaders, traesOnCssMap.get(geneId), fpkmSettings);
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
            myData = new BufferedReader(new FileReader(POPSEQ));
            Pattern p = Pattern.compile("\t");
            String inputLine; // = myData.readLine(); //SKIPPING HEADER ROW            
            while ((inputLine = myData.readLine()) != null) {
                if (inputLine.startsWith("contig")) {
                    continue;
                }
                if(DEBUG && !inputLine.startsWith("1")) {
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
            while ((inputLine = myData.readLine()) != null) {
                if(DEBUG && !inputLine.startsWith("Traes_1")) {
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

    private HashMap<String, ArrayList<Double>> getGenesTissuesFPKMs(String FPKMsFileName) {
//        HashMap<String, ArrayList<Double>> genesTissuesFPKMsMap = new HashMap<>(100000, 1);
        if (genesToExpressionMap == null) {
            genesToExpressionMap = new HashMap<>(100000, 1);
        }
        File file = new File(FPKMsFileName);
        BufferedReader myData = null;
        try {
//            int countCasesOfMultipleFpkmValuesPerGenePrediction = 0;
            String inputLine;
            myData = new BufferedReader(new FileReader(file));
            String readLine = myData.readLine();
            Pattern p = Pattern.compile("\t");
            fpkmTableHeaders = p.split(readLine);

            while ((inputLine = myData.readLine()) != null) {
                if(DEBUG && !inputLine.startsWith("Traes_1")) {
                    continue;
                }
                String toks[] = p.split(inputLine);
                if (toks != null && toks.length > 1 && toks[0].startsWith("Traes")) {
                    ArrayList<Double> storedGenePerTissueFPKMs = genesToExpressionMap.get(toks[0]);
                    boolean overwrite = false;
                    if (storedGenePerTissueFPKMs == null) {
                        overwrite = true;
                    } else {
//                        countCasesOfMultipleFpkmValuesPerGenePrediction++;
                        double lenStored = storedGenePerTissueFPKMs.get(2) - storedGenePerTissueFPKMs.get(1) + 1;
                        double lenCurrent = Double.parseDouble(toks[2]) - Double.parseDouble(toks[1]) + 1;
                        if (lenCurrent > lenStored) {
                            overwrite = true;
                        }
                    }
                    if (overwrite) { //storing only if empty or if cufflinks reported length is longer for the same gene
                        storedGenePerTissueFPKMs = new ArrayList<>(18);
                        for (int i = 1; i < toks.length; i++) { //1 and 2 are coordinates
                            storedGenePerTissueFPKMs.add(Double.parseDouble(toks[i]));
                        }
                        genesToExpressionMap.put(toks[0], storedGenePerTissueFPKMs);
                    }
                    ///storedGeneIds.add(toks[0]);
                }
            }
//            System.err.println("not allowing musleFpkmValuesPerGenePrediction = " + countCasesOfMultipleFpkmValuesPerGenePrediction + "  in " + this.toString());

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
        return genesToExpressionMap;
    }

    private Annotation getAnnotation(String wheatGeneId, HashMap<String, String[]> mipsIdToAnnotationStringToksMap, boolean isRice) {
        return new Annotation(wheatGeneId, mipsIdToAnnotationStringToksMap, isRice);
    }

    public String[] getFpkmTableHeaders() {
        return fpkmTableHeaders;
    }

    public ArrayList<Gene> getGenesBinned(String chromosome) {
        return chrToBinnedGenesMap.get(chromosome);
    }

    public ArrayList<Gene> getGenesAll(String chromosome) {
        return chrToAllGenesMap.get(chromosome);
    }

    public HashMap<String, ArrayList<Double>> getGenesToExpressionMap() {
        return genesToExpressionMap;
    }

}
