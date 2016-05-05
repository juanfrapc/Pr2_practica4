package practica4;

import java.util.LinkedList;
import java.util.Stack;

/**
 * Clase para la gestión de un arbol B en memoria secundaria. Persistente.
 */
public class ContenedorDeEnteros {

    // Atributos

    int raiz; // Dirección de la raiz.
    int numElem; // Numero de datos en el árbol
    int orden; // orden del arbol B
    int tamañoDatos = 4; // tamaño de cada dato almacenado.
    String nombreFichero; // Fichero donde se almacenan los nodos
    FicheroAyuda fichero = new FicheroAyuda();

    LinkedList<InfoPila> cola = new LinkedList<>();
    int minimoClaves;

    /**
     * Clase auxiliar nodo que contiene la información de enlaces y valores de
     * clave.
     */
    protected class Nodo {

        // Atributos

        private final int[] clavei;
        private final int[] enlacei;
        private int numElei;
        private int direccioni;

        /**
         * Constructor por defecto, inicializa los valores del nodo.
         */
        Nodo() {
            direccioni = FicheroAyuda.dirNula;
            numElei = 0;
            clavei = new int[orden];
            enlacei = new int[orden + 1];
        }

        /**
         * Devuelve el tamaño del nodo.
         * @return int - tamaño del nodo
         */
        private int tamaño() {
            int tam = 2 * Conversor.INTBYTES;
            tam += (orden - 1) * tamañoDatos;
            tam += orden * Conversor.INTBYTES;
            return tam;
        }

        /**
         * Devuelve el valor de clave según la posición indicada.
         * @param i posición de la clave en el nodo
         * @return valor de clave
         */
        private int clave(int i) {
            return clavei[i - 1];
        }

        /**
         * Asigna valor de clave según posición indicada
         *
         * @param i posición de la clave en el nodo
         * @param d valor de clave a modificar
         */
        private void clave(int i, int d) {
            clavei[i - 1] = d;
        }

        /**
         * Devuelve el enlace según posición indicada
         *
         * @param i posición en el nodo
         * @return valor de enlace
         */
        private int enlace(int i) {
            return enlacei[i];
        }

        /**
         * Modifica o establece el valor de enlace según posición indicada
         *
         * @param i posición en el nodo
         * @param d valor del enlace a modificar
         */
        private void enlace(int i, int d) throws Exception {
            enlacei[i] = d;
        }

        /**
         * Devuelve la dirección donde se almacena
         *
         * @return int
         */
        private int direccion() {
            return direccioni;
        }

        /**
         * Modifica o establece la dirección donde se almacena
         *
         * @param d - int
         */
        private void direccion(int d) {
            direccioni = d;
        }

        /**
         * Número de datos almacenados en el nodo.
         *
         * @return int
         */
        public int cardinal() {
            return numElei;
        }

        /**
         * Establece o modifica el número de datos almacenados
         *
         * @param n - int
         */
        private void cardinal(int n) {
            numElei = n;
        }

        /**
         * @param datos Vector de bytes que hemos leído del fichero Al leer de
         * un fichero, leemos vectores de bytes. Con este método pasamos ese
         * vector a un Nodo.
         */
        private void deByte(byte[] datos) {
            int leb = Conversor.INTBYTES;
            direccion(Conversor.aInt(Conversor.toma(datos, 0, leb)));
            numElei = Conversor.aInt(Conversor.toma(datos, leb, leb));
            int baseClaves = leb * 2;
            int baseEnlaces = baseClaves + (numElei) * tamañoDatos;

            for (int i = 0; i < numElei; i++) {
                clavei[i] = Conversor.aInt(Conversor.toma(datos, baseClaves
                        + i * tamañoDatos, tamañoDatos));
            }

            for (int i = 0; i <= numElei; i++) {
                byte[] dato = Conversor.toma(datos,
                        baseEnlaces + i * leb, leb);
                enlacei[i] = Conversor.aInt(dato);
            }
        }

