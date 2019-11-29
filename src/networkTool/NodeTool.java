package networkTool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import dataStruct.IPPort;
import dataStruct.TotalData;

/**
 * 节点工具
 * @author HIT_1170300119
 *
 */
public class NodeTool {

  /**
   * Ping
   * @param targetIP
   * @param port
   * @return
   */
  public static boolean ping(IPPort ip) {
    try {
      Socket s = new Socket(InetAddress.getByName(ip.getIp()),ip.getPort());
      OutputStream os;
      os = s.getOutputStream();
      String str = "Ping";
      os.write(str.getBytes());
      InputStream is = s.getInputStream();
      byte[] bys = new byte[1024];
      int len;
      len = is.read(bys);
      if(len==-1) {
        s.close();
        return false;
      }
      InetAddress address = s.getInetAddress();
      System.out.println("sender:"+address);
      String result=new String(bys,0,len);
      s.close();
      if(result.equals("Ping Success")) {
        return true;
      }else {
        return false;
      }
    } catch (IOException e) {
      return false;
    }
  }
  
  /**
   * 获取目标哈希值的下一个有效IP
   * @param nodeHash
   * @param startIP
   * @param host
   * @return
   */
  public static IPPort getNextIP(byte[] nodeHash,TotalData totalData) {
    try {
      Socket s = new Socket(InetAddress.getByName(totalData.getNextIP().getIp()),totalData.getNextIP().getPort());
      OutputStream os;
      os = s.getOutputStream();
      int isBig=1;
      int compare=ByteTool.bytesCompare(totalData.getId(), nodeHash);
      if(compare==0) {
        s.close();
        return new IPPort("0.0.0.0", 0);
      }else if(compare<0) {
        isBig=0;
      }
      String str = "GETNEXTIP "+isBig +" "+ByteTool.conver16HexStr(nodeHash);
      os.write(str.getBytes());
      InputStream is = s.getInputStream();
      byte[] bys = new byte[1024];
      int len;
      len = is.read(bys);
      if(len==-1) {
        s.close();
        return null;
      }
      InetAddress address = s.getInetAddress();
      System.out.println("sender:"+address);
      String result=new String(bys,0,len);
      s.close();
      if(result.indexOf("IP")==0) {
        String[] parts=result.split(" ");
        String ip=parts[1];
        int port=Integer.parseInt(parts[3]);
        return new IPPort(ip, port);
      }else if(result.equals("YES")) {
        return new IPPort(totalData.getNextIP().getIp(), totalData.getNextIP().getPort());
      }
      else {
        return null;
      }
    } catch (IOException e) {
      return null;
    }
  }
}
