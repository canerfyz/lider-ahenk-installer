package tr.org.liderahenk.installer.lider.wizard.pages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import tr.org.liderahenk.installer.lider.wizard.model.DatabaseClusterNodeModel;
import tr.org.pardus.mys.liderahenksetup.utils.FontProvider;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

public class DatabaseClusterConfPage extends WizardPage implements IDatabasePage {

	private LiderSetupConfig config;
	
	private Composite cmpMain;
	
	private Button btnAddRemoveNode;
	
	private Text txtDbRootPwd;
	private Text txtClusterName;
	private Text txtSstUsername;
	private Text txtSstPwd;
	
	private Map<Integer, DatabaseClusterNodeModel> nodeMap = new HashMap<Integer, DatabaseClusterNodeModel>();
	
	public DatabaseClusterConfPage(LiderSetupConfig config) {
		super(DatabaseClusterConfPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("2.2 " + Messages.getString("DATABASE_CLUSTER_CONF"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {
		
		cmpMain = GUIHelper.createComposite(parent, 1);
		setControl(cmpMain);
		
		Label lblGeneralInfo = GUIHelper.createLabel(cmpMain, Messages.getString("MARIADB_CLUSTER_GENERAL_INFO"));
		lblGeneralInfo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
		
		Composite cmpGeneralInfo = GUIHelper.createComposite(cmpMain, 2);
		cmpGeneralInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		// General parameters' inputs
		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("MARIA_CLUSTER_NAME"));
		txtClusterName = GUIHelper.createText(cmpGeneralInfo);
		txtClusterName.setText("MariaDB_Cluster");
		txtClusterName.setMessage(Messages.getString("ENTER_NAME_FOR_CLUSTER"));
		txtClusterName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("MARIA_DB_ROOT_PWD"));
		txtDbRootPwd = GUIHelper.createText(cmpGeneralInfo);
		txtDbRootPwd.setMessage(Messages.getString("ENTER_PWD_FOR_DB_ROOT_USER"));
		txtDbRootPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		
		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("SST_AUTH_USERNAME"));
		txtSstUsername = GUIHelper.createText(cmpGeneralInfo);
		txtSstUsername.setText("sst_user");
		txtSstUsername.setMessage(Messages.getString("ENTER_USERNAME_FOR_SST_USER"));
		txtSstUsername.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		
		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("SST_AUTH_PWD"));
		txtSstPwd = GUIHelper.createText(cmpGeneralInfo); 
		txtSstPwd.setMessage(Messages.getString("ENTER_PWD_FOR_SST_USER"));
		txtSstPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		
		Label lblNodeInfo = GUIHelper.createLabel(cmpMain, Messages.getString("MARIADB_CLUSTER_NODE_INFO"));
		lblNodeInfo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
		
		// Labels for headers
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
		
		updatePageCompleteStatus();
	}
	
