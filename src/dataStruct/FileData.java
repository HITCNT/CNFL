package dataStruct;

/**
 * �ļ���Ϣ��
 * Ŀǰ����������������Ҫʹ�õ���Ϣ���밴����չ��
 * @author HIT_1170300119
 *
 */
public class FileData {
  private String fileName;      //�ļ���
  private byte[] fileHash;      //�ļ���ϣֵ
  private int fileLength;       //�ļ���С
  
  /**
   * ���췽������ʼ����
   * @param fileName �ļ���
   * @param fileHash �ļ���ϣֵ
   * @param fileLength �ļ�����
   */
  public FileData(String fileName,byte[] fileHash,int fileLength) {
    this.fileHash=fileHash;
    this.fileLength=fileLength;
    this.fileName=fileName;
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
}
