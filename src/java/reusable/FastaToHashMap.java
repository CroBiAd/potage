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
import java.util.HashMap;
import java.util.TreeSet;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
public class FastaToHashMap  implements Serializable{

    
    /**
     * Reads in a fasta file and generates a HashMap of Sequence objects input
     * fasta sequences can be broken over multiple lines
     *
     * @param fastaFileName
     * @return
     */
    public static HashMap<String, Sequence> hashMapOfSequencesFromFasta(String fastaFileName, int initialSize, TreeSet<String> requiredContigIds) {
        HashMap<String, Sequence> sequencesMap = new HashMap<String, Sequence>(initialSize);
        InReader in = new InReader(fastaFileName);
        ArrayList<String> contents = in.returnInput();
        String id = "";
        StringBuilder seqBuilder = new StringBuilder();
        for (int i = 0; i < contents.size(); i++) {
            String line = contents.get(i);
            if (line.startsWith(">")) {
                if (seqBuilder.length() > 0) {
                    String identifierString[] = id.split(" ");
                    String key = identifierString[0];
                    if (requiredContigIds == null || requiredContigIds.contains(key)) { //addedd to preserve memory only!
                        Sequence previous = sequencesMap.put(key, new Sequence(seqBuilder.toString(), id));
                        if (previous != null) {
                            System.err.println("Fatal error: non unique sequence identifier: " + key + " in " + fastaFileName);
                            System.exit(5);
                        }
                    }
                    seqBuilder = new StringBuilder();
                }
                id = line.substring(1);
            } else {
                seqBuilder.append(line.trim());
            }
        }
        String identifierString[] = id.split(" ");
        String key = identifierString[0];
        if (requiredContigIds == null || requiredContigIds.contains(key)) { //addedd to preserve memory only!
            Sequence previous = sequencesMap.put(key, new Sequence(seqBuilder.toString(), id));
            if (previous != null) {
                System.err.println("Fatal error: non unique sequence identifier: " + key + " in " + fastaFileName);
                System.exit(5);
            }
        }
        return sequencesMap;
    }
    
    /**
     * Given a fasta file name
     * generates a hashmap Key: sequence identifier Value: Sequence object
     * (identifier plus the sequence, but also provides interface for e.g.
     * reverse-complementing, triming and hopefully more in the future)
     *
     * @param fastaFileName
     * @param initialSize
     * @return
     */
    public static HashMap<String, Sequence> fastaToHashMapWRONG(String fastaFileName, int initialSize) {
//        //load all ogahira sequences that ware previously labeled as "good" by Ute (in principle these aligned to the "correct" chromosome-arm)
//        reusable.InReader in = new InReader(fastaFileName);
//
//        //Create and populate a hashmap:
//        //Key: ogihara sequence identifier
//        //Value: Sequence object (identifier plus the sequence, but also provides interface for e.g. reverse-complementing, triming and hopefully more in the future)
//        HashMap<String, Sequence> sequencesMap = new HashMap<>(initialSize);
//        Iterator<String> it = in.returnInput().iterator();
//        while (it.hasNext()) {
//            String line = it.next();
//            if (line.startsWith(">")) {
//                String identifierString[] = line.substring(1).split(" ");
//                String key = identifierString[0];
//                Sequence previous = sequencesMap.put(key, new Sequence(it.next(), line.substring(1)));
//                if(previous != null ) {
//                    System.err.println("Fatal error: non unique sequence identifier: "+line.substring(1)+" in "+fastaFileName);
//                    System.exit(5);
//                }
//            }
//        }
        System.err.println("INCORRECT METHOD USE FastaOps.hashMapOfSequencesFromFasta !!!!");
        System.exit(5);
        return null;
    }
    
//    public static HashMap<String, Sequence> fastaToHashMapFromListOfStrings(ArrayList<String> fastaSequences) {
//        //Create and populate a hashmap:
//        //Key: ogihara sequence identifier
//        //Value: Sequence object (identifier plus the sequence, but also provides interface for e.g. reverse-complementing, triming and hopefully more in the future)
//        HashMap<String, Sequence> sequencesMap = new HashMap<String, Sequence>(fastaSequences.size());
//        Iterator<String> it = fastaSequences.iterator();
//        while (it.hasNext()) {
//            String line = it.next();
//            if (line.startsWith(">")) {
//                sequencesMap.put(line.substring(1), new Sequence(it.next(), line.substring(1)));
//            }
//        }
//        return sequencesMap;
//    }
    
//    public static HashMap<String, Sequence> fastaToHashMapFromListOfSequences(ArrayList<Sequence> fastaSequences) {
//        //Create and populate a hashmap:
//        //Key: ogihara sequence identifier
//        //Value: Sequence object (identifier plus the sequence, but also provides interface for e.g. reverse-complementing, triming and hopefully more in the future)
//        HashMap<String, Sequence> sequencesMap = new HashMap<String, Sequence>(fastaSequences.size());
//        Iterator<Sequence> it = fastaSequences.iterator();
//        while (it.hasNext()) {
//            Sequence s = it.next();
//            sequencesMap.put(s.getIdentifierString(), s);
//        }
//        return sequencesMap;
//    }

}
