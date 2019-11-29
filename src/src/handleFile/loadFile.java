/**
 * 
 */
package handleFile;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;

import dataStruct.TransferData;

/**
 * @author LC12138
 *
 */
public class loadFile implements Runnable{
  private DatagramChannel channel;
  private SocketAddress socketaddress;
  private byte[] filename;
  private int port;
  private InetAddress addr = null;

  public final int WINDOWSSIZE = 5; //滑动窗口尺寸
  public static int MAXDATASIZE = 1024; //可传送数据的最大字节数

  private TransferData trans;
  private int process = 0;


  public loadFile(int port,SocketAddress socketaddress,byte[] filename,TransferData trans) {
    this.port = port;
    this.socketaddress = socketaddress;
    this.filename = filename;
    this.trans = trans;

    try {
      addr = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    SocketAddress locationAddr = new InetSocketAddress(addr.getHostAddress(), this.port); //服务器的地址

    try {
      channel = DatagramChannel.open(); // 获取通道
      channel.bind(locationAddr);
    }catch (Exception e) {
      e.printStackTrace();
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    // TODO Auto-generated method stub
    try {
      while (true) {

        byte[] message = null;
        ByteBuffer buffer = ByteBuffer.allocate(MAXDATASIZE);
        buffer.clear();
        try {
          this.channel.receive(buffer);

          message = new byte[buffer.position()];
          buffer.flip();  //读取准备 
          buffer.get(message,0,buffer.position());
        }catch (Exception e) {
          e.printStackTrace();
        }

        writeFile(message);

        sendAck();

        if (message.length == 2) {
          break;
        }

        process += message.length;
        this.trans.setProgress(process);

      }
    }catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void writeFile(byte[] message) {
    String filename = new String(this.filename);
    String filepath = "src/" + filename;
    File file = new File(filepath);
    try {
      if (!file.exists()) {
        file.createNewFile();
      }

      ByteBuffer bb = ByteBuffer.wrap(message);
      @SuppressWarnings("resource")
      FileChannel fc = new FileOutputStream(filepath).getChannel();
      fc.write(bb);

      fc.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void sendAck() {
    byte[] ack = new byte[2];
    ack[1] = 0xff&1;
    try {
      ByteBuffer buffer1 = ByteBuffer.allocate(2);
      buffer1.put(ack);
      buffer1.flip();
      this.channel.send(buffer1, socketaddress);
      Thread.sleep(500);
    }catch(Exception e) {
      e.printStackTrace();
    }
  }

}
