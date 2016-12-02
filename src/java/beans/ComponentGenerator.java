/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.behavior.ajax.AjaxBehaviorListenerImpl;
import org.primefaces.component.chart.Chart;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.dialog.Dialog;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.context.RequestContext;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import poplogic.Gene;

/**
 *
 * @author rad
 */
public class ComponentGenerator implements Serializable {

//    public DefaultMenuModel generateDynamicMenuForOtherContigs() {
//
//        DefaultMenuModel menuModel = new DefaultMenuModel();
//        String genomes[] = {"A", "B", "D"};
//        for (String g : genomes) {
//            DefaultSubMenu submenu = new DefaultSubMenu();
//            submenu.setLabel(g);
//            for (int i = 1; i < 8; i++) {
//                String chrArm = "" + i;
//                String chrArm1 = chrArm + g;
//                DefaultMenuItem item = new DefaultMenuItem();
//                item.setValue(chrArm1);
//                item.setId("_" + chrArm1);
//                item.setUpdate(":formCentre:dataTable");
//                item.setOnstart("PF('blockUI').show()");
//                item.setOncomplete("PF('blockUI').hide()");
//                item.setCommand("#{mainBean.onSelect(\'" + chrArm1 + "\')}");
////                item.setIcon("/images/chr_logos/" + chrArm1 + ".png"); //would have to be proper icons not png ????
//                submenu.addElement(item);
//            }
//            menuModel.addElement(submenu);
//        }
//        return menuModel;
//    }

    /**
     * not used with dock
     * @return 
     */
    public DefaultMenuModel generateDynamicMenuMultilevel() {
        DefaultMenuModel menuModel = new DefaultMenuModel();
        String genomes[] = {"A", "B", "D"};
        String arms[] = {"L", "S"};
        for (int i = 1; i < 8; i++) {
            DefaultSubMenu submenu = new DefaultSubMenu();
            submenu.setLabel("Chromosome " + i);
            String chrArm = "" + i;
            for (String g : genomes) {
                String chrArm1 = chrArm + g;
                DefaultMenuItem item = new DefaultMenuItem();
                item.setValue(chrArm1);
                item.setId("_" + chrArm1);
                item.setUpdate(":formCentre:dataTable");
                item.setOnstart("PF('blockUI').show()");
                item.setOncomplete("PF('blockUI').hide()");
                item.setCommand("#{mainBean.onSelect(\'" + chrArm1 + "\')}");
//                item.setIcon("/images/chr_logos/" + chrArm1 + ".png"); //would have to be proper icons not png ????
                submenu.addElement(item);
            }
            menuModel.addElement(submenu);
        }
        return menuModel;
    }

    public DefaultMenuModel generateDynamicMenuSingleLevel() {
        DefaultMenuModel menuModelSimple = new DefaultMenuModel();

        //add help icon/button
        DefaultMenuItem helpMenuItem = new DefaultMenuItem();
        helpMenuItem.setId("helpIcon");
        helpMenuItem.setIcon("/images/help1.png");
        helpMenuItem.setValue("Help");
        helpMenuItem.setOnclick("PF('helpPanel').show()");
        menuModelSimple.addElement(helpMenuItem);

        //add search button-icon
        DefaultMenuItem searchMenuItem = new DefaultMenuItem();
        searchMenuItem.setId("searchAllIcon");
        searchMenuItem.setIcon("/images/search5.png");
        searchMenuItem.setValue("Search by identifier");
        searchMenuItem.setOnclick("PF('searchPanel').show()");
//        elem.setId("_searchButton");
        menuModelSimple.addElement(searchMenuItem);

//add search button-icon
        DefaultMenuItem searchSeqMenuItem = new DefaultMenuItem();
        searchSeqMenuItem.setId("searchSeqIcon");
        searchSeqMenuItem.setIcon("/images/searchATGC.png");
        searchSeqMenuItem.setValue("Search by sequence");
        searchSeqMenuItem.setOnclick("PF('searchSeqPanel').show()");
//        elem.setId("_searchButton");
        menuModelSimple.addElement(searchSeqMenuItem);

        DefaultMenuItem searchOtherContigs = new DefaultMenuItem();
        searchOtherContigs.setId("searchOtherContigsIcon");
        searchOtherContigs.setIcon("/images/list.png");
        searchOtherContigs.setValue("Search by genetic map position");
        searchOtherContigs.setOnclick("PF('allContigsDialog').show()");
        menuModelSimple.addElement(searchOtherContigs);

        String genomes[] = {"A", "B", "D"};
        for (int i = 1; i < 8; i++) {
            String chrArm = "" + i;
            for (String g : genomes) {
                String chrArm1 = chrArm + g;
                DefaultMenuItem item = new DefaultMenuItem();
                item.setValue("Display chromosome " + chrArm1);
//                item.setId("_" + chrArm1);
                item.setUpdate(":formCentre:dataTable,:formCentre:chartsGrid");
                item.setOnstart("PF('blockUI').show()");
//                item.setOncomplete("PF('blockUI').hide();PF('dataTable').filter()");
//                item.setOncomplete("PF('dataTable').filter();PF('blockUI').hide();");
                item.setOncomplete("PF('blockUI').hide();");
                item.setCommand("#{mainBean.onSelect(\'" + chrArm1 + "\')}");
                item.setIcon("/images/chr_logos/" + chrArm1 + ".png");
                menuModelSimple.addElement(item);
            }
        }

        return menuModelSimple;
    }

