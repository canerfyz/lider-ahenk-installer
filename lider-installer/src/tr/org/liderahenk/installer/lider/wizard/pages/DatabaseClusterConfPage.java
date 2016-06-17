package tr.org.liderahenk.installer.lider.wizard.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.FontProvider;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

public class DatabaseClusterConfPage extends WizardPage implements IDatabasePage, ControlNextEvent {

	private LiderSetupConfig config;
	
	private NextPageEventType nextPageEventType;
	
	private Composite cmpMain;
	
	private Button btnAddRemoveNode;
	
	private Text txtDbRootPwd;
	private Text txtClusterName;
	private Text txtSstUsername;
	private Text txtSstPwd;
	
	private Text txtNodeIp;
	private Text txtNodeName;
	private Text txtNodeRootPwd;
	private Button btnNodeNewSetup;
	
	public DatabaseClusterConfPage(LiderSetupConfig config) {
		super(DatabaseClusterConfPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("2.2 " + Messages.getString("DATABASE_CLUSTER_CONF"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {
		
		cmpMain = GUIHelper.createComposite(parent, 1);
		setControl(cmpMain);
		
		// TODO add general info label
		
		Composite cmpGeneralInfo = GUIHelper.createComposite(cmpMain, 2);
		cmpGeneralInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("MARIA_DB_ROOT_PWD"));
		txtDbRootPwd = GUIHelper.createText(cmpGeneralInfo); 

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("MARIA_CLUSTER_NAME"));
		txtClusterName = GUIHelper.createText(cmpGeneralInfo); 
		
		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("SST_AUTH_USERNAME"));
		txtSstUsername = GUIHelper.createText(cmpGeneralInfo); 

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("SST_AUTH_PWD"));
		txtSstPwd = GUIHelper.createText(cmpGeneralInfo); 
		
		Composite cmpLabels = GUIHelper.createComposite(cmpMain, 4);
		cmpLabels.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridData gdLabels = new GridData();
		gdLabels.widthHint = 205;
		Label lblNodeIp = GUIHelper.createLabel(cmpLabels, Messages.getString("NODE_IP"));
		lblNodeIp.setLayoutData(gdLabels);
		Label lblNodeName = GUIHelper.createLabel(cmpLabels, Messages.getString("NODE_NAME"));
		lblNodeName.setLayoutData(gdLabels);
		Label lblNodeRootPwd = GUIHelper.createLabel(cmpLabels, Messages.getString("NODE_ROOT_PWD"));
		lblNodeRootPwd.setLayoutData(gdLabels);
		Label lblNodeNewSetup = GUIHelper.createLabel(cmpLabels, Messages.getString("NODE_NEW_SETUP"));
		lblNodeNewSetup.setLayoutData(gdLabels);
		
		// TODO add general parameters
		// TODO add labels for headers
		Composite cmpNodeList = GUIHelper.createComposite(cmpMain, 2);
		cmpNodeList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		// Add at least 3 nodes
		createNewNode(cmpNodeList);
		GUIHelper.createLabel(cmpNodeList);
		createNewNode(cmpNodeList);
		GUIHelper.createLabel(cmpNodeList);
		createNewNode(cmpNodeList);

		btnAddRemoveNode = GUIHelper.createButton(cmpNodeList, SWT.PUSH);
		btnAddRemoveNode.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/add.png")));
		btnAddRemoveNode.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				handleAddButtonClick(event);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		
		setPageComplete(false);
	}

	private void handleAddButtonClick(SelectionEvent event) {
		Composite parent = (Composite) ((Button) event.getSource()).getParent();
		createNewNode(parent);
		
		Button btnRemoveNode = GUIHelper.createButton(parent, SWT.PUSH);
		btnRemoveNode.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/remove.png")));
		btnRemoveNode.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				handleRemoveButtonClick(event);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		
		redraw();
	}
	
	private void handleRemoveButtonClick(SelectionEvent event) {
		Button btnThis = (Button) event.getSource();
		Composite parent = btnThis.getParent();
		Control[] children = parent.getChildren();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				if (children[i].equals(btnThis) && i - 1 > 0) {
					children[i - 1].dispose();
					children[i].dispose();
					redraw();
					break;
				}
			}
		}
	}
	
	private void createNewNode(Composite cmpNodeList) {
		Group grpClusterNode = GUIHelper.createGroup(cmpNodeList, 5);
		grpClusterNode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		GridData gd = new GridData();
		gd.widthHint = 190;
		
		txtNodeIp = GUIHelper.createText(grpClusterNode);
		txtNodeIp.setLayoutData(gd);
		
		txtNodeName = GUIHelper.createText(grpClusterNode);
		txtNodeName.setLayoutData(gd);
		
		txtNodeRootPwd = GUIHelper.createText(grpClusterNode);
		txtNodeRootPwd.setLayoutData(gd);

		btnNodeNewSetup = new Button(grpClusterNode, SWT.CHECK | SWT.BORDER);
		btnNodeNewSetup.setFont(FontProvider.getInstance().get(FontProvider.LABEL_FONT));
		btnNodeNewSetup.setSelection(true);
	}
	
	private void redraw() {
		cmpMain.redraw();
		cmpMain.layout(true, true);
	}

	@Override
	public NextPageEventType getNextPageEventType() {
		return this.nextPageEventType;
	}

	@Override
	public void setNextPageEventType(NextPageEventType nextPageEventType) {
		this.nextPageEventType = nextPageEventType;
	}
	
}
