package com.webapp.WebService;


public class GeometryObject {
	
	private final Integer id;
	private final String geometry;
	
	public GeometryObject(Integer id, String geometry) {
		this.id = id;
		this.geometry = geometry;
	}
	
	public Integer getID() {
		return id;
	}
	
	public String getGeometry() {
		return geometry;
	}

}