    public ArrayList<UIComponent> generateDialogContainers(int start, int stop, String parentId) {
        ArrayList<UIComponent> availableDialogContainers = new ArrayList<>(stop - start + 1);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent form = facesContext.getViewRoot().findComponent(parentId);
        for (int i = start; i < stop; i++) {
            OutputLabel outputLabel = new OutputLabel();
            String id = "dialogsDisplay_" + i;
            outputLabel.setId(id);
            availableDialogContainers.add(outputLabel);
            form.getChildren().add(outputLabel);
//            OutputPanel outputPanel = new OutputPanel();
//            String id = "dialogsDisplay_" + i;
//            outputPanel.setId(id);
//            availableDialogContainers.add(outputPanel);
//            form.getChildren().add(outputPanel);
        }
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.update(parentId);
//        System.err.println("\tDialog contrainers generated!");
        return availableDialogContainers;
    }

    public Chart generateChart(String idSuffix, int dialogWidth, int dialogHeight, BarChartModel barModel) {

        int chartWidth = dialogWidth - dialogWidth / 15; /// 20;
        int chartHeight = dialogHeight - dialogHeight / 25; /// 25;
        Chart chartComponent = new Chart();
        chartComponent.setType("bar");
        chartComponent.setModel(barModel);
        chartComponent.setStyle("height:" + chartHeight + "px; width: " + chartWidth + "px");
        chartComponent.setId("chart_" + idSuffix);

        return chartComponent;
    }

