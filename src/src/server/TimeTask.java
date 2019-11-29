/**
 * 
 */
package server;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author LC12138
 *
 */
public class TimeTask{
  Timer t=new Timer();
  /**
   * 
   */
  public Server server;
  public TimeTask(Server server) {
    // TODO Auto-generated constructor stub
    this.server = server;
    TimeTask tt=this;
    t.schedule(new TimerTask() {

      @Override
      public void run() {
        // TODO Auto-generated method stub
        tt.server.upData();
      }
    }, 1000,1000);
  }



}
