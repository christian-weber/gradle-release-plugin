package org.gradle.plugin.release.action;

import static org.tmatesoft.svn.core.wc.SVNClientManager.newInstance;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.InvalidUserDataException;
import org.gradle.plugin.release.SVNInfo;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * {@link ReleaseAction} implementation used to verify if all relevant SVN
 * folders are present.
 * 
 * @author christian.weber
 * @since 1.0
 */
public abstract class VerifySvnFolderReleaseAction implements ReleaseAction {

	private static final SVNRevision HEAD = SVNRevision.HEAD;

	@Override
	public void action(SVNInfo info) {

		final String us = info.getUsername();
		final String pw = info.getPassword();

		try {
			// create a SVN client manager instance
			DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
			SVNClientManager cm = newInstance(options, us, pw);

			// create a SVNLogClient instance
			SVNLogClient client = cm.getLogClient();

			// list the SVN entries
			ISVNDirEntryHandler handler = new MuteSVNDirEntryHandler();

			SVNURL svnUrl = SVNURL.parseURIEncoded(info.getUrl());
			
			while (!svnUrl.getPath().endsWith(info.getProjectName())) {
				svnUrl = svnUrl.removePathTail();
			}
			
			svnUrl = svnUrl.appendPath(getSvnFolder(), true);

			client.doList(svnUrl, HEAD, HEAD, false, SVNDepth.UNKNOWN,
					SVNDirEntry.DIRENT_TIME, handler);

		} catch (SVNException e) {
			throw new InvalidUserDataException(e);
		}

	}

	/**
	 * {@link ISVNDirEntryHandler} implementation with null operation.
	 * 
	 * @author christian.weber
	 */
	private class MuteSVNDirEntryHandler implements ISVNDirEntryHandler {

		@Override
		public void handleDirEntry(SVNDirEntry dirEntry) throws SVNException {
			// do nothing
		}

	}

	/**
	 * Returns the SVN folder name.
	 * 
	 * @return String
	 */
	protected abstract String getSvnFolder();

	/**
	 * {@link VerifySvnFolderReleaseAction} implementation used to verify if the
	 * 'tags' folder exists.
	 * 
	 * @author christian.weber
	 */
	public static class VerifyTagFolderReleaseAction extends
			VerifySvnFolderReleaseAction {

		@Override
		protected String getSvnFolder() {
			return "tags";
		}

	}

	/**
	 * {@link VerifySvnFolderReleaseAction} implementation used to verify if the
	 * 'branches' folder exists.
	 * 
	 * @author christian.weber
	 */
	public static class VerifyBranchFolderReleaseAction extends
			VerifySvnFolderReleaseAction {

		@Override
		protected String getSvnFolder() {
			return "branches";
		}

	}

}
