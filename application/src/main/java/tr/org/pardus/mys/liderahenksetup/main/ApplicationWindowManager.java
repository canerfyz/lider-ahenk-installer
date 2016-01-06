package tr.org.pardus.mys.liderahenksetup.main;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import tr.org.pardus.mys.liderahenksetup.ahenk.wizard.AhenkSetupWizard;
import tr.org.pardus.mys.liderahenksetup.lider.wizard.LiderSetupWizard;
import tr.org.pardus.mys.liderahenksetup.utils.FontProvider;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

public class ApplicationWindowManager {

	private static final Logger logger = Logger.getLogger(ApplicationWindowManager.class.getName());

	@PostConstruct
	public Control createContents(final Composite composite) {

		composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		final TableViewer tblVwrSetup = new TableViewer(composite,
				SWT.SINGLE | SWT.FULL_SELECTION | SWT.PUSH | SWT.V_SCROLL);

		final Table table = tblVwrSetup.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(false);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		tblVwrSetup.setContentProvider(new ArrayContentProvider());
		createTableColumns(tblVwrSetup);
		tblVwrSetup.setInput(createTableRows(composite));
		tblVwrSetup.getControl().setBackground(GUIHelper.getApplicationBackground());
		tblVwrSetup.getControl().setFont(FontProvider.getInstance().get(FontProvider.HEADER_FONT));
		tblVwrSetup.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tblVwrSetup.getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof SetupTableItem) {
					((SetupTableItem) firstElement).getListener().onClick();
				}
			}
		});

		logger.log(Level.FINE, "Created installer table");

		return composite;
	}

	private Object createTableRows(final Composite composite) {

		ArrayList<SetupTableItem> items = new ArrayList<SetupTableItem>();

		Image image = new Image(Display.getCurrent(), getInputStream("lider-setup.png"));
		SetupTableItem liderSetupItem = new SetupTableItem("Lider Kurulum", image, new IOnClickListener() {
			@Override
			public void onClick() {
				WizardDialog wizardDialog = new WizardDialog(composite.getShell(), new LiderSetupWizard());
				wizardDialog.open();
			}
		});
		items.add(liderSetupItem);

		image = new Image(Display.getCurrent(), getInputStream("ahenk-setup.png"));
		SetupTableItem ahenkSetupItem = new SetupTableItem("Ahenk Kurulum", image, new IOnClickListener() {
			@Override
			public void onClick() {
				WizardDialog wizardDialog = new WizardDialog(composite.getShell(), new AhenkSetupWizard());
				wizardDialog.open();
			}
		});
		items.add(ahenkSetupItem);

		return items;
	}

	private InputStream getInputStream(String filename) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream(filename);
		return stream;
	}

	private void createTableColumns(final TableViewer tblVwrSetup) {

		TableViewerColumn imageCol = createTableViewerColumn(tblVwrSetup, "", 200);
		imageCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				if (element instanceof SetupTableItem) {
					return ((SetupTableItem) element).getImage();
				}
				return null;
			}

			@Override
			public String getText(Object element) {
				return null;
			}
		});

		TableViewerColumn descCol = createTableViewerColumn(tblVwrSetup, "", 200);
		descCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof SetupTableItem) {
					return ((SetupTableItem) element).getDescription();
				}
				return null;
			}
		});

	}

	private TableViewerColumn createTableViewerColumn(final TableViewer tblVwrSetup, String title, int width) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tblVwrSetup, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(width);
		column.setResizable(true);
		column.setMoveable(false);
		column.setAlignment(SWT.CENTER);
		return viewerColumn;
	}

}