	private void handleRemoveButtonClick(SelectionEvent event) {
		Button btnThis = (Button) event.getSource();
		Composite parent = btnThis.getParent();
		Control[] children = parent.getChildren();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				if (children[i].equals(btnThis) && i - 1 > 0) {
					Group group = (Group) children[i - 1];
					Control[] childrenOfGroup = group.getChildren();
					Label number = (Label) childrenOfGroup[0];
					Integer intNumber = Integer.parseInt(number.getText());
					nodeMap.remove(intNumber);
					children[i - 1].dispose();
					children[i].dispose();
					redraw();
					break;
				}
			}
		}
		
		updatePageCompleteStatus();
	}
	
	private void createNewNode(Composite cmpNodeList) {
		Group grpClusterNode = GUIHelper.createGroup(cmpNodeList, 6);
		grpClusterNode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		GridData gd = new GridData();
		gd.widthHint = 190;
		
		DatabaseClusterNodeModel clusterNode = new DatabaseClusterNodeModel();
		
		Integer nodeNumber = nodeMap.size() + 1;
		GUIHelper.createLabel(grpClusterNode, nodeNumber.toString());
		
		Text txtNodeIp = GUIHelper.createText(grpClusterNode);
		txtNodeIp.setLayoutData(gd);
		txtNodeIp.setMessage(Messages.getString("ENTER_IP_FOR_THIS_NODE"));
		txtNodeIp.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		clusterNode.setTxtNodeIp(txtNodeIp);
		
		Text txtNodeName = GUIHelper.createText(grpClusterNode);
		txtNodeName.setLayoutData(gd);
		txtNodeName.setMessage(Messages.getString("ENTER_NAME_FOR_THIS_NODE"));
		txtNodeName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		clusterNode.setTxtNodeName(txtNodeName);
		
		Text txtNodeRootPwd = GUIHelper.createText(grpClusterNode);
		txtNodeRootPwd.setLayoutData(gd);
		txtNodeRootPwd.setMessage("ENTER_ROOT_PWD_OF_THIS_NODE");
		txtNodeRootPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		clusterNode.setTxtNodeRootPwd(txtNodeRootPwd);
		
		Button btnNodeNewSetup = new Button(grpClusterNode, SWT.CHECK | SWT.BORDER);
		btnNodeNewSetup.setFont(FontProvider.getInstance().get(FontProvider.LABEL_FONT));
		btnNodeNewSetup.setSelection(true);
		btnNodeNewSetup.setToolTipText(Messages.getString("UNCHECK_IF_THIS_NODE_IS_ALREADY_INSTALLED"));
		clusterNode.setBtnNodeNewSetup(btnNodeNewSetup);
		
		nodeMap.put(nodeNumber, clusterNode);
	}
	
	private void redraw() {
		cmpMain.redraw();
		cmpMain.layout(true, true);
	}

	@Override
	public IWizardPage getNextPage() {
		
		setConfigVariables();
		
		return super.getNextPage();
	}

	private void setConfigVariables() {
		
		config.setDatabaseClusterAddress(createWsrepClusterAddress());
		config.setDatabaseClusterName(txtClusterName.getText());
		config.setDatabaseRootPassword(txtDbRootPwd.getText());
		config.setDatabaseSstUsername(txtSstUsername.getText());
		config.setDatabaseSstPwd(txtSstPwd.getText());
		config.setDatabaseNodeMap(nodeMap);
	}

	private String createWsrepClusterAddress() {
		
		String wsrepClusterAddress = "";
		
		for (Iterator<Entry<Integer, DatabaseClusterNodeModel>> iterator = nodeMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, DatabaseClusterNodeModel> entry = iterator.next();
			DatabaseClusterNodeModel clusterNode = entry.getValue();
			wsrepClusterAddress += clusterNode.getTxtNodeIp().getText() + ",";
			
		}
		
		// Delete last comma
		wsrepClusterAddress = wsrepClusterAddress.substring(0, wsrepClusterAddress.length()-1);

		return wsrepClusterAddress;
		
	}
	
	private void updatePageCompleteStatus() {
		
		boolean pageComplete = false;
		
		if (!txtClusterName.getText().isEmpty() &&
				!txtDbRootPwd.getText().isEmpty() &&
				!txtSstUsername.getText().isEmpty() &&
				!txtSstPwd.getText().isEmpty()) {
			for (Iterator<Entry<Integer, DatabaseClusterNodeModel>> iterator = nodeMap.entrySet().iterator(); iterator.hasNext();) {
				Entry<Integer, DatabaseClusterNodeModel> entry = iterator.next();
				DatabaseClusterNodeModel node = entry.getValue();
				if (!node.getTxtNodeIp().getText().isEmpty() &&
						!node.getTxtNodeName().getText().isEmpty() && 
						!node.getTxtNodeRootPwd().getText().isEmpty() &&
						node.getBtnNodeNewSetup().getSelection()) {
					pageComplete = true;
				} else {
					pageComplete = false;
					break;
				}
				
			}
			
			setPageComplete(pageComplete);
		} else {
			setPageComplete(false);
		}
	}
	
	
	
	
	
}
