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
//      Mila NetWhistler        <netwhistler@gmail.com>
//      http://www.netwhistler.spb.ru/
package nnm.inet;

 import java.util.*;
 import nnm.NetworkManager;
 public class IPCalc
   {
       private int[] ipTokens;
       private String[] ipTokensBinary;
       private String ipString;
       private String ipBinaryString;

       private String subnetBinaryString;
       private String[] subnetTokensBinary;
       private int subnetNumber;
       private int[] subnetTokensInt;

       private String networkBinaryString;
       private String[] networkTokensBinary;
       private int[] networkTokensInt;

       private String broadcastBinaryString;
       private String[] broadcastTokensBinary;
       private int[] broadcastTokensInt;

       // HostMin
       private String hostMaxBinaryString;
       private String[] hostMaxTokensBinary;
       private int[] hostMaxTokensInt;

       // HostMax
       private String hostMinBinaryString;
       private String[] hostMinTokensBinary;
      private int[] hostMinTokensInt;

       // Network-Class
       private String networkClass;

       private String networkType;

       private int numberOfHosts;

       public IPCalc(String inputString)
       {
           StringTokenizer inputStringTokens = new StringTokenizer(inputString);

           this.ipString = inputStringTokens.nextToken("/");
           this.subnetNumber = Integer.parseInt(inputStringTokens.nextToken("/"));

           // IP 
           this.ipTokens = new int[4];
           this.ipTokensBinary = new String[4];
           this.ipString = ipString;
           this.ipBinaryString = "";

           // subnet
           this.subnetBinaryString = new String();
           this.subnetTokensBinary = new String[4];
          this.subnetTokensInt = new int[4];

          // network
          this.networkBinaryString = "";
          this.networkTokensBinary = new String[4];
          this.networkTokensInt = new int[4];

          // broadcast
          this.broadcastBinaryString = "";
          this.broadcastTokensBinary = new String[4];
          this.broadcastTokensInt = new int[4];

          // hostMin
          this.hostMinBinaryString = "";
          this.hostMinTokensBinary = new String[4];
          this.hostMinTokensInt = new int[4];

          // hostMax
          this.hostMaxBinaryString = "";
          this.hostMaxTokensBinary = new String[4];
          this.hostMaxTokensInt = new int[4];

          this.networkClass = "Unknown";
          this.networkType = "Unknown";
          this.numberOfHosts = 0;

          this.evaluateString();
      }

      private void setIPTokens()
      {
          int counter = 0;
          StringTokenizer ipTok = new StringTokenizer(this.ipString);

          while(ipTok.hasMoreElements())
          {
              ipTokens[counter] = Integer.parseInt(ipTok.nextToken("."));
              counter++;
          }
      }
      private void setIPBinaryTokens()
      {
         for(int i = 0; i < 4; i++)
         {
              ipTokensBinary[i] = Integer.toBinaryString(ipTokens[i]);
         }
      }

      private void setIPBinaryString()
      {
          for(int i = 0; i < 4; i++)
          {
              ipBinaryString += ipTokensBinary[i];
          }
      }
       private void setSubnetBinaryString()
      {
          for(int i = 0; i < 32; i++)
          {
              if(i < subnetNumber) subnetBinaryString += "1";

              else subnetBinaryString += "0";

          }
      }

      private void setSubnetBinaryTokens()
      {
          subnetTokensBinary[0] = this.subnetBinaryString.substring(0, 8);
          subnetTokensBinary[1] = this.subnetBinaryString.substring(8, 16);
          subnetTokensBinary[2] = this.subnetBinaryString.substring(16, 24);
          subnetTokensBinary[3] = this.subnetBinaryString.substring(24, 32);
      }

      private void setSubnetTokens()
      {
          for(int i = 0; i < 4; i++)
          {
              subnetTokensInt[i] = Integer.parseInt(subnetTokensBinary[i], 2);
          }
      }
    
      private void setNetworkBinaryString()
      {
          for(int i = 0; i < 32; i++)
          {
              if(i < subnetNumber)
                  networkBinaryString += ipBinaryString.charAt(i);

               else networkBinaryString += "0";
          }
      }

      private void setNetworkTokensBinary()
      {
          networkTokensBinary[0] = this.networkBinaryString.substring(0, 8);
          networkTokensBinary[1] = this.networkBinaryString.substring(8, 16);
          networkTokensBinary[2] = this.networkBinaryString.substring(16, 24);
          networkTokensBinary[3] = this.networkBinaryString.substring(24, 32);
      }

      private void setNetworkTokens()
      {
          for(int i = 0; i < 4; i++)
          {
              networkTokensInt[i] = Integer.parseInt(networkTokensBinary[i], 2);
          }
      }

      private void setBroadcastBinaryString()
      {
          for(int i = 0; i < 32; i++)
          {
              if(i < subnetNumber)
                  broadcastBinaryString += ipBinaryString.charAt(i);

               else broadcastBinaryString += "1";
          }
      }

      private void setBroadcastTokensBinary()
      {
          broadcastTokensBinary[0] = this.broadcastBinaryString.substring(0, 8);
          broadcastTokensBinary[1] = this.broadcastBinaryString.substring(8, 16);
          broadcastTokensBinary[2] = this.broadcastBinaryString.substring(16, 24);
          broadcastTokensBinary[3] = this.broadcastBinaryString.substring(24, 32);
      }

      private void setBroadcastTokens()
      {
          for(int i = 0; i < 4; i++)
          {
              broadcastTokensInt[i] = Integer.parseInt(broadcastTokensBinary[i], 2);
          }
      }


      private void setHostMinBinaryString()
      {
          for(int i = 0; i < 31; i++)
          {
              if(i < subnetNumber)
                  hostMinBinaryString += ipBinaryString.charAt(i);

               else hostMinBinaryString += "0";
          }

          hostMinBinaryString += "1";
      }

      private void setHostMinTokensBinary()
      {
          hostMinTokensBinary[0] = this.hostMinBinaryString.substring(0, 8);
          hostMinTokensBinary[1] = this.hostMinBinaryString.substring(8, 16);
          hostMinTokensBinary[2] = this.hostMinBinaryString.substring(16, 24);
          hostMinTokensBinary[3] = this.hostMinBinaryString.substring(24, 32);
      }

      private void setHostMinTokens()
      {
          for(int i = 0; i < 4; i++)
          {
              hostMinTokensInt[i] = Integer.parseInt(hostMinTokensBinary[i], 2);
          }
      }

      private void setHostMaxBinaryString()
      {
          for(int i = 0; i < 31; i++)
          {
              if(i < subnetNumber)
                  hostMaxBinaryString += ipBinaryString.charAt(i);

               else hostMaxBinaryString += "1";
          }

          hostMaxBinaryString += "0";
      }

      private void setHostMaxTokensBinary()
      {
          hostMaxTokensBinary[0] = this.hostMaxBinaryString.substring(0, 8);
          hostMaxTokensBinary[1] = this.hostMaxBinaryString.substring(8, 16);
          hostMaxTokensBinary[2] = this.hostMaxBinaryString.substring(16, 24);
          hostMaxTokensBinary[3] = this.hostMaxBinaryString.substring(24, 32);
      }

      private void setHostMaxTokens()
      {
          for(int i = 0; i < 4; i++)
          {
              hostMaxTokensInt[i] = Integer.parseInt(hostMaxTokensBinary[i], 2);
          }
      }

      private void setNetworkClass()
      {
         // System.out.println("networkclass: " + ipTokensBinary[0].substring(0,2));

          if(ipTokensBinary[0].substring(0,1).equals("0"))
                  networkClass = "(Class A)";
          else if(ipTokensBinary[0].substring(0,2).equals("10"))
                  networkClass = "(Class B)";
          else if(ipTokensBinary[0].substring(0,3).equals("110"))
                  networkClass = "(Class C)";

          else if(ipTokensBinary[0].substring(0,4).equals("1110"))
                  networkClass = "(Class D)";

          else if(ipTokensBinary[0].substring(0,5).equals("11110"))
                  networkClass = "(Class E)";

          else networkClass = "(Unknown)";

      }

      private void setNetworkType()
      {
          if(ipTokens[0] == 10) this.networkType = "private";

          else if((ipTokens[0] == 169) && (ipTokens[1] == 254))
                                                    this.networkType = "private";

          else if((ipTokens[0] == 172) && (ipTokens[1] >= 16) && (ipTokens[1] <= 31))
                                                    this.networkType = "private";

          else if((ipTokens[0] == 192) && (ipTokens[1] == 168))
                                                    this.networkType = "private";

          else this.networkType = "public";
      }

      private void setHosts()
      {
          int hosts = 32 - this.subnetNumber;
          this.numberOfHosts = (int)Math.pow(2, hosts) -2;
      }

      private void printIPTokens()
      {
          System.out.print(this.ipTokens[0] + "." + this.ipTokens[1] + "."
                          + this.ipTokens[2] + "." + this.ipTokens[3]);
      }

      private void printIPBinaryString()
      {
           System.out.print(this.ipTokensBinary[0] + "." + this.ipTokensBinary[1] + "."
                          + this.ipTokensBinary[2] + "." + this.ipTokensBinary[3]);
      }

      private void printSubnetMask()
      {
          System.out.print(this.subnetTokensInt[0] + "." + this.subnetTokensInt[1] + "."
                          + this.subnetTokensInt[2] + "." + this.subnetTokensInt[3]);
      }

      private void printSubnetBinaryString()
      {
           System.out.print(this.subnetTokensBinary[0] + "." + this.subnetTokensBinary[1] + "."
                          + this.subnetTokensBinary[2] + "." + this.subnetTokensBinary[3]);
      }

      private int getSubnetNumber()
      {
          return this.subnetNumber;
      }

    
      private String getNetworkString()
      {
           return(this.networkTokensInt[0] + "." + this.networkTokensInt[1] + "."
                          + this.networkTokensInt[2] + "." + this.networkTokensInt[3] + "/" + this.subnetNumber);
      }

      private void printNetworkBinaryString()
      {
          System.out.print(this.networkTokensBinary[0] + "." + this.networkTokensBinary[1] + "."
                          + this.networkTokensBinary[2] + "." + this.networkTokensBinary[3]);
          System.out.print(" " + this.networkClass);
      }

    
      private void printBroadcastString()
      {
           System.out.print(this.broadcastTokensInt[0] + "." + this.broadcastTokensInt[1] + "."
                          + this.broadcastTokensInt[2] + "." + this.broadcastTokensInt[3]);
      }

      private void printBroadcastBinaryString()
      {
         System.out.print(this.broadcastTokensBinary[0] + "." + this.broadcastTokensBinary[1] + "."
                          + this.broadcastTokensBinary[2] + "." + this.broadcastTokensBinary[3]);

      }

    
      private String getHostMinString()
      {
           return(this.hostMinTokensInt[0] + "." + this.hostMinTokensInt[1] + "."
                          + this.hostMinTokensInt[2] + "." + this.hostMinTokensInt[3]);
      }

      private void printHostMinBinaryString()
      {
           System.out.print(this.hostMinTokensBinary[0] + "." + this.hostMinTokensBinary[1] + "."
                          + this.hostMinTokensBinary[2] + "." + this.hostMinTokensBinary[3]);

      }

   
      private void printHostMaxString()
      {
           System.out.print(this.hostMaxTokensInt[0] + "." + this.hostMaxTokensInt[1] + "."
                          + this.hostMaxTokensInt[2] + "." + this.hostMaxTokensInt[3]);
      }

      private void printHostMaxBinaryString()
      {
           System.out.print(this.hostMaxTokensBinary[0] + "." + this.hostMaxTokensBinary[1] + "."
                          + this.hostMaxTokensBinary[2] + "." + this.hostMaxTokensBinary[3]);
      }


      public static String[] prependZeros(String[] array)
      {
          for(int i = 0; i < array.length; i++)
          {
              while(array[i].length() < 8)
              {
                  array[i] = "0" + array[i];
              }
          }
          return array;
      }
    
      private void evaluateString()
      {
          
          this.setIPTokens();
          this.setIPBinaryTokens();
          this.ipTokensBinary = IPCalc.prependZeros(this.ipTokensBinary);
          this.setIPBinaryString();

        
          this.setSubnetBinaryString();
          this.setSubnetBinaryTokens();
          this.setSubnetTokens();

          // Network
          this.setNetworkBinaryString();
          this.setNetworkTokensBinary();
          this.setNetworkTokens();

          // Broadcast
          this.setBroadcastBinaryString();
          this.setBroadcastTokensBinary();
          this.setBroadcastTokens();

          // HostMin
          this.setHostMinBinaryString();
          this.setHostMinTokensBinary();
          this.setHostMinTokens();

          // HostMin
          this.setHostMaxBinaryString();
          this.setHostMaxTokensBinary();
          this.setHostMaxTokens();

          this.setNetworkClass();
          this.setNetworkType();
          this.setHosts();
     }


      public String getHostMin()
      {
             return this.getHostMinString();
           
      }
      public  boolean isThisNetwork()
      {
    	  boolean ok = true;
    	  if (!this.getNetworkString().equals(ipString+"/"+subnetNumber))
    		  ok = false;
    	  return ok;	 
      }

      public  boolean checkNetwork()
      {
    	  boolean ok = false;
    	  if (this.getNetworkString().equals(NetworkManager.currentNetwork))
    		  ok = true;
    	  return ok;	  
      }

  }
