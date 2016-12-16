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
package beans;

import java.io.IOException;
import java.io.Serializable;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
@SessionScoped
@ManagedBean(name = "loginController")
public class LoginController  implements Serializable{

    public void timeout() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect("expired.xhtml");
    }
    
    public void redirect() throws IOException {
//        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

         FacesContext fc = FacesContext.getCurrentInstance();
         ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler)fc.getApplication().getNavigationHandler();
         nav.performNavigation("potage.xhtml");
    
    }

//    public void isSessionValid() {
//
//        try {
//            final FacesContext context = FacesContext.getCurrentInstance();
//            final HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
//            final HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//            session.invalidate();
//        } catch (Exception e) {
//            System.err.println(" Error invalidating session :: " + e.getMessage());
//        }
//     <p:idleMonitor timeout="${session.maxInactiveInterval*1000}">
//            <p:ajax event="idle" listener="#{loginController.isSessionValid}"
//                    oncomplete="Javascript:window.location='${request.contextPath}/*sessionTimeout=true'" />
//        </p:idleMonitor>
    
//    }
}
