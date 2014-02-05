package org.gradle.plugin.release.action;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.InvalidUserDataException;
import org.gradle.plugin.release.SVNInfo;

/**
 * {@link ReleaseAction} implementation used to verify the SVN URL pattern.
 * 
 * @author christian.weber
 * @since 1.0
 */
public class VerifyUrlReleaseAction implements ReleaseAction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void action(SVNInfo info) {

		if (!isTrunkSvnUrl(info) && !isBranchSvnUrl(info)) {
			throw new InvalidUserDataException("svn url must direct on a trunk or branch location");
		}

	}

	/**
	 * Indicates if the given SVNInfo directs on a trunk URL.
	 * 
	 * @param info
	 * @return boolean
	 */
	private boolean isTrunkSvnUrl(SVNInfo info) {
		if (info == null) {
			throw new InvalidUserDataException("SVNInfo must not be null");
		}
		final String url = info.getUrl();
		return url.endsWith("/trunk");
	}

	/**
	 * Indicates if the given SVNInfo directs on a branch URL.
	 * 
	 * @param info
	 * @return boolean
	 */
	private boolean isBranchSvnUrl(SVNInfo info) {
		if (info == null) {
			throw new InvalidUserDataException("SVNInfo must not be null");
		}
		final String url = info.getUrl();
		return url.contains("/branches/");
	}

}
