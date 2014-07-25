/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reusable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 *
 * @author rad
 */
public class BlastHit implements Comparable<BlastHit>, Serializable {

    private int hitNum;
    private ArrayList<BlastHitHsp> hitHSPs; //need some sorted structure? Different orderings might be of use, e.g. hsps as ordered by blast, or according to their order along the sequence
    private BlastHitHsp topHsp;
    private String queryId;
    private int queryLength;
    private String hitId;
    private String hitDef;
    private String hitAccession;
    private int hitLen;
    private int overlappingLengthQuery = -1;
    private int overlappingLengthSubject = -1;
    private double overlappingPIDQuery = -1;
    private double overlappingPIDSubject = -1;
    private String summaryString;

    public BlastHit() {
        hitHSPs = new ArrayList<BlastHitHsp>();
    }

    /**
     * CAREFUL!!!! Changed to compare by hit id first, then by query id! may not be suitable for some uses with promoters but meant for klausdata analysis
     * @param another
     * @return 
     */
    @Override    
    public int compareTo(BlastHit another) {
        int queryCompare = getHitId().compareTo(another.getHitId());
        if (queryCompare == 0) {
            return getQueryId().compareTo(another.getQueryId());
        } else {
            return queryCompare;
        }
    }

    public String getSummaryString() {
        return summaryString;
    }

    public void setSummaryString(String summaryString) {
        this.summaryString = summaryString;
    }
    
    
    public void setHitNum(int hitNum) {
        this.hitNum = hitNum;
    }

    public void setHitId(String hitId) {
        this.hitId = hitId;
    }

    public void setHitDef(String hitDef) {
        this.hitDef = hitDef;
    }

    public void setHitAccession(String hitAccession) {
        this.hitAccession = hitAccession;
    }

    public void setHitLen(int hitLen) {
        this.hitLen = hitLen;
    }

    public void addHsp(BlastHitHsp hsp) {
        hitHSPs.add(hsp);
        if (hsp.isTopHspOfAHit()) {
            topHsp = hsp;
        }
    }

    public ArrayList<BlastHitHsp> getHitHSPs() {
        return hitHSPs;
    }

    public String getHitId() {
        return hitId;
    }

    public String getHitDef() {
        return hitDef;
    }

    public String getHitAccession() {
        return hitAccession;
    }

    public int getHitLen() {
        return hitLen;
    }