        /**
         * Este método traduce un Nodo en un vector de bytes, que se usará
         * posteriormente para escribirlo en el fichero.
         *
         * @return byte[]
         */
        byte[] abyte() {
            int tam = tamaño();
            byte[] res = new byte[tam];
            int pos = 0;
            pos = Conversor.añade(res, Conversor.aByte(direccioni), pos);
            pos = Conversor.añade(res, Conversor.aByte(numElei), pos);

            for (int i = 0; i < numElei; i++) {
                pos = Conversor.añade(res, Conversor.aByte(clavei[i]), pos);
            }

            for (int i = 0; i <= numElei; i++) {
                pos = Conversor.añade(res, Conversor.aByte(enlacei[i]), pos);
            }
            return res;
        }

        /**
         * Inserta una clave en la posición especificada.
         * @param e
         * @param dir
         * @param pos
         */
        void insertar(int e, int dir, int pos) throws Exception {
            numElei++;
            for (int i = numElei - 1; i >= pos; i--) {
                clave(i + 1, clave(i));
                enlace(i + 1, enlace(i));
            }
            clave(pos, e);
            enlace(pos, dir);
        }

        /**
         * Extrae una clave de la posición especificada.
         * @param pos
         */
        void extraer(int pos) throws Exception {
            for (int i = pos; i < numElei; i++) {
                clave(i, clave(i + 1));
                enlace(i, enlace(i + 1));
            }
            numElei--;
        }

        /**
         * Busca la clave pasada por parámetro.
         * @param e
         * @return true si la clave está, falso si no lo está.
         * @throws java.lang.Exception
         */
        public boolean buscar(int e) throws Exception {
            int prim, ulti, med;
            prim = 1;
            ulti = cardinal();

            while (prim <= ulti) {
                med = (prim + ulti) / 2;
                if (e == clave(med)) {
                    return true;
                }
                if (e < clave(med)) {
                    ulti = med - 1;
                } else {
                    prim = med + 1;
                }
            }
            return false;
        }

        /**
         * Devuelve la posición en la que se encuentra la clave
         * @param e
         * @return la posición en que se encuentra la clave.
         */
        private int buscarPos(int e) throws Exception {
            int pos, prim, ulti, med;
            prim = 1;
            ulti = cardinal();

            while (prim <= ulti) {
                med = (prim + ulti) / 2;
                if (e == clave(med)) {
                    pos = med;
                    return pos;
                }
                if (e < clave(med)) {
                    ulti = med - 1;
                } else {
                    prim = med + 1;
                }
            }
            pos = prim - 1;
            return pos;
        }
    }

    /**
     * Crea un fichero asociado al arbol e inicializa el arbol.
     * @param ruta
     * @param Orden
     * @throws java.lang.Exception
     */
    public void crear(String ruta, int Orden) throws Exception {
        cerrar();
        this.orden = Orden;
        if (Orden < 5) {
            throw new Exception("Orden inferior a 5 en"
                    + " árbol B no está permitido");
        }
        Nodo nodo = new Nodo();
        nombreFichero = ruta;
        fichero.crear(nombreFichero, nodo.tamaño(), 4);
        raiz = FicheroAyuda.dirNula;
        numElem = 0;
        minimoClaves = (Orden + 1) / 2 - 1;
        fichero.adjunto(0, raiz);
        fichero.adjunto(1, numElem);
        fichero.adjunto(2, tamañoDatos);
        fichero.adjunto(3, Orden);
    }

    /**
     * Abre el árbol B almacenado en el fichero pasado por parámetro
     * (una String con la ruta del fichero) y lo asocie al objeto.
     * @param ruta
     */
    public void abrir(String ruta) {
        fichero.abrir(ruta);
        raiz = fichero.adjunto(0);
        numElem = fichero.adjunto(1);
        tamañoDatos = fichero.adjunto(2);
        orden = fichero.adjunto(3);
        minimoClaves = (orden + 1) / 2 - 1;
    }

    /**
     * Cierra el fichero asociado al arbol actual.
     */
    public void cerrar() {
        fichero.cerrar();
    }

    /**
     * Devuelve el numero de claves almacenadas en el arbol
     * @return cardinal
     */
    public int cardinal() {
        return numElem;
    }

