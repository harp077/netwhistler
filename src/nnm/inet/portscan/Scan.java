// This file is part of the Mila NetWhistler Network Monitor.
// Copyright (C) 2005 Mila NetWhistler.  All rights reserved.
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.                                                            
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//       
// For more information contact: 
//      Alexander Eremin    <netwhistler@gmail.com>
//		http://www.netwhistler.spb.ru
//

package nnm.inet.portscan;

import java.util.Date;
import nnm.inet.outputPrint;
import java.net.*;
import nnm.FPinger;


public class Scan {

	public static String addr;

	static int port;

	static int start;

	static int end;

	static boolean good;

	static Socket socket;

	static boolean stop;

	public static String[] serv = new String[1024];

	public static void stop() {
		try {
			socket.close();
			stop = true;
		} catch (Exception e) {

		}
	}

	public static void run(String host, int sport, int eport) {

		addr = host;
		start = sport;
		end = eport;
		serv[0] = "reserved";
		serv[1] = "tcpmux";
		serv[2] = "compressnet";
		serv[3] = "compressnet";
		serv[4] = "unassigned";
		serv[5] = "rje";
		serv[6] = "unassigned";
		serv[7] = "echo";
		serv[8] = "unassigned";
		serv[9] = "discard";
		serv[10] = "unassigned";
		serv[11] = "systat";
		serv[12] = "unassigned";
		serv[13] = "daytime";
		serv[14] = "unassigned";
		serv[15] = "unassigned";
		serv[16] = "unassigned";
		serv[17] = "qotd";
		serv[18] = "msp";
		serv[19] = "chargen";
		serv[20] = "ftp-data";
		serv[21] = "ftp";
		serv[22] = "ssh";
		serv[23] = "telnet";
		serv[24] = "unknown";
		serv[25] = "smtp";
		serv[26] = "unassigned";
		serv[27] = "nsw-fe";
		serv[28] = "unassigned";
		serv[29] = "msg-icp";
		serv[30] = "unassigned";
		serv[31] = "msg-auth";
		serv[32] = "unassigned";
		serv[33] = "dsp";
		serv[34] = "unassigned";
		serv[35] = "unknown";
		serv[36] = "unassigned";
		serv[37] = "time";
		serv[38] = "rap";
		serv[39] = "rlp";
		serv[40] = "unassigned";
		serv[41] = "graphics";
		serv[42] = "name";
		serv[42] = "nameserver";
		serv[43] = "nicname";
		serv[44] = "mpm-flags";
		serv[45] = "mpm";
		serv[46] = "mpm-snd";
		serv[47] = "ni-ftp";
		serv[48] = "auditd";
		serv[49] = "tacacs";
		serv[50] = "re-mail-ck";
		serv[51] = "la-maint";
		serv[52] = "xns-time";
		serv[53] = "domain";
		serv[54] = "xns-ch";
		serv[55] = "isi-gl";
		serv[56] = "xns-auth";
		serv[57] = "unknown";
		serv[58] = "xns-mail";
		serv[59] = "unknown";
		serv[60] = "unknown";
		serv[61] = "ni-mail";
		serv[62] = "acas";
		serv[63] = "whois++";
		serv[64] = "covia";
		serv[65] = "tacacs-ds";
		serv[66] = "sql*net";
		serv[67] = "bootps";
		serv[68] = "bootpc";
		serv[69] = "tftp";
		serv[70] = "gopher";
		serv[71] = "netrjs-1";
		serv[72] = "netrjs-2";
		serv[73] = "netrjs-3";
		serv[74] = "netrjs-4";
		serv[75] = "unknown";
		serv[76] = "deos";
		serv[77] = "unknown";
		serv[78] = "vettcp";
		serv[79] = "finger";
		serv[80] = "http";
		serv[80] = "www";
		serv[80] = "www-http";
		serv[81] = "hosts2-ns";
		serv[82] = "xfer";
		serv[83] = "mit-ml-dev";
		serv[84] = "ctf";
		serv[85] = "mit-ml-dev";
		serv[86] = "mfcobol";
		serv[87] = "unknown";
		serv[88] = "kerberos";
		serv[89] = "su-mit-tg";
		serv[90] = "dnsix";
		serv[91] = "mit-dov";
		serv[92] = "npp";
		serv[93] = "dcp";
		serv[94] = "objcall";
		serv[95] = "supdup";
		serv[96] = "dixie";
		serv[97] = "swift-rvf";
		serv[98] = "tacnews";
		serv[99] = "metagram";
		serv[100] = "newacct";
		serv[101] = "hostname";
		serv[102] = "iso-tsap";
		serv[103] = "gppitnp";
		serv[104] = "acr-nema";
		serv[105] = "cso";
		serv[105] = "cso";
		serv[105] = "csnet-ns";
		serv[106] = "3com-tsmux";
		serv[107] = "rtelnet";
		serv[108] = "snagas";
		serv[109] = "pop2";
		serv[110] = "pop3";
		serv[111] = "sunrpc";
		serv[112] = "mcidas";
		serv[113] = "ident";
		serv[113] = "auth";
		serv[114] = "audionews";
		serv[115] = "sftp";
		serv[116] = "ansanotify";
		serv[117] = "uucp-path";
		serv[118] = "sqlserv";
		serv[119] = "nntp";
		serv[120] = "cfdptkt";
		serv[121] = "erpc";
		serv[122] = "smakynet";
		serv[123] = "ntp";
		serv[124] = "ansatrader";
		serv[125] = "locus-map";
		serv[126] = "unitary";
		serv[127] = "locus-con";
		serv[128] = "gss-xlicen";
		serv[129] = "pwdgen";
		serv[130] = "cisco-fna";
		serv[131] = "cisco-tna";
		serv[132] = "cisco-sys";
		serv[133] = "statsrv";
		serv[134] = "ingres-net";
		serv[135] = "epmap";
		serv[136] = "profile";
		serv[137] = "netbios-ns";
		serv[138] = "netbios-dgm";
		serv[139] = "netbios-ssn";
		serv[140] = "emfis-data";
		serv[141] = "emfis-cntl";
		serv[142] = "bl-idm";
		serv[143] = "imap";
		serv[144] = "news";
		serv[145] = "uaac";
		serv[146] = "iso-tp0";
		serv[147] = "iso-ip";
		serv[148] = "jargon";
		serv[149] = "aed-512";
		serv[150] = "sql-net";
		serv[151] = "hems";
		serv[152] = "bftp";
		serv[153] = "sgmp";
		serv[154] = "netsc-prod";
		serv[155] = "netsc-dev";
		serv[156] = "sqlsrv";
		serv[157] = "knet-cmp";
		serv[158] = "pcmail-srv";
		serv[159] = "nss-routing";
		serv[160] = "sgmp-traps";
		serv[161] = "snmp";
		serv[162] = "snmptrap";
		serv[163] = "cmip-man";
		serv[164] = "cmip-agent";
		serv[165] = "xns-courier";
		serv[166] = "s-net";
		serv[167] = "namp";
		serv[168] = "rsvd";
		serv[169] = "send";
		serv[170] = "print-srv";
		serv[171] = "multiplex";
		serv[172] = "cl/1";
		serv[173] = "xyplex-mux";
		serv[174] = "mailq";
		serv[175] = "vmnet";
		serv[176] = "genrad-mux";
		serv[177] = "xdmcp";
		serv[178] = "nextstep";
		serv[179] = "bgp";
		serv[180] = "ris";
		serv[181] = "unify";
		serv[182] = "audit";
		serv[183] = "ocbinder";
		serv[184] = "ocserver";
		serv[185] = "remote-kis";
		serv[186] = "kis";
		serv[187] = "aci";
		serv[188] = "mumps";
		serv[189] = "qft";
		serv[190] = "gacp";
		serv[191] = "prospero";
		serv[192] = "osu-nms";
		serv[193] = "srmp";
		serv[194] = "irc";
		serv[195] = "dn6-nlm-aud";
		serv[196] = "dn6-smm-red";
		serv[197] = "dls";
		serv[198] = "dls-mon";
		serv[199] = "smux";
		serv[200] = "src";
		serv[201] = "at-rtmp";
		serv[202] = "at-nbp";
		serv[203] = "at-3";
		serv[204] = "at-echo";
		serv[205] = "at-5";
		serv[206] = "at-zis";
		serv[207] = "at-7";
		serv[208] = "at-8";
		serv[209] = "qmtp";
		serv[210] = "z39.50";
		serv[211] = "914c/g";
		serv[212] = "anet";
		serv[213] = "ipx";
		serv[214] = "vmpwscs";
		serv[215] = "softpc";
		serv[216] = "CAIlic";
		serv[217] = "dbase";
		serv[218] = "mpp";
		serv[219] = "uarps";
		serv[220] = "imap3";
		serv[221] = "fln-spx";
		serv[222] = "rsh-spx";
		serv[223] = "cdc";
		serv[242] = "direct";
		serv[243] = "sur-meas";
		serv[244] = "dayna";
		serv[245] = "link";
		serv[246] = "dsp3270";
		serv[256] = "rap";
		serv[257] = "set";
		serv[258] = "yak-chat";
		serv[259] = "esro-gen";
		serv[260] = "openport";
		serv[261] = "nsiiops";
		serv[262] = "arcisdms";
		serv[263] = "hdap";
		serv[280] = "http-mgmt";
		serv[281] = "personal-link";
		serv[282] = "cableport-ax";
		serv[309] = "entrusttime";
		serv[344] = "pdap";
		serv[345] = "pawserv";
		serv[346] = "zserv";
		serv[347] = "fatserv";
		serv[348] = "csi-sgwp";
		serv[349] = "mftp";
		serv[350] = "matip-type-a";
		serv[351] = "matip-type-b";
		serv[352] = "dtag-ste-sb";
		serv[371] = "clearcase";
		serv[372] = "ulistproc";
		serv[373] = "legent-1";
		serv[374] = "legent-2";
		serv[375] = "hassle";
		serv[376] = "nip";
		serv[377] = "tnETOS";
		serv[378] = "dsETOS";
		serv[379] = "is99c";
		serv[380] = "is99s";
		serv[381] = "hp-collector";
		serv[382] = "hp-managed-node";
		serv[383] = "hp-alarm-mgr";
		serv[384] = "arns";
		serv[385] = "ibm-app";
		serv[386] = "asa";
		serv[387] = "aurp";
		serv[388] = "unidata-ldm";
		serv[389] = "ldap";
		serv[390] = "uis";
		serv[391] = "synotics-relay";
		serv[392] = "synotics-broker";
		serv[393] = "dis";
		serv[394] = "embl-ndt";
		serv[395] = "netcp";
		serv[396] = "netware-ip";
		serv[397] = "mptn";
		serv[398] = "kryptolan";
		serv[399] = "iso-tsap-c2";
		serv[400] = "work-sol";
		serv[401] = "ups";
		serv[402] = "genie";
		serv[403] = "decap";
		serv[404] = "nced";
		serv[405] = "ncld";
		serv[406] = "imsp";
		serv[407] = "timbuktu";
		serv[408] = "prm-sm";
		serv[409] = "prm-nm";
		serv[410] = "decladebug";
		serv[411] = "rmt";
		serv[412] = "synoptics-trap";
		serv[413] = "smsp";
		serv[414] = "infoseek";
		serv[415] = "bnet";
		serv[416] = "silverplatter";
		serv[417] = "onmux";
		serv[418] = "hyper-g";
		serv[419] = "ariel1";
		serv[420] = "smpte";
		serv[421] = "ariel2";
		serv[422] = "ariel3";
		serv[423] = "opc-job-start";
		serv[424] = "opc-job-track";
		serv[425] = "icad-el";
		serv[426] = "smartsdp";
		serv[427] = "svrloc";
		serv[428] = "ocs_cmu";
		serv[429] = "ocs_amu";
		serv[430] = "utmpsd";
		serv[431] = "utmpcd";
		serv[432] = "iasd";
		serv[433] = "nnsp";
		serv[434] = "mobileip-agent";
		serv[435] = "mobilip-mn";
		serv[436] = "dna-cml";
		serv[437] = "comscm";
		serv[438] = "dsfgw";
		serv[439] = "dasp";
		serv[440] = "sgcp";
		serv[441] = "decvms-sysmgt";
		serv[442] = "cvc_hostd";
		serv[443] = "https";
		serv[444] = "snpp";
		serv[445] = "microsoft-ds";
		serv[446] = "ddm-rdb";
		serv[447] = "ddm-dfm";
		serv[448] = "ddm-byte";
		serv[449] = "as-servermap";
		serv[450] = "tserver";
		serv[451] = "sfs-smp-net";
		serv[452] = "sfs-config";
		serv[453] = "creativeserver";
		serv[454] = "contentserver";
		serv[455] = "creativepartnr";
		serv[456] = "macon-tcp";
		serv[457] = "scohelp";
		serv[458] = "appleqtc";
		serv[459] = "ampr-rcmd";
		serv[460] = "skronk";
		serv[461] = "datasurfsrv";
		serv[462] = "datasurfsrvsec";
		serv[463] = "alpes";
		serv[464] = "kpasswd";
		serv[465] = "smtps";
		serv[466] = "digital-vrc";
		serv[467] = "mylex-mapd";
		serv[468] = "photuris";
		serv[469] = "rcp";
		serv[470] = "scx-proxy";
		serv[471] = "mondex";
		serv[472] = "ljk-login";
		serv[473] = "hybrid-pop";
		serv[474] = "tn-tl-w1";
		serv[475] = "tcpnethaspsrv";
		serv[475] = "tcpnethaspsrv";
		serv[476] = "tn-tl-fd1";
		serv[477] = "ss7ns";
		serv[478] = "spsc";
		serv[479] = "iafserver";
		serv[480] = "iafdbase";
		serv[481] = "ph";
		serv[482] = "bgs-nsi";
		serv[483] = "ulpnet";
		serv[484] = "integra-sme";
		serv[485] = "powerburst";
		serv[486] = "avian";
		serv[487] = "saft";
		serv[488] = "gss-http";
		serv[489] = "nest-protocol";
		serv[490] = "micom-pfs";
		serv[491] = "go-login";
		serv[492] = "ticf-1";
		serv[493] = "ticf-2";
		serv[494] = "pov-ray";
		serv[495] = "intecourier";
		serv[496] = "pim-rp-disc";
		serv[497] = "dantz";
		serv[498] = "siam";
		serv[499] = "iso-ill";
		serv[500] = "isakmp";
		serv[501] = "stmf";
		serv[502] = "asa-appl-proto";
		serv[503] = "intrinsa";
		serv[504] = "citadel";
		serv[505] = "mailbox-lm";
		serv[506] = "ohimsrv";
		serv[507] = "crs";
		serv[508] = "xvttp";
		serv[509] = "snare";
		serv[510] = "fcp";
		serv[511] = "mynet";
		serv[512] = "exec";
		serv[513] = "login";
		serv[514] = "shell";
		serv[515] = "printer";
		serv[516] = "videotex";
		serv[517] = "talk";
		serv[518] = "ntalk";
		serv[519] = "utime";
		serv[520] = "efs";
		serv[521] = "ripng";
		serv[522] = "ulp";
		serv[523] = "ibm-db2";
		serv[523] = "ibm-db2";
		serv[524] = "ncp";
		serv[525] = "timed";
		serv[526] = "tempo";
		serv[527] = "stx";
		serv[528] = "custix";
		serv[529] = "irc-serv";
		serv[529] = "irc-serv";
		serv[530] = "courier";
		serv[531] = "conference";
		serv[532] = "netnews";
		serv[533] = "netwall";
		serv[534] = "mm-admin";
		serv[535] = "iiop";
		serv[536] = "opalis-rdv";
		serv[537] = "nmsp";
		serv[538] = "gdomap";
		serv[539] = "apertus-ldp";
		serv[540] = "uucp";
		serv[541] = "uucp-rlogin";
		serv[542] = "commerce";
		serv[543] = "klogin";
		serv[544] = "kshell";
		serv[545] = "appleqtcsrvr";
		serv[546] = "dhcpv6-client";
		serv[547] = "dhcpv6-server";
		serv[548] = "afpovertcp";
		serv[549] = "idfp";
		serv[550] = "new-rwho";
		serv[551] = "cybercash";
		serv[552] = "deviceshare";
		serv[553] = "pirp";
		serv[554] = "rtsp";
		serv[555] = "dsf";
		serv[556] = "remotefs";
		serv[557] = "openvms-sysipc";
		serv[558] = "sdnskmp";
		serv[559] = "teedtap";
		serv[560] = "rmonitor";
		serv[561] = "monitor";
		serv[562] = "chshell";
		serv[563] = "nntps";
		serv[564] = "9pfs";
		serv[565] = "whoami";
		serv[566] = "streettalk";
		serv[567] = "banyan-rpc";
		serv[568] = "ms-shuttle";
		serv[569] = "ms-rome";
		serv[570] = "meter";
		serv[571] = "meter";
		serv[572] = "sonar";
		serv[573] = "banyan-vip";
		serv[574] = "ftp-agent";
		serv[575] = "vemmi";
		serv[576] = "ipcd";
		serv[577] = "vnas";
		serv[578] = "ipdd";
		serv[579] = "decbsrv";
		serv[580] = "sntp-heartbeat";
		serv[581] = "bdp";
		serv[582] = "scc-security";
		serv[583] = "philips-vc";
		serv[584] = "keyserver";
		serv[585] = "imap4-ssl";
		serv[586] = "password-chg";
		serv[587] = "submission";
		serv[600] = "ipcserver";
		serv[606] = "urm";
		serv[607] = "nqs";
		serv[608] = "sift-uft";
		serv[609] = "npmp-trap";
		serv[610] = "npmp-local";
		serv[611] = "npmp-gui";
		serv[612] = "hmmp-ind";
		serv[613] = "hmmp-op";
		serv[614] = "sshell";
		serv[615] = "sco-inetmgr";
		serv[616] = "sco-sysmgr";
		serv[617] = "sco-dtmgr";
		serv[618] = "dei-icda";
		serv[619] = "digital-evm";
		serv[620] = "sco-websrvrmgr";
		serv[621] = "escp-ip";
		serv[633] = "servstat";
		serv[634] = "ginad";
		serv[635] = "rlzdbase";
		serv[636] = "ldaps";
		serv[637] = "lanserver";
		serv[666] = "mdqs";
		serv[666] = "doom";
		serv[667] = "disclose";
		serv[668] = "mecomm";
		serv[669] = "meregister";
		serv[670] = "vacdsm-sws";
		serv[671] = "vacdsm-app";
		serv[672] = "vpps-qua";
		serv[673] = "cimplex";
		serv[674] = "acap";
		serv[704] = "elcsd";
		serv[705] = "agentx";
		serv[709] = "entrust-kmsh";
		serv[710] = "entrust-ash";
		serv[729] = "netviewdm1";
		serv[730] = "netviewdm2";
		serv[731] = "netviewdm3";
		serv[741] = "netgw";
		serv[742] = "netrcs";
		serv[744] = "flexlm";
		serv[747] = "fujitsu-dev";
		serv[748] = "ris-cm";
		serv[749] = "kerberos-adm";
		serv[750] = "rfile";
		serv[751] = "pump";
		serv[752] = "qrh";
		serv[753] = "rrh";
		serv[754] = "tell";
		serv[758] = "nlogin";
		serv[759] = "con";
		serv[760] = "ns";
		serv[761] = "rxe";
		serv[762] = "quotad";
		serv[763] = "cycleserv";
		serv[764] = "omserv";
		serv[765] = "webster";
		serv[767] = "phonebook";
		serv[769] = "vid";
		serv[770] = "cadlock";
		serv[771] = "rtip";
		serv[772] = "cycleserv2";
		serv[773] = "submit";
		serv[774] = "rpasswd";
		serv[775] = "entomb";
		serv[776] = "wpages";
		serv[780] = "wpgs";
		serv[786] = "concert";
		serv[800] = "mdbs_daemon";
		serv[801] = "device";
		serv[886] = "iclcnet-locate";
		serv[887] = "iclcnet_svinfo";
		serv[888] = "accessbuilder";
		serv[911] = "xact-backup";
		serv[911] = "xact-backup";
		serv[989] = "ftps-data";
		serv[990] = "ftps";
		serv[991] = "nas";
		serv[992] = "telnets";
		serv[993] = "imaps";
		serv[994] = "ircs";
		serv[995] = "pop3s";
		serv[996] = "vsinet";
		serv[997] = "maitrd";
		serv[998] = "busboy";
		serv[999] = "garcon";
		serv[999] = "puprouter";
		serv[1000] = "cadlock";
		serv[1023] = "unknown";

		Date now = new Date();
		long startTime = System.currentTimeMillis();
		outputPrint.redirectOutput(ScanFrame.scanArea);
		System.out.println("Starting NetWhistler portscan at " + now);

		FPinger pinger = new FPinger();

		if (pinger.Fping(addr)) {

			System.out.println(" Interesting ports on " + addr + " :");
			System.out.println(" PORT " + " STATE " + " SERVICE ");

			stop = false;
			for (int i = start; i < end; i++) {
				good = false;
				port = i;
				Thread th = new Thread(new Runnable() {
					public void run() {
						doScan();
					}
				});
				th.start();
				try {
					th.join(1000);
				} catch (Exception ex) {
				} // wait for socket thread to complete
				if (th.isAlive()) {
					// System.out.println("timeout on port="+i);
					th.interrupt(); // stop socket thread if still running
				} else {
					if (good) {
						System.out.println(" " + i + "/tcp  open   " + serv[i]);
						// else System.out.println("No service on port="+i);
					}
				}
			}

			//Date end = new Date();
			//long diff = System.currentTimeMillis() - startTime;
			System.out
					.println(" NetWhistler portscan completed -- 1 IP address (1 host up) scanned");
			// there may be some orphaned threads
		} else {
			System.out
					.println(" Note: Host seems down. If it is really up, but blocking our ping probes, try nmap");
			System.out
					.println(" NetWhistler scan completed -- 1 IP address (0 hosts up) scanned");

		}
	}

	static void doScan() {
		if (!stop) {

			try {
				socket = new Socket(addr, port);
				good = true;
				socket.close();
			} catch (Exception e) {

			}
		}
	}

}
