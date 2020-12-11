package aed.airport;

import es.upm.aedlib.Entry;
import es.upm.aedlib.priorityqueue.*;
import es.upm.aedlib.map.*;
import es.upm.aedlib.positionlist.*;

/**
 * A registry which organizes information on airplane arrivals.
 */
public class IncomingFlightsRegistry {
  private PriorityQueue<Long, String> flightQueue;
  private Map<String, Entry<Long, String>> flightMap;

  /**
   * Constructs an class instance.
   */
  public IncomingFlightsRegistry() {
    flightQueue = new HeapPriorityQueue<Long, String>();
    flightMap = new HashTableMap<String, Entry<Long, String>>();
  }

  /**
   * A flight is predicted to arrive at an arrival time (in seconds).
   */
  public void arrivesAt(String flight, long time) {
    if (flightMap.containsKey(flight)) {
      flightQueue.remove(flightMap.get(flight));
    }
    flightMap.put(flight, flightQueue.enqueue(time, flight));
  }

  /**
   * A flight has been diverted, i.e., will not arrive at the airport.
   */
  public void flightDiverted(String flight) {
    if (!flightMap.containsKey(flight))
      return;
    flightQueue.remove(flightMap.get(flight));
    flightMap.remove(flight);
  }

  /**
   * Returns the arrival time of the flight.
   * 
   * @return the arrival time for the flight, or null if the flight is not
   *         predicted to arrive.
   */
  public Long arrivalTime(String flight) {
    Entry<Long, String> flightE = flightMap.get(flight);
    return (flightE == null) ? null : flightE.getKey();
  }

  /**
   * Returns a list of "soon" arriving flights, i.e., if any is predicted to
   * arrive at the airport within nowTime+180 then adds the predicted earliest
   * arriving flight to the list to return, and removes it from the registry.
   * Moreover, also adds to the returned list, in order of arrival time, any other
   * flights arriving withinfirstArrivalTime+120; these flights are also removed
   * from the queue of incoming flights.
   * 
   * @return a list of soon arriving flights.
   */
  public PositionList<FlightArrival> arriving(long nowTime) {
    PositionList<FlightArrival> arrivals = new NodePositionList<>();

    if (flightQueue.isEmpty())
      return arrivals;

    Entry<Long, String> flightToAdd = flightMap.get(flightQueue.dequeue().getValue());

    PositionList<Entry<Long, String>> reAdd = new NodePositionList<>();
    while (!flightQueue.isEmpty() && arrivalTime(flightToAdd.getValue()) < nowTime) {
      reAdd.addLast(flightToAdd);
      flightToAdd = flightQueue.dequeue();
    }

    if ((flightToAdd.getKey() - nowTime) > 180) {
      flightQueue.enqueue(flightToAdd.getKey(), flightToAdd.getValue());
      putBack(reAdd);
      return arrivals;
    }
    flightMap.remove(flightToAdd.getValue());
    arrivals.addFirst(new FlightArrival(flightToAdd.getValue(), flightToAdd.getKey()));
    Long newNowTime = flightToAdd.getKey();
    boolean emtpy = flightQueue.isEmpty();
    while (!emtpy && ((flightToAdd = flightQueue.dequeue()).getKey() - newNowTime) <= 120) {
      arrivals.addLast(new FlightArrival(flightToAdd.getValue(), arrivalTime(flightToAdd.getValue())));
      flightMap.remove(flightToAdd.getValue());
      emtpy = flightQueue.isEmpty();
    }
    if ((flightToAdd.getKey() - newNowTime) > 120)
      flightQueue.enqueue(flightToAdd.getKey(), flightToAdd.getValue());

    putBack(reAdd);
    return arrivals;
  }

  private void putBack(PositionList<Entry<Long, String>> list){
    list.forEach((entry) -> {
      flightQueue.enqueue(entry.getKey(), entry.getValue());
    });
  }
}
