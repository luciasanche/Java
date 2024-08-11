package superheroes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.cj.xdevapi.PreparableStatement;



public class SuperheroesDatabase {
	private Connection conn = null;
	public SuperheroesDatabase() {
	}

	//--------------Sesion 1---------------------------
	/*
    Metodo que abre la conexion.
    Si la consigue abrir devuelve true. De lo contrario
    devuelve false.
	 */
	public boolean openConnection() {
		try {
			if(conn == null || conn.isClosed()) {
				String serverAddress = "localhost:3306";
				String db = "superheroes";
				String user = "superheroes_user";
				String pass = "superheroes_pass";
				String url = "jdbc:mysql://" + serverAddress + "/" + db;
				conn = DriverManager.getConnection(url,user,pass);
				return true;
			} else
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}


	}

	/*
	    Metodo que cierra la conexion. De poder hacerlo,
	    devuelve true. De lo contrario devuelve false.
	 */
	public boolean closeConnection() {
		try {
			if(conn == null || conn.isClosed()) {
				return true;
			}
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	/*
	  Metodo que crea una tabla llamada escena.
	   Si la puede crear devuelve true y si no devuelve false.
	 */
	public boolean createTableEscena() {
		Statement st = null;
		boolean aux = false;
		try {
			openConnection();
			st = conn.createStatement();
			st.executeUpdate("create table escena ( id_pelicula INT NOT NULL, n_orden INT, titulo VARCHAR(100), duracion INT,PRIMARY KEY (id_pelicula, n_orden), FOREIGN KEY (id_pelicula) REFERENCES pelicula (id_pelicula) ON DELETE CASCADE ON UPDATE CASCADE )");
			aux = true;
		} catch (SQLException e) {
		}
		try {	
			if(st!=null) {st.close();}
		} catch (SQLException e) {
		}
		return aux;
	}

	/* Metodo de crea una tabla llamada rival.
	  Si la puede crear devuelve true y si no devuelve false.
	 */
	public boolean createTableRival() {
		Statement st = null;
		boolean aux = false;
		try {
			openConnection();
			st = conn.createStatement();
			st.executeUpdate("create table rival (id_sup INT, id_villano INT, fecha_primer_encuentro DATE, PRIMARY KEY (id_sup, id_villano), FOREIGN KEY (id_sup) REFERENCES superheroe (id_sup) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY (id_villano) REFERENCES villano (id_villano) ON DELETE CASCADE ON UPDATE CASCADE)");
			aux = true;

		}catch(SQLException e) {

		}
		try {

			if(st!=null) {st.close();}

		} catch (SQLException e) {

		}
		return aux;

	}

	//--------------Sesion 2--------------------------
	/*
	  Metodo que toma las escenas que figuran en un fichero dado por parametro y las introduce
	  en la tabla escena. Tratatando cada insercion como una transaccion separada al resto.
	  Devuelve el numero de elementos insertados en la tabla.
	 */
	public int loadEscenas(String fileName) {
		openConnection();
		int num = 0;
		BufferedReader buf = null;
		PreparedStatement pst = null;
		try {
			buf = new BufferedReader(new FileReader(fileName));
			String line = null;
			String query = "INSERT INTO escena(id_pelicula,n_orden,titulo,duracion) VALUES (?,?,?,?);";
			while( (line = buf.readLine()) != null) {
				String[] columnas = line.split(";");
				pst = conn.prepareStatement(query);
				try {  
					for( int i = 0; i < columnas.length; i++ ) {
						pst.setString(i+1, columnas[i]);
					}
					num += pst.executeUpdate();
				} catch ( Exception e ) {
					System.out.println(e.getMessage());
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
		try {
			if(pst !=null) {pst.close();};
			if(buf!=null) {buf.close();};
		} catch (IOException e) {

		} catch (SQLException e) {

		}
		return num;
	}

	/*
	  Metodo que carga los datos de la tabla protagoniza que figuran en un fichero dado por parametro
	  tratando todo como una sola operacion. Devuelve el numero de elementos insertados en la base de datos
	 */
	public int loadProtagoniza(String fileName) {
		openConnection();
		int num = 0;
		BufferedReader buf = null;
		PreparedStatement pst = null;
		try {
			conn.setAutoCommit(false);
			buf = new BufferedReader(new FileReader(fileName));
			String line = null;
			String query = "INSERT INTO protagoniza(id_sup, id_villano, id_pelicula) VALUES (?,?,?);";
			while ( (line = buf.readLine()) != null ) {
				String[] columnas = line.split(";");
				pst = conn.prepareStatement(query);
				for( int i = 0; i < columnas.length; i++ ) {
					pst.setString(i+1, columnas[i]);
				}
				num += pst.executeUpdate();
				num += check(columnas[0],columnas[1]);
			}
			conn.commit();
		}catch ( Exception e ) {
			try {
				conn.rollback();
				num = 0;
			} catch (SQLException e1) {

			}  
		}try {
			if(pst !=null) {pst.close();};
			if(buf!=null) {buf.close();};
		} catch (IOException e) {

		} catch (SQLException e) {

		}
		return num;
	}

	/*
	  Metodo auxiliar check usado en loadProtagoniza para comprobar si un superheroe y un villano 
	  ya son rivales.
	 */
	private int check(String id_sup, String id_villano) {
		int aux = 0;
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			String query = "select * from rival where id_sup = ? and id_villano = ?;";
			st = conn.prepareStatement(query);
			st.setString(1,id_sup);
			st.setString(2, id_villano);
			rs = st.executeQuery();
			rs.next();
			if(!rs.next()) {
				st.close();
				st = conn.prepareStatement("INSERT INTO rival VALUES(?,?,null)");
				st.setString(1,id_sup);
				st.setString(2, id_villano);
				st.executeUpdate();
				aux = 1;
			}

		} catch (SQLException e) {

		}
		try {
			if(st !=null) {st.close();};
			if(rs!=null) {rs.close();};
		} catch (SQLException e) {

		}
		return aux;
	}


	//-----------------Sesion 3-----------------------------

	/*
	  Metodo que devuelve una lista de todas las peliculas de la base de datos.
	 */

	public String catalogo() {
		openConnection();
		String res = "{";
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery("select titulo from pelicula order by titulo;");
			if(!rs.next()) {
				res = "{}";
			}else {
				res += rs.getString(1);
				while (rs.next()) {
					res +=  ", " + rs.getString(1);
				}
				res += "}";
			}

		} catch (SQLException e) {
			res = null;
		}
		try {
			if(st !=null) {st.close();};
			if(rs!=null) {rs.close();};
		} catch (SQLException e) {

		}
		return res;
	}

	/*
	  Metodo que, dada una pelicula por parametro, devuelve su duracion.
	 */
	public int duracionPelicula(String nombrePelicula) {
		openConnection();
		PreparedStatement st = null;
		ResultSet rs = null;
		int res = -2;
		try {
			String query1 = "select id_pelicula from pelicula where titulo = ?;";
			st = conn.prepareStatement(query1);
			st.setString(1,nombrePelicula);
			rs = st.executeQuery();
			if(!rs.next()) {
				res = -1; 
			} else {
				int id = rs.getInt(1);
				String query = "select sum(duracion) from escena where id_pelicula = ?;";
				st.close();
				rs.close();
				st = conn.prepareStatement(query);
				st.setInt(1, id);
				rs = st.executeQuery();
				if (!rs.next()) {
					res = 0;
				} else {
					res = rs.getInt(1);
				}
			}

		} catch (SQLException e) {
			res = -2;
		}
		try {
			if(st !=null) {st.close();};
			if(rs!=null) {rs.close();};
		} catch (SQLException e) {

		}
		return res;

	}

	/*
	  Metodo que devuelve una lista de todas las escenas en las que aparece
	  un villano dado por parametro.
	 */
	public String getEscenas(String nombreVillano) {
		openConnection();
		String list = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			prep = conn.prepareStatement("select escena.titulo from escena, villano, protagoniza, pelicula where "
					+ "villano.nombre = ? and villano.id_villano = protagoniza.id_villano and "
					+ "pelicula.id_pelicula = protagoniza.id_pelicula and pelicula.id_pelicula = escena.id_pelicula "
					+ "group by escena.titulo order by pelicula.titulo asc, escena.n_orden asc;");
			prep.setString(1, nombreVillano);
			rs = prep.executeQuery();

			list = (rs.next())? "{" + rs.getString(1): "{";

			while( rs.next() ) {
				list += ", " + rs.getString(1);
			}

			list += "}";

		}catch(Exception e) {
			System.out.println(e.getMessage());
			list = null;
		}
		try {
			if(prep !=null) {prep.close();};
			if(rs!=null) {rs.close();};
		} catch (SQLException e) {

		}
		return list;
	}

	/*
	  Metodo que mete el avatar de una persona dada por parametro y lo guarda en la direccion dada por parametro
	  Devuelve true si la imagen se encuentra en la base de datos y false si no existe
	 */
	public boolean desenmascara(String nombre, String apellido, String filename) {
		openConnection();
		PreparedStatement st = null;
		ResultSet rs = null;
		FileOutputStream fos = null;
		boolean res = false;
		String query = "select avatar from superheroe where id_persona = (select id_persona from persona_real where nombre = ? and apellido = ?);";
		try {
			st = conn.prepareStatement(query);
			st.setString(1, nombre);
			st.setString(2, apellido);
			rs = st.executeQuery();
			Blob bl;
			if(rs.next() && (bl =rs.getBlob(1))!= null) {
				fos = new FileOutputStream(filename);
				fos.write(bl.getBytes(1, (int) bl.length()));
				fos.close();
				st.close();
				rs.close();
				res = true;
			} else 
				res = false;
		} catch (SQLException e) {
			res = false;
		} catch (FileNotFoundException e) {
			res = false;
		} catch (IOException e) {
			res = false;
		}
		try {
			if(fos!=null) {fos.close();};
			if(st !=null) {st.close();};
			if(rs!=null) {rs.close();};
		} catch (SQLException e) {

		} catch (IOException e) {

		}
		return res;
	}

}