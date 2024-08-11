package ccc;

import es.upm.babel.cclib.Semaphore;

class CC_04_MutexSem {
   private static int N_THREADS = 2;
   private static int N_PASOS = 1000000;
   static Semaphore sem = new Semaphore(1);

   static class Contador {
      private volatile int n;
      public Contador() {
         this.n = 0;
      }
      public int valorContador() {
         return this.n;
      }
      public void inc () {
         this.n++;
      }
      public void dec () {
         this.n--;
      }
   }	

   static class Incrementador extends Thread {
      private Contador cont;
      public Incrementador (Contador c) {
         this.cont = c;
      }
      public void run() {
         for (int i = 0; i < N_PASOS; i++) {
             sem.await();
             System.out.println("inc");
        	 this.cont.inc();
        	 sem.signal();
         }
      }
   }
   
   static class Decrementador extends Thread {
      private Contador cont;
      public Decrementador (Contador c) {
         this.cont = c;
      }
      public void run() {
         for (int i = 0; i < N_PASOS; i++) {
             sem.await();
             System.out.println("dec");
        	 this.cont.dec();
        	 sem.signal();
         }
      }
   }
   
   public static void main(String args[])
   {
      // Creaci�n del objeto compartido
      Contador cont = new Contador();
      
      // Creaci�n de los arrays que contendr�n los threads
      Incrementador[] tInc =
         new Incrementador[N_THREADS];
      Decrementador[] tDec =
         new Decrementador[N_THREADS];
      
      // Creacion de los objetos threads
      for (int i = 0; i < N_THREADS; i++) {
         tInc[i] = new Incrementador(cont);
         tDec[i] = new Decrementador(cont);
      }
      
      // Lanzamiento de los threads
      for (int i = 0; i < N_THREADS; i++) {
         tInc[i].start();
         tDec[i].start();
      }
      
      // Espera hasta la terminacion de los threads
      try {
         for (int i = 0; i < N_THREADS; i++) {
            tInc[i].join();
            tDec[i].join();
         }
      } catch (Exception ex) {
         ex.printStackTrace();
         System.exit (-1);
      }
      
      // Simplemente se muestra el valor final de la variable:
      System.out.println(cont.valorContador());
      System.exit (0);
   }
}