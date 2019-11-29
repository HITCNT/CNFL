package networkTool;

public class ByteTool {

  public static int bytesCompare(byte[] a,byte[] b) {
    for(int i=0;i<a.length;i++) {
      int result=new Byte(a[i]).compareTo(new Byte(b[i]));
      if(result!=0) {
        return result;
      }
    }
    
    return 0;
  }
  
  public static String conver16HexStr(byte[] b) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < b.length; i++) {
      if ((b[i] & 0xff) < 0x10)
        result.append("0");
      result.append(Long.toString(b[i] & 0xff, 16));
    }
    return result.toString().toUpperCase();
  }

  public static byte[] conver16HexToByte(String hex16Str) {
    char[] c = hex16Str.toCharArray();
    byte[] b = new byte[c.length / 2];
    for (int i = 0; i < b.length; i++) {
        int pos = i * 2;
        b[i] = (byte) ("0123456789ABCDEF".indexOf(c[pos]) << 4 | "0123456789ABCDEF".indexOf(c[pos + 1]));
    }
    return b;
}
}
