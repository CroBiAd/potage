/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.IOException;
import java.io.Serializable;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author rad
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
