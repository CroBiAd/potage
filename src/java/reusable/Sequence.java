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

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
public class Sequence implements Serializable {

    private char[] sequenceChars;
    private String sequenceString;
    private String identifierString;
    private String trimmedString;
    private ArrayList<BlastHit> allBlastXHits;
    private ArrayList<BlastHit> allBlastNHits;
    private String commentString;


    /**
     * Construct a Sequence object from a squence string and an indentifier
     * string
     *
     * @param sequenceString
     * @param identifierString
     */
    public Sequence(String sequenceString, String identifierString) {
        this.sequenceString = sequenceString.trim();
        this.sequenceChars = this.sequenceString.toCharArray();
        this.identifierString = identifierString.trim();
        allBlastXHits = new ArrayList<BlastHit>();
        allBlastNHits = new ArrayList<BlastHit>();
    }

    public int getLength() {
        return sequenceString.length();
    }

    /**
     * @return the sequence String
     */
    public String getSequenceString() {
        return sequenceString;
    }

    /**
     * @return the identifier of the Sequence
     */
    public String getIdentifierString() {
//        String s = identifierString;
//        if (identifierString.endsWith("\n")) {
//            s = identifierString.substring(0, identifierString.length() - 3);
//        }
//        if (s.endsWith("\r")) {
//            s = s.substring(0, identifierString.length() - 3);
//        }
//        return s;
        return identifierString;
    }

    /**
     * Discards the "head" of the sequence upstream from 'position' and returns
     * the reminder
     *
     * @param position
     * @param frame : if negative the sequence is reverse complemented and tail
     * is trimmed off, head returned
     * @return Note : does not modify the sequence object! Essentially returns
     * some substring.
     */
    public String trimUpstreamFromPosition(int position, int frame) {
        if (frame > 0) {
            return sequenceString.substring(position - 1);
        } else if (frame < 0) {
            //trim off the end
            char[] trimmedSequence = getSequenceString().substring(0, position).toCharArray();
            //then reverse complement?
            return getReverseComplementString(trimmedSequence);

        } else {
            System.err.println("frame == 0 ???");
        }
        return null;
    }

    
    public String getReverseComplementString() {
        return getReverseComplementString(sequenceChars);
    }

    public String getReverseComplementString(char[] sequenceChars) {
        StringBuilder sb = new StringBuilder(sequenceChars.length);
        for (int i = sequenceChars.length - 1; i > -1; --i) {
            char c;
            char before = sequenceChars[i];
            if (Character.isLowerCase(before)) {
                c = Character.toUpperCase(before);
            } else {
                c = before;
            }

            switch (c) {
                case 'A':
                    c = 'T';
                    break;
                case 'T':
                    c = 'A';
                    break;
                case 'U':
                    c = 'A';
                    break;
                case 'C':
                    c = 'G';
                    break;
                case 'G':
                    c = 'C';
                    break;
                case 'N':
                    c = 'N';
                    break;
                case 'Y':
                    c = 'R';
                    break;
                case 'R':
                    c = 'Y';
                    break;
                case 'K':
                    c = 'M';
                    break;
                case 'M':
                    c = 'K';
                    break;
                case 'B':
                    c = 'V';
                    break;
                case 'V':
                    c = 'B';
                    break;
                case 'H':
                    c = 'D';
                    break;
                case 'D':
                    c = 'H';
                    break;
                case 'W':
                    break;
                case 'S':
                    break;
                case '.':
                    break;
                case '-':
                    break;
                default:
                    int j = 0;
                    break;
            }
            if (Character.isLowerCase(before)) {
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }

        }
        return sb.toString();
    }
    
