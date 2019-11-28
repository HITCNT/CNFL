/**
 * 
 */
package server;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dataStruct.FileData;
import dataStruct.InternetNode;
import dataStruct.TotalData;
import dataStruct.TransferData;
import dataStruct.TransferState;
import handleFile.loadFile;
import handleFile.upFile;

/**
 * @author LC12138
 *
 */
public class Server implements Serverable{

  private static int PORT = 10000;
  private static final int k = 2;//设置k=2
  public static int MAXDATASIZE = 1024; //可传送数据的最大字节数
  public static FileInputStream input= null; //接收到的数据存入文件。
  public List<byte[]> data_buffer = null; //发送数据缓冲区
  public ByteBuffer buffer = null; //接收数据缓冲区
  public DatagramChannel channel = null;
  private InetAddress addr = null;

  private TotalData totalData = null;
  private byte[] id;

  public Server(TotalData totalData) {

    this.totalData = totalData;
    id = totalData.getId();
    //初始化公网IP节点
    int distance = distance(id,new byte[id.length]);
    InternetNode pubNode = new InternetNode(new byte[4],"128.16.1.1","80"); //需修改
    List<InternetNode> nodeList = new ArrayList<InternetNode>();
    nodeList.add(pubNode);
    this.totalData.getK_bucket().put(distance, nodeList);

    try {
      addr = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    SocketAddress locationAddr = new InetSocketAddress(addr.getHostAddress(), 8080); //服务器的地址

    try {
      channel = DatagramChannel.open(); // 获取通道
      channel.configureBlocking(false);//设置不阻塞
      channel.bind(locationAddr);
    }catch (Exception e) {
      e.printStackTrace();
    }

    buffer = ByteBuffer.allocate(MAXDATASIZE); //设置缓冲区大小

  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    // TODO Auto-generated method stub
    try {
      while (true) {
        buffer.clear();
        SocketAddress socketAddress = channel.receive(buffer);
        if(socketAddress == null) {
          Thread.sleep(500);
          continue;
        }

        byte[] req = getReq(socketAddress);
        byte[] command = new byte[12];
        byte[] infor = null;
        System.arraycopy(req, 0, command, 0, 12);
        System.arraycopy(req, 12, infor, 0, req.length - 12);

        String order = new String(command,"UTF-8");

        if (order.equals("Ping")) {
          reply1(socketAddress);
        } else if (order.equals("Find_Node")) {
          reply2(socketAddress,infor);
        } else if (order.equals("Find_value")) {
          reply3(socketAddress,infor);
        } else if (order.equals("Store")) {
          reply4(socketAddress,infor);
        } else if (order.equals("Load")) {
          reply5(socketAddress,infor);
        } else if (order.equals("Node")) {
          reply6(socketAddress,infor);
        }


      }
    }catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * 提取指令
   * @param socketAddress
   * @return
   */
  private byte[] getReq(SocketAddress socketAddress) {
    byte[] information = null;

    if (socketAddress != null) {
      int position = buffer.position();

      if (position < 12) {
        System.out.println("指令错误");
        System.exit(0);
      }
      information = new byte[position - 12];
      buffer.flip();
      for(int i=0; i<position; ++i) {

        information[i] = buffer.get();
      }
    }


    return information;
  }

  /**
   * 回复Ping指令
   * @param socketAddress
   */
  private void reply1(SocketAddress socketAddress) {

    byte[] com = new byte[12];
    System.arraycopy("Alive".getBytes(), 0, com, 0, "Alive".getBytes().length);
    try {
      ByteBuffer buffer1 = ByteBuffer.allocate(1024);
      buffer1.put(com);
      buffer1.flip();
      this.channel.send(buffer1, socketAddress);
    }catch (Exception e) {
      e.printStackTrace();
      System.out.println("send_mess出错");
    }
  }

  /**
   * 回复Find_Node指令
   * @param socketAddress
   * @return
   */
  private void reply2(SocketAddress socketAddress,byte[] infor) {
    int distance = distance(this.id,infor);
    distance = (int)(Math.log(distance)/Math.log(2)) + 1;

    if (Arrays.equals(infor, this.id)) {
      byte[] ip = addr.getHostAddress().getBytes();
      byte[] port = "8080".getBytes();

      byte[] message = setmessage(id, ip, port);

      try {
        ByteBuffer buffer1 = ByteBuffer.allocate(1024);
        buffer1.put(message);
        buffer1.flip();
        this.channel.send(buffer1, socketAddress);
      }catch (Exception e) {
        e.printStackTrace();
        System.out.println("send_mess出错");
      }
    }  else {
      List<InternetNode> bucket = this.totalData.getK_bucket().get(distance);

      if (bucket != null) {
        for (InternetNode node : bucket) {
          if (Arrays.equals(node.getId(), infor)) {
            byte[] message = setmessage(node.getId(), node.getIp().getBytes(), node.getPort().getBytes());
            try {
              ByteBuffer buffer1 = ByteBuffer.allocate(1024);
              buffer1.put(message);
              buffer1.flip();
              this.channel.send(buffer1, socketAddress);
            }catch (Exception e) {
              e.printStackTrace();
              System.out.println("send_mess出错");
            }
          }
        }
      }

      /*
       * 
       * 关于找不到对应的点，是否返回对应点，待时修改
       */
      int count = 0;
      byte[] temp = new byte[70];
      ByteBuffer buffer = ByteBuffer.wrap(temp);
      if (bucket == null) {
        int find = distance - 1;
        while (true) {
          List<InternetNode> nodes = this.totalData.getK_bucket().get(find);
          if (nodes == null) {
            find = find - 1;
            continue;
          }

          for (InternetNode node : nodes) {
            byte[] message = setmessage(node.getId(), node.getIp().getBytes(), node.getPort().getBytes());
            buffer.put(message);
            count++;

            if (count == k) {
              break;
            }
          }

          if (count == k) {
            break;
          }
        }

        byte[] message = buffer.array();
        if (message != null) {
          try {
            ByteBuffer buffer1 = ByteBuffer.allocate(1024);
            buffer1.put(message);
            buffer1.flip();
            this.channel.send(buffer1, socketAddress);
          }catch (Exception e) {
            e.printStackTrace();
            System.out.println("send_mess出错");
          }
        }


      }


    }
  }

  /**
   * 设置node数据包,规定id 4byte，ip 15byte， port 4byte
   * @param id
   * @param ip
   * @param port
   * @return
   */
  private byte[] setmessage(byte[] id ,byte[] ip, byte[] port) {
    byte[] com = new byte[12];
    byte[] id1 = new byte[4];
    byte[] ip1 = new byte[15];
    byte[] port1 = new byte[4];
    System.arraycopy("Node".getBytes(), 0, com, 0, "Node".getBytes().length);
    System.arraycopy(id, 0, id1, 0, id.length);
    System.arraycopy(ip, 0, ip1, 0, ip.length);
    System.arraycopy(port, 0, port1, 0, port.length);
    int messagelength = 35;
    byte[] temp = new byte[messagelength];

    ByteBuffer buffer = ByteBuffer.wrap(temp);
    buffer.put(com);
    buffer.put(id);
    buffer.put(ip);
    buffer.put(port);

    byte[] message = buffer.array();

    return message;
  }

  /**
   * 回复Find_Value
   * @param socketAddress
   * @param infor
   */
  private void reply3(SocketAddress socketAddress, byte[] infor) {
    FileData file = this.totalData.getFile().get(infor);

    if (file != null) {
      byte[] message = setmessage(id, addr.getHostAddress().getBytes(), "8080".getBytes());
      try {
        ByteBuffer buffer1 = ByteBuffer.allocate(1024);
        buffer1.put(message);
        buffer1.flip();
        this.channel.send(buffer1, socketAddress);
      }catch (Exception e) {
        e.printStackTrace();
        System.out.println("send_mess出错");
      }

    }


    /*
     * 
     * 找不到对应值的节点，待时处理
     */
    if (file == null) {
      int distance = distance(this.id,infor);
      int count = 0;

      byte[] temp = new byte[70];
      ByteBuffer buffer = ByteBuffer.wrap(temp);

      int find = (int)(Math.log(distance)/Math.log(2.0)) + 1;
      while (true) {
        List<InternetNode> nodes = this.totalData.getK_bucket().get(find);
        if (nodes == null) {
          find = find - 1;
          continue;
        }

        for (InternetNode node : nodes) {
          byte[] message = setmessage(node.getId(), node.getIp().getBytes(), node.getPort().getBytes());
          buffer.put(message);
          count++;

          if (count == k) {
            break;
          }
        }

        if (count == k) {
          break;
        }
      }

      byte[] message = buffer.array();
      if (message != null) {
        try {
          ByteBuffer buffer1 = ByteBuffer.allocate(1024);
          buffer1.put(message);
          buffer1.flip();
          this.channel.send(buffer1, socketAddress);
        }catch (Exception e) {
          e.printStackTrace();
          System.out.println("send_mess出错");
        }
      }
    }
  }



  /**
   * 回复Store指令.
   * @param socketAddress
   * @param infor
   */
  private void reply4(SocketAddress socketAddress, byte[] infor) {
    /*
     * 规定FileData中文件名为12byte，哈希值为4byte,文件长度4。
     */
    byte[] filename = new byte[12];
    System.arraycopy(infor, 0, filename, 0, 12);

    byte[] filehash = new byte[4];
    System.arraycopy(infor, 12, filehash, 0, 4);

    byte[] filelength = new byte[4];
    System.arraycopy(infor, 16, filelength, 0, 4);
    int length = 0;
    for (int i = 0; i < 4; i++) {
      int temp = 0xff&filelength[i] * 16^i;
      length += temp;
    }

    FileData file = new FileData(new String(filename),filehash,length);
    this.totalData.getFile().put(filehash, file);

    byte[] com = new byte[12];
    System.arraycopy("Load".getBytes(), 0, com, 0, "Load".getBytes().length);
    try {
      ByteBuffer buffer1 = ByteBuffer.allocate(1024);
      buffer1.put(com);
      buffer1.flip();
      this.channel.send(buffer1, socketAddress);
      Thread.sleep(500);
    }catch (Exception e) {
      e.printStackTrace();
      System.out.println("send_mess出错");
    }


    TransferData t = new TransferData(TransferState.ShareDownload,file);
    this.totalData.getTransferList().add(t);
    loadFile load = new loadFile(PORT,socketAddress,filename,t);
    PORT++;
    //运行下载文件线程

    load.run();

  }

  /**
   * 回复Load指令.
   * @param socketAddress
   * @param infor
   */
  private void reply5(SocketAddress socketAddress,byte[] infor) {
    FileData file = this.totalData.getFile().get(infor);
    TransferData trans = new TransferData(TransferState.ShareUpload,file);
    PORT++;
    //运行上传文件线程
    upFile up = new upFile(PORT,socketAddress,file.getFileName().getBytes(),trans);
    up.run();
  }

  /**
   * 回复Node指令
   * @param socketAddress
   * @param infor
   */
  private void reply6(SocketAddress socketAddress, byte[] infor) {
    int length = infor.length + 12;
    int number = length/35; //计算节点个数.

    for (int i = 0; i < number; i++) {
      int base = (35+12)*i;
      byte[] id = new byte[4];
      byte[] ip = new byte[15];
      byte[] port = new byte[4];

      System.arraycopy(infor, base, id, 0, 4);
      System.arraycopy(infor, base + 4 , ip, 0, 15);
      System.arraycopy(infor, base + 19, port, 0, 4);

      InternetNode node = new InternetNode(id,new String(ip),new String(port));

      int distance = distance(this.id,id);
      distance = (int)(Math.log(distance)/Math.log(2)) + 1;

      List<InternetNode> nodes = this.totalData.getK_bucket().get(distance);
      if (nodes == null) {
        nodes = new ArrayList<InternetNode>();
        nodes.add(node);

        this.totalData.getK_bucket().put(distance, nodes);
      } else {
        boolean flag = false;
        for (InternetNode n : nodes) {
          if (Arrays.equals(n.getId(), id)) {
            flag = true;
            break;
          }
        }

        if (!flag) {
          this.totalData.getK_bucket().get(distance).add(node);
        }
      }

    } 

  }



  private int distance(byte[] id1, byte[] id2) {
    if (id1 == null || id2 == null || id1.length != id2.length) {
      System.out.println("节点id错误");
      return -1;
    }

    int distance = 0;
    int length = id1.length;
    for (int i = 0; i < length; i++) {
      int temp = (id1[i]^id2[i])*16^i;

      distance += temp;
    }

    return distance;
  }

  public static Serverable getServer(TotalData totalData) {
    return new Server(totalData);
  }

}
