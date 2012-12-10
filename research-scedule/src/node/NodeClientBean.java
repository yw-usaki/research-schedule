package node;

import java.io.Serializable;

public class NodeClientBean  implements Serializable{

	private static final long serialVersionUID = 1L;
	public int procID;
        public int coreID;
	
	public String clientAddress;
        public int clientPort;
	public int clientId;
        
        
        public void outputdata(){
            System.out.println(
                    "ProcID : " + procID +
                    " CoreID : " + coreID +
                    " clientAddress : " + clientAddress +
                    " clientPort : " + clientPort +
                    " clientID : " + clientId
                    );
        }

}
