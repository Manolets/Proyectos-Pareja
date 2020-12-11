package aed.treesearch;

import java.util.Iterator;

import es.upm.aedlib.Pair;
import es.upm.aedlib.Position;
import es.upm.aedlib.set.*;
import es.upm.aedlib.positionlist.*;
import es.upm.aedlib.tree.*;

public class TreeSearch {
  public static Set<Position<String>> search(Tree<String> t, PositionList<String> searchExprs) {
    if (t.isEmpty())
      return null;
    return search(t, searchExprs, t.root(), searchExprs.first());
  }

  private static Set<Position<String>> search(Tree<String> t, PositionList<String> searchExprs,
      Position<String> tPointer, Position<String> ePointer) {
    Set<Position<String>> set = new PositionListSet<>();
    if (!ePointer.element().equals("*") && !ePointer.element().equals(tPointer.element()))
      return set;

    if ((ePointer.element().equals("*") || ePointer.element().equals(tPointer.element()))
        && ePointer.equals(searchExprs.last())) {
      set.add(tPointer);
      return set;
    }
    for (Position<String> pointer : t.children(tPointer)) {
      search(t, searchExprs, pointer, searchExprs.next(ePointer)).forEach((elem) -> set.add(elem));
    }

    if (tPointer.element().equals(ePointer.element()) && ePointer.equals(searchExprs.last()))
      set.add(tPointer);
    ePointer = searchExprs.next(ePointer);
    return set;
  }

  public static Tree<String> constructDeterministicTree(Set<PositionList<String>> paths) {
    GeneralTree<String> tree = new LinkedGeneralTree<String>();
    Iterator<PositionList<String>> itCaminos = paths.iterator();
    PositionList<String> currentPath;
    Position<String> treeP;
    Set<Pair<String, String>> added = new PositionListSet<>();

    while (itCaminos.hasNext()) {
      currentPath = itCaminos.next();
      Iterator<String> pathIt = currentPath.iterator();
      if (tree.isEmpty()){
        tree.addRoot(currentPath.first().element());
        added.add(new Pair<>(null, currentPath.first().element()));  
      }
      treeP = tree.root();
      String previous = null;
      while (pathIt.hasNext()) {
        String nodo = pathIt.next();
        Pair<String, String> pair = new Pair<String,String>(previous, nodo);
        previous = nodo;
        if (!added.contains(pair)){
          treeP = tree.addChildFirst(treeP, nodo);
          added.add(pair);
        } else {
          for (Position<String> hijo : tree.children(treeP)) {
            if (hijo.element().equals(nodo))
              treeP = hijo;
          }
        }
          
      }
    }
    return tree;
  }
}
