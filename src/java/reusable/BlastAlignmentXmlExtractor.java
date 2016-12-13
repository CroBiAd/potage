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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import reusable.InReader;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
public class BlastAlignmentXmlExtractor  implements Serializable{

    private TreeSet<BlastHit> allHits; //NOTE: this treeSet will not allow more than one hit for any (queryID, subjectId) pair! This was implemented as the set of randomly perturbed data ended up with multiple occurances of a given query e.g. 2x ogihara_1115-2.12422.6D_beheaded_13_pct
    private HashMap<String, Sequence> sequencesMap;

    /**
     * Reads in data from blast's xml output generating an
     * ArrayList<BlastAlignment> of all the hits
     *
     * @param filename
     */
    public BlastAlignmentXmlExtractor(String filename) {
        allHits = extractAlignmentsFromXML(filename);
    }

    /**
     * Reads in data from blast's xml output generating an
     * ArrayList<BlastAlignment> of all the hits Reads in query sequences from
     * the provided fasta file and generates a HashMap Key: sequence identifier
     * Value: Sequence object (identifier plus the sequence) Links hits to their
     * respective query Sequence object, with a separate reference to the top
     * hit.
     *
     * @param xmlFilename
     * @param querySequencesFastaFileName
     */
    public BlastAlignmentXmlExtractor(String xmlFilename, HashMap<String, Sequence> sequencesMap, boolean blastXnotN) {
        allHits = extractAlignmentsFromXML(xmlFilename);
        this.sequencesMap = sequencesMap;

        //add all the hits to the query seqquence object
        for (BlastHit hit : allHits) {
            String qseqidString = hit.getQueryId();

            Sequence querySequence = sequencesMap.get(qseqidString);
            if (querySequence == null) {
//                System.err.println("Query sequence " + qseqidString + " from blast output not found in the fasta file provided!");
            } else {
                if (blastXnotN) {
                    querySequence.addBlastXHit(hit);
                } else {
                    querySequence.addBlastNHit(hit);
                }
            }
        }
    }
    
    public BlastAlignmentXmlExtractor(String xmlFilename,  Sequence querySequence, boolean blastXnotN) {
        allHits = extractAlignmentsFromXML(xmlFilename);
        //add all the hits to the query seqquence object
        for (BlastHit hit : allHits) {
            if (blastXnotN) {
                querySequence.addBlastXHit(hit);
            } else {
                querySequence.addBlastNHit(hit);
            }
        }
    }

//
//    public ArrayList<BlastHit> getTopHits() {
//        ArrayList<BlastHit> topHits = new ArrayList<>();
//        for (BlastHit ba : allHits) {
//            if (ba.is TopHit()) {
//                topHits.add(ba);
//            }
//        }
//        return topHits;
//    }
    public TreeSet<BlastHit> getAllHits() {
        return allHits;
    }

