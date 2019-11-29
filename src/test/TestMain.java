package test;

import dataStruct.IPPort;
import networkTool.NodeTool;
import server.Server;

public class TestMain {

  public static void main(String[] args) {
    Server server=new Server();
    new Thread(server).start();
    System.out.println(NodeTool.ping(new IPPort("121.0.0.2", 12345)));

  }

}