    public Dialog generateDialog(Gene geneSelectedForDialogDisplay, HashMap<String, Dialog> geneIdToDialogMap, int dialogWidth, int dialogHeight, ArrayList<UIComponent> availableDialogContainers) {
        Random random = new Random();
        Dialog dialog = new Dialog();
        dialog.setVisible(true);
        dialog.setMinimizable(true);
        dialog.setWidth(dialogWidth + "px");
        dialog.setHeight(dialogHeight + "px");
        String leftOffset = (20 + random.nextInt(40)) + "%";
        String topOffset = (20 + random.nextInt(50)) + "%";
        dialog.setPosition("left+" + leftOffset + ",top+" + topOffset);
//        dialog.setHeader(leftOffset + "," + topOffset);
        dialog.setFitViewport(true);
        dialog.setClosable(true);
        dialog.setResizable(false);
//        dialog.setTransient(true);
//        dialog.setMinimizable(false);

//        String dialogId = "chartDialog_" + suffix;
        String dialogId = geneSelectedForDialogDisplay.getGeneId();
        dialog.setId(dialogId);
        dialog.setWidgetVar(dialogId);
//        dialogIdToDialogMap.put(dialogId, dialog);
        dialog.setHeader(dialogId + " on " + geneSelectedForDialogDisplay.getContig().getId());
        geneIdToDialogMap.put(geneSelectedForDialogDisplay.getGeneId(), dialog);

        FacesContext facesContext = FacesContext.getCurrentInstance();

        //works but prefer built in close on dialog? 
//        CommandButton button = new CommandButton();
//        button.setIcon("ui-icon-close");
//        ExpressionFactory ef = facesContext.getApplication().getExpressionFactory();
//        String exp = "#{mainBean.handleClose('" + dialogId + "')}";
//        MethodExpression expression = ef.createMethodExpression(facesContext.getELContext(), exp, null, new Class[]{Object.class}); //expectedParamTypes); //If you receive parameters put new Class[]{Object.class});
//        button.setActionExpression(expression);
//        button.setOncomplete("PF('" + dialogId + "').hide()");
//        dialog.getChildren().add(button);
////        Draggable drag = new Draggable();
////        drag.setFor(currentDisplay + ":" + dialogId);
////        Ajax event handing needed to be able to permamently remove closed dialogs from the tree (otherwise they are just hidden onClose)
        String exp = "#{mainBean.handleClose('" + dialogId + "')}"; //there is no such method!
        AjaxBehavior ajaxBehavior = new AjaxBehavior();
        MethodExpression expression2 = facesContext.getApplication().getExpressionFactory().createMethodExpression(facesContext.getELContext(), exp, null, null); //expectedParamTypes); //If you receive parameters put new Class[]{Object.class});
        ajaxBehavior.setListener(expression2);
        ajaxBehavior.addAjaxBehaviorListener(new CloseDialoglListener(geneIdToDialogMap, availableDialogContainers));
        dialog.addClientBehavior("close", ajaxBehavior);

//        CommandButton button = new CommandButton();
//        button.setIcon("ui-icon-newwin");
//        button.setActionExpression(facesContext.getApplication().getExpressionFactory().createMethodExpression(facesContext.getELContext(), "#{mainBean.exportChart(gene)}", null, null));
//        button.setOncomplete("exportChart(xhr, status, args);");
//        button.setTitle("Export chart as image");      
//        dialog.getChildren().add(button);
        return dialog;
    }

    protected void removeChildren(UIComponent component) {
        List<UIComponent> children = component.getChildren();
        for (UIComponent child : children) { //loop to remove dialog and the drag component
            removeChildren(child);
            children.remove(child);
        }
    }

    /**
     * Required to use ajax close event of the dialog component
     */
    private class CloseDialoglListener extends AjaxBehaviorListenerImpl {

        protected HashMap<String, Dialog> geneIdToDialogMap;
        protected ArrayList<UIComponent> availableDialogContainers;

        public CloseDialoglListener(HashMap<String, Dialog> geneIdToDialogMap, ArrayList<UIComponent> availableDialogContainers) {
            this.geneIdToDialogMap = geneIdToDialogMap;
            this.availableDialogContainers = availableDialogContainers;
        }

        public void setGeneIdToDialogMap(HashMap<String, Dialog> geneIdToDialogMap) {
            this.geneIdToDialogMap = geneIdToDialogMap;
        }

        public void setAvailableDialogContainers(ArrayList<UIComponent> availableDialogContainers) {
            this.availableDialogContainers = availableDialogContainers;
        }

        @Override
        public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
            if (geneIdToDialogMap == null || availableDialogContainers == null) {
                System.err.println(this.toString() + " geneIdToDialogMap == null || availableDialogContainers == null  -> must link references to enable this ajax handling!!! ");
            } else {
                Dialog dialog = (Dialog) event.getSource();
//            dialogIdToDialogMap.remove(dialog.getId());
                geneIdToDialogMap.remove(dialog.getId());
                UIComponent container = dialog.getParent();
                removeChildren(container);
//            System.out.println("Containers before removal: " + availableDialogContainers.size());
                availableDialogContainers.add(container);
//            System.out.println("Containers after removal: " + availableDialogContainers.size());
                RequestContext.getCurrentInstance().update(container.getId());
            }
        }
    }
}
