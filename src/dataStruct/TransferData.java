package dataStruct;

/**
 * 文件传输情况描述。
 * 请按需添加。
 * @author HIT_1170300119
 *
 */
public class TransferData {
  private TransferState state;      //文件传输状态
  private FileData fileData;        //文件描述信息
  private int progress=0;           //文件传输进度(已完成长度)
  
  /**
   * 构造方法，初始化
   * @param state 文件传输状态
   * @param fileData 文件描述信息
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
