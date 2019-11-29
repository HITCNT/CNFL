package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import dataStruct.TotalData;

public class Server implements Serverable {
  
  private TotalData totalData;

  public Server(TotalData totalData) {
    this.totalData=totalData;
  }
  
  @Override
  public void run() {
    ServerSocket ss;
    try {
      ss = new ServerSocket(12345);
      while (true) {
        Socket s = ss.accept();
        new Thread(new ServerHandler(s,totalData)).start();
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
