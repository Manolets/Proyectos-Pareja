package aed.airport;

import org.junit.jupiter.api.Test;
import es.upm.aedlib.positionlist.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class Tests {

    @Test
    public void propiedad1(){
        IncomingFlightsRegistry registry = new IncomingFlightsRegistry();
        registry.arrivesAt("AvionT1", 1050);
        registry.arrivesAt("AvionT1", 1200);
        assertEquals(1200, registry.arrivalTime("AvionT1"));
    }

    @Test
    public void propiedad2(){
        IncomingFlightsRegistry registry = new IncomingFlightsRegistry();
        registry.arrivesAt("Avion1", 20);
        registry.arrivesAt("Avion2", 10);
        PositionList<FlightArrival> res = new NodePositionList<FlightArrival>();
        res.addFirst(new FlightArrival("Avion2", 10));
        res.addLast(new FlightArrival("Avion1", 20));
        assertEquals(res, registry.arriving(0));
    }
}

