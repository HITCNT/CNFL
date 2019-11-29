package dataStruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * �����ݳض���
 * �����������ڴ����ݾ��ڱ����й���
 * Ŀǰ����������������Ҫ�����ݣ��������ɱ��ඨ�塣
 * @author HIT_1170300119
 *
 */
public class TotalData {
  private byte[] id;                            //�ͻ���ID���״�����������ɣ��������������ɺͼ���
  private List<TransferData> TransferList;      //������Ϣ�б�������չʾʹ�ã��ɷ������Ϳͻ���д�룬��ע���̰߳�ȫ
  private List<TransferData> SharedList;        //��ǰ�ѷ����������չʾʹ�ã��ɿͻ���д��

  private Map<byte[],FileData> File = new HashMap<byte[],FileData>(); //�����ļ���Ŀ
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
