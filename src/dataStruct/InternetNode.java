/**
 * 
 */
package dataStruct;

/**
 * @author LC12138
 *
 */
public class InternetNode {
  private byte[] id;
  private String ip;
  private String port;


  public InternetNode(byte[] id, String ip, String port) {
    this.id = id;
    this.ip = ip;
    this.port = port;
  }


  public byte[] getId() {
    return this.id;
  }


  public String getIp() {
    return this.ip;
  }

  public String getPort() {
    return this.port;
  }

}
