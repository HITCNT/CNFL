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
  private static final int k = 2;//����k=2
  public static int MAXDATASIZE = 1024; //�ɴ������ݵ�����ֽ���
  public static FileInputStream input= null; //���յ������ݴ����ļ���
  public List<byte[]> data_buffer = null; //�������ݻ�����
  public ByteBuffer buffer = null; //�������ݻ�����
  public DatagramChannel channel = null;
  private InetAddress addr = null;

  private TotalData totalData = null;
  private byte[] id;

  public Server(TotalData totalData) {

    this.totalData = totalData;
    id = totalData.getId();
    //��ʼ������IP�ڵ�
    int distance = distance(id,new byte[id.length]);
    InternetNode pubNode = new InternetNode(new byte[4],"128.16.1.1","80"); //���޸�
    List<InternetNode> nodeList = new ArrayList<InternetNode>();
    nodeList.add(pubNode);
    this.totalData.getK_bucket().put(distance, nodeList);

    try {
      addr = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    SocketAddress locationAddr = new InetSocketAddress(addr.getHostAddress(), 8080); //�������ĵ�ַ

    try {
      channel = DatagramChannel.open(); // ��ȡͨ��
      channel.configureBlocking(false);//���ò�����
      channel.bind(locationAddr);
    }catch (Exception e) {
      e.printStackTrace();
    }

    buffer = ByteBuffer.allocate(MAXDATASIZE); //���û�������С

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
   * ��ȡָ��
   * @param socketAddress
   * @return
   */
  private byte[] getReq(SocketAddress socketAddress) {
    byte[] information = null;

    if (socketAddress != null) {
      int position = buffer.position();

      if (position < 12) {
        System.out.println("ָ�����");
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
   * �ظ�Pingָ��
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
      System.out.println("send_mess����");
    }
  }

  /**
   * �ظ�Find_Nodeָ��
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
        System.out.println("send_mess����");
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
              System.out.println("send_mess����");
            }
          }
        }
      }

      /*
       * 
       * �����Ҳ�����Ӧ�ĵ㣬�Ƿ񷵻ض�Ӧ�㣬��ʱ�޸�
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
            System.out.println("send_mess����");
          }
        }


      }


    }
  }

  /**
   * ����node���ݰ�,�涨id 4byte��ip 15byte�� port 4byte
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
   * �ظ�Find_Value
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
        System.out.println("send_mess����");
      }

    }


    /*
     * 
     * �Ҳ�����Ӧֵ�Ľڵ㣬��ʱ����
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
          System.out.println("send_mess����");
        }
      }
    }
  }



  /**
   * �ظ�Storeָ��.
   * @param socketAddress
   * @param infor
   */
  private void reply4(SocketAddress socketAddress, byte[] infor) {
    /*
     * �涨FileData���ļ���Ϊ12byte����ϣֵΪ4byte,�ļ�����4��
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
      System.out.println("send_mess����");
    }


    TransferData t = new TransferData(TransferState.ShareDownload,file);
    this.totalData.getTransferList().add(t);
    loadFile load = new loadFile(PORT,socketAddress,filename,t);
    PORT++;
    //���������ļ��߳�

    load.run();

  }

  /**
   * �ظ�Loadָ��.
   * @param socketAddress
   * @param infor
   */
  private void reply5(SocketAddress socketAddress,byte[] infor) {
    FileData file = this.totalData.getFile().get(infor);
    TransferData trans = new TransferData(TransferState.ShareUpload,file);
    PORT++;
    //�����ϴ��ļ��߳�
    upFile up = new upFile(PORT,socketAddress,file.getFileName().getBytes(),trans);
    up.run();
  }

  /**
   * �ظ�Nodeָ��
   * @param socketAddress
   * @param infor
   */
  private void reply6(SocketAddress socketAddress, byte[] infor) {
    int length = infor.length + 12;
    int number = length/35; //����ڵ����.

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
      System.out.println("�ڵ�id����");
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
