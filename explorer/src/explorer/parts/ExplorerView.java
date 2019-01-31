package explorer.parts;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import javafx.embed.swt.FXCanvas;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class ExplorerView {
	private FXCanvas canvas;

	@PostConstruct
	public void createPartControl(Composite parent) throws IOException {
		canvas = new FXCanvas(parent,SWT.None);
		Parent root = FXMLLoader.load(getClass().getResource("explorer.fxml"));
		canvas.setScene(new Scene(root));

	}

	@Focus
	public void setFocus() {
		canvas.setFocus();

	}

}
