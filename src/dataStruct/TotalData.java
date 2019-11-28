package dataStruct;

import java.util.List;

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
}
