package reusable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 *
 * @author x3002128
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
        }catch (IOException e){ System.err.println("\nCANT WRITE\n"); }
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
        }catch (IOException e){ System.err.println("\nCANT WRITE\n"); }
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
        }catch (IOException e){ System.err.println("\nCANT WRITE\n"); }
//        }catch (IOException e){ System.err.println("\nCANT WRITE\n"); e.printStackTrace(); }
    }

    private void writeFile(String fName, String contents) {
       try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fName));
            out.write(contents);
            out.newLine();
            out.close();
        }catch (IOException e){ System.err.println("\nCANT WRITE\n"); e.printStackTrace(); }
//        }catch (IOException e){e.printStackTrace();   }
    }

}