    /**
     * Inserta la clave n en el arbol
     * @param n
     * @return true si se inserta, false ne otro caso.
     * @throws Exception
     */
    public boolean insertar(int n) throws Exception {
        Stack<InfoPila> pila = new Stack<>();
        if (buscar(n, pila)) {
            return false;
        }
        Nodo nodoActual = new Nodo();
        InfoPila info;
        ParejaInsertar pa = new ParejaInsertar();
        pa.clave = n;
        pa.direccion = FicheroAyuda.dirNula;
        numElem++;
        fichero.adjunto(1, numElem);
        if (!pila.empty()) {
            info = pila.pop();
            nodoActual = info.nodo;
            int pos = info.pos;
            nodoActual.insertar(pa.clave, pa.direccion, pos + 1);
            if (nodoActual.cardinal() < orden) {
                escribir(nodoActual);
                return true;
            }
            while (!pila.empty()) {
                info = pila.pop();
                Nodo der = new Nodo();
                Nodo izq = new Nodo();
                Nodo padre = info.nodo;
                pos = info.pos;
                if (pos > 0) {
                    izq = leer(padre.enlace(pos - 1));
                    if (izq.cardinal() < orden - 1) {
                        rotacionderizq(padre, pos - 1, izq, nodoActual);
                        return true;
                    }
                }
                if (pos < padre.cardinal()) {
                    der = leer(padre.enlace(pos + 1));
                    if (der.cardinal() < orden - 1) {
                        rotacionizqder(padre, pos, nodoActual, der);
                        return true;
                    }
                }
                if (pos == 0) {
                    particion_2_3(padre, pos, nodoActual, der);
                } else {
                    particion_2_3(padre, pos - 1, izq, nodoActual);
                }
                if (padre.cardinal() < orden) {
                    escribir(padre);
                    return true;
                }
                nodoActual = padre;
            }
            pa = particion_1_2(nodoActual);
        }
        nodoActual.cardinal(1);
        nodoActual.enlace(0, raiz);
        nodoActual.clave(1, pa.clave);
        nodoActual.enlace(1, pa.direccion);
        nodoActual.direccion(fichero.tomarPágina());
        raiz = nodoActual.direccion();
        escribir(nodoActual);
        fichero.adjunto(0, raiz);
        return true;
    }

    /**
     * Extrae la clave n del arbol si se encuentra
     * @param n
     * @return true si se extrae, false si no se extrae.
     * @throws Exception
     */
    public boolean extraer(int n) throws Exception {
        Stack<InfoPila> pila = new Stack<>();
        if (!buscar(n, pila)) {
            return false;
        }
        numElem--;
        fichero.adjunto(1, numElem);
        InfoPila info = pila.pop();
        Nodo nodoActual = info.nodo;
        int pos = info.pos;
        if (nodoActual.enlace(0) != FicheroAyuda.dirNula) {
            pila.add(new InfoPila(info.nodo, info.pos));
            int dir = nodoActual.enlace(pos);
            do {
                nodoActual = leer(dir);
                dir = nodoActual.enlace(0);
                if (dir == FicheroAyuda.dirNula) {
                    pos = 1;
                } else {
                    pos = 0;
                }
                cola.addLast(new InfoPila(nodoActual, pos));
            } while (dir != FicheroAyuda.dirNula);
            info = pila.pop();
            info.nodo.clave(info.pos, nodoActual.clave(1));
            escribir(info.nodo);
            pila.add(info);
            while (!cola.isEmpty()) {
                pila.add(cola.getFirst());
                cola.removeFirst();
            }
            info = pila.pop();
            nodoActual = info.nodo;
            pos = info.pos;
        }
        nodoActual.extraer(pos);
        while (nodoActual.cardinal()
                < minimoClaves && nodoActual.direccion() != raiz) {
            Nodo padre, der = new Nodo(), izq = new Nodo();
            info = pila.pop();
            padre = info.nodo;
            pos = info.pos;
            if (pos < padre.cardinal()) {
                der = leer(padre.enlace(pos + 1));
                if (der.cardinal() > minimoClaves) {
                    rotacionderizq(padre, pos, nodoActual, der);
                    return true;
                }
            }
            if (pos > 0) {
                izq = leer(padre.enlace(pos - 1));
                if (izq.cardinal() > minimoClaves) {
                    rotacionizqder(padre, pos - 1, izq, nodoActual);
                    return true;
                }
            }
            if (pos > 0 && pos < padre.cardinal()) {
                recombinacion_3_2(padre, pos, izq, nodoActual, der);
            } else if (pos > 0) {
                recombinacion_2_1(padre, pos - 1, izq, nodoActual);
            } else {
                recombinacion_2_1(padre, pos, nodoActual, der);
            }
            nodoActual = padre;
        }
        if (nodoActual.cardinal() > 0) {
            escribir(nodoActual);
        } else {
            raiz = nodoActual.enlace(0);
            fichero.liberarPágina(nodoActual.direccion());
            fichero.adjunto(0, raiz);
        }
        return true;
    }

