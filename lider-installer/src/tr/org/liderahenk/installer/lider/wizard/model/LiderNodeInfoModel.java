package tr.org.liderahenk.installer.lider.wizard.model;

public class LiderNodeInfoModel {

	private int nodeNumber;
	private String nodeIp;
	private String nodeName;
	private String nodeRootPwd;
	
	public LiderNodeInfoModel(int nodeNumber, String nodeIp, String nodeName, String nodeRootPwd) {
		super();
		this.nodeNumber = nodeNumber;
		this.nodeIp = nodeIp;
		this.nodeName = nodeName;
		this.nodeRootPwd = nodeRootPwd;
	}
	
	public int getNodeNumber() {
		return nodeNumber;
	}
	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}
	public String getNodeIp() {
		return nodeIp;
	}
	public void setNodeIp(String nodeIp) {
		this.nodeIp = nodeIp;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getNodeRootPwd() {
		return nodeRootPwd;
	}
	public void setNodeRootPwd(String nodeRootPwd) {
		this.nodeRootPwd = nodeRootPwd;
	}
}
