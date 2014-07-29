/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reusable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import reusable.BlastOps;
import reusable.FastaOps;
import reusable.OutWriter;

/**
 *
 * NOTE: multiple hardcoded filenames used for generating test data,
 *
 * @author rad
 */
public class QesHits implements Serializable{

    private static final boolean DEBUG_MODE = false;
//    private String databaseIWGSC;//= "/mnt/storage/storage1/BLAST_DBs/seqDataBase/IWGSC/iwgsc_all_rad";

    public ArrayList<HitsForQuery> findHits(ArrayList<Sequence> sequences, String blastdbPathIWGSC) {
        String databaseIWGSC = blastdbPathIWGSC.replace(".nal", "");
        ArrayList<HitsForQuery>  results = new ArrayList<>();
        
        HashMap<String, Sequence> hashMapOfSequences = new HashMap<String, Sequence>(sequences.size());
        for (Sequence s : sequences) {
            hashMapOfSequences.put(s.getIdentifierString(), s);
        }

        //EXAMPLE DATA PRECOMPUTED
//        String infile = "/home/rad/example.fasta";
//        String blastnFile = "/home/rad/example.blastn.xml";
        //TODO uncomment this::::
        String infile = "/tmp/" + this.toString() + Math.random() + ".fasta";
        writeToFastaFile(sequences, infile);
        String blastnFile = "/tmp/" + this.toString() + Math.random() + ".blastn.xml";
        String cmd1[] = {"nice", "blastn", "-task", "blastn", "-dust", "no", "-query", infile, "-db", databaseIWGSC, "-evalue", "1e-5", "-out", blastnFile, "-max_target_seqs", "10", "-outfmt", "5", "-num_threads", "2"};
        reusable.ExecProcessor.execute(cmd1);
        linkAlignmentResultsToSequencesInHashMap(hashMapOfSequences, blastnFile);
        for (Sequence s : sequences) {
            ArrayList<Hit> hits = getGoodHitsForQuery(s, databaseIWGSC);
            if(!hits.isEmpty())
                results.add(new HitsForQuery(hits,s.getIdentifierString()));
        }
//TODO uncomment this::::
        String cmd2[] = {"rm", infile};
        reusable.ExecProcessor.execute(cmd2);
        return results;

    }


