/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poplogic;

import beans.Location_cMFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author rad
 */
public class InputProcessor implements Serializable {

    private ArrayList<Gene> genes;
//    private Integer queryFoundAt;
    private ArrayList<Integer> queryFoundAt;
    private Integer queryFoundAtMin;
    private Integer queryFoundAtMax;
    private String[] fpkmTableHeaders;

    private Location_cMFilter cM_filter;
    private String chromosome;

    HashMap<String, ArrayList<String>> cssToTraesIdMap;
    HashMap<String, String> traesOnCssMap;
//    private ChartBean donutBean;

    public InputProcessor(String inputFileName, String queryId, int offset, String annotationFileName, String annotationRiceFileName,
            String traesToCssMapFileName, String FPKMsFileName, String FPKMsFileNameUnordered) {
        boolean isRiceAnnotation = false;
        HashMap<String, String[]> mipsIdToAnnotationStringToksMap = buildAnnotationMap(annotationFileName, isRiceAnnotation);
        isRiceAnnotation = true;
        HashMap<String, String[]> mipsIdToRiceAnnotationStringToksMap = buildAnnotationMap(annotationRiceFileName, isRiceAnnotation);
        buildCssToTraesAndReverseMaps(traesToCssMapFileName);
        HashMap<String, ArrayList<Double>> genesTissuesFPKMsMap = getGenesTissuesFPKMs(FPKMsFileName);

        chromosome = inputFileName.split("_")[2];
        processInput(inputFileName, queryId, mipsIdToAnnotationStringToksMap, mipsIdToRiceAnnotationStringToksMap, cssToTraesIdMap, genesTissuesFPKMsMap);

        //IF we want the unordered genes to be included as well
        if (FPKMsFileNameUnordered != null) {
            HashMap<String, ArrayList<Double>> unorderedGenesTissuesFPKMsMap = getGenesTissuesFPKMs(FPKMsFileNameUnordered);
            addUnorderedGenes(mipsIdToAnnotationStringToksMap, mipsIdToRiceAnnotationStringToksMap, unorderedGenesTissuesFPKMsMap);
        }
    }

    public InputProcessor(String traesToCssMapFileName) {
        cssToTraesIdMap = buildCssToTraesAndReverseMaps(traesToCssMapFileName);
    }

    public InputProcessor() {
    }

    public Location_cMFilter getcM_filter() {
        return cM_filter;
    }

//
//    public ChartBean getDonutBean() {
//        if (donutBean == null) {
//            return new beans.ChartBean(null, null);
//        }
//        return donutBean;
//    }
    public ArrayList<Gene> getGenes() {
        return genes;
    }

