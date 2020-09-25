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

  private int buscarPorId(String id) {
    int count = 0;
    boolean encontrado = false;
    while (!encontrado && count < cuentas.size()) {
      if (cuentas.get(count).getId().equals(id)) {
        encontrado = true;
      } else {
        count++;
      }
    }

    return (encontrado) ? count : -1;
  }

  private int buscarCuenta(String id) {
    return buscarCuentaAux(0, cuentas.size()-1, (cuentas.size()-1)/2, id);
  }

  private void inputCuenta(Cuenta cuenta){

  }

  private int buscarCuentaAux(int inicio, int end, int mitad, String id) {
    int cmp = compareId(id, cuentas.get(mitad).getId());
    int signo = cmp>=0? 2 : 1;

    if (cmp==0)
      return mitad;
    else if(mitad==inicio || mitad==end)
      return -Integer.parseInt(signo+""+mitad);
    else if(cmp>0)
      inicio++;
    else
      end--;
    return buscarCuentaAux(inicio, end, (end+inicio)/2, id);

  }

  private int compareId(String idCuenta1, String idCuenta2){
    String[] cuenta1 = idCuenta1.split("/");
    String[] cuenta2 = idCuenta2.split("/");
    if (Integer.parseInt(cuenta1[0]) - Integer.parseInt(cuenta2[0]) == 0)
      return Integer.parseInt(cuenta1[1]) - Integer.parseInt(cuenta2[1]);
    else
      return Integer.parseInt(cuenta1[0]) - Integer.parseInt(cuenta2[0]);
  }

  /**
   * Swaps the element with the previos one in list
   * 
   * @param list
   */
  private void swapForPrev(IndexedList<Cuenta> list, int pos) {
    Cuenta temp = list.get(pos);

    list.set(pos, list.get(pos - 1));
    list.set(pos - 1, temp);
  }

  // ----------------------------------------------------------------------
  // NOTAD. No se deberia cambiar este metodo.
  public String toString() {
    return "banco";
  }

  @Override
  public IndexedList<Cuenta> getCuentasOrdenadas(Comparator<Cuenta> cmp) {
    IndexedList<Cuenta> ordenadas = this.cuentas;

    for (int i = 1; i < ordenadas.size(); i++) {
      if (cmp.compare(ordenadas.get(i - 1), ordenadas.get(i)) > 0)
        swapForPrev(ordenadas, i);
    }

    return ordenadas;
  }

  @Override
  public String crearCuenta(String dni, int saldoIncial) {
    Cuenta nueva = new Cuenta(dni, saldoIncial);
    cuentas.add(cuentas.size(), nueva);
    return nueva.getId();
  }

  @Override
  public void borrarCuenta(String id) throws CuentaNoExisteExc, CuentaNoVaciaExc {
    int posCuenta = buscarPorId(id);
    if (posCuenta == -1)
      throw new CuentaNoExisteExc();
    if (cuentas.get(posCuenta).getSaldo() != 0)
      throw new CuentaNoVaciaExc();

    cuentas.removeElementAt(posCuenta);

  }

  @Override
  public int ingresarDinero(String id, int cantidad) throws CuentaNoExisteExc {
    int posCuenta = buscarPorId(id);
    if (posCuenta == -1)
      throw new CuentaNoExisteExc();

    cuentas.get(posCuenta).ingresar(cantidad);
    return cuentas.get(posCuenta).getSaldo();
  }

  @Override
  public int retirarDinero(String id, int cantidad) throws CuentaNoExisteExc, InsuficienteSaldoExc {
    int posCuenta = buscarPorId(id);
    if (posCuenta == -1)
      throw new CuentaNoExisteExc();
    if (cuentas.get(posCuenta).getSaldo() < cantidad ) {
      throw new InsuficienteSaldoExc();
    } else {
      cuentas.get(posCuenta).retirar(cantidad);
    }

    return cuentas.get(posCuenta).getSaldo();
  }

  @Override
  public int consultarSaldo(String id) throws CuentaNoExisteExc {
    int posCuenta = buscarPorId(id);
    if (posCuenta == -1){
      throw new CuentaNoExisteExc();
    }
    return cuentas.get(posCuenta).getSaldo();
  }

  @Override
  public void hacerTransferencia(String idFrom, String idTo, int cantidad)
      throws CuentaNoExisteExc, InsuficienteSaldoExc {
    // TODO Auto-generated method stub

  }

  @Override
  public IndexedList<String> getIdCuentas(String dni) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getSaldoCuentas(String dni) {
    // TODO Auto-generated method stub
    return 0;
  }

}