    private TreeSet<BlastHit> extractAlignmentsFromXML(String filename) {
//        reusable.InReader in = new InReader(filename);
//        ArrayList<String> fileContents = in.returnInput();

        String query = "";

        //TODO:
        //hit info --------------------------GET RID OF ALL THESE AND STORE DIRECTLY IN hit or hsp object!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
        //TBD
        int hitNum = -1;
        String subject = "";
        String hitDef = "";
        String accssion = "";
        int hitLen = -1;
        BlastHit hit = null;
        TreeSet<BlastHit> allHitsList = new TreeSet<BlastHit>();
        int qlen = -1;

        //hsp info
        int qstart = -1;
        int qend = -1;
        int sstart = -1;
        int send = -1;
        double identity = -1;
        double alnLen = -1;
        int hspNum = -1;
        String alignedQuerySquence = "";
        String alignedSubjectSequence = "";
        String subjectDescription = "";
        int qframe = 0;
        int sframe = 0;

//        Iterator<String> it = fileContents.iterator();
//        while (it.hasNext()) {
        File newFile = new File(filename);
        BufferedReader myData = null;
//    newFile.
        try {
            String line;
            myData = new BufferedReader(new FileReader(newFile));
            while ((line = myData.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("<Iteration_query-def>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    query = toks[1];
                } else if (line.startsWith("<Iteration_query-len>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    qlen = Integer.parseInt(toks[1]);
                } else if (line.startsWith("<Hit_num>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    hitNum = Integer.parseInt(toks[1]);
                } else if (line.startsWith("<Hit_id>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    subject = toks[1];
                } else if (line.startsWith("<Hit_def>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    hitDef = toks[1];
                } else if (line.startsWith("<Hit_accession>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    accssion = toks[1];
                } else if (line.startsWith("<Hit_len>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    hitLen = Integer.parseInt(toks[1]);
                } else if (line.startsWith("<Hit>")) {
                    hit = new BlastHit();

                } else if (line.startsWith("</Hit>")) {
//                hit = new BlastHit(query, hitNum, subject, hitDef, accssion, hitLen);
                    hit.setQueryId(query);
                    hit.setQueryLength(qlen);
                    hit.setHitNum(hitNum);
                    hit.setHitId(subject);
                    hit.setHitDef(hitDef);
                    hit.setHitAccession(accssion);
                    hit.setHitLen(hitLen);
                    allHitsList.add(hit);
                    //END HIT INFO
                } else if (line.startsWith("<Hsp_num>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    hspNum = Integer.parseInt(toks[1]);
                } else if (line.startsWith("<Hsp_query-from>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    qstart = Integer.parseInt(toks[1]);
                } else if (line.startsWith("<Hsp_query-to>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    qend = Integer.parseInt(toks[1]);
                } else if (line.startsWith("<Hsp_query-frame>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    qframe = Integer.parseInt(toks[1]);
                } else if (line.startsWith("<Hsp_hit-frame>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    sframe = Integer.parseInt(toks[1]);
                } else if (line.startsWith("<Hsp_identity>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    identity = Double.parseDouble(toks[1]);
                } else if (line.startsWith("<Hsp_qseq>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    alignedQuerySquence = toks[1];
                } else if (line.startsWith("<Hsp_hseq>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    alignedSubjectSequence = toks[1];
                } else if (line.startsWith("<Hit_def>")) {
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    subjectDescription = toks[1];
                } else if (line.startsWith("<Hsp_hit-from>")) {  //ADDED for BLAST N
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    sstart = Integer.parseInt(toks[1]);
                } else if (line.startsWith("<Hsp_hit-to>")) {  //ADDED for BLAST N
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    send = Integer.parseInt(toks[1]);
                } else if (line.startsWith("<Hsp_align-len>")) {  //ADDED for BLAST N
                    String toks[] = getNonEmptyTokens(line, ">|<");
                    alnLen = Integer.parseInt(toks[1]);
                } else if (line.startsWith("</Hsp>")) {
                    double pid = identity / alnLen;
//                    if(query.equalsIgnoreCase("ogihara_1822-1.13944.2A_beheaded_n_4")) {
//                        System.err.println("ogihara_1822-1.13944.2A_beheaded_n_4, qstart="+qstart+", qend="+qend);
//                    }
                    BlastHitHsp alignment = new BlastHitHsp(query, subject, hspNum, pid, qstart, qend, qframe, sframe, alignedQuerySquence, alignedSubjectSequence, subjectDescription, sstart, send);
                    hit.addHsp(alignment);
                } else if (line.startsWith("</Iteration>")) {
//                System.out.println("Total good hits for query "+query+" : " + goodHitsPerQuery+"\n");      
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println("File not found exception!\n\t" + newFile.getName());
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
        return allHitsList;
    }

    private TreeSet<BlastHit> extractAlignmentsFromXML____OLD(String filename) {
        reusable.InReader in = new InReader(filename);
        ArrayList<String> fileContents = in.returnInput();

        String query = "";

        //TODO:
        //hit info --------------------------GET RID OF ALL THESE AND STORE DIRECTLY IN hit or hsp object!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
        //TBD
        int hitNum = -1;
        String subject = "";
        String hitDef = "";
        String accssion = "";
        int hitLen = -1;
        BlastHit hit = null;
        TreeSet<BlastHit> allHitsList = new TreeSet<BlastHit>();
        int qlen = -1;

        //hsp info
        int qstart = -1;
        int qend = -1;
        int sstart = -1;
        int send = -1;
        double identity = -1;
        double alnLen = -1;
        int hspNum = -1;
        String alignedQuerySquence = "";
        String alignedSubjectSequence = "";
        String subjectDescription = "";
        int qframe = 0;
        int sframe = 0;

        Iterator<String> it = fileContents.iterator();
        while (it.hasNext()) {
            String line = it.next();
            if (line.startsWith("<Iteration_query-def>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                query = toks[1];
            } else if (line.startsWith("<Iteration_query-len>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                qlen = Integer.parseInt(toks[1]);
            } else if (line.startsWith("<Hit_num>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                hitNum = Integer.parseInt(toks[1]);
            } else if (line.startsWith("<Hit_id>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                subject = toks[1];
            } else if (line.startsWith("<Hit_def>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                hitDef = toks[1];
            } else if (line.startsWith("<Hit_accession>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                accssion = toks[1];
            } else if (line.startsWith("<Hit_len>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                hitLen = Integer.parseInt(toks[1]);
            } else if (line.startsWith("<Hit>")) {
                hit = new BlastHit();

            } else if (line.startsWith("</Hit>")) {
//                hit = new BlastHit(query, hitNum, subject, hitDef, accssion, hitLen);
                hit.setQueryId(query);
                hit.setQueryLength(qlen);
                hit.setHitNum(hitNum);
                hit.setHitId(subject);
                hit.setHitDef(hitDef);
                hit.setHitAccession(accssion);
                hit.setHitLen(hitLen);
                allHitsList.add(hit);
                //END HIT INFO
            } else if (line.startsWith("<Hsp_num>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                hspNum = Integer.parseInt(toks[1]);
            } else if (line.startsWith("<Hsp_query-from>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                qstart = Integer.parseInt(toks[1]);
            } else if (line.startsWith("<Hsp_query-to>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                qend = Integer.parseInt(toks[1]);
            } else if (line.startsWith("<Hsp_query-frame>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                qframe = Integer.parseInt(toks[1]);
            } else if (line.startsWith("<Hsp_hit-frame>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                sframe = Integer.parseInt(toks[1]);
            } else if (line.startsWith("<Hsp_identity>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                identity = Double.parseDouble(toks[1]);
            } else if (line.startsWith("<Hsp_qseq>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                alignedQuerySquence = toks[1];
            } else if (line.startsWith("<Hsp_hseq>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                alignedSubjectSequence = toks[1];
            } else if (line.startsWith("<Hit_def>")) {
                String toks[] = getNonEmptyTokens(line, ">|<");
                subjectDescription = toks[1];
            } else if (line.startsWith("<Hsp_hit-from>")) {  //ADDED for BLAST N
                String toks[] = getNonEmptyTokens(line, ">|<");
                sstart = Integer.parseInt(toks[1]);
            } else if (line.startsWith("<Hsp_hit-to>")) {  //ADDED for BLAST N
                String toks[] = getNonEmptyTokens(line, ">|<");
                send = Integer.parseInt(toks[1]);
            } else if (line.startsWith("<Hsp_align-len>")) {  //ADDED for BLAST N
                String toks[] = getNonEmptyTokens(line, ">|<");
                alnLen = Integer.parseInt(toks[1]);
            } else if (line.startsWith("</Hsp>")) {
                double pid = identity / alnLen;
                BlastHitHsp alignment = new BlastHitHsp(query, subject, hspNum, pid, qstart, qend, qframe, sframe, alignedQuerySquence, alignedSubjectSequence, subjectDescription, sstart, send);
//                if (!topHitStored) {
//                    alignment.setIsTopHit(true);
//                    topHitStored = true;
//                }
                hit.addHsp(alignment);
//                allHitsList.add(alignment);
//                }
            } else if (line.startsWith("</Iteration>")) {
//                System.out.println("Total good hits for query "+query+" : " + goodHitsPerQuery+"\n");      
//                currentHits = new ArrayList<>();
//                topHitStored = false;
            }
        }


//        System.out.println("Total number of sequences meeting criteria = " + selectedAlignments.size());
//        System.out.println("\nTotal number of top hits = " + topHits.size());


//        System.exit(3);
        return allHitsList;
        //  report on top hits with low PID
        //  report on top hits with mismatch at qstart (as we are trying to identify the correct qstart i.e. the ATG position
        //  
        //for each top hit, record either accession number or a reference, s.t. we can use batch entrez to see what subjects are best matches
    }

    /**
     * Extension of java's .split()
     *
     * @param toSplit : String to be split into substrings
     * @param delimiter
     * @return
     */
    private static String[] getNonEmptyTokens(String toSplit, String delimiter) {
        String[] tokens = toSplit.split(delimiter);
        ArrayList<String> temp = new ArrayList<String>();
        for (String s : tokens) {
            if (!s.isEmpty()) {
                temp.add(s);
            }
        }
        return temp.toArray(new String[temp.size()]);
    }

    public HashMap<String, Sequence> getSequencesMap() {
        return sequencesMap;
    }
}
