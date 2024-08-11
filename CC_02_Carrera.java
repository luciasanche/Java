package ccc;

class Hilo1 extends Thread{
	int N;
	public Hilo1 (int N) {
	   this.N = N;
	}
    @Override
	public void run(){
      for(int i=0; i<N; i++) { // hace N incrementos
    	  CC_02_Carrera.n++; //incrementa el valor n de la clase CC_02_Carrera
   	  }
	}
}
class Hilo2 extends Thread{
	int N;
	public Hilo2 (int N) {
	    this.N = N;   
	}
	@Override
    public void run(){
	  for(int i=0; i<N; i++) { //hace N decrementos
		  CC_02_Carrera.n--; //decrementa el valor n de la clase CC_02_Carrera
	  }
		
	}
}

public class CC_02_Carrera {
	public static int n = 0;
	
	public static void main(String[] args) throws InterruptedException {
	 int M = 100;
	 int N = 5;
	 Hilo1[] h1 = new Hilo1[M];
	 Hilo2[] h2 = new Hilo2[M];
	 for (int i=0; i<M;i++) {
		 h1[i] = new Hilo1(N);
		 h2[i] = new Hilo2(N);
		 h1[i].start();
		 h2[i].start();
	 }
	 for (int i=0; i<M;i++) {
		 h1[i].join();
		 h2[i].join();
	 }
	 System.out.println(n);
	 
	}
}

