package dataStruct;

/**
 * 文件信息。 目前仅定义了主程序需要使用的信息，请按需扩展。
 * 
 * @author HIT_1170300119
 *
 */
public class FileData {
  private String fileName; // 文件名
  private byte[] fileHash; // 文件哈希值
  private int fileLength; // 文件大小

  /**
   * 构造方法，初始化。
   * 
   * @param fileName   文件名
   * @param fileHash   文件哈希值
   * @param fileLength 文件长度
   */
  public FileData(String fileName, byte[] fileHash, int fileLength) {
    this.fileHash = fileHash;
    this.fileLength = fileLength;
    this.fileName = fileName;
  }

  private static String conver16HexStr(byte[] b) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < b.length; i++) {
      if ((b[i] & 0xff) < 0x10)
        result.append("0");
      result.append(Long.toString(b[i] & 0xff, 16));
    }
    return result.toString().toUpperCase();
  }
  
  public byte[] getFileHash() {
    return fileHash;
  }

  public int getFileLength() {
    return fileLength;
  }

  public String getFileName() {
    return fileName;
  }

  @Override
  public String toString() {
    return fileName+"   ("+fileLength+"字节  哈希值："+conver16HexStr(fileHash)+")";
  }
}
