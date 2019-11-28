package dataStruct;

/**
 * 文件传输状态枚举。
 * @author HIT_1170300119
 *
 */
public enum TransferState {
  ShareDownload,            //共享性文件下载，即用户通过资源搜索开展的文件下载
  TransferDownload,         //传输性文件下载，即节点承担存储义务开展的文件下载
  ShareUpload,              //共享性文件上传，即用户通过文件共享开展的文件上传
  TransferUpload            //传输性文件上传，即节点承担存储义务开展的文件上传
}
