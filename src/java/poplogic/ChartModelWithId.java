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
package poplogic;

import org.primefaces.model.chart.BarChartModel;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
public class ChartModelWithId extends BarChartModel{

    
    private final String chartId;
    private final String shortTitle;
    

    public ChartModelWithId( String chartId, String shortTitle) {
        this.shortTitle = shortTitle;
        this.chartId = chartId;
//            System.err.println(model+" "+chartId);
    }


    public String getChartId() {
        return chartId;
    }

//    public String getTitle() {
//        return getTitle().split(" in ")[1];
//    }

    public String getShortTitle() {
        return shortTitle;
    }
    
    
 }
