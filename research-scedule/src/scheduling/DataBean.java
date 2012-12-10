package scheduling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import node.NodeBean;
import node.NodeClientBean;

public class DataBean  implements Serializable {

	//実行モード設定用
	public String task = "";
		
	//スレーブのIPとポートをマスターから教えてやるために用意
	public String slaveIp;
	
	//何番のタスクからのデータになるか．送信前にセットする．
	public int sendFromTaskNum = 0;
	
	public ExTaskList ExTaskLists = new ExTaskList();
	public LinkedList<NodeClientBean> nodeClientBeans;
	
	//public boolean dummyFlag = false;
	public long compWeight = 1000000000L;
	public int commWeight = 1000*1000;
	public byte[] dummyByte;
	public int syncSec = 1000;
	
	public String masterIp;
	public int masterPort;

	public int sendFromProcNum;
	public int rootTaskID;
        public int finalTaskID;
	
        Proclist Plist;
        DAG TaskGraph;
        
	//ノードにノードIP通知用
	public DataBean(String ip){
		this.slaveIp = ip;
	}
	

	public DataBean(){

        }
        
        /*
	public DataBean copyDeep() {
		
		DataBean newData = new DataBean();
		
		newData.commWeight = this.commWeight;
		newData.compWeight = this.compWeight;
		newData.crashTask = this.crashTask;
		newData.dummyByte = this.dummyByte;
		newData.sendFromTaskNum = this.sendFromTaskNum;
		newData.slaveIp = this.slaveIp;
		newData.startTime = this.startTime;
		newData.syncSec = this.syncSec;
		newData.task = this.task;
		newData.sendFromProcNum = this.sendFromProcNum;
		newData.rebootTime = this.rebootTime;
		newData.entryTaskNumber = this.entryTaskNumber;
		
		LinkedList<MachineExamBeans> copiedMachineExamBeans = new LinkedList<MachineExamBeans>();
		for(MachineExamBeans v:this.machineExamBeans){
			MachineExamBeans machineExamBean = new MachineExamBeans();
			machineExamBean.compCost = v.compCost;
			machineExamBean.crash = v.crash;
			machineExamBean.procNumber = v.procNumber;
			machineExamBean.taskNumber = v.taskNumber;
			
			LinkedList<NextPath> copiedNextPaths = new LinkedList<NextPath>();
			for(NextPath w: v.nextPaths){
				copiedNextPaths.add(new NextPath(w.taskNumber, w.prevCommCost));
			}
			machineExamBean.nextPaths = copiedNextPaths;
			
			LinkedList<PrevPath> copiedPrevPaths = new LinkedList<PrevPath>();
			for(PrevPath w: v.prevPaths){
				copiedNextPaths.add(new NextPath(w.taskNumber, w.succCommCost));
			}
			machineExamBean.prevPaths = copiedPrevPaths;
			
			copiedMachineExamBeans.add(machineExamBean);
		}
		newData.machineExamBeans = copiedMachineExamBeans;
		
		LinkedList<NodeClientBean> copiedNodeClientBeans = new LinkedList<NodeClientBean>();
		for(NodeClientBean v : this.nodeClientBeans){
			
			NodeClientBean nodeClientBean = new NodeClientBean();
			nodeClientBean.clientAddress = v.clientAddress;
			nodeClientBean.clientId = v.clientId;
			nodeClientBean.clientPort = v.clientPort;
			nodeClientBean.procNum = v.procNum;
		
			copiedNodeClientBeans.add(nodeClientBean);
		}
		
		newData.nodeClientBeans = copiedNodeClientBeans;

		return newData;
	
	}
        * 
        */
}