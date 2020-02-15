import java.security.SecureRandom;

public class Add128 implements SymCipher{
  private byte[] key;

  public Add128(){
    SecureRandom random = new SecureRandom();
    key = new byte[128];
    random.nextBytes(key);
  }

  public Add128(byte [] arr){
    key = arr;
  }
  public byte [] getKey(){
    return key;
  }
  public byte [] encode(String s){
    byte [] convert = s.getBytes();
    byte [] res = new byte[convert.length];
    //System.out.println("\n\t\t\t****Encoded byte of array****\t\t\t ");
    for(int x = 0; x < convert.length; x++){
      res[x] = (byte)(key[x%key.length] + convert[x]);
    //  System.out.print(res[x]+" ");
    }
    return res;
  }
  public String decode(byte [] arr){
    byte [] res = new byte[arr.length];
  //  System.out.println("\n\t\t\t****Decoded byte of array****\t\t\t ");
    for(int y = 0; y < arr.length; y++){
      res[y] = (byte)(arr[y] - key[y%key.length]);
    //  System.out.print(res[y] +" ");
    }
    String result = new String(res);
    return result;
  }
}
