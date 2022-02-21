import java.security.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AMC_TD_SEncryption_Hashing {

	public static void main(String args[]) throws Exception {
		// TODO Auto-generated method stub
		
		KeyGenerator keygen = KeyGenerator.getInstance("DES");
        SecretKey secretKey = keygen.generateKey();
        
	     
	     
      //Parameters
  
        Global.input = 1;   //programmer will decide input

        Global.queueSize=25;  //program will decide queueSize
        setSize setside= new setSize();
        setside.setSize(Global.input,Global.queueSize);
        SenderComponent senderComponent = new SenderComponent(secretKey);
        SecureSenderConnector.aSecureSenderConnector();

        SecureReceiverConnector.aSecureReceiverConnector();

        ReceiverComponent receiverComponent = new ReceiverComponent(secretKey);
        senderComponent.t_senderComponent.join();
        SecureSenderConnector.t_SecuritySenderCoordinator.join();
        SecureSenderConnector.t_AsynchronousMCSender.join();
        SecureReceiverConnector.t_SecurityReceiverCoordinator.join();
        SecureReceiverConnector.t_AsynchronousMCReceiver.join();
        receiverComponent.t_receiverComponent.join();
}
}

class setSize{
    Global global = new Global();

   public void setSize(int a,int b) {

        Global.senderComponentQueue = new MessageQueue(b);
        Global.q2 = new ByteMessageQueue(b);
        Global.q3 = new ByteMessageQueue(b);
        Global.q4 = new ByteMessageQueue(b);
        Global.receiverComponentQueue = new MessageQueue(b);
    }
}

class SenderComponent  implements Runnable {
    Thread t_senderComponent;
    SecretKey secretKey;
    SenderComponent(SecretKey sk)    {
        t_senderComponent = new Thread(this, "SenderComponent");
        t_senderComponent.start();
        this.secretKey = sk;
    }
    
    
    
    public void run()
    {
        int i = 0;

        Message message = new Message();
        while(i<Global.input)
        {
            i++;
            message.messageName = new String("ConfirmShipment "+ i);
            message.messageContent = new String("ShipmentConfirmation " + i);

            message.secretKey = secretKey;
            System.out.println("SenderComponent: messageName = " + message.messageName);
            System.out.println("SenderComponent: messageContent = " + message.messageContent + "\n");

            Global.senderComponentQueue.put(message);
            
          
            
        }
    }
}

class ReceiverComponent implements Runnable
{
    Thread t_receiverComponent;

    SecretKey secretKey;
    public ReceiverComponent(SecretKey sk)
    {
        t_receiverComponent = new Thread(this, "ReceiverComponent");
        t_receiverComponent.start();
        this.secretKey = sk;
        
      
    }

      
    
    public void run()
    {
        int i = 0;
        Message message = new Message();
        KeyRequestMessage keyRequestMessage = new KeyRequestMessage();

        while(i<Global.input)
        {
            i++;


            keyRequestMessage = Global.mbrSecretKey.receive();

            if (keyRequestMessage.messageName.equals("Request Key"))
            {
                Global.mbrSecretKey.reply(secretKey);
            }


            message = Global.receiverComponentQueue.get();
            
            System.out.println("ReceiverComponent: messageName = " + message.messageName);
            System.out.println("ReceiverComponent: messageContent = " + message.messageContent + "\n");
            
          
           


}

	
    }
}