    public int getHitNum() {
        return hitNum;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public int getQueryLength() {
        return queryLength;
    }

    public void setQueryLength(int queryLength) {
        this.queryLength = queryLength;
    }
  
    /**
     * Only reflects position in blast output, not necessarily best hit!
     *
     * @return
     */
    public boolean isTopHit() {
        if (hitNum == 1) {
            return true;
        }
        return false;
    }

    /**
     * Only reflects position in blast output, not necessarily best hsp!
     *
     * @return
     */
    public BlastHitHsp getTopHsp() {
        return topHsp;
    }

    /**
     * Returns hsp covering the postions closest to the begining of the query
     * (qstart)
     *
     * @return
     */
    public BlastHitHsp getLeftmostHsp() {
        BlastHitHsp leftmost = hitHSPs.get(0);

        if (leftmost.getQseqid().startsWith("ogihara_1449-1.12907.4A_beheaded_n_3")) {
            int i = 1;
        }

        int leftmostStart = leftmost.getActualQstart();
        if (leftmostStart == 1) {
            return leftmost;
        }
        for (int i = 1; i < hitHSPs.size(); i++) {
            BlastHitHsp currentHsp = hitHSPs.get(i);
            int currentHspStart = currentHsp.getActualQstart();
            if (currentHspStart < leftmostStart) {
                leftmost = currentHsp;
                leftmostStart = currentHspStart;
                if (currentHspStart == 1) {
                    break;
                }
            }
        }
        return leftmost;
    }

    /**
     * Given an ArrayList of alignment strings from blast (meant to alignments
     * of various fragments of sequence A to sequence B) calculate the total
     * length covered by these possibly overlapping alignments
     *
     * @param alignmentStrings
     * @param queryOrSubject
     * @param startIndex 6 or 8 (column in blast tabular counting from 0)
     * @param endIndex 7 or 9 (column in blast tabular counting from 0)
     * @return
     */
    private int calaculateLengthOfOverlappingAlignments(boolean queryNotSubject) {
        int size = hitHSPs.size();
        int len1;
        int start;// = Integer.MIN_VALUE;
        int end;// = Integer.MIN_VALUE;
        if (size == 1) {
            BlastHitHsp hsp = hitHSPs.get(0);
            if (queryNotSubject) {
                start = hsp.getQstart();
                end = hsp.getQend();
            } else {
                start = hsp.getSstart();
                end = hsp.getSEnd();
            }
            int min = Math.min(start, end);
            int max = Math.max(start, end);
            len1 = (max - min + 1);
            return len1;
        } else if (size == 2) {
            BlastHitHsp hsp = hitHSPs.get(0);
            if (queryNotSubject) {
                start = hsp.getQstart();
                end = hsp.getQend();
            } else {
                start = hsp.getSstart();
                end = hsp.getSEnd();
            }
            int min0 = Math.min(start, end);
            int max0 = Math.max(start, end);

            BlastHitHsp hsp1 = hitHSPs.get(1);
            if (queryNotSubject) {
                start = hsp1.getQstart();
                end = hsp1.getQend();
            } else {
                start = hsp1.getSstart();
                end = hsp1.getSEnd();
            }

            int min1 = Math.min(start, end);
            int max1 = Math.max(start, end);

            //no overlap
            if (max0 < min1 || max1 < min0) {
                len1 = max0 - min0 + max1 - min1 + 2;
            } else {
                len1 = Math.max(max0, max1) - Math.min(min0, min1) + 1;
            }
            return len1;
        }

        //SO essentially cardinality of the union of the sets
        TreeSet<Integer> unionSet = new TreeSet<Integer>();
        for (int i = 0; i < size; i++) {
            BlastHitHsp hsp = hitHSPs.get(i);
            if (queryNotSubject) {
                start = hsp.getQstart();
                end = hsp.getQend();
            } else {
                start = hsp.getSstart();
                end = hsp.getSEnd();
            }
            int min = Math.min(start, end);
            int max = Math.max(start, end);
            for (int j = min; j < max + 1; j++) {
                unionSet.add(j);
            }
        }
        return unionSet.size();
    }

    /**
     * trying calp like approach for calculating cummulative (max) pid...
     *
     * 0. Given a blast hit. 1. For each position of query OR subject covered by
     * hsp(s), record the maxPID among hsp's that cover that position. 2.
     * Calculate the averageMaxPID by dividing the cummulative sum of maxPID by
     * the number of positions covered... or e.g. query length
     *
     */
      private double calaculateMaxOverlapIdentity(boolean queryNotSubject) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int start;
        int end;
        //Identify the extremities - PERHAPS COULD DO WITHOUT, instead arrSize = max(qlen,slen)
        for (BlastHitHsp hsp : hitHSPs) {
            if (queryNotSubject) {
                start = hsp.getActualQstart();
                end = hsp.getActualQend();
            } else {
                start = hsp.getActualSstart();
                end = hsp.getActualSend();
            }
            if (start < min) {
                min = start;
            }
            if (end > max) {
                max = end;
            }
        }
        int arrSize = max - min + 1;
        //assuring that we don't count forward and reverse hsps together
        double maxIdentForward[] = new double[arrSize]; 
        double maxIdentReverse[] = new double[arrSize];
      
        for (BlastHitHsp hsp : hitHSPs) {
            if (queryNotSubject) {
                start = hsp.getActualQstart();
                end = hsp.getActualQend();
            } else {
                start = hsp.getActualSstart();
                end = hsp.getActualSend();
            }
            double pid = hsp.getPident();
            for (int i = start - min; i < end - min + 1; i++) {
                if(hsp.getQframe()> 0 && hsp.getSframe() >= 0) {
                    if(pid > maxIdentForward[i]) {
                        maxIdentForward[i] = pid;
                    }
                } else if((hsp.getQframe()< 0 && hsp.getSframe() >= 0)|| (hsp.getQframe() >= 0 && hsp.getSframe() < 0)) { 
                    if(pid > maxIdentReverse[i]) {
                        maxIdentReverse[i] = pid;
                    }
                } else if(hsp.getQframe()< 0 && hsp.getSframe() < 0) { 
                    System.err.println("Fatal error for ("+this.getQueryId()+", "+this.hitId+")");
                    System.err.println("qframe="+hsp.getQframe());
                    System.err.println("sframe="+hsp.getSframe());
                    System.err.println("Why would blast reverse both query and subject?!");
//                    System.exit(1);                    
                } else {
                    System.err.println("Fatal error for ("+this.getQueryId()+", "+this.hitId+")");
                    System.err.println("qframe="+hsp.getQframe());
                    System.err.println("sframe="+hsp.getSframe());
                    System.err.println("Perhaps trying to retrieve hit frame of a blastx hit? That would be 0 by default."); //valid comment if hsp.getSframe and not hsp.getQframe()< 0
//                    System.exit(1);
                }
            }
        }
//        System.out.println("max PID array:");
        double cummPidForward = 0;
        double cummPidReverse = 0;
        for (int i = 0; i < maxIdentReverse.length; i++) {
//            System.out.printf("%.2f ", pid);
            cummPidForward += maxIdentForward[i];
            cummPidReverse += maxIdentReverse[i];
        }
        if(cummPidForward != 0 && cummPidReverse != 0) {
            System.err.println(this.getQueryId()+", "+this.hitId+", cummulative PID \treverse = "+reusable.CommonMaths.round(cummPidReverse, 3)+", forward = "+reusable.CommonMaths.round(cummPidForward,3));
        }
        return Math.max(cummPidForward, cummPidReverse);
    }
      
//    private double calaculateMaxOverlapIdentity(boolean queryNotSubject) {
//        int min = Integer.MAX_VALUE;
//        int max = Integer.MIN_VALUE;
//        int start;
//        int end;
//        //Identify the extremities
//        for (BlastHitHsp hsp : hitHSPs) {
//            if (queryNotSubject) {
//                start = hsp.getActualQstart();
//                end = hsp.getActualQend();
//            } else {
//                start = hsp.getActualSstart();
//                end = hsp.getActualSend();
//            }
//            if (start < min) {
//                min = start;
//            }
//            if (end > max) {
//                max = end;
//            }
//        }
//        int arrSize = max - min + 1;
//        double maxIdent[] = new double[arrSize];
//        for (BlastHitHsp hsp : hitHSPs) {
//            if (queryNotSubject) {
//                start = hsp.getActualQstart();
//                end = hsp.getActualQend();
//            } else {
//                start = hsp.getActualSstart();
//                end = hsp.getActualSend();
//            }
//            double pid = hsp.getPident();
//            for (int i = start - min; i < end - min + 1; i++) {
//                if (pid > maxIdent[i]) {
//                    maxIdent[i] = pid;
//                }
//            }
//        }
////        System.out.println("max PID array:");
//        double cummPID = 0;
//        int usedCells = 0;
//        for (double pid : maxIdent) {
////            System.out.printf("%.2f ", pid);
//            cummPID += pid;
//            if (pid > 0) {
//                usedCells++;
//            }
//        }
////        System.out.println();
////        double result = cummPID / usedCells;
////        double result = cummPID / qlen;
////        return result; 
//        return cummPID;
//    }
    
