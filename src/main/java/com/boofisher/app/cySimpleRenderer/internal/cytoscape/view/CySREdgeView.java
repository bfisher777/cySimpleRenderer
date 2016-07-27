package com.boofisher.app.cySimpleRenderer.internal.cytoscape.view;

import org.cytoscape.model.CyEdge;

public class CySREdgeView extends CySRView<CyEdge> {

	private final CyEdge edge;
	private String name;
	
	public CySREdgeView(DefaultValueVault defaultValueVault, CyEdge edge) {
		super(defaultValueVault);
		this.edge = edge;
	}
	
	@Override
	public CyEdge getModel() {
		return edge;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
}
