package org.gradle.plugin.release.action;

import static org.tmatesoft.svn.core.wc.SVNClientManager.newInstance;
import static org.tmatesoft.svn.core.wc.SVNRevision.HEAD;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.plugin.release.SVNInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCopyClient;
import org.tmatesoft.svn.core.wc.SVNCopySource;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * {@link ReleaseAction} implementation designed to create a branch or tag.
 * 
 * @author christian.weber
 * @since 1.0
 */
public abstract class CopyReleaseAction implements ReleaseAction {

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

		// create a SVN copy client instance
		SVNCopyClient copy = cm.getCopyClient();

		try {
			// parse the SVN source URL
			SVNURL svnUrl = SVNURL.parseURIEncoded(info.getUrl());
			// parse the SVN destination URL
			SVNURL url = getSvnUrl(info);
			// prepare the copy source
			SVNCopySource[] sources = { new SVNCopySource(HEAD, null, svnUrl) };
			// create the tag
			copy.doCopy(sources, url, false, false, false, getComment(), null);
		} catch (SVNException e) {
			Project project = info.getProject();
			Logger logger = project.getLogger();
			logger.error("--> error while copying SVN folder", e);

			throw new StopExecutionException();
		}
	}

	/**
	 * Returns the URL to the new SVN folder by the given {@link SVNInfo}.
	 * 
	 * @param info
	 * @return SVNURL
	 * @throws SVNException
	 */
	protected abstract SVNURL getSvnUrl(SVNInfo info) throws SVNException;

	/**
	 * Returns the commit comment.
	 * 
	 * @return String
	 */
	protected abstract String getComment();

	/**
	 * {@link ReleaseAction} implementation designed to create a new branch.
	 * 
	 * @author christian.weber
	 */
	public static class BranchReleaseAction extends CopyReleaseAction {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected SVNURL getSvnUrl(SVNInfo info) throws SVNException {
			final String pn = info.getProjectName();
			final String v = info.getDevelopmentVersion();
			final String fn = defaultStr(info.getFolderName(), pn + "-" + v);

			SVNURL svnUrl = SVNURL.parseURIEncoded(info.getUrl());

			while (!svnUrl.getPath().endsWith(info.getProjectName())) {
				svnUrl = svnUrl.removePathTail();
			}
			svnUrl = svnUrl.appendPath("branches", true);
			svnUrl = svnUrl.appendPath(fn, true);

			return svnUrl;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String getComment() {
			return "gradle-release: new branch committed";
		}

	}

	/**
	 * {@link ReleaseAction} implementation designed to create a new tag.
	 * 
	 * @author christian.weber
	 */
	public static class TagReleaseAction extends CopyReleaseAction {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected SVNURL getSvnUrl(SVNInfo info) throws SVNException {
			final String pn = info.getProjectName();
			final String v = info.getTagVersion();
			final String fn = defaultStr(info.getFolderName(), pn + "-" + v);

			SVNURL svnUrl = SVNURL.parseURIEncoded(info.getUrl());
			svnUrl = svnUrl.removePathTail();
			svnUrl = svnUrl.appendPath("tags", true);
			svnUrl = svnUrl.appendPath(fn, true);

			return svnUrl;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String getComment() {
			return "gradle-release: new tag commited";
		}

	}

	/**
	 * Returns str1 if not null else str2.
	 * 
	 * @param str1
	 * @param str2
	 * @return String
	 */
	private static String defaultStr(String str1, String str2) {
		return str1 != null ? str1 : str2;
	}

}
