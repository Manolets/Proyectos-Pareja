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

  private int buscarCuenta(String id) {
    if (cuentas.isEmpty())
      return -100;
    return buscarCuentaAux(0, cuentas.size(), (cuentas.size()) / 2, id);
  }


  /**
   * Implementa el algoritmo de búsqueda binaria. Para casos donde 
   * el elemento no se encuentra en la lista devuelve -mitad, siendo mitad 
   * la posición que le corresponde en la lista ordenada
   * @param inicio
   * @param end
   * @param mitad
   * @param id
   * @return
   */
  private int buscarCuentaAux(int inicio, int end, int mitad, String id) {
    int cmp = compareId(id, cuentas.get(mitad).getId());

    if (cmp == 0)
      return mitad;
    else if (mitad == inicio || mitad == end){
      mitad = cmp >= 0 ? mitad+1 : mitad;
      return -mitad;
    }
    else if (cmp > 0)
      inicio++;
    else
      end--;
    return buscarCuentaAux(inicio, end, (end + inicio) / 2, id);

  }

  private int compareId(String idCuenta1, String idCuenta2) {
    String[] cuenta1 = idCuenta1.split("/");
    String[] cuenta2 = idCuenta2.split("/");
    if (cuenta1[0].compareTo(cuenta2[0]) == 0)
      return cuenta1[1].compareTo(cuenta2[1]);
    else
      return cuenta1[0].compareTo(cuenta2[0]);
  }

  private boolean isFormatoValido(String id) {
    return id.contains("/") && id.replace("/", "").matches("[0-9]+");
  }

  /**
   * Inserta el elemento en la posición que le corresponde en la lista
   * @param list    Lista donde insertar
   * @param cuenta  Elemento a insertar
   * @param cmp     Criterio de comparación para la ordenación
   */
  private void insertarOrdenado(IndexedList<Cuenta> list, Cuenta cuenta, Comparator<Cuenta> cmp) {
    boolean insertado = false;
    int i = 0;
    while (!insertado && i < list.size()) {
      if (cmp.compare(cuenta, list.get(i)) < 0) {
        insertado = true;
        list.add(i, cuenta);
      }
      i++;
    }
  }

  // ----------------------------------------------------------------------
  // NOTAD. No se deberia cambiar este metodo.
  public String toString() {
    return "banco";
  }

  @Override
  public IndexedList<Cuenta> getCuentasOrdenadas(Comparator<Cuenta> cmp) {
    IndexedList<Cuenta> ordenadas = new ArrayIndexedList<Cuenta>();

    for (int i = 0; i < cuentas.size(); i++) {
      insertarOrdenado(ordenadas, cuentas.get(i), cmp);
    }

    return ordenadas;
  }

  @Override
  public String crearCuenta(String dni, int saldoIncial) {
    Cuenta nueva = new Cuenta(dni, saldoIncial);
    if (cuentas.isEmpty())
      cuentas.add(0, nueva);
    else {
      int index = buscarCuenta(nueva.getId());
      cuentas.add(index, nueva);
    }
    return nueva.getId();
  }

  @Override
  public void borrarCuenta(String id) throws CuentaNoExisteExc, CuentaNoVaciaExc {
    int posCuenta = buscarCuenta(id);
    if (posCuenta < 0)
      throw new CuentaNoExisteExc();
    if (cuentas.get(posCuenta).getSaldo() != 0)
      throw new CuentaNoVaciaExc();

    cuentas.removeElementAt(posCuenta);

  }

  @Override
  public int ingresarDinero(String id, int cantidad) throws CuentaNoExisteExc {
    int posCuenta = buscarCuenta(id);
    if (posCuenta < 0)
      throw new CuentaNoExisteExc();

    cuentas.get(posCuenta).ingresar(cantidad);
    return cuentas.get(posCuenta).getSaldo();
  }

  @Override
  public int retirarDinero(String id, int cantidad) throws CuentaNoExisteExc, InsuficienteSaldoExc {
    int posCuenta = buscarCuenta(id);
    if (posCuenta < 0)
      throw new CuentaNoExisteExc();
    if (cuentas.get(posCuenta).getSaldo() < cantidad) {
      throw new InsuficienteSaldoExc();
    } else {
      cuentas.get(posCuenta).retirar(cantidad);
    }

    return cuentas.get(posCuenta).getSaldo();
  }

  @Override
  public int consultarSaldo(String id) throws CuentaNoExisteExc {
    int posCuenta = buscarCuenta(id);
    if (posCuenta < 0) {
      throw new CuentaNoExisteExc();
    }
    return cuentas.get(posCuenta).getSaldo();
  }

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

  @Override
  public IndexedList<String> getIdCuentas(String dni) {
    IndexedList<String> idCuentas = new ArrayIndexedList<String>();
    IndexedList<Cuenta> arrayCuentas = getCuentas(dni);
    for (int i = 0; i < arrayCuentas.size(); i++) {
      idCuentas.add(idCuentas.size(), arrayCuentas.get(i).getId());
    }
    return idCuentas;
  }

  private IndexedList<Cuenta> getCuentas(String dni) {
    IndexedList<Cuenta> arrayCuentas = new ArrayIndexedList<Cuenta>();
    int posCuenta = buscarCuenta(dni + "/0");
    if (posCuenta < 0)
      posCuenta = i.charAt(1) == '1' ? Integer.parseInt(i.substring(2)) : Integer.parseInt(i.substring(2)) + 1;
    while (posCuenta < cuentas.size() && cuentas.get(posCuenta).getDNI().equals(dni)) {
      arrayCuentas.add(arrayCuentas.size(), cuentas.get(posCuenta));
      posCuenta++;
    }
    return arrayCuentas;
  }

  @Override
  public int getSaldoCuentas(String dni) {
    IndexedList<Cuenta> arrayCuentas = getCuentas(dni);
    int saldoCuentas = 0;
    for (int i = 0; i < arrayCuentas.size(); i++)
      saldoCuentas += arrayCuentas.get(i).getSaldo();
    return saldoCuentas;
  }

}