    /**
     * Busca la clave n en el arbol
     * @param n
     * @return true si está, flase si no.
     * @throws Exception
     */
    public boolean buscar(int n) throws Exception {
        return buscar(n, new Stack<>());
    }

    /**
     * Vacia y reinicia el arbol y el ficehro asociado.
     * @throws Exception
     */
    public void vaciar() throws Exception {
        fichero.cerrar();
        crear(nombreFichero, orden);
    }

    /**
     * Devuelve un vector con las claves ordenadas segun el recorrido inorden
     * a partir de un nodo
     * @param p nodo inicial
     * @param v vector resultado
     * @param i numero de elementos en el vector v.
     * @return vector con claves en inorden.
     * @throws Exception
     */
    private int recorridoInorden(Nodo p, int[] v, int[] i) throws Exception {
        if (p.enlace(0) == FicheroAyuda.dirNula) {
            for (int j = 1; j <= p.cardinal(); j++) {
                v[i[0]] = p.clave(j);
                i[0]++;
            }
            return i[0];
        } else {
            i[0] = recorridoInorden(leer(p.enlace(0)), v, i);
            for (int j = 1; j <= p.cardinal(); j++) {
                v[i[0]] = p.clave(j);
                i[0]++;
                if (p.enlace(j) != FicheroAyuda.dirNula) {
                    Nodo nodo = leer(p.enlace(j));
                    i[0] = recorridoInorden(nodo, v, i);
                }
            }
            return i[0];
        }
    }

    /**
     * Devuelve un vector de claves del arbol ordenadas
     * @return vector ordenado.
     * @throws Exception
     */
    public int[] elementos() throws Exception {
        if (numElem == 0) {
            return new int[0];
        }
        int[] v = new int[this.cardinal()];
        int[] j = {0};
        Nodo nodo = leer(raiz);
        recorridoInorden(nodo, v, j);
        return v;
    }

    /**
     * Clase auxiliar para insertar.
     */
    private class ParejaInsertar {

        public int clave;
        public int direccion;
    }

    /**
     * Clase privada para la busqueda
     * @param e
     * @param pila
     * @return true si está, falso si no.
     * @throws Exception
     */
    private boolean buscar(int e, Stack<InfoPila> pila) throws Exception {
        if (numElem == 0) {
            return false;
        }
        int dirNodo, pos;
        Nodo nodo;
        dirNodo = raiz;
        pila.clear();

        while (dirNodo != FicheroAyuda.dirNula) {
            nodo = leer(dirNodo);
            pos = nodo.buscarPos(e);
            pila.add(new InfoPila(nodo, pos));
            if (nodo.buscar(e)) {
                return true;
            }
            dirNodo = nodo.enlace(pos);
        }
        return false;
    }

    /**
     * Realiza una partición 1/2
     * @param nodo
     * @return Nodo padre creado
     * @throws Exception
     */
    private ParejaInsertar particion_1_2(Nodo nodo) throws Exception {
        ParejaInsertar pa = new ParejaInsertar();
        Nodo nuevoNodo = new Nodo();
        int ncnuevo = orden / 2;
        int ncnodo = orden - ncnuevo - 1;
        int dirNuevo = fichero.tomarPágina();
        nuevoNodo.direccion(dirNuevo);
        nuevoNodo.cardinal(ncnuevo);
        nuevoNodo.enlace(0, nodo.enlace(ncnodo + 1));

        for (int i = 1; i <= nuevoNodo.cardinal(); i++) {
            nuevoNodo.clave(i, nodo.clave(ncnodo + 1 + i));
            nuevoNodo.enlace(i, nodo.enlace(ncnodo + 1 + i));
        }
        pa.clave = nodo.clave(ncnodo + 1);
        pa.direccion = nuevoNodo.direccion();
        nodo.cardinal(ncnodo);
        escribir(nodo);
        escribir(nuevoNodo);
        return pa;
    }

