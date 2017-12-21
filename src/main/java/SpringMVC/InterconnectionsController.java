package SpringMVC;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InterconnectionsController {

    private FlightsSearchingMachine fsm = new FlightsSearchingMachine();

    @RequestMapping("ryanair/interconnections")
    public String interconnections(
            @RequestParam(value = "departure", defaultValue = "DUB") String departureAirport,
            @RequestParam(value = "departureDateTime", defaultValue = "2017-12-21T12:00") String departureDateTime,
            @RequestParam(value = "arrival", defaultValue = "WRO") String arrivalAirport,
            @RequestParam(value = "arrivalDateTime", defaultValue = "2017-12-22T20:00") String arrivalDateTime
    ) {

        return fsm.getFlightsByDateTime(departureAirport, arrivalAirport, departureDateTime, arrivalDateTime);
    }
}