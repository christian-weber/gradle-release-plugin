package org.gradle.plugin.release.action;

import static org.tmatesoft.svn.core.wc.SVNClientManager.newInstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.gradle.api.Project;
import org.gradle.api.InvalidUserDataException;
import org.gradle.plugin.release.SVNInfo;
import org.gradle.plugin.release.action.ReleaseAction;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * {@link ReleaseAction} implementation designed to set the project version.
 * 
 * @author christian.weber
 * @since 1.0
 */
public abstract class SetVersionReleaseAction implements ReleaseAction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void action(SVNInfo info) {

		if (!SVNRevision.HEAD.equals info.getRevision()) {
			info.getLogger().info("--> SVNRevision not equals HEAD, skip set version");
			return;
		}

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

			FileOutputStream os = new FileOutputStream(gradleProperties);
			properties.put("version", getVersion(info));
			properties.store(os, "GRADLE properties");

			// commit 'gradle.properties' file
			File[] paths = new File[] { gradleProperties };
			if (!info.isSimulateRun()) {
				SVNProperties props = new SVNProperties();
				client.doCommit(paths, false, getComment(), props,
						new String[0], true, true, SVNDepth.UNKNOWN);
			} else {
				info.getLogger().info(
						"simulate set version to " + getVersion(info));
			}

		} catch (IOException e) {
			info.getLogger().error("--> error while setting version", e);
			throw new InvalidUserDataException();
		} catch (SVNException e) {
			info.getLogger().error("--> error while setting version", e);
			throw new InvalidUserDataException();
		}
	}

	/**
	 * Returns the version.
	 * 
	 * @param info
	 * @return String
	 */
	protected abstract String getVersion(SVNInfo info);

	/**
	 * Returns the SVN commit comment.
	 * 
	 * @return String
	 */
	protected abstract String getComment();

	/**
	 * {@link SetVersionReleaseAction} implementation for tag version.
	 * 
	 * @author christian.weber
	 */
	public static class SetTagVersionReleaseAction extends
			SetVersionReleaseAction {

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

		@Override
		protected String getComment() {
			return "gradle-release: project version set to tag version";
		}

	}

	/**
	 * {@link SetVersionReleaseAction} implementation for development version.
	 * 
	 * @author christian.weber
	 */
	public static class SetDevVersionReleaseAction extends
			SetVersionReleaseAction {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String getVersion(SVNInfo info) {
			return info.getDevelopmentVersion();
		}

		@Override
		protected String getComment() {
			return "gradle-release: project version set to development version";
		}

	}

}