    /**
     * Given a query sequence object that has been previously provided with
     * references to blastx and blastn alignments to rice/brachy and IWGS,
     * respectively, Infer the promoter sequence. More specifically: 1. Infer
     * the position of the start codon from blastx results 2. Based on the
     * inferred position, prepend a number of nucleotides from IWGS hit(s) to
     * the query sequence
     *
     * @param querySequence
     * @return
     */
    private ArrayList<Hit> getGoodHitsForQuery(Sequence querySequence, String databaseString) {
        StringBuilder sb = new StringBuilder(); //instead of print statements
        boolean capidOnQueryNotSubjectB = true;
        int offset = 0;
        capidOnQueryNotSubjectB = true;
        //APPLY SOME THRESHOLD(S) FOR BLASTx HITS/HSPs!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        //List all hits falling into different categories
        ArrayList<BlastHit> allBlasNHits = querySequence.getAllBlastNHits();
        ArrayList<Hit> goodHits = new ArrayList<Hit>();

        sb.append("\nQuery :").append(querySequence.getIdentifierString()).append("\n");
        sb.append(String.format("%10s%10s%10s%20s%10s", "id/qlen", "id/qaln", "qaln/qlen", "accession", "hitlen\n"));
        for (BlastHit hit : allBlasNHits) {
            double caPid = hit.getOverlappingPID(capidOnQueryNotSubjectB);
            double caPidOverQueryLength = reusable.CommonMaths.round(caPid / querySequence.getLength(), 4);
            int overlapingQlen = hit.getOverlappingLength(capidOnQueryNotSubjectB);
            double caPidOverAlignedQueryLength = reusable.CommonMaths.round(caPid / overlapingQlen, 4);
            double percentOfQueryCovered = reusable.CommonMaths.round((double) overlapingQlen / (double) querySequence.getLength(), 4);
            if (percentOfQueryCovered > 0.4) {
                hit.setSummaryString(String.format("%10.3f%10.3f%10.3f%20s%10d", caPidOverQueryLength, caPidOverAlignedQueryLength, percentOfQueryCovered, hit.getHitAccession(), hit.getHitLen()));
                sb.append(String.format("%10.3f%10.3f%10.3f%20s%10d", caPidOverQueryLength, caPidOverAlignedQueryLength, percentOfQueryCovered, hit.getHitAccession(), hit.getHitLen()));
//            System.out.print(caPidOverQueryLength + "(id/qlen), "+ caPidOverAlignedQueryLength+"(id/qaln), "+ percentOfQueryCovered+"(qaln/qlen), "+ hit.getHitAccession() + ", length=" + hit.getHitLen());
//                if (caPidOverQueryLength >= 0.97 || (caPidOverAlignedQueryLength >= 0.98 && percentOfQueryCovered > 0.6)) { //1. "True" hit(s? - should be one!)
                if ((caPidOverQueryLength >= 0.97) || (caPidOverAlignedQueryLength >= 0.90 && percentOfQueryCovered > 0.3)) { //2. Homeologs
                    goodHits.add(new Hit(querySequence.getIdentifierString(), hit.getHitId(), hit, caPidOverQueryLength, caPidOverAlignedQueryLength, percentOfQueryCovered));
                } else { //3. No decent hits                
                    sb.append(" <- Not a decent hit...\n");
                }
            }

        }
        Collections.sort(goodHits, new BestHitPromoterComparator());

        return goodHits;  
    }


    private void linkAlignmentResultsToSequencesInHashMap(HashMap<String, Sequence> sequencesMap, String xmlBlastN) {
        boolean blastXnotN = false;
        BlastAlignmentXmlExtractor blastnAlignmentXmlExtractor = new BlastAlignmentXmlExtractor(xmlBlastN, sequencesMap, blastXnotN);
    }



    class BestHitComparator implements Comparator<BlastHit> {

        @Override
        public int compare(BlastHit hit, BlastHit anotherHit) {
            boolean capidOnQueryNotSubject = true;
            double caPid = hit.getOverlappingPID(capidOnQueryNotSubject);
            int overlapingQlen = hit.getOverlappingLength(capidOnQueryNotSubject);
            double caPidOverAlignedQueryLength = caPid / overlapingQlen;

            double anotherCaPid = anotherHit.getOverlappingPID(capidOnQueryNotSubject);
            int anotherOverlapingQlen = anotherHit.getOverlappingLength(capidOnQueryNotSubject);
            double anotherCaPidOverAlignedQueryLength = anotherCaPid / anotherOverlapingQlen;
            if (caPidOverAlignedQueryLength > anotherCaPidOverAlignedQueryLength) {
                return -1;
            } else if (caPidOverAlignedQueryLength < anotherCaPidOverAlignedQueryLength) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    class BestHitPromoterComparator implements Comparator<Hit> {

        @Override
        public int compare(Hit promoter, Hit anotherPromoter) {

            double caPidOverQueryLength = promoter.getPercIdOverQlenDouble();
            double anotherCaPidOverQueryLength = anotherPromoter.getPercIdOverQlenDouble();
            if (caPidOverQueryLength > anotherCaPidOverQueryLength) {
                return -1;
            } else if (caPidOverQueryLength < anotherCaPidOverQueryLength) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public void writeToFastaFile(ArrayList<Sequence> sequences, String fileName) {
        StringBuilder sb = new StringBuilder();
        for (Sequence s : sequences) {
            sb.append(">").append(s.getIdentifierString()).append("\n").append(s.getSequenceString()).append("\n");
        }
        new OutWriter(fileName, sb.toString());
    }
}
