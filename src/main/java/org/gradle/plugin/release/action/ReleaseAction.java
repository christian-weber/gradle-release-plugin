package org.gradle.plugin.release.action;

import org.gradle.plugin.release.SVNInfo;

/**
 * Interface contract designed to define specific release behavior like tagging
 * or branching.
 * 
 * @author christian.weber
 * @since 1.0
 */
public interface ReleaseAction {

	/**
	 * Invokes the release action.
	 */
	void action(SVNInfo info);
	
}
