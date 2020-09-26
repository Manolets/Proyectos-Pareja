package aed.bancofiel;

import java.util.Comparator;

import es.upm.aedlib.indexedlist.IndexedList;
import es.upm.aedlib.indexedlist.ArrayIndexedList;

/**
 * Implements the code for the bank application. Implements the client and the
 * "gestor" interfaces.
 */
public class BancoFiel implements ClienteBanco, GestorBanco {

  // NOTAD. No se deberia cambiar esta declaracion.
  public IndexedList<Cuenta> cuentas;

  // NOTAD. No se deberia cambiar esta constructor.
  public BancoFiel() {
    this.cuentas = new ArrayIndexedList<Cuenta>();
  }

  // ----------------------------------------------------------------------
  // Anadir metodos aqui ...

  /**
   * Implementa el algoritmo de búsqueda binaria. Para casos donde
   * el elemento no se encuentra en la lista devuelve -(pos+1), siendo pos
   * la posición que le corresponde en la lista ordenada
   * @param id identificador del elemento a encontrar en la lista
   * @return Posición del elemento en la lista o menos la posición
   * siguiente al hueco que le corresponde
   */
  private int buscarCuenta(String id) {
    if (cuentas.isEmpty())
      return -1;
    return buscarCuentaAux(0, cuentas.size(), id);
  }

  private int buscarCuentaAux(int inicio, int end, String id) {
    int mitad = (end+inicio)/2;
    int cmp = compareId(id, cuentas.get(mitad).getId());

    if (cmp == 0)
      return mitad;
    else if (mitad == inicio || mitad == end){
      mitad = cmp >= 0 ? mitad+2 : mitad+1;
      return -mitad;
    }
    else if (cmp > 0)
      inicio++;
    else
      end--;
    return buscarCuentaAux(inicio, end, id);

  }

  /**
   * Compara los identificadores según su orden numérico.
   * @param idCuenta1 identificador de un elemento a comparar
   * @param idCuenta2 identificador del otro elemento a comparar
   * @return si idCuenta1 es mayor devuelve un entero positivo,
   * si es menor, un entero negativo si son iguales devuelve 0.
   */
  private int compareId(String idCuenta1, String idCuenta2) {
    String[] cuenta1 = idCuenta1.split("/");
    String[] cuenta2 = idCuenta2.split("/");
    if (cuenta1[0].compareTo(cuenta2[0]) == 0)
      return cuenta1[1].compareTo(cuenta2[1]);
    else
      return cuenta1[0].compareTo(cuenta2[0]);
  }
  /**
   * Comprueba que el id es de la forma num/num siendo num una secuencia numérica
   * @param id identificador de un elemento a comparar
   * @return sigue el fórmato válido
   */
  private boolean isFormatoValido(String id) {
    return id.contains("/") && id.replace("/", "").matches("[0-9]+");
  }

  /**
   * Inserta el elemento en la posición que le corresponde en la lista
   * @param list Lista donde insertar
   * @param cuenta Elemento a insertar
   * @param cmp Criterio de comparación para la ordenación
   */
  private void insertarOrdenado(IndexedList<Cuenta> list, Cuenta cuenta, Comparator<Cuenta> cmp) {
    boolean insertado = false;
    int i = 0;
    while (!insertado && i < list.size()) {
      if (cmp.compare(cuenta, list.get(i)) < 0)
        insertado = true;
      else
        i++;
    }
    list.add(i, cuenta);
  }


  @Override
  public IndexedList<Cuenta> getCuentasOrdenadas(Comparator<Cuenta> cmp) {
    IndexedList<Cuenta> ordenadas = new ArrayIndexedList<Cuenta>();
    for (int i = 0; i < cuentas.size(); i++) {
      insertarOrdenado(ordenadas, cuentas.get(i), cmp);
    }
    return ordenadas;
  }

  /**
   * Crea una cuenta y la introduce de forma ordenada en la lista
   * @param dni dni del usuario de la nueva cuenta
   * @param saldoIncial saldo incial del usuario de la nueva cuenta
   * @return Identificador correspondiente a la nueva cuenta
   */
  @Override
  public String crearCuenta(String dni, int saldoIncial) {
    Cuenta nueva = new Cuenta(dni, saldoIncial);
    if(!isFormatoValido(nueva.getId()))
      return "Formato no válido";
    if (cuentas.isEmpty())
      cuentas.add(0, nueva);
    else {
      int index = -(buscarCuenta(nueva.getId())+1);
      cuentas.add(index, nueva);
    }
    return nueva.getId();
  }

  /**
   * Borra la cuenta con el que tiene el identificador -id-.
   * @param id id del usuario de la cuenta a borrar
   * @throws CuentaNoExisteExc si la cuenta no se ha creado
   * @throws CuentaNoVaciaExc si la cuenta todavía guarda dinero
   */
  @Override
  public void borrarCuenta(String id) throws CuentaNoExisteExc, CuentaNoVaciaExc {
    int posCuenta = buscarCuenta(id);
    if (posCuenta < 0)
      throw new CuentaNoExisteExc();
    if (cuentas.get(posCuenta).getSaldo() != 0)
      throw new CuentaNoVaciaExc();
    cuentas.removeElementAt(posCuenta);
  }

