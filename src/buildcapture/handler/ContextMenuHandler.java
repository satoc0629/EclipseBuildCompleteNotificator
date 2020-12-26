/*
 * Copyright(c) 2020 NEXCO Systems company limited All rights reserved.
 */
package buildcapture.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;

import buildcapture.Activator;
import buildcapture.constant.BuilderIdConstant;

/**
 * 繧ｳ繝ｳ繝�繧ｭ繧ｹ繝医Γ繝九Η繝ｼ縺ｮ繝懊ち繝ｳ縺梧款縺輔ｌ縺溘ｉ縺薙�ｮ繧ｯ繝ｩ繧ｹ縺悟他縺ｰ繧後ｋ縲�
 */
public class ContextMenuHandler extends AbstractHandler {

	private IEclipseContext context;
	private IEventBroker broker;
	private List<String> externalsEvent = new ArrayList<>();

	public ContextMenuHandler() {
		context = Activator.getEclipseContext();
		broker = (IEventBroker) context.get(IEventBroker.class.getName());

		externalsEvent.add("org/osgi/service/log/LogEntry/LOG_ERROR");
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// 隱ｿ譟ｻ逕ｨ縺�縺｣縺溘�ｮ縺ｧ繧ｳ繝｡繝ｳ繝医い繧ｦ繝�
		// addEventHandler(event);

		Set<IProject> projects = Activator.getProject();
		try {
			for (IProject project : projects) {
				// before
				IProjectDescription description = project.getDescription();
				ICommand[] buildSpec = description.getBuildSpec();
				List<ICommand> list = Arrays.asList(buildSpec);
				boolean anyMatch = list.stream().anyMatch(e -> {
					return e.getBuilderName().equals(BuilderIdConstant.START_BUILDER_ID) ||
							e.getBuilderName().equals(BuilderIdConstant.NEW_BUILDER_ID);
				});
				if (anyMatch)
					continue; // 谺｡縺ｮ繝励Ο繧ｸ繧ｧ繧ｯ繝�

		        ICommand[] newBuildSepc = new ICommand[buildSpec.length + 2];
				System.arraycopy(buildSpec, 0, newBuildSepc, 1, buildSpec.length);

				// start
				// 蜈磯�ｭ縺ｫ霑ｽ蜉�
				ICommand startBuilder = description.newCommand();
				startBuilder.setBuilderName(BuilderIdConstant.START_BUILDER_ID);
				newBuildSepc[0] = startBuilder;

				// 譛�蠕悟ｰｾ縺ｫ霑ｽ蜉�
				// 騾夂衍縺吶ｋ縺�縺代�ｮ繝薙Ν繝�繝ｼ繧堤匳骭ｲ縺吶ｋ縲�
				ICommand newCommand = description.newCommand();
				newCommand.setBuilderName(BuilderIdConstant.NEW_BUILDER_ID);
				newBuildSepc[newBuildSepc.length - 1] = newCommand;

				// after
				description.setBuildSpec(newBuildSepc);
				project.setDescription(description, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void addEventHandler(ExecutionEvent event) {
		org.osgi.service.event.EventHandler handler = new org.osgi.service.event.EventHandler() {

			@Override
			public void handleEvent(Event event) {
				StringJoiner joiner = new StringJoiner(",");
				for (String name : event.getPropertyNames()) {
					if (externalsEvent.contains(name)) {
						continue;
					}
					Object prop = event.getProperty(name);
					joiner.add(name + ":" + prop == null ? "null" : prop.toString());
				}
				System.out.println(event.getTopic() + ">" + joiner.toString());
			}
		};
		// 縺吶∋縺ｦ縺ｮ繧､繝吶Φ繝医ｒ蟇ｾ雎｡縺ｨ縺励※縺ｿ繧九��
		broker.subscribe("*", handler);

		System.out.println("繧､繝吶Φ繝医ワ繝ｳ繝峨Λ繝ｼ繧堤匳骭ｲ縺励∪縺励◆縲�");
	}

}