    /**
     * Realiza una partición 2/3
     * @param padre
     * @param posizq
     * @param izq
     * @param der
     * @throws Exception
     */
    private void particion_2_3(Nodo padre, int posizq, Nodo izq, Nodo der)
            throws Exception {
        int clavesRepartir = izq.cardinal() + der.cardinal() - 1;
        Nodo reg = new Nodo();
        int ncizq = (clavesRepartir) / 3;
        int ncreg = (clavesRepartir + 1) / 3;
        int ncder = (clavesRepartir + 2) / 3;
        int antncder = der.cardinal();
        int antncizq = izq.cardinal();
        reg.direccion(fichero.tomarPágina());
        padre.insertar(izq.clave(ncizq + 1), reg.direccion(), posizq + 1);
        reg.cardinal(ncreg);
        reg.enlace(0, izq.enlace(ncizq + 1));
        for (int i = ncizq + 2; i <= antncizq; i++) {
            reg.clave(i - ncizq - 1, izq.clave(i));
            reg.enlace(i - ncizq - 1, izq.enlace(i));
        }
        izq.cardinal(ncizq);
        reg.clave(antncizq - ncizq, padre.clave(posizq + 2));
        int posl = antncizq - ncizq;
        reg.enlace(posl, der.enlace(0));
        for (int i = posl + 1; i <= ncreg; i++) {
            reg.clave(i, der.clave(i - posl));
            reg.enlace(i, der.enlace(i - posl));
        }
        int ncpas = antncder - ncder;
        padre.clave(posizq + 2, der.clave(ncpas));
        der.enlace(0, der.enlace(ncpas));
        for (int i = ncpas + 1; i <= antncder; i++) {
            der.clave(i - ncpas, der.clave(i));
            der.enlace(i - ncpas, der.enlace(i));
        }
        der.cardinal(ncder);
        escribir(izq);
        escribir(reg);
        escribir(der);
    }

    /**
     * Realiza una rotación izquierda derecha.
     * @param padre
     * @param posizq
     * @param izq
     * @param der
     */
    private void rotacionizqder(Nodo padre, int posizq, Nodo izq, Nodo der)
            throws Exception {
        int clavesRepartir = izq.cardinal() + der.cardinal();
        int ncizq = (clavesRepartir) / 2;
        int ncder = clavesRepartir - ncizq;
        int ncpas = ncder - der.cardinal();
        int antncder = der.cardinal();
        der.cardinal(ncder);
        for (int i = antncder; i >= 1; i--) {
            der.clave(i + ncpas, der.clave(i));
            der.enlace(i + ncpas, der.enlace(i));
        }
        der.enlace(ncpas, der.enlace(0));
        der.clave(ncpas, padre.clave(posizq + 1));
        for (int i = ncizq + 2; i <= izq.cardinal(); i++) {
            der.clave(i - (ncizq + 1), izq.clave(i));
            der.enlace(i - (ncizq + 1), izq.enlace(i));
        }
        der.enlace(0, izq.enlace(ncizq + 1));
        padre.clave(posizq + 1, izq.clave(ncizq + 1));
        izq.cardinal(ncizq);
        escribir(padre);
        escribir(izq);
        escribir(der);
    }

