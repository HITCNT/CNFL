package client;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dataStruct.FileData;
import dataStruct.InternetNode;
import dataStruct.TotalData;
import dataStruct.TransferData;
import dataStruct.TransferState;
import handleFile.loadFile;
import handleFile.upFile;

public class Client implements Clientable{

  private TotalData totaldata;
  public Client(TotalData totaldata) {
    this.totaldata = totaldata;
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

  private byte[] intToBytearray(int n) {
    byte[] result = new byte[4];

    result[3] = (byte)(n >> 24);
    result[2] = (byte)(n >> 16);
    result[1] = (byte)(n >> 8);
    result[0] = (byte)n;

    return result;
  }


  @Override
  public List<FileData> searchFile(String keyWords) {
    List<FileData> filelist = new ArrayList<FileData>();

    byte[] filename = keyWords.getBytes();
    Map<Integer,List<InternetNode>> K_bucket = totaldata.getK_bucket();
    int distance = distance(filename,totaldata.getId());
    distance = (int)(Math.log(distance)/Math.log(2))+1;
    while(true){
      List<InternetNode> internetnode = K_bucket.get(distance);
      if(internetnode == null) {
        distance--;
        continue;
      }
      if(distance == 0) {
        break;
      }
      for(int i=0;i<internetnode.size()-1;i++) {
        DatagramChannel channel = null;
        InetAddress addr = null;
        try {
          addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e2) {
          e2.printStackTrace();
        }
        SocketAddress socketAddress = new InetSocketAddress(addr.getHostAddress(), 90);
        try {
          channel = DatagramChannel.open();
          channel.configureBlocking(false);//设置不阻塞
          channel.bind(socketAddress);
        } catch (IOException e1) {
          e1.printStackTrace();
        }

        byte[] com = new byte[12];
        byte[] id1 = new byte[4];
        System.arraycopy("Find_Value".getBytes(), 0, com, 0, "Find_Value".getBytes().length);
        System.arraycopy(filename, 0, id1, 0, filename.length);
        int messagelength = 16;
        byte[] temp = new byte[messagelength];

        ByteBuffer buffer = ByteBuffer.wrap(temp);
        buffer.put(com);
        buffer.put(id1);
        byte[] message = buffer.array();
        try {
          ByteBuffer buffer1 = ByteBuffer.allocate(1024);
          buffer1.put(message);
          buffer1.flip();
          channel.send(buffer1, socketAddress);
        }catch (Exception e) {
          e.printStackTrace();
          System.out.println("send_mess出错");
        }

        byte[] message1 = null;
        ByteBuffer buffer2 = ByteBuffer.allocate(1024);
        buffer2.clear();
        try {
          channel.receive(buffer);
          message1 = new byte[buffer.position()];
          buffer.flip();  //读取准备 
          buffer.get(message1,0,buffer.position());
        }catch (Exception e) {
          e.printStackTrace();
        }

        byte[] b1 = new byte[12];
        System.arraycopy(message1, 0, b1, 0, 12);
        byte[] b2 = new byte[4];
        System.arraycopy(message1, 12, b2, 0, 4);
        byte[] b3 = new byte[4];
        System.arraycopy(message1, 16, b3, 0, 4);
        byte[] b4 = new byte[23];
        System.arraycopy(message1, 20, b4, 0, 23);
        totaldata.getFileInNode().put(b4, internetnode);
        FileData filedata = new FileData(b1.toString(), b2, (b3[3] & 0xFF | (b3[2] & 0xFF) << 8 |
            (b3[1] & 0xFF) << 16 | (b3[0] & 0xFF) << 24));
        filelist.add(filedata);
      }
    }

    return filelist;
  }

  @Override
  public boolean download(FileData fileData, String fileName, String filePath){

    byte[] filename = fileName.getBytes();
    Map<Integer,List<InternetNode>> K_bucket = totaldata.getK_bucket();
    int distance = distance(filename,totaldata.getId());
    distance = (int)(Math.log(distance)/Math.log(2))+1;
    while(true){
      List<InternetNode> internetnode = K_bucket.get(distance);
      if(internetnode == null) {
        distance--;
        continue;
      }
      if(distance == 0) {
        break;
      }

      for(int i=0;i<internetnode.size()-1;i++) {
        DatagramChannel channel = null;
        InetAddress addr = null;
        try {
          addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e2) {
          e2.printStackTrace();
        }
        SocketAddress socketAddress = new InetSocketAddress(addr.getHostAddress(), 90);
        try {
          channel = DatagramChannel.open();
          channel.configureBlocking(false);//设置不阻塞
          channel.bind(socketAddress);
        } catch (IOException e1) {
          e1.printStackTrace();
        }

        byte[] com = new byte[12];
        byte[] code = new byte[4];
        System.arraycopy("Load".getBytes(), 0, com, 0, "Load".getBytes().length);
        char[] ch = fileName.toCharArray();
        int hash = 0;
        for(int j=0;j<ch.length-1;j++) {
          hash += ch[j];
        }
        System.arraycopy(intToBytearray(hash), 0, code, 0, code.length);
        int messagelength = 16;
        byte[] temp = new byte[messagelength];

        ByteBuffer buffer = ByteBuffer.wrap(temp);
        buffer.put(com);
        buffer.put(code);
        byte[] message = buffer.array();
        try {
          ByteBuffer buffer1 = ByteBuffer.allocate(1024);
          buffer1.put(message);
          buffer1.flip();
          channel.send(buffer1, socketAddress);
        }catch (Exception e) {
          e.printStackTrace();
          System.out.println("send_mess出错");
        }

        TransferData trans = new TransferData(TransferState.ShareDownload, fileData);
        loadFile download = new loadFile(90, socketAddress, filename, trans);
        download.run();
      }
    }

    return true;
  }

  @Override
  public boolean upload(String fileName, String filePath) {

    File file = new File(filePath);
    if(!file.exists()){
      System.out.print("本地不存在该文件！");
      return false;
    }

    byte[] filename = fileName.getBytes();
    Map<Integer,List<InternetNode>> K_bucket = totaldata.getK_bucket();
    int distance = distance(filename,totaldata.getId());
    distance = (int)(Math.log(distance)/Math.log(2))+1;

    while(true){
      List<InternetNode> internetnode = K_bucket.get(distance);
      if(internetnode == null) {
        distance--;
        continue;
      }
      if(distance == 0) {
        break;
      }

      for(int i=0;i<internetnode.size()-1;i++) {
        DatagramChannel channel = null;
        InetAddress addr = null;
        try {
          addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e2) {
          e2.printStackTrace();
        }
        SocketAddress socketAddress = new InetSocketAddress(addr.getHostAddress(), 90);
        try {
          channel = DatagramChannel.open();
          channel.configureBlocking(false);//设置不阻塞
          channel.bind(socketAddress);
        } catch (IOException e1) {
          e1.printStackTrace();
        }

        byte[] com = new byte[12];
        byte[] name = new byte[12];
        byte[] code = new byte[4];
        byte[] len = new byte[4];
        System.arraycopy("Store".getBytes(), 0, com, 0, "Store".getBytes().length);
        System.arraycopy(filename, 0, name, 0, filename.length);
        char[] ch = fileName.toCharArray();
        int hash = 0;
        for(int j=0;j<ch.length-1;j++) {
          hash += ch[j];
        }
        System.arraycopy(intToBytearray(hash), 0, code, 0, code.length);
        System.arraycopy(intToBytearray(1024), 0, len, 0, len.length);
        int messagelength = 32;
        byte[] temp = new byte[messagelength];

        ByteBuffer buffer = ByteBuffer.wrap(temp);
        buffer.put(com);
        buffer.put(name);
        buffer.put(code);
        buffer.put(len);
        byte[] message = buffer.array();
        try {
          ByteBuffer buffer1 = ByteBuffer.allocate(1024);
          buffer1.put(message);
          buffer1.flip();
          channel.send(buffer1, socketAddress);
        }catch (Exception e) {
          e.printStackTrace();
          System.out.println("send_mess出错");
        }

        byte[] message1 = null;
        ByteBuffer buffer2 = ByteBuffer.allocate(1024);
        buffer2.clear();
        try {
          channel.receive(buffer);
          message1 = new byte[buffer.position()];
          buffer.flip();  //读取准备 
          buffer.get(message1,0,buffer.position());
        }catch (Exception e) {
          e.printStackTrace();
        }

        byte[] b1 = new byte[12];
        System.arraycopy(message1, 0, b1, 0, 12);

        FileData filedata = new FileData(fileName, code, 1024);
        TransferData trans = new TransferData(TransferState.ShareUpload, filedata);
        upFile upload = new upFile(90, socketAddress, filename, trans);
        upload.run();
      }
    }

    return true;

  }

}
