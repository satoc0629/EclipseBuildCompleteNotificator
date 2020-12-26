/*
 * Copyright(c) 2020 NEXCO Systems company limited All rights reserved.
 */
package buildcapture.nature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 */
public class MyNature implements IProjectNature {
	private IProject project;
	private String NAME = "buildcapture.nature.mynature";

	public MyNature() {
		super();
	}

	public void configure() throws CoreException {
		System.out.println("nature configure.");
		// Add nature-specific information
		// for the project, such as adding a builder
		// to a project's build spec.
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = NAME;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
			
		} catch (CoreException e) {
			// Something went wrong
		}
	}

	public void deconfigure() throws CoreException {
		// Remove the nature-specific information here.
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] nextNatures = new String[description.getNatureIds().length - 1];
			int i = 0;
			for (String nature : natures) {
				if (NAME.equals(nature)) {
					continue;
				}
				nextNatures[i] = nature;
				i++;
			}
			description.setNatureIds(nextNatures);
			project.setDescription(description, null);
		} catch (CoreException e) {
			// Something went wrong
		}

	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject value) {
		project = value;
	}

}
