package org.gradle.plugin.release;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.StopExecutionException
import org.gradle.plugin.release.action.CopyReleaseAction
import org.gradle.plugin.release.action.ReleaseAction
import org.gradle.plugin.release.action.VerifySvnFolderReleaseAction
import org.gradle.plugin.release.action.VerifyUrlReleaseAction
import org.gradle.plugin.release.action.CopyReleaseAction.BranchReleaseAction
import org.gradle.plugin.release.action.CopyReleaseAction.TagReleaseAction
import org.gradle.plugin.release.action.SetVersionReleaseAction.SetDevVersionReleaseAction
import org.gradle.plugin.release.action.SetVersionReleaseAction.SetTagVersionReleaseAction
import org.gradle.plugin.release.action.VerifySvnFolderReleaseAction.VerifyBranchFolderReleaseAction
import org.gradle.plugin.release.action.VerifySvnFolderReleaseAction.VerifyTagFolderReleaseAction

/**
 * GRADLE Plugin designed for releasing purpose. Supports the creation 
 * of tags and branches as well as the setting of a new version. 
 *
 * @author christian.weber
 * @since 1.0
 */
public class GradleReleasePlugin implements Plugin<Project>  {

	private final ReleaseAction tagAction = new TagReleaseAction();
	private final ReleaseAction branchAction = new BranchReleaseAction();
	private final ReleaseAction verifyTagFolderAction = new VerifyTagFolderReleaseAction();
	private final ReleaseAction verifyBranchFolderAction = new VerifyBranchFolderReleaseAction();
	private final ReleaseAction verifyUrlAction = new VerifyUrlReleaseAction();
	private final ReleaseAction setTagVersionAction = new SetTagVersionReleaseAction();
	private final ReleaseAction setDevVersionAction = new SetDevVersionReleaseAction();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void apply(Project project) {

		// create tag task
		project.task("createTag") << {
			SVNInfo info = parseCreateTagInfo(project)

			setTagVersionAction.action(info)
			tagAction.action(info)
			setDevVersionAction.action(info)
		}

		// create branch task
		project.task("createBranch") << {
			SVNInfo info = parseCreateBranchInfo(project)

			setTagVersionAction.action(info)
			branchAction.action(info)
			setDevVersionAction.action(info)
		}

		// set version task
		project.task("setVersion") << {
			SVNInfo info = parseSetVersionInfo(project)

			setDevVersionAction.action(info)
		}
		
		project.tasks["createTag"].group ="Release" 
		project.tasks["createBranch"].group ="Release" 
		project.tasks["setVersion"].group ="Release" 
		
		project.tasks["createTag"].description = "Creates a new tag of the project"
		project.tasks["createBranch"].description = "Creates a new branch of the project"
		project.tasks["setVersion"].description = "Sets the version of the project"
	}

	/**
	 * Parses the GRADLE project start parameters and returns a {@link SVNInfo} instance
	 * 
	 * @param project
	 * @return SVNInfo
	 */
	private SVNInfo parseCreateTagInfo(Project project) {
		notNull(project, "username")
		notNull(project, "password")
		notNull(project, "url")
		notNull(project, "developmentVersion")
		notNull(project, "tagVersion")

		def info = new SVNInfo(project)

		verifyUrlAction.action(info)
		verifyTagFolderAction.action(info);

		return info
	}

	/**
	 * Parses the GRADLE project start parameters and returns a {@link SVNInfo} instance
	 * 
	 * @param project
	 * @return SVNInfo
	 */
	private SVNInfo parseCreateBranchInfo(Project project) {
		notNull(project, "username")
		notNull(project, "password")
		notNull(project, "url")
		notNull(project, "developmentVersion")

		def info = new SVNInfo(project)

		verifyUrlAction.action(info)
		verifyBranchFolderAction.action(info);

		return info
	}

	/**
	 * Parses the GRADLE project start parameters and returns a {@link SVNInfo} instance
	 * 
	 * @param project
	 * @return SVNInfo
	 */
	private SVNInfo parseSetVersionInfo(Project project) {
		notNull(project, "username")
		notNull(project, "password")
		notNull(project, "url")
		notNull(project, "developmentVersion")

		def info = new SVNInfo(project)

		verifyUrlAction.action(info)

		return info
	}

	/**
	 * Returns the GRADLE project start parameter with the given name. Throws a StopExecutionException
	 * if the property is missing.
	 * 
	 * @param project
	 * @param property
	 * @return String
	 */
	private void notNull(Project project, String property) {
		if (!project.hasProperty(property)) {
			project.logger.error("--> property " + property + " must not be null")
			throw new StopExecutionException()
		}
	}

}
