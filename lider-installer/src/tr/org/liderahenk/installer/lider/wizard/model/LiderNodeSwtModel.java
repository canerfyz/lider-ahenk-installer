package tr.org.liderahenk.installer.lider.wizard.model;

import org.eclipse.swt.widgets.Text;

public class LiderNodeSwtModel {

	private int nodeNumber;
	private Text txtNodeIp;
	private Text txtNodeRootPwd;
	
	public Text getTxtNodeIp() {
		return txtNodeIp;
	}
	public void setTxtNodeIp(Text txtNodeIp) {
		this.txtNodeIp = txtNodeIp;
	}
	public Text getTxtNodeRootPwd() {
		return txtNodeRootPwd;
	}
	public void setTxtNodeRootPwd(Text txtNodeRootPwd) {
		this.txtNodeRootPwd = txtNodeRootPwd;
	}
	public int getNodeNumber() {
		return nodeNumber;
	}
	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}
	
}
