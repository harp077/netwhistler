package nnm.inet;



import nnm.Node;
import nnm.inet.services.monCitrix;
import nnm.inet.services.monFTP;
import nnm.inet.services.monHTTP;
import nnm.inet.services.monImap;
import nnm.inet.services.monPOP3;
import nnm.inet.services.monSMB;
import nnm.inet.services.monSMTP;
import nnm.inet.services.monSSH;

public class Service {
public String ServiceName="";
public boolean Status;
public Node aNode;

  public Service() {
    this("",  true);
  }

  public Service(String aService, boolean aStatus) {
    ServiceName=aService;
    Status = aStatus;

  }
 public void setService(String s) {
   ServiceName=s;
 }
 
 public void setStatus(boolean s) {
   Status=s;
 }

  public String getServiceName() {
    return ServiceName;
  }
 
   public boolean getStatus() {
      return Status;
    }
  public void Check(Node sNode){
	aNode = sNode;  
   if (ServiceName.equals("FTP"))
		monFTP.checkFTP(aNode);
   else if (ServiceName.equals("SSH"))
		monSSH.checkSSH(aNode);
   else if (ServiceName.equals("SMTP"))
		monSMTP.checkSMTP(aNode);
   else if (ServiceName.equals("POP3"))
		monPOP3.checkPOP3(aNode);
   else if (ServiceName.equals("HTTP"))
		monHTTP.checkHTTP(aNode);
   else if (ServiceName.equals("IMAP"))
		monImap.checkImap(aNode);
   else if (ServiceName.equals("SAMBA"))
		monSMB.checkSMB(aNode);
   else if (ServiceName.equals("CITRIX"))
		monCitrix.checkCitrix(aNode);
    } 
  
}