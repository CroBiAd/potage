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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
public class OutWriter implements Serializable {
    public OutWriter(String fName, ArrayList<String> contents) {
        writeFile(fName, contents);
    }

    public OutWriter(ArrayList<Integer> contents, String fName) {
        writeFileInt(fName, contents);
    }

    public OutWriter(String fName, TreeSet<String> contents) {
        writeFile(fName, contents);
    }

    public OutWriter(String fName, String contents) {
        writeFile(fName, contents);
    }

    public OutWriter(String fName, int[][] matrix) {
        ArrayList<String> contents = new ArrayList();
        for(int i=0; i<matrix.length; i++) {
            String line = "";
            for(int j=0; j<matrix[0].length; j++) {
                line += String.format("%d, ", matrix[i][j]);
            }
            contents.add(""+line);
        }
        writeFile(fName, contents);
    }

    private void writeFile(String fName, ArrayList<String> contents) {
       try {
        BufferedWriter out = new BufferedWriter(new FileWriter(fName));
            for(int i=0; i<contents.size(); i++) {
                out.write(contents.get(i));
                out.newLine();
            }
        out.close();
        }catch (IOException e){ System.err.println("\nCAN'T WRITE\n"); }
//        }catch (IOException e){ System.err.println("\nCANT WRITE\n"); e.printStackTrace(); }
    }

    private void writeFileInt(String fName, ArrayList<Integer> contents) {
       try {
        BufferedWriter out = new BufferedWriter(new FileWriter(fName));
            for(int i=0; i<contents.size(); i++) {
                out.write(contents.get(i).toString());
                out.newLine();
            }
        out.close();
        }catch (IOException e){ System.err.println("\nCAN'T WRITE\n"); }
//        }catch (IOException e){ System.err.println("\nCANT WRITE\n"); e.printStackTrace(); }
    }

    private void writeFile(String fName, TreeSet<String> contents) {
       try {
        BufferedWriter out = new BufferedWriter(new FileWriter(fName));
        Iterator<String> it = contents.iterator();
        while(it.hasNext()) {
            out.write(it.next());
            out.newLine();
        }
        out.close();
        }catch (IOException e){ System.err.println("\nCAN'T WRITE\n"); }
//        }catch (IOException e){ System.err.println("\nCANT WRITE\n"); e.printStackTrace(); }
    }

    private void writeFile(String fName, String contents) {
       try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fName));
            out.write(contents);
            out.newLine();
            out.close();
        }catch (IOException e){ System.err.println("\nCAN'T WRITE\n"); e.printStackTrace(); }
//        }catch (IOException e){e.printStackTrace();   }
    }

}
