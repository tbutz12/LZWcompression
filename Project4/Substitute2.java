import java.security.SecureRandom;

public class Substitute implements SymCipher{
  private byte[] key;
  public Substitute(){
    key = new byte[256];
    int c = 0;
    for(int x = -128; x <= 127; x++){
      key[c++] = (byte)x;
    }
    SecureRandom random = new SecureRandom();
    for(int y = 0; y < key.length; y++){
      int ran = random.next(256);
      byte temp = key[y];
      key[y] = key[ran];
      key[ran] = temp;
    }

}
  public Substitute(byte [] arr){
    key = arr;
  }
  public byte[] getKey(){
    return key;
  }
  public byte[] encode(String s){
    byte [] arr = s.getBytes();
    byte [] res = new byte[arr.length];
    for(int x = 0; x < arr.length; x++){
      int id = (int)(arr[x]+128);
      res[x] = key[id];
    }
    return res;
  }
  public String decode(byte[] arr){
    byte [] index = new byte[arr.length];
    for(int i = 0; i < index.length; i++){
      for(int j = 0; j < key.length; j++){
        if(key[j] == arr[i]){
          index[i] = (byte)(j-128);
        }
      }
    }
    String result = new String(index);
    return result;
}

}
