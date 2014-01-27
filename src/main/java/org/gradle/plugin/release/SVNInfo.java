package org.gradle.plugin.release;

import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.StopExecutionException;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * This class stores all relevant SVN information used to invoke a release
 * action.
 * 
 * @author christian.weber
 * @since 1.0
 */
public class SVNInfo {

	private final Project project;

	private final String username;
	private final String password;
	private final String url;
	private final String developmentVersion;
	private final String tagVersion;
	private final String folderName;
	private final String simulateRun;
	private final String revision;

	public SVNInfo(Project project) {
		this.project = project;
		this.username = property(project, "username");
		this.password = property(project, "password");
		this.url = property(project, "url");
		this.developmentVersion = property(project, "developmentVersion");
		this.tagVersion = property(project, "tagVersion");
		this.folderName = property(project, "folderName");
		this.simulateRun = property(project, "simulateRun");
		this.revision = property(project, "revision");
	}

	/**
	 * Returns the project property with the given name.
	 * 
	 * @param project
	 * @param property
	 * @return String
	 */
	private String property(Project project, String property) {
		final Map<String, ?> properties = project.getProperties();
		if (properties.containsKey(property)) {
			return properties.get(property).toString();
		}

		Logger logger = project.getLogger();
		logger.info("--> property " + property + " is null");

		return null;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	public String getProjectName() {
		return project.getName();
	}

	public String getDevelopmentVersion() {
		return developmentVersion;
	}

	public String getTagVersion() {
		return tagVersion;
	}

	public Project getProject() {
		return project;
	}

	public String getFolderName() {
		return folderName;
	}
	
	public boolean isSimulateRun() {
		return "true".equalsIgnoreCase(simulateRun);
	}
	
	public Logger getLogger() {
		return project.getLogger();
	}
	
	public SVNRevision getRevision() {
		if (revision == null) {
			return SVNRevision.HEAD;
		}
		
		SVNRevision svnr = SVNRevision.parse(revision);
		if (SVNRevision.UNDEFINED.equals(svnr)) {
			getLogger().error("--> revision " + revision +" is invalid");
			throw new StopExecutionException();
		}
		return svnr;
	}

}
