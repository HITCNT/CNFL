/**
 * 
 */
package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    InternetNode pubNode = new InternetNode(new byte[4],"49.232.146.5","1234");
    List<InternetNode> nodeList = new ArrayList<InternetNode>();
    nodeList.add(pubNode);
    this.totalData.getK_bucket().put(distance, nodeList);

    try {
      addr = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    SocketAddress locationAddr = new InetSocketAddress(1234); //服务器的地址

    try {
      channel = DatagramChannel.open(); // 获取通道
      channel.configureBlocking(false);//设置不阻塞
      channel.socket().bind(locationAddr);
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
      this.upData();
      while (true) {
        buffer.clear();
        SocketAddress socketAddress = channel.receive(buffer);
        if(socketAddress == null) {
          Thread.sleep(500);
          continue;
        }

        byte[] req = getReq(socketAddress);
        byte[] command = new byte[12];
        byte[] infor = new byte[req.length - 12];
        System.arraycopy(req, 0, command, 0, 12);
        System.arraycopy(req, 12, infor, 0, req.length - 12);

        String order = new String(command,"UTF-8");

        if (order.equals("Ping")) {
          reply1(socketAddress,infor);
        } else if (order.equals("Find_Node")) {
          reply2(socketAddress,infor);
        } else if (order.equals("Find_Value")) {
          reply3(socketAddress,infor);
        } else if (order.equals("Store")) {
          reply4(socketAddress,infor);
        } else if (order.equals("Load")) {
          reply5(socketAddress,infor);
        } else if (order.equals("Node")) {
          reply6(infor);
        }

        new TimeTask(this);
      }
    }catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * 更新K_bucket
   */
  public  void upData() {
    int k = 5;
    Set<Integer> distance = this.totalData.getK_bucket().keySet();

    if(addr.getHostAddress().equals("49.232.146.5")) {
      if(distance == null) {
        distance = new HashSet<Integer>();
      }
    }

    if (distance == null) {
      for (int i = 1; i <= k; i++) {
        updata1(i);
      }
    } else {
      int count = 1;
      while (count <= 5) {
        if (distance.contains(count)) {
          List<InternetNode> nodes = this.totalData.getK_bucket().get(count);
          InternetNode node = nodes.get(0);
          byte[] testid = new byte[4];
          System.arraycopy(this.id, 0, testid, 0, 4);

          byte[] cmd = new byte[12];
          System.arraycopy("Find_Node".getBytes(), 0, cmd, 0, "Find_Node".getBytes().length);

          byte[] temp = new byte[16];
          ByteBuffer buffer = ByteBuffer.wrap(temp);

          buffer.put(cmd);
          buffer.put(testid);

          byte[] message = buffer.array();
          SocketAddress socketaddress = new InetSocketAddress(node.getIp(), 1234);
          try {
            ByteBuffer buffer1 = ByteBuffer.allocate(MAXDATASIZE);
            buffer1.put(message);
            buffer1.flip();
            this.channel.send(buffer1, socketaddress);
          }catch (Exception e) {
            e.printStackTrace();
            System.out.println("send_mess出错");
          }

          ByteBuffer buffer2 = ByteBuffer.allocate(MAXDATASIZE);
          try {
            this.channel.configureBlocking(true);//设置阻塞
            SocketAddress socketAddress = channel.receive(buffer2);
            byte[] req = getReq(socketAddress);

            reply6(req);


          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        } else {
          if (!addr.getHostAddress().equals("49.232.146.5")) {
            updata1(count);
          }

        }

        count++;
      }
    }
  }


  /**
   * 
   * @param distance
   */
  private void updata1(int distance) {
    byte[] testid = new byte[4];
    for (int j = 0; j < 4; j++) {
      if (j == 0) {
        testid[j] = (byte)(this.id[j]^(2*(distance - 1) + 1));
      } else {
        testid[j] = this.id[j];
      }

    }

    byte[] cmd = new byte[12];
    System.arraycopy("Find_Node".getBytes(), 0, cmd, 0, "Find_Node".getBytes().length);

    byte[] temp = new byte[16];
    ByteBuffer buffer = ByteBuffer.wrap(temp);

    buffer.put(cmd);
    buffer.put(testid);

    byte[] message = buffer.array();
    SocketAddress socketaddress = new InetSocketAddress("49.232.146.5", 80);
    try {
      ByteBuffer buffer1 = ByteBuffer.allocate(1024);
      buffer1.put(message);
      buffer1.flip();
      this.channel.send(buffer1, socketaddress);
    }catch (Exception e) {
      e.printStackTrace();
      System.out.println("send_mess出错");
      System.out.println(addr.getHostAddress());
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
      information = new byte[position];
      buffer.flip();
      for(int i=0; i<position; i++) {

        information[i] = buffer.get();
      }
    }


    return information;
  }

  /**
   * 回复Ping指令
   * @param socketAddress
   */
  private void reply1(SocketAddress socketAddress,byte[] infor) {
    byte[] id = new byte[4];
    byte[] ip = new byte[15];
    byte[] port = new byte[4];
    System.arraycopy(infor, 0, id, 0, 4);
    System.arraycopy(ip, 4, ip, 0, 15);
    System.arraycopy(id, 19, port, 0, 4);
    InternetNode node1 = new InternetNode(id,new String(ip),new String(port));
    if (addr.getHostAddress().equals("49.232.146.5")) {
      int dis = distance(this.id,id);
      List<InternetNode> nodes = this.totalData.getK_bucket().get(dis);

      if (nodes == null) {
        nodes = new ArrayList<InternetNode>();
        nodes.add(node1);

        synchronized(this) {
          this.totalData.getK_bucket().put(dis, nodes);
        }
      } else {
        this.totalData.getK_bucket().put(dis, nodes);
      }
    } 
    byte[] com = new byte[12];
    System.arraycopy("Alive".getBytes(), 0, com, 0, "Alive".getBytes().length);
    byte[] node = setNode(this.id,addr.getHostAddress().getBytes(),"1234".getBytes());

    byte[] temp = new byte[32];
    ByteBuffer buffer = ByteBuffer.wrap(temp);
    buffer.put(com);
    buffer.put(node);

    byte[] message = buffer.array();

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

      byte[] message = setNode(id, ip, port);

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
            byte[] message = setNode(node.getId(), node.getIp().getBytes(), node.getPort().getBytes());
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
          if (find < 0) {
            break;
          }
          List<InternetNode> nodes = this.totalData.getK_bucket().get(find);
          if (nodes == null) {
            find = find - 1;
            continue;
          }

          for (InternetNode node : nodes) {
            byte[] message = setNode(node.getId(), node.getIp().getBytes(), node.getPort().getBytes());
            buffer.put(message);
            count++;

            if (count == k) {
              break;
            }
          }

          if (count == k) {
            break;
          }
          find = find - 1;
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
  private byte[] setNode(byte[] id ,byte[] ip, byte[] port) {
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
   * 设置FileData数据包.Filehash4字节
   * @param Filename
   * @param Filehash
   * @param Filelength
   * @return
   */
  private byte[] setFileData(String Filename,byte[] Filehash, int Filelength) {
    byte[] filename = new byte[12];
    byte[] filehash = new byte[4];
    byte[] filelength = new byte[4];

    System.arraycopy(Filename.getBytes(), 0, filename, 0, Filename.getBytes().length);
    System.arraycopy(Filehash, 0, filehash, 0, Filehash.length);
    System.arraycopy(intToBytearray(Filelength), 0 , filelength, 0, 4);

    byte[] temp = new byte[20];
    ByteBuffer buffer = ByteBuffer.wrap(temp);
    buffer.put(filename);
    buffer.put(filehash);
    buffer.put(filelength);

    byte[] message = buffer.array();

    return message;
  }

  /**
   * 回复Find_Value
   * @param socketAddress
   * @param infor
   */
  private void reply3(SocketAddress socketAddress, byte[] infor) {
    String filename = new String(infor);

    int sum = 0;
    for (int i = 0; i < filename.length(); i++) {
      int temp = filename.charAt(i);
      sum += temp;
    }

    byte[] filehash = intToBytearray(sum);

    FileData file = this.totalData.getFile().get(filehash);
    byte[] node = setNode(id, addr.getHostAddress().getBytes(), "8080".getBytes());

    if (file != null) {

      byte[] filedata = setFileData(file.getFileName(),file.getFileHash(),file.getFileLength());


      byte[] temp = new byte[55];
      ByteBuffer buffer = ByteBuffer.wrap(temp);
      buffer.put(filedata);
      buffer.put(node);

      byte[] message = buffer.array();


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
      String sfilename = new String(filename);
      Set<byte[]> files = this.totalData.getFile().keySet();
      if (files != null) {
        for (byte[] f : files) {
          String name = new String(f);
          if (name.contains(sfilename)) {
            FileData f1 = this.totalData.getFile().get(f);
            byte[] filedata = setFileData(f1.getFileName(),f1.getFileHash(),f1.getFileLength());

            byte[] temp = new byte[55];
            ByteBuffer buffer = ByteBuffer.wrap(temp);
            buffer.put(node);
            buffer.put(filedata);

            byte[] message = buffer.array();


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

      int distance = distance(this.id,filehash);
      int count = 0;

      byte[] temp = new byte[70];
      ByteBuffer buffer = ByteBuffer.wrap(temp);

      int find = (int)(Math.log(distance)/Math.log(2.0)) + 1;
      while (true) {
        if (find < 0) {
          break;
        }
        List<InternetNode> nodes = this.totalData.getK_bucket().get(find);
        if (nodes == null) {
          find = find - 1;
          continue;
        }

        for (InternetNode n : nodes) {
          byte[] message = setNode(n.getId(), n.getIp().getBytes(), n.getPort().getBytes());
          buffer.put(message);
          count++;

          if (count == k) {
            break;
          }
        }

        if (count == k) {
          break;
        }

        find = find - 1;
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
   * 整型转Byte数组
   * @param n
   * @return
   */
  private byte[] intToBytearray(int n) {
    byte[] result = new byte[4];

    result[3] = (byte)(n >> 24);
    result[2] = (byte)(n >> 16);
    result[1] = (byte)(n >> 8);
    result[0] = (byte)n;

    return result;
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

    synchronized(this) {
      this.totalData.getFile().put(filehash, file);
    }

    byte[] com = new byte[12];
    System.arraycopy("Load".getBytes(), 0, com, 0, "Load".getBytes().length);
    try {
      ByteBuffer buffer1 = ByteBuffer.allocate(1024);
      buffer1.put(com);
      buffer1.flip();
      this.channel.send(buffer1, socketAddress);
    }catch (Exception e) {
      e.printStackTrace();
      System.out.println("send_mess出错");
    }


    TransferData t = new TransferData(TransferState.ShareDownload,file);
    synchronized(this) {
      this.totalData.getTransferList().add(t);
    }
    loadFile load = new loadFile(1234,socketAddress,filename,t);
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
  private void reply6(byte[] infor) {
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

        synchronized(this) {
          this.totalData.getK_bucket().put(distance, nodes);
        }
      } else {
        boolean flag = false;
        for (InternetNode n : nodes) {
          if (Arrays.equals(n.getId(), id)) {
            flag = true;
            break;
          }
        }

        if (!flag) {
          synchronized(this) {
            this.totalData.getK_bucket().get(distance).add(node);
          }
        }
      }

    } 

  }



  /**
   * 计算距离.
   * @param id1
   * @param id2
   * @return
   */
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
