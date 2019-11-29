/**
 * 
 */
package handleFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;

import dataStruct.TransferData;

/**
 * @author LC12138
 *
 */
public class upFile implements Runnable{

  private byte[] filename;
  private DatagramChannel channel;
  private SocketAddress socketaddress;
  private int port;
  private InetAddress addr = null;

  private TransferData trans;
  private int process = 0;

  public upFile(int port, SocketAddress socketAddress, byte[] filename, TransferData trans) {
    this.filename = filename;
    this.port = port;
    this.socketaddress = socketAddress;
    this.trans = trans;

    try {
      addr = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    SocketAddress locationAddr = new InetSocketAddress(addr.getHostAddress(), this.port); //服务器的地址

    try {
      this.channel = DatagramChannel.open();
      channel.bind(locationAddr);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    // TODO Auto-generated method stub

    /*
     * 先把文件读入缓冲区
     */
    List<byte[]> myBuffer = getMessage();
    for (int i = 0; i < myBuffer.size(); i++) {
      try {
        ByteBuffer buffer1 = ByteBuffer.allocate(1024);
        buffer1.put(myBuffer.get(i));
        buffer1.flip();
        this.channel.send(buffer1, socketaddress);

        byte[] ack = new byte[2];
        ByteBuffer buffer = ByteBuffer.allocate(2);
        this.channel.receive(buffer);
        buffer.flip();  //读取准备 
        buffer.get(ack,0,2);

        this.process += myBuffer.get(i).length;
        this.trans.setProgress(process);
      }catch (Exception e) {
        e.printStackTrace();
        System.out.println("send_mess出错");
      }


    }
  }

  private List<byte[]> getMessage() {
    String filepath = "src/" + new String(this.filename);

    File file = new File(filepath);


    InputStream in = null;
    byte[] bytes = null;

    try {
      in = new FileInputStream(file);
      bytes = new byte[in.available()];
      in.read(bytes);
      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    List<byte[]> result = new ArrayList<byte[]>();
    for (int i = 0;; i++) {
      if(bytes.length < 1024*(i + 1)) {
        int length = 1024*(i+1) - bytes.length;
        byte[] temp = new byte[length];
        System.arraycopy(bytes, 1024*i, temp, 0, length);
        result.add(temp);

        byte[] end = new byte[2];
        result.add(end);
        break;
      }

      byte[] temp = new byte[1024];
      System.arraycopy(bytes, 1024*i, temp, 0, 1024);
      result.add(temp);
    }
    return result;
  }

}
