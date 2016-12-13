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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
public class ExecProcessor extends Thread  implements Serializable{

    private InputStream inputStream;
    private String streamTypeString;
    private OutputStream outputStream;
    
    private String stdOutStore;

    public ExecProcessor(InputStream inputStream, String streamType) {
        this.inputStream = inputStream;
        this.streamTypeString = streamType;
    }

    public ExecProcessor(InputStream inputStream, String streamType, OutputStream redirect) {
        this.inputStream = inputStream;
        this.streamTypeString = streamType;
        this.outputStream = redirect;
    }

    @Override
    public void run() {
        try {
            StringBuilder sb = new StringBuilder();
            PrintWriter printWriter = null;
            if (outputStream != null) {
                printWriter = new PrintWriter(outputStream);
            }
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (printWriter != null) {
                    printWriter.println(line);
                    sb.append(line);
                }
                if (outputStream == null) {
                    if(streamTypeString.isEmpty())
                        System.out.println(streamTypeString + line);
                    sb.append(line);
                }
            }
            if (printWriter != null) {
                printWriter.flush();
            }
            stdOutStore = sb.toString();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    public String getStdOutStore() {
        return stdOutStore;
    }

    
    public static Process execute(String cmd[]) {
        return execute(cmd, null);
    }

    /**
     * CAUTION using output redirection to String outFileNameString does not always work!
     * @param cmd
     * @param outFileNameString
     * @return 
     */
    public static Process execute(String cmd[], String outFileNameString) {
        Process proc = null;
        Runtime rt = Runtime.getRuntime();
        try {
            proc = rt.exec(cmd);
            reusable.ExecProcessor errorStreamProcessor = new reusable.ExecProcessor(proc.getErrorStream(), "stdErr");

            FileOutputStream fileOutputStream = null;
            reusable.ExecProcessor inputStreamProcessor;
            if (outFileNameString != null) {
                fileOutputStream = new FileOutputStream(outFileNameString);
                inputStreamProcessor = new reusable.ExecProcessor(proc.getInputStream(), "", fileOutputStream);
            } else {
                inputStreamProcessor = new reusable.ExecProcessor(proc.getInputStream(), "");
//                inputStreamProcessor = new reusable.ExecProcessor(proc.getInputStream(), "stdOut: ");
            }
//            reusable.ExecProcessor inputStreamProcessor = new reusable.ExecProcessor(proc.getInputStream(), "stdOut");

            errorStreamProcessor.start();
            inputStreamProcessor.start();
            
            
            int exitVal = proc.waitFor();
            if (exitVal != 0) {
                System.err.println("ExitValue: " + exitVal+ " executing:");
                for (String string : cmd) {
                    System.err.print(string+" ");                    
                }
                System.err.println();
//                System.exit(exitVal);
            }
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }          
        } catch (IOException t) {
            t.printStackTrace();
        } catch ( InterruptedException t) {
            t.printStackTrace();
        }
        return proc;
    }
    
    /**
     * CAUTION using output redirection to String outFileNameString does not always work!
     * @param cmd
     * @param outFileNameString
     * @return 
     */
    public static String executeString(String cmd[]) {
        Process proc = null;
        Runtime rt = Runtime.getRuntime();
        String stdOutStored = "";
        try {
            proc = rt.exec(cmd);
            reusable.ExecProcessor errorStreamProcessor = new reusable.ExecProcessor(proc.getErrorStream(), "stdErr");

            FileOutputStream fileOutputStream = null;
            reusable.ExecProcessor inputStreamProcessor;
            inputStreamProcessor = new reusable.ExecProcessor(proc.getInputStream(), "don't print");
//            reusable.ExecProcessor inputStreamProcessor = new reusable.ExecProcessor(proc.getInputStream(), "stdOut");

            errorStreamProcessor.start();
            inputStreamProcessor.start();

            int exitVal = proc.waitFor();
//            int i = 0;
            while (inputStreamProcessor.isAlive()) {              //ensuring I can fetch processes stdout  
//                if( i% 100 == 0)
//                    System.out.print(".");
//                if( i% 10000 == 0)
//                    System.out.println();
//                i++;
            }
            stdOutStored = inputStreamProcessor.getStdOutStore();
            if (exitVal != 0) {
                System.err.println("ExitValue: " + exitVal+ " executing:");
                for (String string : cmd) {
                    System.err.print(string+" ");                    
                }
                System.err.println();                    
//                System.exit(exitVal);
            }
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }          
        } catch (IOException t) {
            t.printStackTrace();
        } catch ( InterruptedException t) {
            t.printStackTrace();
        }
        return stdOutStored;
    }
    
    public static void email (String contents, String subject, String emailAddressTo, String emailAddressFrom) {
        String scriptName = "/dev/shm/email" + Math.random() + ".sh";
        String mailFileName = "/dev/shm/email" + Math.random() + ".txt";
        String mailFileBody = "date: todays-date\nto: "+emailAddressTo
                + "\nFrom: "+emailAddressFrom
                + "\nSubject: "+subject
                + "\n\n"+contents;
        String script = "#!/bin/bash\n"
                + "sendmail -i -f  "+emailAddressFrom+" "+emailAddressTo+" < "+mailFileName;
//        String script = "#!/bin/bash\necho \""+contents+"\" | mail -s \""+subject+"\" "+emailAddress+"";
//        String script = "#!/bin/bash\n echo \"${1}\" | mail -s \"${2}\" ${3}";
        OutWriter outWriter = new reusable.OutWriter(scriptName, script);
        outWriter = new reusable.OutWriter(mailFileName, mailFileBody);
        String[] cmd0 = {"chmod", "+x", scriptName};
        Process proc0 = reusable.ExecProcessor.execute(cmd0);
//        String[] cmd = {scriptName, contents, subject, emailAddress};
        String[] cmd = {scriptName};
//        System.out.println();
//        for(String s: cmd) {
//            System.out.print(s+" ");
//        }
//        System.out.println();
        Process proc = reusable.ExecProcessor.execute(cmd);
        String[] cmd1 = {"rm", scriptName, mailFileName};
        Process proc1 = reusable.ExecProcessor.execute(cmd1);
    }
    
    
    
}