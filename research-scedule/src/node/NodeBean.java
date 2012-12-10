package node;

public class NodeBean {

	public int pid; //クライアントのID
        public int cid; //クライアント内のコアID
	public String ip; //クライアントのIPアドレス
	public int port; //クライアント側のポート
	
	public NodeBean(int pid, int cid, String ip, int port){
		this.pid = pid;
                this.cid = cid;
		this.ip = ip;
		this.port = port;
	}

	public int getProcId() {
		return pid;
	}
        

	public int getCoreId() {
		return cid;
	}

	public String getIp() {
		return ip;
	}


	public int getPort() {
		return port;
	}
	
        public void outputdata(){
            System.out.println("ProcID　:　"+getProcId()+"  CoreID　:　"+getCoreId()+"  IP　:　"+getIp()+"  Port　:　"+getPort());
        }
	
	
}