    public double getOverlappingPID(boolean queryNotSubject) {
        if (queryNotSubject) {
            if (overlappingPIDQuery == -1) {
                overlappingPIDQuery = calaculateMaxOverlapIdentity(queryNotSubject);
            }
            return overlappingPIDQuery;
        } else {
            if (overlappingPIDSubject == -1) {
                overlappingPIDSubject = calaculateMaxOverlapIdentity(queryNotSubject);
            }
            return overlappingPIDSubject;
        }
    }

    public int getOverlappingLength(boolean queryNotSubject) {
        if (queryNotSubject) {
            if (overlappingLengthQuery == -1) {
                overlappingLengthQuery = calaculateLengthOfOverlappingAlignments(queryNotSubject);
            }
            return overlappingLengthQuery;
        } else {
            if (overlappingLengthSubject == -1) {
                overlappingLengthSubject = calaculateLengthOfOverlappingAlignments(queryNotSubject);
            }
            return overlappingLengthSubject;
        }
    }
    
    public int[] getRangeMinMax(boolean queryNotSubject) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int start;
        int end;
        //Identify the extremities
        for (BlastHitHsp hsp : hitHSPs) {
            if (queryNotSubject) {
                start = hsp.getActualQstart();
                end = hsp.getActualQend();
            } else {
                start = hsp.getActualSstart();
                end = hsp.getActualSend();
            }
            if (start < min) {
                min = start;
            }
            if (end > max) {
                max = end;
            }
        }
        int range[] = {min, max};
        return range;
    }
}
