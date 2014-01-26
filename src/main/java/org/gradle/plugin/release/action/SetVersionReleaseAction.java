package org.gradle.plugin.release.action;

import static org.tmatesoft.svn.core.wc.SVNClientManager.newInstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.plugin.release.SVNInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * {@link ReleaseAction} implementation designed to set the project version.
 * 
 * @author christian.weber
 * @since 1.0
 */
public abstract class SetVersionReleaseAction implements ReleaseAction {

	private static final String COMMENT = "GRADLE properties";
	private static final String COMMENT2 = "project version set to tag version";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void action(SVNInfo info) {

		final String us = info.getUsername();
		final String pw = info.getPassword();

		// create a SVN client manager instance
		DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
		SVNClientManager cm = newInstance(options, us, pw);

		// create a SVNCommitClient instance
		SVNCommitClient client = cm.getCommitClient();

		try {
			// modify the 'gradle.properties' file
			Project project = info.getProject();
			File projectDir = project.getProjectDir();

			File gradleProperties = new File(projectDir, "gradle.properties");

			Properties properties = new Properties();
			properties.load(new FileInputStream(gradleProperties));

			properties.put("version", getVersion(info));
			properties.store(new FileOutputStream(gradleProperties), COMMENT);

			// commit 'gradle.properties' file
			File[] paths = new File[] { gradleProperties };
			client.doCommit(paths, false, COMMENT2, new SVNProperties(),
					new String[0], true, true, SVNDepth.UNKNOWN);

		} catch (IOException e) {
			Project project = info.getProject();
			Logger logger = project.getLogger();
			logger.error("--> error while setting version", e);

			throw new StopExecutionException();
		} catch (SVNException e) {
			Project project = info.getProject();
			Logger logger = project.getLogger();
			logger.error("--> error while setting version", e);

			throw new StopExecutionException();
		}
	}

	/**
	 * Returns the version.
	 * 
	 * @param info
	 * @return String
	 */
	protected abstract String getVersion(SVNInfo info);

	public static class SetTagVersionReleaseAction extends SetVersionReleaseAction {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String getVersion(SVNInfo info) {
			final String version = info.getTagVersion();

			if (version == null) {
				Project project = info.getProject();
				return project.getVersion().toString();
			}

			return version;
		}

	}
	
	public static class SetDevVersionReleaseAction extends SetVersionReleaseAction {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String getVersion(SVNInfo info) {
			return info.getDevelopmentVersion();
		}

	}

	
}
