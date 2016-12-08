package reusable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.util.ArrayList;
//import java.util.Arrays;

/**
 *
 * @author x3002128
 */
public class InReader  implements Serializable{

    private ArrayList<String> inputContents;
    private String parentPath;

    public InReader(String fileName) {
        File file = new File(fileName);
        parentPath = file.getParent();
        readInput(file);
    }

    public InReader(File directory) {
        if (directory.isDirectory()) {
            inputContents = new ArrayList<String>();
            File[] listOfFiles = directory.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) //              System.out.println("out = "+listOfFiles[i].getName());
                {
                    inputContents.add(listOfFiles[i].getName());
                }
            }
        }
    }

    public String getParentPath() {
        return parentPath;
    }

    

    public ArrayList<String> returnInput() {
        return inputContents;
    }

    private void readInput(File newFile) {
        BufferedReader myData = null;
//    newFile.
        try {
            String inputLine;
            inputContents = new ArrayList<String>();
            myData = new BufferedReader(new FileReader(newFile));
            while ((inputLine = myData.readLine()) != null) {
//            int max = inputLine.indexOf(" ");
//            inputLine = inputLine.substring(0, max);
                inputLine = inputLine.trim();
                inputContents.add(inputLine);
//            System.out.println("Read: "+inputLine);
            }
            inputContents.trimToSize();
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
    }

    public static String readInputToString(String fileName) {
        String newline = System.getProperty("line.separator");
        File newFile = new File(fileName);
        BufferedReader myData = null;
        StringBuilder sb = new StringBuilder();
        try {
            String inputLine;
            myData = new BufferedReader(new FileReader(newFile));
            while ((inputLine = myData.readLine()) != null) {
                inputLine = inputLine;
                sb.append(inputLine).append(newline);
            }
        } catch (FileNotFoundException ex) {
            System.err.println("File not found exception!\n\t" + newFile.getName());
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
        return sb.toString();
    }
}
