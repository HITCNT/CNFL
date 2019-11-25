package server;

import dataStruct.TotalData;

/**
 * 服务器接口。
 * 服务器应当自动化地完成Kad协议要求的服务器操作。
 * 服务器本身应是一个线程(Runnable)，主程序在完成初始化后将启动该线程。启动线程后主程序不再与该线程交互。
 * 请注意线程安全。
 * 
 * @author HIT_1170300119
 *
 */
public interface Serverable extends Runnable {
  /**
   * 获取服务器默认实现。
   * TODO 请在实现后修改本方法。
   * @return 服务器对象
   */
  public static Serverable getServer(TotalData totalData) {
    return null;
  }
}
