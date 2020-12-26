/*
 * Copyright(c) 2020 NEXCO Systems company limited All rights reserved.
 */
package buildcapture.builder;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import buildcapture.constant.GuiConstant;
import buildcapture.propertypage.PropertyPageImpl;

/**
 * 
 */
public class Notificator extends IncrementalProjectBuilder {
	public Notificator() {
		super();
	}

	@Override
	protected IProject[] build(int kind, Map<String, String> arg1, IProgressMonitor arg2) throws CoreException {

		LocalDateTime startTime = ProjectBuildStartTimeMap.get(getProject().getName());
		LocalDateTime endTime = LocalDateTime.now();
		Duration duration = Duration.between(startTime, endTime);

		Long checkSeconds = 15L;
		Object checkSecondsString = new PropertyPageImpl().getValue(getProject(), GuiConstant.KEY_MESSAGE);
		if (checkSecondsString != null) {
			checkSeconds = Long.valueOf((String) checkSecondsString);
		}
		// 15秒以上ビルドに掛かるなら表示する。
		if (duration.getSeconds() >= checkSeconds) {
			notification(duration);
		}

		return null;
	}

	IProject[] notification(Duration duration) {
		IProject project = getProject();
		if (project == null) {
			// project = Activator.getProject();
		}
		if (project != null) {
			Object checkEnable = new PropertyPageImpl().getValue(getProject(), GuiConstant.KEY_ENABLE);
			if (Boolean.valueOf((String) checkEnable))
				notification(project.getName(), duration);
			return new IProject[] { project };
		}
		return null;
	}

	void notification(String projectName, Duration duration) {
		SystemTray tray = SystemTray.getSystemTray();
		try {
			URL resource = getClass().getResource("Dummy.png");
			Image image = Toolkit.getDefaultToolkit().createImage(resource);
			TrayIcon trayIcon = new TrayIcon(image);
			trayIcon.setImageAutoSize(true);
			trayIcon.setToolTip("eclipse build");
			tray.add(trayIcon);
			String message = MessageFormat.format("{0}のビルド処理が完了しました。" + "所要時間：({1,number,00}:{2,number,00})。",
					new Object[] { projectName, duration.getSeconds() / 60, duration.getSeconds() % 60 });
			trayIcon.displayMessage("eclipse build　通知", message, MessageType.INFO);
			Runnable endTray = new Runnable() {

				@Override
				public void run() {
					// 少し待ってから
					try {
						Thread.sleep(60_000l);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// 除外する
					tray.remove(trayIcon);
				}

				@Override
				protected void finalize() throws Throwable {
					super.finalize();
				}
			};
			Thread thread = new Thread(endTray);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter printWriter = new PrintWriter(sw);
			e.printStackTrace(printWriter);
		}

	}

}
