package SpringMVC.controller;

import SpringMVC.FlightsSearchingMachine;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class InterconnectionsController {

    private final FlightsSearchingMachine fsm = new FlightsSearchingMachine();

    // Default path " http://localhost:8080/ryanair/interconnections "
    @RequestMapping("ryanair/interconnections")
    public String interconnections(
            @RequestParam(value = "departure", defaultValue = "DUB") String departureAirport,
            @RequestParam(value = "departureDateTime", defaultValue = "2018-02-21T12:00") String departureDateTime,
            @RequestParam(value = "arrival", defaultValue = "WRO") String arrivalAirport,
            @RequestParam(value = "arrivalDateTime", defaultValue = "2018-02-22T20:00") String arrivalDateTime
    ) {

        String result = fsm.getFlightsByDateTime(departureAirport, arrivalAirport, departureDateTime, arrivalDateTime);


        return "<pre>" + result + "</pre>";
    }

    public String getValidDate(int plusDays) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateTime = now.plusDays(plusDays);
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }
}