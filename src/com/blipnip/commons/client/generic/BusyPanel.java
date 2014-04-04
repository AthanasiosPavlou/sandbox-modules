package com.blipnip.commons.client.generic;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Adapted from:
 * http://turbomanage.wordpress.com/2009/10/22/how-to-show-a-loading-pop-up-in-your-gwt-app/
 * busy.gif is generated from: http://www.ajaxload.info/
 * 
 * @author muquit@muquit.com Nov 27, 2012 7:53:42 PM
 */
public class BusyPanel extends PopupPanel
{
    private final Image busyImage = new Image("images/small/busy.png");
    private final FlowPanel container = new FlowPanel();
    private String title = "Please wait..";
    private Grid grid;
    
    public BusyPanel()
    {
        setStyleName("busy-PopupPanel");
       setGlassEnabled(true);
       grid = new Grid(1,2);
       grid.setWidget(0,0,busyImage);
       this.container.add(grid);
       add(this.container);
    }
    
    public void setTitle(String message)
    {
        if (message == null)
            grid.setText(0,1,this.title);
        else
            grid.setText(0,1,message);
    }
    
    public void startProcessing(String message)
    {
        //center();
        setPopupPosition(20, 110);
        setTitle(message);
        show();
    }
    
    public void stopProcessing()
    {
        hide();
    }
    
    public void showWidget()
    {
        startProcessing(title);
    }

}
