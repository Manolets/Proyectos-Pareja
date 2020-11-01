package aed.cache;

import es.upm.aedlib.Position;
import es.upm.aedlib.map.*;
import es.upm.aedlib.positionlist.*;

public class Cache<Key, Value> {

  // Tamano de la cache
  private int maxCacheSize;

  // NO MODIFICA ESTOS ATTRIBUTOS, NI CAMBIA SUS NOMBRES: mainMemory,
  // cacheContents, keyListLRU

  // Para acceder a la memoria M
  private Storage<Key, Value> mainMemory;
  // Un 'map' que asocia una clave con un ``CacheCell''
  private Map<Key, CacheCell<Key, Value>> cacheContents;
  // Una PositionList que guarda las claves en orden de
  // uso -- la clave mas recientemente usado sera el keyListLRU.first()
  private PositionList<Key> keyListLRU;

  // Constructor de la cache. Especifica el tamano maximo
  // y la memoria que se va a utilizar
  public Cache(int maxCacheSize, Storage<Key, Value> mainMemory) {
    this.maxCacheSize = maxCacheSize;

    // NO CAMBIA
    this.mainMemory = mainMemory;
    this.cacheContents = new HashTableMap<Key, CacheCell<Key, Value>>();
    this.keyListLRU = new NodePositionList<Key>();
  }

  private void updateKeyList(Key key) {
    boolean isInList = false;
    Position<Key> pointer = keyListLRU.first();
    while (pointer != null && !isInList) {
      isInList = pointer.element().equals(key);
      if (isInList)
        keyListLRU.remove(pointer);
      else
        pointer = keyListLRU.next(pointer);
    }
    keyListLRU.addFirst(key);
    if (keyListLRU.size() > maxCacheSize) {
      CacheCell<Key, Value> element = cacheContents.get(keyListLRU.last().element());
      if (element.getDirty())
        mainMemory.write(element.getPos().element(), element.getValue());
      cacheContents.remove(element.getPos().element());
      keyListLRU.remove(keyListLRU.last());
    }
  }

  // Devuelve el valor que corresponde a una clave "Key"
  public Value get(Key key) {
    boolean containsKey = cacheContents.containsKey(key);
    Value value = (containsKey)? cacheContents.get(key).getValue(): mainMemory.read(key);
    if (value == null)
      return null;
    boolean isDirty = (containsKey)?cacheContents.get(key).getDirty():false;

    updateKeyList(key);
    CacheCell<Key, Value> res = this.cacheContents.get(key);
    if (res == null || res.getPos().element() == null) {
      res = new CacheCell<Key, Value>(value, isDirty, keyListLRU.first());
      cacheContents.put(key, res);
    }
    return res.getValue();
  }

  // Establece un valor nuevo para la clave en la memoria cache
  public void put(Key key, Value value) {
    updateKeyList(key);
    CacheCell<Key, Value> cell = new CacheCell<Key, Value>(value, true, keyListLRU.first());
    cacheContents.put(key, cell);
  }

  // NO CAMBIA
  public String toString() {
    return "cache";
  }
}
