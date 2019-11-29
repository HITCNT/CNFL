package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import dataStruct.IPPort;
import dataStruct.TotalData;
import networkTool.ByteTool;
import networkTool.NodeTool;

public class ServerHandler implements Runnable {

  private Socket s;
  private TotalData totalData;
  
  public ServerHandler(Socket s,TotalData totalData) {
    this.s=s;
    this.totalData=totalData;
  }
  
  @Override
  public void run() {
    try {
      InputStream is;
      is = s.getInputStream();
      byte[] bys = new byte[1024];
      int len;
      len = is.read(bys);
      InetAddress address = s.getInetAddress();
      System.out.println("sender:" + address);
      String infromation=new String(bys, 0, len);
      if(infromation.equals("Ping")) {
        pingHandel(s);
      }else if(infromation.indexOf("GETNEXTIP")==0) {
        getNextIPHandel(s, totalData, infromation);
      }
      s.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private void pingHandel(Socket s) {
    try {
      OutputStream os;
      os = s.getOutputStream();
      String str = "Ping Success";
      os.write(str.getBytes());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private void getNextIPHandel(Socket s,TotalData totalData,String information) {
    String[] parts=information.split(" ");
    int wasBig=Integer.parseInt(parts[1]);
    String hashX=parts[2];
    byte[] hash=ByteTool.conver16HexToByte(hashX);
    int compareResult=ByteTool.bytesCompare(totalData.getId(), hash);
    if(compareResult==0||(compareResult>0&&wasBig==0)) {
      try {
        OutputStream os;
        os = s.getOutputStream();
        String str = "Yes";
        os.write(str.getBytes());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }else {
      IPPort ip=NodeTool.getNextIP(hash, totalData);
      try {
        OutputStream os;
        os = s.getOutputStream();
        String str = "IP "+ip.getIp()+" port "+ip.getPort();
        os.write(str.getBytes());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
    
    
  }

}
