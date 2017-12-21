
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InterconnectionsController {

    private FlightsSearchingMachine fsm = new FlightsSearchingMachine();

    @RequestMapping("ryanair/interconnections")
    public String interconnections(
            @RequestParam(value = "departure", defaultValue = "DUB") String departureAirport,
            @RequestParam(value = "departureDateTime", defaultValue = "2016-03-01T07:00") String departureDateTime,
            @RequestParam(value = "arrival", defaultValue = "WRO") String arrivalAirport,
            @RequestParam(value = "arrivalDateTime", defaultValue = "22.12.2017 09:00") String arrivalDateTime
    ) {
        System.out.println(fsm.getFlightsByDateTime(departureAirport, departureDateTime, arrivalAirport, arrivalDateTime));
        return "Hello Spring";
    }
}


/*

departure={departure}&arrival={arrival}&depa
rtureDateTime={departureDateTime}&arrivalDateTime={arrivalDateTime}

?departure=DUB&arrival=WRO&dep
artureDateTime=2016-03-01T07:00&arrivalDateTime=2016-03-03T21:00


Schedules API:
https://api.ryanair.com/timetable/3/schedules/{departure}/{arrival}/years/{year}/months/{month}
which returns a list of available flights for a given departure airport IATA code, an arrival airport
IATA code, a year and a month. For example
https://api.ryanair.com/timetable/3/schedules/DUB/WRO/years/2016/months/6


*/