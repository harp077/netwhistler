package nnm.snmp;

import nnm.NetworkManagerGUI;
import nnm.Node;

public class OidToCheck {
	public String OidName = "";

	public String Oid = "";

	public String Value = "";

	public Node aNode;

	public OidToCheck() {
		this("", "", "");
	}

	public OidToCheck(String aService, String aOid, String aValue) {
		OidName = aService;
		Oid = aOid;
		Value = aValue;

	}

	public void setOidtoCheck(String s) {
		Oid = s;
	}

	public void setOidName(String s) {
		OidName = s;
	}

	public void setOidValue(String s) {
		Value = s;
	}

	public String getOidName() {
		return OidName;
	}

	public String getOidtoCheck() {
		return Oid;
	}

	public String getOidValue() {
		return Value;
	}
	
}