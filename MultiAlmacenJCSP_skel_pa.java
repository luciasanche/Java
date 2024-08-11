package ccc;

// MultiAlmacenJCSP_skel2.java
// Esqueleto de código para peticiones aplazadas.
// Julio Mariño, 2014
import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.MultiAlmacen;

import java.util.LinkedList;
import java.util.Queue;

// importamos la librería JCSP
import org.jcsp.lang.*;

class MultiAlmacenJCSP implements MultiAlmacen, CSProcess {

	// Canales para enviar y recibir peticiones al/del servidor
	private final Any2OneChannel chAlmacenar = Channel.any2one();
	private final Any2OneChannel chExtraer = Channel.any2one();
	private int TAM;
	private AltingChannelInput petExtraer;
	private AltingChannelInput petAlmacenar;
	private One2OneChannel chResp = Channel.one2one();
	private Queue<Producto> cola = new LinkedList<Producto>();
	private Queue<Producto> esperanA = new LinkedList<Producto>();
	private Queue<ChannelOutput> esperanE = new LinkedList<ChannelOutput>();

	// Para evitar la construcción de almacenes sin inicializar la
	// capacidad
	private MultiAlmacenJCSP() {
	}

	public MultiAlmacenJCSP(int n) {
		this.TAM = n;

		// COMPLETAR: inicialización de otros atributos
		petExtraer = chExtraer.in();
		petAlmacenar = chAlmacenar.in();
	}

	public void almacenar(Producto[] productos) {

		// COMPLETAR: comunicación con el servidor
		for (int i = 0; i < productos.length; i++) {
			((ChannelOutput) petAlmacenar).write(productos[i]);
		}
	}

	public Producto[] extraer(int n) {
		Producto[] result = new Producto[n];

		// COMPLETAR: comunicación con el servidor
		for (int i = 0; i < n; i++) {
			((ChannelOutput) petExtraer).write(chResp.out());
			result[i] = (Producto) chResp.in().read();
		}
		return result;
	}

	// codigo del servidor
	private static final int ALMACENAR = 0;
	private static final int EXTRAER = 1;

	public void run() {
		// COMPLETAR: declaracion de canales y estructuras auxiliares

		
		final boolean[] sincCond = new boolean [2]; 
		ChannelOutput resp;
		Producto item;
		
		Guard[] entradas = { chAlmacenar.in(), chExtraer.in() };
		Alternative servicios = new Alternative(entradas);
		int choice = 0;

		while (true) {
			try {
				choice = servicios.fairSelect();
			} catch (ProcessInterruptedException e) {
				
			}
			switch (choice) {
			case ALMACENAR:
				// COMPLETAR: tratar/guardar la petición
				item = (Producto) ((ChannelInput) entradas[0]).read();
				if(cola.size()>=TAM) {
					esperanA.add(item);
				}
				cola.add(item);
				
				break;
			case EXTRAER:
				// COMPLETAR: tratar/guardar la petición
				resp = (ChannelOutput) ((ChannelInput) entradas[1]).read();
				if(cola.size()<=0) {
					esperanE.add(resp);
				}
				resp.write(cola.peek());
				cola.poll();
			
				break;
			}
			// COMPLETAR: atención de peticiones pendientes
			for (int i=0; i<esperanA.size(); i++) {
				if(cola.size()<TAM) {
					cola.add(esperanA.peek());
					esperanA.poll();
				}
			}
			for (int j=0; j<esperanE.size();j++) {
				if(cola.size()>0) {
					esperanE.peek().write(cola.peek());
					cola.poll();
				}
			}

			// OJO!! no debemos volver al comienzo del
			// while si hay peticiones pendientes
		}
	}
}
