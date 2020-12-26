package buildcapture.tester;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JavaProject;

import buildcapture.constant.BuilderIdConstant;

/**
 * メニュー表示条件。
 * 
 * <pre>
 * すでにプロジェクトのビルダーに登録されている場合、メニューに表示しない。
 * </pre>
 * 
 * @author satakagi
 *
 */
public class VisibleMenuTester extends PropertyTester {

	@Override
	public boolean test(Object arg0, String arg1, Object[] arg2, Object arg3) {

		// org.eclipse.jdt.internal.core.JavaProject
		// org.eclipse.core.internal.resources.Project

		IProject project = null;
		if (arg0 instanceof IJavaProject) {
			IJavaProject javaProject = (IJavaProject) arg0;
			project = javaProject.getResource().getProject();
		}

		if (arg0 instanceof IProject) {

			project = (IProject) arg0;
		}
		
		if (project == null) {
			return false;
		}

		try {
			IProjectDescription description = project.getDescription();
			ICommand[] buildSpec = description.getBuildSpec();
			List<ICommand> list = Arrays.asList(buildSpec);

			boolean anyMatch = list.stream().anyMatch(e -> {
				return e.getBuilderName().equals(BuilderIdConstant.START_BUILDER_ID)
						|| e.getBuilderName().equals(BuilderIdConstant.NEW_BUILDER_ID);
			});
			if (anyMatch) {
				return false;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return true;
	}

}