    public boolean containsNonIUPAC() {
        for (int i = sequenceChars.length - 1; i > -1; --i) {
            char c;
            char before = sequenceChars[i];
            if (Character.isLowerCase(before)) {
                c = Character.toUpperCase(before);
            } else {
                c = before;
            }

            switch (c) {
                case 'A':
                    break;
                case 'T':
                    break;
                case 'U':
                    break;
                case 'C':
                    break;
                case 'G':
                    break;
                case 'N':
                    break;
                case 'Y':
                    break;
                case 'R':
                    break;
                case 'K':
                    break;
                case 'M':
                    break;
                case 'B':
                    break;
                case 'V':
                    break;
                case 'H':
                    break;
                case 'D':
                    break;
                case 'W':
                    break;
                case 'S':
                    break;
                case '.':
                    break;
                case '-':
                    break;
                default:
                    return true;
            }
        }
        return false;
    }
    
//    public String getReverseComplementString() {
//        return getReverseComplementString(sequenceChars);
//    }
//
//    public String getReverseComplementString(char[] sequenceChars) {
//        StringBuilder sb = new StringBuilder(sequenceChars.length);
//        for (int i = sequenceChars.length - 1; i > -1; --i) {
//            char c = sequenceChars[i];
//            switch (c) {
//                case 'A':
//                    sb.append("T");
//                    break;
//                case 'T':
//                    sb.append("A");
//                    break;
//                case 'C':
//                    sb.append("G");
//                    break;
//                case 'G':
//                    sb.append("C");
//                    break;
//                case 'a':
//                    sb.append("t");
//                    break;
//                case 't':
//                    sb.append("a");
//                    break;
//                case 'c':
//                    sb.append("g");
//                    break;
//                case 'g':
//                    sb.append("c");
//                    break;
//                default:
//                    sb.append(c);
//            }
//        }
//        return sb.toString();
//    }

    /**
     * Only meant for use with TestDataGenerator, which first sets the value of
     * that field this is supposed to be the sequence trimmed such that it
     * starts with an ATG
     */
    public String getTrimmedString() {
        return trimmedString;
    }

    /**
     * Only meant for use with TestDataGenerator
     *
     * @param trimmedString
     */
    public void setTrimmedString(String trimmedString) {
        this.trimmedString = trimmedString;
    }

    public void addBlastXHit(BlastHit hit) {
        if(hit.getQueryId().compareTo(this.getIdentifierString()) == 0) {
            allBlastXHits.add(hit);
        }
    }

    public ArrayList<BlastHit> getAllBlastXHits() {
        return allBlastXHits;
    }

    public void addBlastNHit(BlastHit hit) {
        if(hit.getQueryId().compareTo(this.getIdentifierString()) == 0) {
            allBlastNHits.add(hit);
        }
    }

    public ArrayList<BlastHit> getAllBlastNHits() {
        return allBlastNHits;
    }

    public String getCommentString() {
        return commentString;
    }

    public void setCommentString(String commentString) {
        this.commentString = commentString;
    }

    public BlastHit getHihestCapIDBlastNHit(boolean capidOnQueryNotSubject, int denominator) {
        BlastHit selected = allBlastNHits.get(0);
        for (int i = 1; i < allBlastNHits.size(); i++) {
            BlastHit hit = allBlastNHits.get(i);
            if (selected.getOverlappingPID(capidOnQueryNotSubject) / denominator < hit.getOverlappingPID(capidOnQueryNotSubject) / denominator) {
//                System.out.println(selected.getOverlappingPID(capidOnQueryNotSubject)/denominator);
                selected = hit;
            }
        }
        return selected;
    }

    public BlastHit getHihestCapIDBlastXHit(boolean capidOnQueryNotSubject, int denominator) {
        if (allBlastXHits.isEmpty()) {
            return null;
        }
        BlastHit selected = allBlastXHits.get(0);
        for (int i = 1; i < allBlastXHits.size(); i++) {
            BlastHit hit = allBlastXHits.get(i);
            if (selected.getOverlappingPID(capidOnQueryNotSubject) / denominator < hit.getOverlappingPID(capidOnQueryNotSubject) / denominator) {
//                System.out.println(selected.getOverlappingPID(capidOnQueryNotSubject)/denominator);
                selected = hit;
            }
        }
        return selected;
    }

    public char[] getSequenceChars() {
        return sequenceChars;
    }

//    public boolean isNonACGT() {
//        return nonACGT;
//    }
//
//    public void setNonACGT(boolean nonACGT) {
//        this.nonACGT = nonACGT;
//    }
    public void writeToFastaFile(String fileName) {
        StringBuilder sb = new StringBuilder(">");
        sb.append(getIdentifierString());
        sb.append("\n");
        sb.append(getSequenceString());
        sb.append("\n");
        new OutWriter(fileName, sb.toString());
    }
}
