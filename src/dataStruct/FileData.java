package dataStruct;

/**
 * �ļ���Ϣ�� Ŀǰ����������������Ҫʹ�õ���Ϣ���밴����չ��
 * 
 * @author HIT_1170300119
 *
 */
public class FileData {
  private String fileName; // �ļ���
  private byte[] fileHash; // �ļ���ϣֵ
  private int fileLength; // �ļ���С

  /**
   * ���췽������ʼ����
   * 
   * @param fileName   �ļ���
   * @param fileHash   �ļ���ϣֵ
   * @param fileLength �ļ�����
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
    return fileName+"   ("+fileLength+"�ֽ�  ��ϣֵ��"+conver16HexStr(fileHash)+")";
  }
}