    /**
     * Realiza una rotación derecha izquierda
     * @param padre
     * @param posizq
     * @param izq
     * @param der
     */
    private void rotacionderizq(Nodo padre, int posizq, Nodo izq, Nodo der)
            throws Exception {
        int clavesRepartir = izq.cardinal() + der.cardinal();
        int ncder = (clavesRepartir) / 2;
        int ncizq = clavesRepartir - ncder;
        int ncpas = der.cardinal() - ncder;
        int antncizq = izq.cardinal();
        izq.cardinal(ncizq);
        izq.clave(antncizq + 1, padre.clave(posizq + 1));
        izq.enlace(antncizq + 1, der.enlace(0));
        for (int i = 1; i < ncpas; i++) {
            izq.clave(antncizq + 1 + i, der.clave(i));
            izq.enlace(antncizq + 1 + i, der.enlace(i));
        }
        padre.clave(posizq + 1, der.clave(ncpas));
        der.enlace(0, der.enlace(ncpas));
        for (int i = 1; i <= ncder; i++) {
            der.clave(i, der.clave(i + ncpas));
            der.enlace(i, der.enlace(i + ncpas));
        }
        der.cardinal(ncder);
        escribir(padre);
        escribir(izq);
        escribir(der);
    }

    /**
     * realiza una recombinación 2/1
     * @param padre
     * @param posizq
     * @param izq
     * @param der
     */
    private void recombinacion_2_1(Nodo padre, int posizq, Nodo izq, Nodo der)
            throws Exception {
        int antncizq = izq.cardinal();
        izq.cardinal(izq.cardinal() + 1 + der.cardinal());
        izq.clave(antncizq + 1, padre.clave(posizq + 1));
        izq.enlace(antncizq + 1, der.enlace(0));
        for (int i = 1; i <= der.cardinal(); i++) {
            izq.clave(antncizq + 1 + i, der.clave(i));
            izq.enlace(antncizq + 1 + i, der.enlace(i));
        }
        padre.extraer(posizq + 1);
        escribir(izq);
        fichero.liberarPágina(der.direccion());
    }

    /**
     * Realiza una recombinación 3/2
     * @param padre
     * @param posReg
     * @param izq
     * @param reg
     * @param der
     */
    private void recombinacion_3_2(Nodo padre, int posReg, Nodo izq, Nodo reg,
            Nodo der) throws Exception {
        int aRepartir = izq.cardinal() + reg.cardinal() + der.cardinal() + 1;
        int ncder = aRepartir / 2;
        int ncizq = aRepartir - ncder;
        int antncizq = izq.cardinal();
        int antncder = der.cardinal();
        izq.cardinal(ncizq);
        izq.clave(antncizq + 1, padre.clave(posReg));
        izq.enlace(antncizq + 1, reg.enlace(0));
        for (int i = antncizq + 2; i <= ncizq; i++) {
            izq.clave(i, reg.clave(i - antncizq - 1));
            izq.enlace(i, reg.enlace(i - antncizq - 1));
        }
        der.cardinal(ncder);
        int ncpas = ncder - antncder;
        for (int i = antncder; i >= 1; i--) {
            der.clave(i + ncpas, der.clave(i));
            der.enlace(i + ncpas, der.enlace(i));
        }
        der.enlace(ncpas, der.enlace(0));
        der.clave(ncpas, padre.clave(posReg + 1));
        for (int i = ncpas - 1; i >= 1; i--) {
            der.clave(i, reg.clave(reg.cardinal() + i - ncpas + 1));
            der.enlace(i, reg.enlace(reg.cardinal() + i - ncpas + 1));
        }
        der.enlace(0, reg.enlace(reg.cardinal() - ncpas + 1));
        fichero.liberarPágina(reg.direccion());
        escribir(izq);
        escribir(der);
        padre.extraer(posReg);
        padre.clave(posReg, reg.clave(reg.cardinal() - ncpas + 1));
    }

    /**
     * Escribe un nodo en el fichero
     * @param n
     */
    private void escribir(Nodo n) {
        fichero.escribir(n.abyte(), n.direccion());
    }

    /**
     * Lee el nodo correspondiente a una dirección
     * @param dir
     * @return
     */
    private Nodo leer(int dir) throws Exception {
        Nodo n = new Nodo();
        n.deByte(fichero.leer(dir));
        if (n.direccion() != dir) {
            throw new Exception("Error al leer un nodo del árbol");
        }
        return n;
    }

    /**
     * Clase auxiliar.
     */
    private class InfoPila {

        public Nodo nodo;
        public int pos;

        public InfoPila(Nodo n, int p) {
            nodo = n;
            pos = p;
        }
    }
}
