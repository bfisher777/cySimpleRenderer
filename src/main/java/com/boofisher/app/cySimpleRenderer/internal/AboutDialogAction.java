package com.boofisher.app.cySimpleRenderer.internal;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;

import com.boofisher.app.cySimpleRenderer.internal.AboutDialog;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.util.swing.OpenBrowser;

public class AboutDialogAction extends AbstractCyAction {

	private final CySwingApplication application;
	private final OpenBrowser openBrowser;
	
	public AboutDialogAction(CySwingApplication application, OpenBrowser openBrowser) {
		super("About CySR...");
		this.application = application;
		this.openBrowser = openBrowser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JDialog aboutDialog = new AboutDialog(application, openBrowser);
		aboutDialog.pack();
        aboutDialog.setLocationRelativeTo(application.getJFrame());
        aboutDialog.setVisible(true);
	}
}