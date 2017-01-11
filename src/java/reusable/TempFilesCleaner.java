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

import java.io.File;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
public class TempFilesCleaner implements Runnable {

    private final String DIR;
    private final long CUTOFF_TIME;

    public TempFilesCleaner(String DIR, int numDays) {
        this.DIR = DIR;
        CUTOFF_TIME = System.currentTimeMillis() - (numDays * 24L * 60L * 60L * 1000L);
    }

    @Override
    public void run() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd EEE HH:mm:ss");
//        Date date = new Date(CUTOFF_TIME);

        File directory = new File(DIR);
        if (directory.exists() && directory.isDirectory()) {
            File fileList[] = directory.listFiles();
//            System.out.println("Cut-off date: " + dateFormat.format(date));
            for (File file : fileList) {
//                long lastModified = file.lastModified();
                if (file.isFile() && file.lastModified() < CUTOFF_TIME) {
//                    System.out.println("File " + file.getName() + " is to be deleted " + dateFormat.format(new Date(lastModified)));
                    try {
                        file.delete();
//                        Files.delete(file.toPath());
//                    } catch (IOException  e) {
//                        //ignoring attempted deletion of non-exsiting file - might have been deleted by another thread since fileList generated
                    } catch (Exception  e) {
                    }
                } else {
//                    System.out.println("File " + file.getName() + " is to be kept" + dateFormat.format(new Date(lastModified)));
                }
            }

        }
    }

}
