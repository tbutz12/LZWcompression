import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.BigInteger;
import java.util.Arrays;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

    public static final int PORT = 8765;
		ObjectInputStream myReader;
		ObjectOutputStream myWriter;
    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
	  Socket connection;
		BigInteger E;
		BigInteger N;
		String type;
		SymCipher cipher;

    public SecureChatClient ()
    {
        try {

        myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
        serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
        InetAddress addr =
                InetAddress.getByName(serverName);
        connection = new Socket(addr, PORT);   // Connect to server with new
                                               // Socket
        myWriter = new ObjectOutputStream(connection.getOutputStream());   // Get Reader and Writer
				myWriter.flush();
        myReader = new ObjectInputStream(connection.getInputStream());
				E = (BigInteger)myReader.readObject();
				N = (BigInteger)myReader.readObject();
				type = (String)myReader.readObject();
				System.out.println("E is: " + E);
				System.out.println("N is: " + N);
				if(type.equals("Add")){
					cipher = new Add128();
				}
				else{
					cipher = new Substitute();
				}
				byte [] key = cipher.getKey();
				BigInteger bigKey = new BigInteger(1,key);
				BigInteger encrypt = bigKey.modPow(E,N);
				myWriter.writeObject(encrypt);
				myWriter.flush();
				byte[] name = cipher.encode(myName);
				byte[] arr = myName.getBytes();
				System.out.println("\n\t\t\t\t\t***Encryption***\n");
				System.out.println("\t\tOriginal message:\t\t" + myName);
				System.out.println("\t\tOriginal Bytes\t\t" + Arrays.toString(arr));
				System.out.println("\t\tEncrypted Bytes\t\t" + Arrays.toString(name));
				myWriter.writeObject(name);
				myWriter.flush();

        this.setTitle(myName);      // Set title to identify chatter

        Box b = Box.createHorizontalBox();  // Set up graphical environment for
        outputArea = new JTextArea(8, 30);  // user
        outputArea.setEditable(false);
        b.add(new JScrollPane(outputArea));

        outputArea.append("Welcome to the Chat Group, " + myName + "\n");

        inputField = new JTextField("");  // This is where user will type input
        inputField.addActionListener(this);

        prompt = new JLabel("Type your messages below:");
        Container c = getContentPane();

        c.add(b, BorderLayout.NORTH);
        c.add(prompt, BorderLayout.CENTER);
        c.add(inputField, BorderLayout.SOUTH);

        Thread outputThread = new Thread(this);  // Thread is to receive strings
        outputThread.start();                    // from Server

	      addWindowListener(
              new WindowAdapter()
              {
                  public void windowClosing(WindowEvent e)
                  {
										try{
										byte [] write = cipher.encode("CLIENT CLOSING");
										myWriter.writeObject(write);
										myWriter.flush();
                    System.exit(0);
									}
									catch(IOException w){
										System.out.println("ERROR " +w + "Closing Client!");
									}
                   }
              }
          );

        setSize(500, 200);
        setVisible(true);

        }
        catch (Exception e)
        {
            System.out.println("Problem starting client!");
        }
    }

    public void run()
    {
        while (true)
        {
             try {
								byte [] msg = (byte[])myReader.readObject();
								String currMsg = cipher.decode(msg);
								byte [] og = currMsg.getBytes();
								System.out.println("\n\t\t\t\t\t***Decryption***\n");
								System.out.println("\t\tArray of bytes received\t\t" + Arrays.toString(msg));
								System.out.println("\t\tArray of bytes decrypted\t\t" + Arrays.toString(og));
								System.out.println("\t\tCorresponding message:\t\t" + currMsg);
			          outputArea.append(currMsg+"\n");
             }
             catch (Exception e)
             {
                System.out.println(e +  ", closing client!");
                break;
             }
        }
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e)
    {
			  boolean flag = true;
        String currMsg = e.getActionCommand();      // Get input value
        inputField.setText("");
				try{
					byte[] write = cipher.encode(myName+":"+currMsg);
					myWriter.writeObject(write);
					myWriter.flush();
					byte[] print = (myName+":"+currMsg).getBytes();
					System.out.println("\n\t\t\t\t\t***Encryption***\n");
					System.out.println("\t\tOriginal message:\t\t" + myName+":"+currMsg);
					System.out.println("\t\tCorresponding array of bytes\t\t" + Arrays.toString(print));
					System.out.println("\t\tEncrypted array of bytes\t\t" + Arrays.toString(write));

				}
				catch(IOException w){
					System.out.println("ERROR " +w + "Closing Client!");

				}
	  }

    public static void main(String [] args)
    {
         SecureChatClient JR = new SecureChatClient();
         JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}
