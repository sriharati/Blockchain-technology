
public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
					      class thread {
							   boolean flag = false;

							   public synchronized void sender(String msg) {
							      if (flag) {
							         try {
							            wait();
							         } catch (InterruptedException e) {
							            e.printStackTrace();
							         }
							      }
							      System.out.println(msg);
							      flag = true;
							      notify();
							   }

							   public synchronized void receiver(String msg) {
							      if (!flag) {
							         try {
							            wait();
							         } catch (InterruptedException e) {
							            e.printStackTrace();
							         }
							      }

							      System.out.println(msg);
							      flag = false;
							      notify();
							   }
							}

							class T1 implements Runnable {
							   thread m;
							   String[] s1 = { "Thread 1 : Hello Ive sent the shipment" };

							   public T1(thread m1) {
							      this.m = m1;
							      new Thread(this, "Sender").start();
							   }

							   public void run() {
							      for (int i = 0; i < s1.length; i++) {
							         m.sender(s1[i]);
							      }
							   }
							}

							class Thread2 implements Runnable {
							   thread m;
							   String[] s2 = { "Thread 2 : Hey! Ive received the shipment" };

							   public Thread2(thread m2) {
							      this.m = m2;
							      new Thread(this, "Receiver").start();
							   }

							   public void run() {
							      for (int i = 0; i < s2.length; i++) {
							         m.receiver(s2[i]);
							      }
							   }
							}
							
							
							class Thread3 implements Runnable {
								   thread m;
							   String[] s3 = { "Thread 3: I did received it" };
							 public Thread3(thread m3) {
								      this.m = m3;
								      new Thread(this, "Sender").start();
							 }

								   public void run() {
								      for (int i = 0; i < s3.length; i++) {
								         m.sender(s3[i]);
								      }
								   }
							 }
							
							
							
							
							

					        

	}

}
