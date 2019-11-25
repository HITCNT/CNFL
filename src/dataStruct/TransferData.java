package dataStruct;

/**
 * �ļ��������������
 * �밴����ӡ�
 * @author HIT_1170300119
 *
 */
public class TransferData {
  private TransferState state;      //�ļ�����״̬
  private FileData fileData;        //�ļ�������Ϣ
  private int progress=0;           //�ļ��������(����ɳ���)
  
  /**
   * ���췽������ʼ��
   * @param state �ļ�����״̬
   * @param fileData �ļ�������Ϣ
   */
  public TransferData(TransferState state,FileData fileData) {
    this.state=state;
    this.fileData=fileData;
  }
  
  public void setProgress(int progress) {
    this.progress = progress;
  }
  
  public FileData getFileData() {
    return fileData;
  }
  
  public int getProgress() {
    return progress;
  }
  
  public TransferState getState() {
    return state;
  }
  
}
