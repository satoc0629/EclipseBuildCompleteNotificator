package buildcapture.propertypage;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import buildcapture.Activator;
import buildcapture.constant.GuiConstant;
import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PropertyPageImpl extends PropertyPage {

	private CheckBox check;
	private TextField checkTime;

	@Override
	protected Control createContents(Composite composite) {
		Platform.setImplicitExit(false);
		FXCanvas canvas = new FXCanvas(composite, SWT.NONE) {
			@Override
			public Point computeSize(int wHint, int hHint, boolean changed) {
				getScene().getWindow().sizeToScene();
				int width = (int) getScene().getWidth();
				int height = (int) getScene().getHeight();

				return new Point(width, height);
			}

		};
		canvas.setScene(createScene(composite));

		super.noDefaultAndApplyButton();
		return composite;
	}

	@Override
	public boolean performOk() {
		IProject project = getProject();
		Platform.setImplicitExit(false);

		setValue(project, GuiConstant.KEY_MESSAGE, checkTime.getText());
		setValue(project, GuiConstant.KEY_ENABLE, String.valueOf(check.isSelected()));

		return true;
	}

	private IProject getProject() {
		IAdaptable adaptable = getElement();
		IProject project = null;
		
		if (adaptable instanceof IJavaProject) {
			project = ((IJavaProject) adaptable).getProject();
		}
		if (adaptable instanceof IProject) {
			project = (IProject) adaptable;
		}

		return project;
	}

	public String getValue(IProject iProject, String key) {
		try {
			QualifiedName prop = new QualifiedName(Activator.PLUGIN_ID, key);
			return iProject.getPersistentProperty(prop);
		} catch (CoreException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(e.getStatus());
			return null;
		}
	}

	public void setValue(IResource project, String key, String value) {
		try {
			project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, key), value);
		} catch (CoreException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(e.getStatus());
		}
	}

	public Scene createScene(Composite composite) {

		VBox vbox = new VBox();
		vbox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		RGB rgb = composite.getBackground().getRGB();
		vbox.setStyle("-fx-background-color: rgb(" + rgb.red + ", " + rgb.green + ", " + rgb.blue + ");");

		Scene scene = new Scene(vbox);

		{
			Label title = new Label("ビルド通知設定");
			title.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
			vbox.getChildren().add(title);
		}

		{
			HBox hbox = new HBox();
			hbox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
			Label label = new Label("通知ON/OFF");
			label.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
			check = new CheckBox();
			check.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
			hbox.getChildren().addAll(label, check);
			vbox.getChildren().add(hbox);

			String value = getValue(getProject(), GuiConstant.KEY_ENABLE);
			if (value != null) {
				if (Boolean.valueOf(value)) {
					check.setSelected(true);
				} else {
					check.setSelected(false);
				}
			} else {
				check.setSelected(true);
			}
		}
		{ // メッセージを入力する行
			HBox hbox = new HBox();
			hbox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

			Label label = new Label("通知しきい値");
			label.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
			checkTime = new TextField();
			checkTime.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
			Label labelSuffix = new Label("秒");
			labelSuffix.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
			hbox.getChildren().addAll(label, checkTime, labelSuffix);
			vbox.getChildren().addAll(hbox);

			String value = getValue(getProject(), GuiConstant.KEY_MESSAGE);
			if (value != null) {
				checkTime.setText(value);
			} else {
				checkTime.setText("15");
			}
		}
		return scene;
	}

}
