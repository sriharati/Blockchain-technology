import java.security.*;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import array.A;
	
	public class Message {

	    String messageName = null;
	    String messageContent = null;

	    SecretKey secretKey = null;
	}

	class MessageQueue { //Message Queue using Message class

	    private int maxCount;
	    private int messageCount = 0;
	    private Message[] buffer;
	    private int bottom = 0, up = 0;

	    MessageQueue(int queueSize) {
	        this.maxCount=queueSize;
	        buffer = new Message[queueSize];
	        for (int i = 0; i < maxCount; i++){
	            buffer[i] = new Message();
	        }
	    }
	    public synchronized void put(Message message) {
	        while (messageCount == maxCount) {
	            try {
	                wait();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	        messageCount++;

	        buffer[up].messageName = message.messageName;
	        buffer[up].messageContent = message.messageContent;

	        buffer[up].secretKey = message.secretKey;

	        up = (up + 1) % maxCount; // to place next item in

	        if (messageCount == 1)
	            notify();
	    }

	    public synchronized Message get() {
	        Message message;
	        while (messageCount == 0) {
	            try {
	                wait();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	        message = buffer[bottom]; // remove message from buffer;
	        bottom = (bottom + 1) % maxCount; // to fetch next item from
	        --messageCount;
	        if (messageCount == maxCount - 1)
	            notify();
	        return message;
	    }
	}


	class ByteMessage {
	    byte[] messageName = null;
	    byte[] messageContent = null;

	    byte[] hashedValue = null;

	}

	class ByteMessageQueue {

	    private int maxCount;
	    private int messageCount = 0;
	    private ByteMessage[] buffer;
	    private int bottom = 0, up = 0;

	    ByteMessageQueue(int queueSize) {
	        this.maxCount=queueSize;
	        buffer = new ByteMessage[queueSize];
	        for (int i = 0; i < maxCount; i++){
	            buffer[i] = new ByteMessage();
	        }
	    }
	    public synchronized void put(ByteMessage byteMessage) {
	        while (messageCount == maxCount) {
	            try {
	                wait();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	        messageCount++;

	        buffer[up].messageName = byteMessage.messageName; // place message in buffer;
	        buffer[up].messageContent = byteMessage.messageContent;

	        buffer[up].hashedValue = byteMessage.hashedValue;




	        up = (up + 1) % maxCount; // to place next item in

	        if (messageCount == 1)
	            notify();
	    }

	    public synchronized ByteMessage get() {
	        ByteMessage byteMessage;
	        while (messageCount == 0)
	            try {
	                wait();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        byteMessage = buffer[bottom];
	        bottom = (bottom + 1) % maxCount;
	        --messageCount;
	        if (messageCount == maxCount - 1)
	            notify();
	        return byteMessage;
	    }
	}

	class KeyRequestMessage {
	    String messageName = null;
	}


	class MBRSecretKey {  //Message Buffer and Response using KeyRequestMessage class, return secret key

	    private SecretKey key;
	    private KeyRequestMessage keyRequestMessage;
	    private boolean messageBufferFull = false;
	    private boolean responseBufferFull = false;


	    synchronized SecretKey send(KeyRequestMessage keyRequestMessage)
	    {
	        this.keyRequestMessage = keyRequestMessage;
	        messageBufferFull = true;
	        notify();
	        while(responseBufferFull==false)
	            try
	            {

	                wait();
	            }
	            catch(InterruptedException e)
	            {
	                System.out.println("InterruptedException caught");
	            }
	        responseBufferFull = false;
	        return key;
	    }

	    synchronized KeyRequestMessage receive()
	    {
	        while(messageBufferFull==false)
	            try
	            {
	                wait();
	            }
	            catch(InterruptedException e)
	            {
	                System.out.println("InterruptedException caught");
	            }
	        messageBufferFull = false;
	        notify();
	        return keyRequestMessage;
	    }
	    synchronized void reply(SecretKey secretKey)
	    {
	        this.key = secretKey;
	        responseBufferFull = true;
	        notify();
	    }
	}


	class Global{

	    public static MessageQueue senderComponentQueue;
	    public static ByteMessageQueue q2;
	    public static ByteMessageQueue q3;
	    public static ByteMessageQueue q4;
	    public static MessageQueue receiverComponentQueue;

	    public static MBRSecretKey mbrSecretKey = new MBRSecretKey();
	    public static int input;
	    public static int queueSize;
	}
	//Hashing_feature to check Integrity
	        class hashValueGenration {
	            public byte[] generate(byte[] msg) throws Exception {
	                MessageDigest md = MessageDigest.getInstance("SHA-1");
	                md.update(msg);
	                byte[] mdbytes = md.digest();
	                return (mdbytes);
	            }
	        }
	        class hashValueVerification {
	            public Boolean verify(byte[] hashValue, byte[] msg) throws Exception {
	                MessageDigest md = MessageDigest.getInstance("SHA-1");
	                md.update(msg);
	                byte[] mdBytes = md.digest();

	                if (MessageDigest.isEqual(hashValue, mdBytes))
	                    return true;
	                else
	                    return false;
	            }
	        }
	//Symmetric Encryption with DES
	        class EncryptionEncryptor {
	            Cipher c;

	            byte[] encrypt(byte plainText[], SecretKey sk) throws Exception
	            {
	                try{
	                    c = Cipher.getInstance("DES/ECB/PKCS5Padding");
	                }
	                catch (Exception e){
	                    e.printStackTrace();
	                }

	                c.init(Cipher.ENCRYPT_MODE, sk);
	                return c.doFinal(plainText);
	            }
	        }

	//Symmetric Encryption with DES
	        class EncryptionDecryptor {
	            Cipher c;

	            byte[] decrypt(byte cyperText[], SecretKey sk) throws Exception
	            {
	                try{
	                    c = Cipher.getInstance("DES/ECB/PKCS5Padding");
	                }
	                catch (Exception e){
	                    e.printStackTrace();
	                }

	                c.init(Cipher.DECRYPT_MODE, sk);
	                return c.doFinal(cyperText);
	            }
	        }

	class SecureSenderConnector {

	    static Thread t_SecuritySenderCoordinator;
	    static Thread t_AsynchronousMCSender;

	    static EncryptionEncryptor ee;

	    static hashValueGenration hs;

	    static void aSecureSenderConnector(){
	        try {


	            ee = new EncryptionEncryptor();
	            hs = new hashValueGenration();
	            SecuritySenderCoordinator securitySenderCoordinator = new SecuritySenderCoordinator();
	            securitySenderCoordinator.sendSecAsync(hs,ee);
	            t_SecuritySenderCoordinator = securitySenderCoordinator.t_SecuritySenderCoordinator;

	            AsynchronousMCSender asynchronousMCSender = new AsynchronousMCSender();
	            asynchronousMCSender.sendSecAsync();
	            t_AsynchronousMCSender = asynchronousMCSender.t_AsynchronousMCSender;
	        }
	        catch (Exception e){
	            e.printStackTrace();
	        }
	    }
	}

	class SecuritySenderCoordinator implements Runnable {

	    Thread t_SecuritySenderCoordinator;

	    EncryptionEncryptor ee;
	    hashValueGenration hs;
	public void sendSecAsync(hashValueGenration hs, EncryptionEncryptor ee)
	throws Exception
	    {
	        t_SecuritySenderCoordinator = new Thread(this, "SecuritySenderCoordinator");
	        t_SecuritySenderCoordinator.start();
	        this.ee = ee;
	        this.hs = hs;

	    }

	    public void run()
	    {
	        int i=0;
	        Message message = new Message();
	        ByteMessage byteMessage = new ByteMessage();

	        while(i<Global.input)
	        {
	            i++;

	            message=Global.senderComponentQueue.get();

	            try {
	                byteMessage.messageContent = (message.messageContent).getBytes();
	                byteMessage.messageName = (message.messageName).getBytes();
	                    byteMessage.messageContent = ee.encrypt(byteMessage.messageContent, message.secretKey);
	                    System.out.println("Encrypted messageContent!");

	                    byteMessage.hashedValue = hs.generate(byteMessage.messageContent);
	                    System.out.println("Hashed value! " + byteMessage.hashedValue);


	                Global.q2.put(byteMessage);

	            } catch (NoSuchAlgorithmException e) {
	                e.printStackTrace();

	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
	class AsynchronousMCSender implements Runnable {
	    Thread t_AsynchronousMCSender;

	    public void sendSecAsync() throws Exception{
	        t_AsynchronousMCSender = new Thread(this, "AsynchronousMCSender");
	        t_AsynchronousMCSender.start();
	    }

	    public void run()
	    {
	        int i = 0;
	        ByteMessage byteMessage = new ByteMessage();

	        while(i<Global.input)
	        {
	            i++;
	            byteMessage = Global.q2.get();

	            Global.q3.put(byteMessage);
	        }
	    }
	}

	class SecureReceiverConnector {
	    static Thread t_AsynchronousMCReceiver;
	    static Thread t_SecurityReceiverCoordinator;    static EncryptionDecryptor ed;
	    static hashValueVerification hsv;
	    public static void aSecureReceiverConnector(){
	        try {

	            ed = new EncryptionDecryptor();
	            hsv = new hashValueVerification();
	            AsynchronousMCReceiver asynchronousMCReceiver = new AsynchronousMCReceiver();
	            asynchronousMCReceiver.sendSecAsync();
	            t_AsynchronousMCReceiver = asynchronousMCReceiver.t_AsynchronousMCReceiver;

	            SecurityReceiverCoordinator securityReceiverCoordinator = new SecurityReceiverCoordinator();

	            securityReceiverCoordinator.sendSecAsync(ed,hsv);
	            t_SecurityReceiverCoordinator = securityReceiverCoordinator.t_SecurityReceiverCoordinator;

	        }
	        catch (Exception e){
	            e.printStackTrace();
	        }

	    }
	}

	class AsynchronousMCReceiver implements Runnable {
	    Thread t_AsynchronousMCReceiver;

	    public void sendSecAsync() throws Exception{

	        t_AsynchronousMCReceiver = new Thread(this, "AsynchronousMCReceiver");
	        t_AsynchronousMCReceiver.start();

	    }

	    public void run()
	    {

	        int i = 0;
	        ByteMessage byteMessage = new ByteMessage();

	        while(i<Global.input)
	        {
	            i++;
	            byteMessage = Global.q3.get();

	            Global.q4.put(byteMessage);
	        }

	    }
	}



	class SecurityReceiverCoordinator implements Runnable
	{
	    Thread t_SecurityReceiverCoordinator;

	    boolean result;

	    Cipher desCipher;
	    EncryptionDecryptor ed;

	    SecretKey desKey;
	    hashValueVerification hsv;
	 public void sendSecAsync(EncryptionDecryptor ed,hashValueVerification hsv)
	throws Exception{
	        t_SecurityReceiverCoordinator = new Thread(this, "SecurityReceiverCoordinator");
	        t_SecurityReceiverCoordinator.start();
	        this.ed = ed;
	        this.hsv = hsv;
	    }
	    public void run(){
	        int i = 0;
	        ByteMessage byteMessage = new ByteMessage();
	        Message message = new Message();
	        KeyRequestMessage keyRequestMessage = new KeyRequestMessage();
	        while(i<Global.input)
	        {
	            i++;
	            try {

	                byteMessage = Global.q4.get();

	                keyRequestMessage.messageName = "Request Key";
	                SecretKey secretKey = Global.mbrSecretKey.send(keyRequestMessage); //mbr = message buffer and response
	                message.messageContent = new String(byteMessage.messageContent);
	                message.messageName = new String(byteMessage.messageName);
	                   //Integrity verification 
	                    message.messageContent = new String(byteMessage.messageContent);
	                    result = hsv.verify(byteMessage.hashedValue, byteMessage.messageContent ); //Hashing_feature
	                    if(!result)
	                    {
	                        System.out.println("Integrity verification failure");
	                    }
	                    System.out.println("Integrity verification Passed");

	                   //Decrypted messageContent
	                    byteMessage.messageContent = ed.decrypt(byteMessage.messageContent, secretKey);
	                    message.messageContent = new String(byteMessage.messageContent);
	                    System.out.println("Decryption happened: "+ message.messageContent );
//	                    message.messageName = new String(byteMessage.messageName);

	                Global.receiverComponentQueue.put(message);

	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
	
	
	

		public static void main(String[] args) throws InterruptedException {
			
	A a=new A();
	a.start();
	    synchronized(a)
	    {System.out.print("Thread 1:Package sent \n");
	    a.wait();
	    
	    System.out.print("Thread 2: Main Thread got notified");
	    }

		}

	}