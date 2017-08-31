package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class ClassObject {
	
	private String className;
	
	private String classMainDatasource;
	
	private String classMainQuery;
	
	private ArrayList<String> classPeers;
	
	private String classDefaultImage;

	private String classRelationships;
	
	public ClassObject() {
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassMainDatasource() {
		return classMainDatasource;
	}

	public void setClassMainDatasource(String classMainDatasource) {
		this.classMainDatasource = classMainDatasource;
	}

	public String getClassMainQuery() {
		return classMainQuery;
	}

	public void setClassMainQuery(String classMainQuery) {
		this.classMainQuery = classMainQuery;
	}

	public void setClassPeers(ArrayList<String> classPeers) {
		this.classPeers = classPeers;
	}

	public ArrayList<String> getClassPeers() {
		return classPeers;
	}

	public String getClassRelationships() {
		return classRelationships;
	}

	public void setClassRelationships(String classRelationships) {
		this.classRelationships = classRelationships;
	}

	public void setClassDefaultImage(String classDefaultImage) {
		this.classDefaultImage = classDefaultImage;
	}

	public String getClassDefaultImage() {
		return classDefaultImage;
	}
}
