package buildcapture;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "BuildCapture"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private static BundleContext context;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Activator.context = context;
		plugin = this;
		System.out.println("bundle start.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		System.out.println("bundle stop.");
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static IEclipseContext getEclipseContext() {
		return EclipseContextFactory.getServiceContext(context);
	}

	public static Set<IProject> getProject() {
		Set<IProject> projects = null;
		try {
			// 驕ｸ謚槭�励Ο繧ｸ繧ｧ繧ｯ繝医°繧�
			projects = getCurrentProjectIfProjectSelection();
			if (projects != null) {
				return projects;
			}

			// 繧｢繧ｯ繝�繧｣繝悶ヵ繧｡繧､繝ｫ縺九ｉ
			projects = getCurrentProjectIfFileSelection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return projects;
	}

	private static Set<IProject> getCurrentProjectIfFileSelection() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null) {
			return null;
		}
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return null;
		}
		IEditorPart editor = page.getActiveEditor();
		IEditorInput editorInput = editor.getEditorInput();
		IFile file = editorInput.getAdapter(IFile.class);
		IProject project = file.getProject();
		Set<IProject> projects = new HashSet<>();
		projects.add(project);
		return projects;
	}

	private static Set<IProject> getCurrentProjectIfProjectSelection() {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		Set<IProject> javaProjects = new HashSet<>();
		if (window != null) {
			ISelection iselection = window.getSelectionService().getSelection();
			IStructuredSelection selection = (IStructuredSelection) iselection;
			if (selection == null) {
				return null;
			}
			
			selection.iterator().forEachRemaining((e)->{
				IProject project = null;
				if (e instanceof IResource) {
					project = ((IResource) e).getProject();
				} else if (e instanceof IPackageFragmentRoot) {
					IJavaProject jProject = ((IPackageFragmentRoot) e)
							.getJavaProject();
					project = jProject.getProject();
				} else if (e instanceof IJavaElement) {
					IJavaProject jProject = ((IJavaElement) e)
							.getJavaProject();
					project = jProject.getProject();
				}
				if (project != null) {
					javaProjects.add(project);
				}
			});


		}
		return javaProjects;
	}
}