  /**
   * Ingresa -cantidad- en la cuenta con el identificador -id-
   * @param id id del usuario de la cuenta
   * @param cantidad dinero a ingresar
   * @throws CuentaNoExisteExc si la cuenta no se ha creado
   * @return nuevo saldo de la cuenta
   */
  @Override
  public int ingresarDinero(String id, int cantidad) throws CuentaNoExisteExc {
    int posCuenta = buscarCuenta(id);
    if (posCuenta < 0)
      throw new CuentaNoExisteExc();
    cuentas.get(posCuenta).ingresar(cantidad);
    return cuentas.get(posCuenta).getSaldo();
  }

  /**
   * Retira -cantidad- en la cuenta con el identificador -id-
   * @param id id del usuario de la cuenta
   * @param cantidad dinero a retirar
   * @throws CuentaNoExisteExc si la cuenta no se ha creado
   * @throws InsuficienteSaldoExc si la cuenta no tiene suficiente dinero
   * @return nuevo saldo de la cuenta
   */
  @Override
  public int retirarDinero(String id, int cantidad) throws CuentaNoExisteExc, InsuficienteSaldoExc {
    int posCuenta = buscarCuenta(id);
    if (posCuenta < 0)
      throw new CuentaNoExisteExc();
    if (cuentas.get(posCuenta).getSaldo() < cantidad)
      throw new InsuficienteSaldoExc();
    else
      cuentas.get(posCuenta).retirar(cantidad);
    return cuentas.get(posCuenta).getSaldo();
  }

  @Override
  public int consultarSaldo(String id) throws CuentaNoExisteExc {
    int posCuenta = buscarCuenta(id);
    if (posCuenta < 0)
      throw new CuentaNoExisteExc();
    return cuentas.get(posCuenta).getSaldo();
  }
  /**
   * Mueve -cantidad- de una cuenta a otra
   * @param idFrom id del usuario de la cuenta que transfiere
   * @param idTo id del usuario de la cuenta que recibe
   * @param cantidad dinero a transferir
   * @throws CuentaNoExisteExc si alguna de las cuentas no se han creado
   * o no cuenta con un formato válido
   * @throws InsuficienteSaldoExc si la cuenta no tiene suficiente dinero
   */
  @Override
  public void hacerTransferencia(String idFrom, String idTo, int cantidad)
          throws CuentaNoExisteExc, InsuficienteSaldoExc {
    if (!isFormatoValido(idFrom) || !isFormatoValido(idTo))
      throw new CuentaNoExisteExc();
    int posFrom = buscarCuenta(idFrom);
    int posTo = buscarCuenta(idTo);
    if (posFrom < 0 || posTo < 0)
      throw new CuentaNoExisteExc();
    if (cuentas.get(posFrom).getSaldo() < cantidad)
      throw new InsuficienteSaldoExc();
    else {
      cuentas.get(posFrom).retirar(cantidad);
      cuentas.get(posTo).ingresar(cantidad);
    }
  }

  /**
   * Devuelve todas las cuentas asociadas a un dni
   * @param dni dni del usuario
   * @return lista indexada de los identificadores de cuentas
   * asociadas al mismo dni
   */
  @Override
  public IndexedList<String> getIdCuentas(String dni) {
    IndexedList<String> idCuentas = new ArrayIndexedList<String>();
    IndexedList<Cuenta> arrayCuentas = getCuentas(dni);
    for (int i = 0; i < arrayCuentas.size(); i++)
      idCuentas.add(idCuentas.size(), arrayCuentas.get(i).getId());
    return idCuentas;
  }

  /**
   * Devuelve todas las cuentas asociadas a un dni
   * @param dni dni del usuario
   * @return lista indexada de las cuentas asociadas al mismo dni
   */
  private IndexedList<Cuenta> getCuentas(String dni) {
    IndexedList<Cuenta> arrayCuentas = new ArrayIndexedList<Cuenta>();
    int posCuenta = buscarCuenta(dni + "/0");
    if (posCuenta < 0)
      posCuenta = -(posCuenta+1);
    while (posCuenta < cuentas.size() && cuentas.get(posCuenta).getDNI().equals(dni)) {
      arrayCuentas.add(arrayCuentas.size(), cuentas.get(posCuenta));
      posCuenta++;
    }
    return arrayCuentas;
  }
  /**
   * Devuelve la suma del saldo total entre todas las
   * cuentas asociadas a un mismo dni
   * @param dni dni del usuario
   * @return suma de saldos de las cuentas asociadas al mismo dni
   */
  @Override
  public int getSaldoCuentas(String dni) {
    IndexedList<Cuenta> arrayCuentas = getCuentas(dni);
    int saldoCuentas = 0;
    for (int i = 0; i < arrayCuentas.size(); i++)
      saldoCuentas += arrayCuentas.get(i).getSaldo();
    return saldoCuentas;
  }

  // ----------------------------------------------------------------------
  // NOTAD. No se deberia cambiar este metodo.
  public String toString() {
    return "banco";
  }


}