    public String[] getFpkmTableHeaders() {
        return fpkmTableHeaders;
    }

//    /**
//     * All contigs have been read in from a file, this method restrict this list
//     * to the contig of interest plus n neighbours.
//     *
//     * @param neighbours
//     * @return null if query not present in this list of contigs
//     */
//    public ArrayList<Contig> getContigsRestrictedList(int neighbours) {
//        if (neighbours < 0 || neighbours >= 2147483647) {
//            neighbours = 1000;
//        }
////        if(queryFoundAt == null) {
//        if (queryFoundAtMin == null || queryFoundAtMax == null) {
//            return null;
//        }
//        ArrayList<Contig> restricted = new ArrayList<>(neighbours + 1);
////        int from = queryFoundAt - (neighbours/2);        
////        int to = queryFoundAt + (neighbours/2) +neighbours%2 +1;
//        int from = queryFoundAtMin - (neighbours / 2);
//        int to = queryFoundAtMax + (neighbours / 2) + neighbours % 2 + 1;
//        if (from < 0) {
//            from = 0;
//        }
//        if (to > genes.size()) {
//            to = genes.size();
//        }
//        for (int i = from; i < to; i++) {
//            restricted.add(genes.get(i));
//        }
//        return restricted;
//    }
//
//    public ArrayList<Contig> getContigsListRestrictedToQueriesOnly() {
//        if (queryFoundAtMin == null || queryFoundAtMax == null) {
//            return null;
//        }
//        ArrayList<Contig> restricted = new ArrayList<>(queryFoundAt.size());
//        Collections.sort(queryFoundAt);
//        for (Integer i : queryFoundAt) {
//            restricted.add(genes.get(i));
//        }
//        return restricted;
//    }
//    public boolean getQueryFound() {
//        if (queryFoundAtMin == null || queryFoundAtMax == null) {
//            return false;
//        }
//        return true;
//    }
//
////    public Integer getQueryFoundAt() {
////        return queryFoundAt;
////    }
//    private void queryFound(int at) {
//        queryFoundAt.add(at);
//        if (queryFoundAtMin == null || queryFoundAtMax == null) {
//            queryFoundAtMin = at;
//            queryFoundAtMax = at;
//        } else {
//            if (at < queryFoundAtMin) {
//                queryFoundAtMin = at;
//            }
//            if (at > queryFoundAtMax) {
//                queryFoundAtMax = at;
//            }
//        }
//    }
    private void processInput(String inputFileName, String queryId, HashMap<String, String[]> mipsIdToAnnotationStringToksMap,
            HashMap<String, String[]> mipsIdToRiceAnnotationStringToksMap, HashMap<String, ArrayList<String>> cssToTraesIdsMap, HashMap<String, ArrayList<Double>> genesTissuesFPKMsMap) {
        cM_filter = new Location_cMFilter();
        genes = new ArrayList<>(99000);
        String queries[] = null;
        if (queryId != null) {
            queries = queryId.split("[^_|\\w]"); //split on anything that is not a alphanumeric or an underscore
            queryFoundAt = new ArrayList<>(queries.length);
        }
        File file = new File(inputFileName);
        BufferedReader myData = null;
        try {
            String inputLine;
            myData = new BufferedReader(new FileReader(file));
            while ((inputLine = myData.readLine()) != null) {
                String toks[] = inputLine.split("\t");
                if (toks != null && toks.length > 1) {
                    ArrayList<String> wheatGeneIdsList = cssToTraesIdsMap.get(toks[0]);

                    Double cM_corrected = Double.parseDouble(toks[2]);
                    Double cM_original = Double.parseDouble(toks[3]);
                    cM_filter.add_cM_valuesToMinMaxAnd_cMRanges(cM_corrected, cM_original);
                    Contig c = new Contig(toks[0], toks[1], cM_corrected, cM_original, wheatGeneIdsList, genesTissuesFPKMsMap);
                    boolean isRice = false;
//                    if (wheatGeneIdsList != null) { //if no mips gene predictions then no annotations to retrieve 
//                        c.setAnnotations(getAnnotations(wheatGeneIdsList, mipsIdToAnnotationStringToksMap, isRice));
//                        isRice = true;
//                        c.setAnnotationsRice(getAnnotations(wheatGeneIdsList, mipsIdToRiceAnnotationStringToksMap, isRice));
//                    }
                    //For each geneId on current contig, create Gene object with a ref to the contig it is derived from

                    if (wheatGeneIdsList == null) {
                        Gene placeholderGene = new Gene(c.getContigId(), c, null, null, null, null, null);
                        genes.add(placeholderGene);
                    } else {
                        for (String geneId : c.getWheatGeneIdsList()) {
                            String entry = traesOnCssMap.get(geneId);
                            Gene g = new Gene(geneId, c, getAnnotation(geneId, mipsIdToAnnotationStringToksMap, false), getAnnotation(geneId, mipsIdToRiceAnnotationStringToksMap, true),
                                    genesTissuesFPKMsMap.get(geneId), fpkmTableHeaders, entry);
                            genes.add(g);
                        }
                    }

                }
            }
            genes.trimToSize();
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
    }

    private void addUnorderedGenes(HashMap<String, String[]> mipsIdToAnnotationStringToksMap,
            HashMap<String, String[]> mipsIdToRiceAnnotationStringToksMap, HashMap<String, ArrayList<Double>> genesTissuesFPKMsMap) {
        Iterator<String> iterator = genesTissuesFPKMsMap.keySet().iterator();
        while (iterator.hasNext()) {
            String id = iterator.next();
            if (id.startsWith("Traes_" + chromosome)) {
                String entry = traesOnCssMap.get(id);
                String contigId = entry.split(",")[1];
                Gene g = new Gene(id, new Contig(contigId), getAnnotation(id, mipsIdToAnnotationStringToksMap, false), getAnnotation(id, mipsIdToRiceAnnotationStringToksMap, true),
                        genesTissuesFPKMsMap.get(id), fpkmTableHeaders, entry);
                genes.add(g);
            }
        }
    }

    public String quickFindQuery(HashMap<String, String> filenames, String queryId, String traesCssMapFile) {
        String foundIn = null;
        for (Map.Entry<String, String> entry : filenames.entrySet()) {
            String chr = entry.getKey();
            String inputFileName = entry.getValue();
//        String queries[] = null;
//        if (queryId != null) {
//            queries = queryId.split("[^_|\\w]"); //split on anything that is not a alphanumeric or an underscore
//            queryFoundAt = new ArrayList<>(queries.length);
//        }
            File file = new File(inputFileName);
            BufferedReader myData = null;
            try {
                String inputLine;
                myData = new BufferedReader(new FileReader(file));
                readFile:
                while ((inputLine = myData.readLine()) != null) {
                    String toks[] = inputLine.split("\t");
                    if (toks != null && toks.length > 1) {
                        if (toks[0].equals(queryId)) {
                            foundIn = chr;// inputFileName + "\t" + inputLine;
                            break;
                        } else {
                            ArrayList<String> wheatGeneIdsList = cssToTraesIdMap.get(toks[0]);
                            if (wheatGeneIdsList != null) {
                                for (String geneId : wheatGeneIdsList) {
                                    if (geneId.equals(queryId)) {
                                        foundIn = chr;// inputFileName + "\t" + inputLine;
                                        break readFile;
                                    }
                                }
                            }

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
        }
        if (foundIn == null) {
            if (traesCssMapFile != null) {
                buildCssToTraesAndReverseMaps(traesCssMapFile);
                String entry = traesOnCssMap.get(queryId);
                if (entry != null) {
                    String id = entry.split(",")[1];
                    foundIn = id.split("_")[0].substring(0, 2);
                } else if (cssToTraesIdMap.containsKey(queryId)) {
                    foundIn = queryId.split("_")[0].substring(0, 2);
                }
            }
        }

        return foundIn;
    }

    private boolean isWithinCoordinates(Double cM_corrected, Double cM_original, Double cM_correctedMin, Double cM_correctedMax, Double cM_originalMin, Double cM_originalMax) {
        return cM_corrected >= cM_correctedMin && cM_corrected <= cM_correctedMax && cM_original >= cM_originalMin && cM_original <= cM_originalMax;
    }

    public PerLocationContigs getContigs(String inputFileName) {
        cM_filter = new Location_cMFilter();
        File file = new File(inputFileName);
        PerLocationContigs contigs = null;
        BufferedReader myData = null;
        try {
            String chromosome = inputFileName.split("_")[2];
            contigs = new PerLocationContigs(chromosome, cM_filter);
            String inputLine;
            myData = new BufferedReader(new FileReader(file));
            while ((inputLine = myData.readLine()) != null) {
                String toks[] = inputLine.split("\t");
                if (toks != null && toks.length > 1) {
                    Double locationCorrected = Double.parseDouble(toks[2]);
                    Double locationOriginal = Double.parseDouble(toks[3]);
                    cM_filter.add_cM_valuesToMinMaxAnd_cMRanges(locationCorrected, locationOriginal);
                    contigs.addContig(new Contig(toks[0], chromosome, locationCorrected, locationOriginal, null, null));
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
        return contigs;
    }

//    public PerLocationContigs getContigsWithinRange(String inputFileName, Double cM_correctedMin, Double cM_correctedMax, Double cM_originalMin, Double cM_originalMax) {
//        File file = new File(inputFileName);
//        PerLocationContigs contigs = null;
//        BufferedReader myData = null;
//        try {
//            String chromosome = inputFileName.split("_")[2];
//            contigs = new PerLocationContigs(chromosome, cM_corrected_min, cM_corrected_max, cM_original_min, cM_original_max);
//            String inputLine;
//            myData = new BufferedReader(new FileReader(file));
//            while ((inputLine = myData.readLine()) != null) {
//                String toks[] = inputLine.split("\t");
//                if (toks != null && toks.length > 1) {
//                    Double locationCorrected = Double.parseDouble(toks[2]);
//                    Double locationOriginal = Double.parseDouble(toks[3]);
//                    if (isWithinCoordinates(locationCorrected, locationOriginal, cM_correctedMin, cM_correctedMax, cM_originalMin, cM_originalMax)) {
//                        contigs.addContig(new Contig(toks[0], chromosome, locationCorrected, locationOriginal, null, null));
//                    }
//                }
//            }
//        } catch (FileNotFoundException ex) {
//            System.err.println("File not found exception!\t" + file.getName());
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                if (myData != null) {
//                    myData.close();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//        return contigs;
//    }
    private HashMap<String, String[]> buildAnnotationMap(String inputFileName, boolean isRiceAnnotation) {
        HashMap<String, String[]> annotationMap = new HashMap<>(125000, 1);
//        System.out.println("Reading annotations file: " + inputFileName);

        File file = new File(inputFileName);
        BufferedReader myData = null;
        try {
            String inputLine;
            myData = new BufferedReader(new FileReader(file));
            int i = 0;
            while ((inputLine = myData.readLine()) != null) {
                String toks[] = inputLine.split("\t");
                if (toks != null && toks.length > 1) {
                    if (isRiceAnnotation) {
                        annotationMap.put(toks[0], toks);
                    } else {
                        annotationMap.put(toks[0].substring(0, toks[0].length() - 2), toks);
                    }
                }
                i++;
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
        return annotationMap;
    }

    private ArrayList<Annotation> getAnnotations(ArrayList<String> wheatGeneIds, HashMap<String, String[]> mipsIdToAnnotationStringToksMap, boolean isRice) {
        ArrayList<Annotation> annotations = new ArrayList<>(2);
        for (String gene : wheatGeneIds) {
            annotations.add(new Annotation(gene, mipsIdToAnnotationStringToksMap, isRice));
        }

        return annotations;
    }

    private Annotation getAnnotation(String wheatGeneId, HashMap<String, String[]> mipsIdToAnnotationStringToksMap, boolean isRice) {
        return new Annotation(wheatGeneId, mipsIdToAnnotationStringToksMap, isRice);
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
            while ((inputLine = myData.readLine()) != null) {
                String toks[] = inputLine.split(",");
                if (toks != null && toks.length > 1) {
                    traesOnCssMap.put(toks[0], inputLine);
                    ArrayList<String> storedGeneIds = cssToTraesIdMap.get(toks[1]);
                    if (storedGeneIds == null) {
                        ArrayList<String> geneIds = new ArrayList<>();
                        geneIds.add(toks[0]);
                        cssToTraesIdMap.put(toks[1], geneIds);
                    } else {
                        storedGeneIds.add(toks[0]);
                    }
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

//    private HashMap<String, HashMap<String,Double>> getGenesTissuesFPKMsMap(String FPKMsFileName) {
//        HashMap<String, HashMap<String,Double>> genesTissuesFPKMsMap = new HashMap<>(100000, 1);
//        File file = new File(FPKMsFileName);
//        BufferedReader myData = null;
//        try {
//            String inputLine;
//            myData = new BufferedReader(new FileReader(file));
//            String headerToks[] = myData.readLine().split("\t");
//            while ((inputLine = myData.readLine()) != null) {
//                String toks[] = inputLine.split("\t");
//                if (toks != null && toks.length > 1 && toks[0].startsWith("Traes")) {
//                    HashMap<String,Double> currentGenePerTissueFPKMsMap = genesTissuesFPKMsMap.get(toks[0]);
//                    if (currentGenePerTissueFPKMsMap == null) {
//                        HashMap<String,Double> tissuesFPKMs = new HashMap<>(15,1);
//                        for (int i = 3; i < toks.length; i++) {
//                            tissuesFPKMs.put(headerToks[i],Double.parseDouble(toks[i]));                            
//                        }
//                        genesTissuesFPKMsMap.put(toks[0], tissuesFPKMs);
//                    } else {
//                        ///storedGeneIds.add(toks[0]);
//                        System.err.println("\n\nnot allowing multiple FPKMs values per Traes gene!! - but ignoring for now... \n\n");
//                        System.exit(1);
//                    }
//                }
//            }
//        } catch (FileNotFoundException ex) {
//            System.err.println("File not found exception!\t" + file.getName());
////            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                if (myData != null) {
//                    myData.close();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//        return genesTissuesFPKMsMap;
//    }
    private HashMap<String, ArrayList<Double>> getGenesTissuesFPKMs(String FPKMsFileName) {
        HashMap<String, ArrayList<Double>> genesTissuesFPKMsMap = new HashMap<>(100000, 1);
        File file = new File(FPKMsFileName);
        BufferedReader myData = null;
        try {
            int countCasesOfMultipleFpkmValuesPerGenePrediction = 0;
            String inputLine;
            myData = new BufferedReader(new FileReader(file));
            fpkmTableHeaders = myData.readLine().split("\t");

            while ((inputLine = myData.readLine()) != null) {
                String toks[] = inputLine.split("\t");
                if (toks != null && toks.length > 1 && toks[0].startsWith("Traes")) {
                    ArrayList<Double> storedGenePerTissueFPKMs = genesTissuesFPKMsMap.get(toks[0]);
                    boolean overwrite = false;
                    if (storedGenePerTissueFPKMs == null) {
                        overwrite = true;
                    } else {
                        countCasesOfMultipleFpkmValuesPerGenePrediction++;
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
                        genesTissuesFPKMsMap.put(toks[0], storedGenePerTissueFPKMs);
                    }
                    ///storedGeneIds.add(toks[0]);
                }
            }
            System.err.println("not allowing multiple FPKMs values per Traes gene!! - but ignoring for now... "
                    + "with countCasesOfMultipleFpkmValuesPerGenePrediction = " + countCasesOfMultipleFpkmValuesPerGenePrediction + "  in " + this.toString());

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
        return genesTissuesFPKMsMap;
    }

}
