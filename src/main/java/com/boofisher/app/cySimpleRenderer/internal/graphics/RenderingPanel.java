package com.boofisher.app.cySimpleRenderer.internal.graphics;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.MouseWheelEvent;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.work.swing.DialogTaskManager;

import com.boofisher.app.cySimpleRenderer.internal.cytoscape.view.CySRNetworkView;
import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import com.boofisher.app.cySimpleRenderer.internal.eventbus.EventBusProvider;
import com.boofisher.app.cySimpleRenderer.internal.tools.NetworkToolkit;
import com.boofisher.app.cySimpleRenderer.internal.tools.PairIdentifier;
import com.google.common.eventbus.EventBus;

public class RenderingPanel extends JPanel{
	
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	
	private static final long serialVersionUID = -8440123285895117402L;
	protected final GraphicsData graphicsData;
	protected final GraphicsConfiguration configuration;
	
	protected ImageIcon iImage;
	protected JLabel picture;
	protected JScrollPane pictureScrollPane;
	    
	public RenderingPanel(
			CySRNetworkView networkView, 
			VisualLexicon visualLexicon, 
			EventBusProvider eventBusProvider, 
			GraphicsConfiguration configuration,									
			JComponent inputComponent) {
		super();		
		
		this.configuration = checkNotNull(configuration);
		EventBus eventBus = eventBusProvider.getEventBus(networkView);		
        
        //Set up the picture and scroll pane.
        picture = new JLabel(iImage);
        
        
        pictureScrollPane = new JScrollPane(picture) {
            
			private static final long serialVersionUID = -4022187082866252353L;

			@Override
            protected void processMouseWheelEvent(MouseWheelEvent e) {
                if (!isWheelScrollingEnabled()) {
                    if (getParent() != null) 
                    	//send event to parent
                        getParent().dispatchEvent(
                                SwingUtilities.convertMouseEvent(this, e, getParent()));
                    return;
                }
                super.processMouseWheelEvent(e);
            }
			
        };
        pictureScrollPane.setWheelScrollingEnabled(false);
        //pictureScrollPane.setPreferredSize(new Dimension(500, 500));
        
        //Put it in this panel.
        add(pictureScrollPane);
        
		this.graphicsData = new GraphicsData(networkView, visualLexicon, eventBus, this, inputComponent, pictureScrollPane);
		
		//set up event listeners / handlers / fit graph in view
		configuration.initialize(graphicsData);
	}
	
	@Override
    public void paintComponent(Graphics g) {										
		
		graphicsData.setScreenHeight(this.getHeight());
		graphicsData.setScreenWidth(this.getWidth());
		
		//now call the graphics configuration rendering procedures
		// Doesn't really need to be split into two methods, but it allows GrapicsConfigurations to 
		// only override update() and leave the drawing to AbstractGraphicsConfiguration.
		//update() handles picking and picked node rendering 
		//configuration.update();
		configuration.drawScene();
		
		iImage = new ImageIcon(graphicsData.getBufferedImage());
		
		//Set up the scroll pane.
        picture = new JLabel(iImage);
        
        pictureScrollPane.setViewportView(picture);
    }	
    
	
	/*
	 * Utility method to create a shape for the network (ellipse, triangle, or rectangle)*/
	public static Shape getShape(CyNetworkView networkView, CyNode node, int midWidth, int midHeight){
		Shape shape = null;
		
		
		View<CyNode> nodeView = networkView.getNodeView(node);
		
		//cast floats and doubles to int
		float x = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION).floatValue();// / distanceScale;
		float y = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION).floatValue();// / distanceScale;			
		
		double width  = nodeView.getVisualProperty(BasicVisualLexicon.NODE_WIDTH);
		double height = nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT);
		shape = new Rectangle2D.Double((x+midWidth), (y+midHeight), width, height);
		
		
		if(NodeShapeVisualProperty.TRIANGLE.equals(nodeView.getVisualProperty(BasicVisualLexicon.NODE_SHAPE))){
			double yPosition = y+midHeight+height;
			double xPosition = x+midWidth;
			Path2D path = new Path2D.Double();
			path.moveTo(xPosition, yPosition);
			double xShifted  = xPosition + width;			
			path.lineTo(xShifted, yPosition);			
			xPosition = (xPosition + xShifted) / 2;//find x-coord for half base
			double top = yPosition - height;
			path.lineTo(xPosition, top);
			path.closePath();
			shape = path;
		}else if(NodeShapeVisualProperty.ELLIPSE.equals(nodeView.getVisualProperty(BasicVisualLexicon.NODE_SHAPE))){
			shape = new Ellipse2D.Double((x+midWidth), (y+midHeight), width, height);
		}else{
			shape = new Rectangle2D.Double((x+midWidth), (y+midHeight), width, height);
		}
				
				
		return shape;
	}
					
}
