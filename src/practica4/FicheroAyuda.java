package practica4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 
 * @author Jose Pérez basado en Juan Carlos Rodríguez
 *
 */
public class FicheroAyuda {
	
    public static class ExcepcionFichero extends RuntimeException {
    	/**
    	 * Constructor de ExcepcionFichero.
    	 * @param e RuntimeException
    	 */
        public ExcepcionFichero(Exception e) {
            super(e);
        }
        /**
    	 * Constructor de ExcepcionFichero.
         * @param r Ristra identificativa
         */
        public ExcepcionFichero(String r) {
            super(r);
        }
    }
    
    private int numeroAdjuntos; //Número de datos enteros añadidos
    private int listaVacías;    //Lista de páginas vacías
    private int desplazamiento; //Inicio de datos cliente
    private int tamañoPágina;   //Tamaño de cada página
    private int adjuntos[];     //Datos adjuntos al fichero
    private RandomAccessFile fichero;
    private String nombre;
    public static final int dirNula = -1;

    
    /**
     * Crea un fichero con adjuntos y lo asocia al objeto actual.
     * Puede producir ExcepcionFichero.
     * @param nombre Ruta del fichero
     * @param lp Tamaño de página = número de bytes de los registros
     * @param adj Número de adjuntos, cada uno de los cuales será un int, numerados de 0 a adj-1.
     */
    public void crear(String nombre, int lp, int adj){
        cerrar();
        this.nombre=nombre;
        File manejador = new File(nombre);
        if (manejador.exists())
            if(!manejador.delete())
                throw new ExcepcionFichero("El fichero no se ha podido borrar");
        try {
            fichero = new RandomAccessFile(nombre, "rw");
        } catch (FileNotFoundException e) {
            throw new ExcepcionFichero(e);
        }
        tamañoPágina=lp;
        numeroAdjuntos=adj;
        listaVacías=dirNula;
        desplazamiento=(3+numeroAdjuntos)*Conversor.INTBYTES;
        posicionarInterno(0);
        escribirInterno(Conversor.aByte(tamañoPágina));
        escribirInterno(Conversor.aByte(numeroAdjuntos));
        escribirInterno(Conversor.aByte(listaVacías));
        if(numeroAdjuntos>0) { //Escribimos los adjuntos en el fichero
           adjuntos = new int[numeroAdjuntos];
           for(int i=0;  i<numeroAdjuntos;i++){
               adjuntos[i]=0;
               escribirInterno(Conversor.aByte(0));
           }
        }
    }
    /**
     * Crea un fichero sin adjuntos y lo asocia al objeto actual.
     * Puede producir ExcepcionFichero.
     * @param nombre Ruta del fichero
     * @param lp Tamaño de página = número de bytes de los registros
     */
    public void crear(String nombre, int lp){
        crear(nombre,lp,0);
    }
    /**
     * Abre un fichero de páginas reutilizables que puede tener adjuntos y lo asocia al objeto actual.
     * Puede producir ExcepcionFichero.
     * @param nombre Ruta del fichero
     */
    public void abrir(String nombre){
        cerrar();
        this.nombre=nombre;
        try {
            fichero = new RandomAccessFile(nombre, "rw");
        } catch (FileNotFoundException e) {
            throw new ExcepcionFichero(e);
        }
        
        if(tamañoByte()<Conversor.INTBYTES*2)
            throw new ExcepcionFichero("Error al abrir el fichero");
        posicionarInterno(0);
        tamañoPágina=Conversor.aInt(leerInterno(Conversor.INTBYTES));
        numeroAdjuntos=Conversor.aInt(leerInterno(Conversor.INTBYTES));
        listaVacías=Conversor.aInt(leerInterno(Conversor.INTBYTES));
        if(numeroAdjuntos>0) {
            adjuntos = new int[numeroAdjuntos];
            for(int i=0;  i<numeroAdjuntos;i++){
                adjuntos[i]=Conversor.aInt(leerInterno(Conversor.INTBYTES));
            }
        }
        desplazamiento=(3+numeroAdjuntos)*Conversor.INTBYTES;
    }
    /**
     * Cierra el fichero en uso por el objeto actual.
     * Puede producir ExcepcionFichero.
     */
    public void cerrar() {
        try {
            if(fichero!=null){
               fichero.close();
               fichero=null;
            }
        } catch (IOException e) {
            throw new ExcepcionFichero(e);
        }
    }
    /**
     * Reserva una página para un registro, reutilizando las liberadas.
     * Puede producir ExcepcionFichero.
     * @return La dirección de la página, si no se produce excepción
     */
    public int tomarPágina() {
        if(listaVacías==-1) 
            return (int)tamaño();
         int pnueva=listaVacías;
         posicionar(listaVacías);
         try {
        	 listaVacías=fichero.readInt();
         } catch (IOException e) {
             throw new ExcepcionFichero(e);
         }
         posicionarInterno(2*Conversor.INTBYTES);
         escribirInterno(Conversor.aByte(listaVacías));
         return pnueva;
    }
    /**
     * Declara una página como libre para ser reutilizada.
     * Puede producir ExcepcionFichero.
     * @param pos Dirección de la página a liberar
     */
    public void liberarPágina(int pos) {
        posicionar(pos);
        escribirInterno(Conversor.aByte(listaVacías));
        listaVacías=pos;
        posicionarInterno(2*Conversor.INTBYTES);
        escribirInterno(Conversor.aByte(listaVacías));
    }
    /**
     * Proporciona el valor de un adjunto: Los adjuntos se numeran de 0 a numeroAdjuntos-1.
     * @param pos Número del adjunto que se desea
     * @return Valor actual del adjunto
     */
    public int  adjunto(int pos){
        return adjuntos[pos];
    }
    /**
     * Modifica el valor de un adjunto: Los adjuntos se numeran de 0 a numeroAdjuntos-1
     * @param pos Número del adjunto que se desea modificar
     * @param valor Nuevo valor del adjunto
     */
    public void adjunto(int pos, int valor){
        adjuntos[pos]=valor;
        posicionarInterno((3+pos)*Conversor.INTBYTES);
        escribirInterno(Conversor.aByte(valor));
    }
    /**
     * Lee una página del fichero.
     * Puede producir ExcepcionFichero.
     * @param pos Número de la página a leer
     * @return Contenido de la pagina en bytes
     */
    public byte[] leer(int pos){
        posicionar(pos);
        return leerInterno(tamañoPágina);
    }
    /**
     * Escribe una página en el fichero.
     * Debe estar reservada previamente.
     * Puede producir ExcepcionFichero.
     * @param dato Contenido de la página en bytes
     * @param pos Posición de la página a escribir
     */
    public void escribir(byte[] dato, int pos){
        if(dato.length>tamañoPágina)
            throw new ExcepcionFichero("Intento de almacenar un dato mayor del permitido"); 
        posicionar(pos);
        if(dato.length < tamañoPágina) {
            byte[] nuevoDato= new byte[tamañoPágina];
            for(int i=0; i<dato.length; i++)
                nuevoDato[i]=dato[i];
            dato=nuevoDato;
        }    
        escribirInterno(dato);
    }
    /**
     * Sitúa el indicador de posición en la página indicada.
     * Puede producir ExcepcionFichero.
     * @param pos Registro que se desea leer/escribir
     */
    public void posicionar(int pos) {
        posicionarInterno(desplazamiento+((long)pos)*tamañoPágina);
    }
    /**
     * Devuelve el tamaño del fichero medido en número de registros.
     * Puede producir ExcepcionFichero.
     * @return Número de páginas del fichero
     */
    public long tamaño(){
        try {
        	return (fichero.length()-desplazamiento)/tamañoPágina;
        } catch (IOException e) {
            throw new ExcepcionFichero(e);
        }
    }
    /**
     * Devuelve la ruta del fichero asociado 
     * @return Nombre del fichero asociado
     */
    public String nombre(){
    	return nombre;
    }
    
//  Realizan las operaciones correspondientes sobre el fichero
    private void posicionarInterno(long pos) {
        try {
            fichero.seek(pos);
        } catch (IOException e) {
            throw new ExcepcionFichero(e);
        }
    }
    private byte[] leerInterno(int lon){
        try {
            byte datos[]= new byte[lon];
            //long tama = fichero.length();
            //long pos = fichero.getFilePointer();
            int leido=fichero.read(datos);
            if(leido != lon)
              throw new ExcepcionFichero("Error en lectura de un fichero");     
            return datos;
        } catch (IOException e) {
            throw new ExcepcionFichero(e);
        }
    }
    private void escribirInterno(byte []dato){
        try {
            fichero.write(dato);
        } catch (IOException e) {
            throw new ExcepcionFichero(e);
        }
    }
    private long tamañoByte() {
        try {
            return fichero.length();
        } catch (IOException e) {
            throw new ExcepcionFichero(e);
        }
    }
}
