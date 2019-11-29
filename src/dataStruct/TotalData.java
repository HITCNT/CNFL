package dataStruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 总数据池对象。
 * 本程序所有内存数据均在本类中管理。
 * 目前仅定义了主程序需要的数据，请持续完成本类定义。
 * @author HIT_1170300119
 *
 */
public class TotalData {
  private byte[] id;                            //客户端ID，首次运行随机生成，由主程序负责生成和加载
  private List<TransferData> TransferList;      //传输信息列表，主程序展示使用，由服务器和客户端写入，请注意线程安全
  private List<TransferData> SharedList;        //当前已分享表，主程序展示使用，由客户端写入

  private Map<byte[],FileData> File = new HashMap<byte[],FileData>(); //储存文件条目
  private Map<Integer,List<InternetNode>> K_bucket = new HashMap<Integer,List<InternetNode>>();//K-bucket

  private Map<byte[],List<InternetNode>> FileInNode = new HashMap<byte[],List<InternetNode>>();

  public TotalData(byte[] id,List<TransferData> TransferList) {
    this.id=id;
    this.TransferList=TransferList;
  }

  public byte[] getId() {
    return id;
  }

  public List<TransferData> getTransferList() {
    return TransferList;
  }

  public List<TransferData> getSharedList() {
    return SharedList;
  }

  public Map<byte[],FileData> getFile() {
    return File;
  }

  public Map<Integer,List<InternetNode>> getK_bucket() {
    return K_bucket;
  }

  public Map<byte[],List<InternetNode>> getFileInNode() {
    return FileInNode;
  }
}
