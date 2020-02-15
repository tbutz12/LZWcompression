/*************************************************************************
*  Compilation:  javac LZWmod.java
*  Execution:    java LZWmod - < input.txt   (compress)
*  Execution:    java LZWmod + < input.txt   (expand)
*  Dependencies: BinaryStdIn.java BinaryStdOut.java
*
*  Compress or expand binary input from standard input using LZW.
*
*
*************************************************************************/
import java.util.ArrayList;
import java.io.File;

public class LZWmod {
  private static final int R = 256;        // number of input chars
  private static int W; //number of codewords... 9-16
  private static int L; //codeword width ... 2^W
  private static String keepsake;
  private static final int max = 130560;

  public static void compress() {
    W = 9;
    L = 512;
    TSTmod<Integer> st = new TSTmod<Integer>();
    int i;
    for (i = 0; i < R; i++)
    st.put(new StringBuilder("" + (char) i), i);
    int code = R+1;  // R is codeword for EOF
    //initialize the current string
    StringBuilder current = new StringBuilder();
    //read and append the first char
    if(BinaryStdIn.isEmpty()){
      return;
    }
    char c = BinaryStdIn.readChar();
    current.append(c);
    Integer codeword = st.get(current);
    //for use of the "n" keyword meaning do nothing mode
        if(keepsake.equals("n")){
          //loop until end of file
    while (!BinaryStdIn.isEmpty()) {
    codeword = st.get(current);
    char d = BinaryStdIn.readChar();
    //append codeword
    current.append(d);
    if(!st.contains(current)){
      //write codeword to dictionary
    BinaryStdOut.write(codeword, W);
    if(code >= L && W != 16){
      //if codeword width gets too large, reset bits of codeword and resize table
    W++;
    L = (int)Math.pow(2, W);
  }
  if (code < L){    // Add to symbol table if not full
  st.put(current, code++);
}
current = new StringBuilder();
current.append(d);
}
}
}
//for use of keyword "r" meaning reset dictionary
    else if(keepsake.equals("r")){
while (!BinaryStdIn.isEmpty()) {
  //System.err.println("Read bits = " + readBits + " Compressed Bits = " + cBits + " ratio is " + ratio);
  codeword = st.get(current);
  char d = BinaryStdIn.readChar();
  current.append(d);
  if(!st.contains(current)){
    BinaryStdOut.write(codeword, W);
    //if codeword width gets too large, reset bits of codeword and resize table
    if(code >= L){
      if(W != 16){
      W++;
      L = (int)Math.pow(2, W);
    }
    else{
      //if bits of codeword equal 16, initialize a reset
      st = new TSTmod<Integer>();
      for (i = 0; i < R; i++)
      st.put(new StringBuilder("" + (char) i), i);
      W = 9;
      L = (int)Math.pow(2, W);
      code = R + 1;
  }
  }
    if (code < L){    // Add to symbol table if not full
      st.put(current, code++);
    }
    current = new StringBuilder();
    current.append(d);
  }
}
  }

codeword = st.get(current);
BinaryStdOut.write(codeword, W);
BinaryStdOut.write(R, W); //Write EOF
BinaryStdOut.close();
}

public static void expand() {
  W = 9;
  L = 512;
  String[] st = new String[max];
  int i;
  //write in char values
  for (i = 0; i < R; i++)
  st[i] = "" + (char) i;
  st[i++] = "";                        // (unused) lookahead for EOF
  if(BinaryStdIn.isEmpty()){
    return;
  }
  int codeword = BinaryStdIn.readInt(W);
  String val = st[codeword];
  //for use of nothing mode
  if(keepsake.equals("n")){
  while (true) {
  BinaryStdOut.write(val);
  codeword = BinaryStdIn.readInt(W);
  if (codeword == R) break;
  //store codeword in string
  String s = st[codeword];
  if (i == codeword) s = val + val.charAt(0);   // special case hack
  if (i < L){
  st[i++] = val + s.charAt(0);
  if(i>=L && W != 16){
  W++;
  L = (int)Math.pow(2, W);
}
}
val = s;
}
}
//for use of reset mode
  else if(keepsake.equals("r")){
while(true){
  //System.err.println("Read bits = " + readBits + " Compressed Bits = " + cBits + " ratio is " + ratio);
  BinaryStdOut.write(val);
  codeword = BinaryStdIn.readInt(W);
  if (codeword == R) break;
  String s = st[codeword];
  if (i == codeword){
     s = val + val.charAt(0);
   }
  if (i < L){
    st[i++] = val + s.charAt(0);
    if(i >= L){
        if(W != 16){
          W++;
          L = (int)Math.pow(2, W);
        }
        else{
          st = new String[max];
          for (i = 0; i < R; i++)
          st[i] = "" + (char) i;
          st[i++] = "";
          W = 9;
          L = 512;
        }
      }
  }
    val = s;
  }

    }
BinaryStdOut.close();
}



public static void main(String[] args) {
  if (args[1].equals("n") || args[1].equals("r")){
    keepsake = args[1];
  }
  if      (args[0].equals("-")) compress();
  else if (args[0].equals("+")) expand();
  else throw new RuntimeException("Illegal command line argument");
}

}
