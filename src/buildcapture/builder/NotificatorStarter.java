/*
 * Copyright(c) 2020 NEXCO Systems company limited All rights reserved.
 */
package buildcapture.builder;

import java.time.LocalDateTime;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * 
 */
public class NotificatorStarter extends IncrementalProjectBuilder {

	@Override
	protected IProject[] build(int kind, Map<String, String> arg1, IProgressMonitor arg2) throws CoreException {
		ProjectBuildStartTimeMap.put(getProject().getName(), LocalDateTime.now());
		return null;
	}

}
