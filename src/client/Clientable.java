package client;

import java.util.List;

import dataStruct.FileData;
import dataStruct.TotalData;

/**
 * 客户端接口。
 * 客户端应当自动化地完成Kad协议要求的客户端操作。
 * 客户端的工作应当由主程序驱动，并向主程序暴露相关信息。
 * 主程序依该接口调用功能，请严格按注释权责实现。
 * @author HIT_1170300119
 *
 */
public interface Clientable {
  /**
   * 获取客户机默认实现。
   * TODO 请在实现后修改本方法。
   * @return 客户机对象
   */
  public static Clientable getClient(TotalData totalData) {
    return null;
  }
  
  /**
   * 按关键词搜索文件。
   * @param keyWords 关键词
   * @return 文件列表。若网络错误则返回NULL。其他错误待讨论。
   */
  public List<FileData> searchFile(String keyWords);
  
  /**
   * 下载目标文件。
   * 应当启动下载并持续修改传输列表，若本地文件夹未存在，应当负责创建。
   * @param fileData 文件描述数据
   * @param fileName 本地存储文件名
   * @param filePath 本地存储文件路径
   * @return 是否成功启动下载
   */
  public boolean download(FileData fileData,String fileName,String filePath);
  
  /**
   * 分享目标文件。
   * 应当启动分享并持续修改传输列表，应当修改当前已分享表，若本地文件未存在则应当报错。
   * @param fileData 文件描述数据
   * @param fileName 本地存储文件名
   * @param filePath 本地存储文件路径
   * @return 是否成功启动上传
   */
  public boolean upload(FileData fileData,String fileName,String filePath);
}
