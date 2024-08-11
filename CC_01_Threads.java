package ccc;


public class CC_01_Threads extends Thread {

	private int i;
	private int T;

	public CC_01_Threads (int i, int T) {
	    this.i = i;
	    this.T=T;
	}
	@Override
	public void run(){
			System.out.println("Soy el hilo: " + i);
			try {
				CC_01_Threads.sleep(T);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("El hilo " + i + " ha terminado");
		
	}
		
	
		
	public static void main(String[] args) throws InterruptedException {
		int N = 4;
		Thread[] h = new Thread[N];
		int T = 1000;
		for (int i=0; i<N; i++) {
		   h[i]= new CC_01_Threads(i, T);
		   h[i].start();
		  
		}
		//espera a que todos acaben para seguir con la ejecución
		for (int i=0; i<N; i++) {
			h[i].join();
		}
		System.out.println("Todos los hilos han acabado");
		
		}
		
		
}



