package SpringMVC;

import SpringMVC.model.Flight;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class FlightsSearchingMachine {


    public String getFlightsByDateTime(String departureAirport, String arrivalAirport, String departureDateTime, String arrivalDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        LocalDateTime departDateTime = LocalDateTime.parse(departureDateTime, formatter);
        LocalDateTime arriveDateTime = LocalDateTime.parse(arrivalDateTime, formatter);

        String url = "https://api.ryanair.com/timetable/3/schedules/" + departureAirport + "/" + arrivalAirport + "/years/" + departDateTime.getYear() + "/months/" + departDateTime.getMonthValue();

        String source = getSource(url).toString();
        List<Flight> parsedFlights = getDirectFlights(source, departDateTime, departureAirport, arrivalAirport);

        List<Flight> filteredListDirectFlights = parsedFlights.stream().filter(it -> it.getDepartureTime().isAfter(departDateTime) && it.getArrivalTime().isBefore(arriveDateTime)).collect(Collectors.toList());

        List<Flight[]> interconnectedFlights = getInterconnectedFlights(departDateTime, departureAirport, arrivalAirport);

        return parsedFlightsToJson(filteredListDirectFlights, interconnectedFlights);

    }

    private String parsedFlightsToJson(List<Flight> directFlights, List<Flight[]> interconnectionFlights) {

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        ObjectNode baseObject = mapper.createObjectNode();

        for (Flight flight : directFlights) {
            baseObject.put("stops", 0);

            ArrayNode legs = mapper.createArrayNode();
            ObjectNode obj1 = null;
            try {
                obj1 = (ObjectNode) mapper.readTree(flight.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }


            legs.add(obj1);
            baseObject.put("legs", legs);
        }


        for (Flight[] interconnectionFlight : interconnectionFlights) {

            baseObject.put("stops", 1);

            ArrayNode legs = mapper.createArrayNode();
            for (Flight flight : interconnectionFlight) {
                JsonNode flightObject = null;
                try {
                    flightObject = mapper.readTree(flight.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                legs.add(flightObject);
            }
            baseObject.put("legs", legs);
        }


        arrayNode.add(baseObject);
        StringBuilder result = new StringBuilder();
        try {
            String string = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
            result.append(string);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    private List<Flight[]> getInterconnectedFlights(LocalDateTime departDateTimeRange, String departureAirport, String arrivalAirport) {
        ArrayList<Flight[]> flightsResult = new ArrayList<>();
        String flightsJson = getSource("https://api.ryanair.com/core/3/routes").toString();

        if (!flightsJson.isEmpty()) {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = null;
            try {
                node = mapper.readTree(flightsJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<Flight> interconnectingFlightsList = new ArrayList<>();
            assert node != null;
            for (JsonNode flight : node) {
                String from = flight.get("airportFrom").toString().replaceAll("\"", "");
                String to = flight.get("airportTo").toString().replaceAll("\"", "");
                String connecting = flight.get("connectingAirport").toString().replaceAll("\"", "");
                if (!connecting.isEmpty() && !connecting.equals("null")) {//if interconnecting field not null and not empty
                    if (from.equals(departureAirport) && to.equals(arrivalAirport)) {// if from filed equal departureAirport and to equal arrivalAirport
                        interconnectingFlightsList.add(new Flight(1, from, to, connecting));
                    }

                }
            }

            for (Flight flight : interconnectingFlightsList) {// get all flights by depart and connecting airport and connecting and arrival airport

                flightsResult.addAll(filterInterconnectingFlight(flight, departDateTimeRange));
            }

        }

        return flightsResult;
    }

    private ArrayList<Flight[]> filterInterconnectingFlight(Flight flight, LocalDateTime departDateTimeRange) {
        ArrayList<Flight[]> flights = new ArrayList<>();

        List<Flight> flights1 = getListOfFlightsByDateTime(flight.getDepartureAirport(), flight.getConnectingAirport(), departDateTimeRange);
        flights1 = flights1.stream().filter(it -> it.getDepartureTime().isBefore(departDateTimeRange)).collect(Collectors.toList());


        for (Flight currentFlight : flights1) {
            Flight[] flightObject = new Flight[2];
            flightObject[0] = currentFlight;

            LocalDateTime firstFlightArrivalDateTime = currentFlight.getArrivalTime().plusHours(2);

            List<Flight> listOfFlightsByDateTime = getListOfFlightsByDateTime(currentFlight.getArrivalAirport(), flight.getArrivalAirport(), firstFlightArrivalDateTime);

            listOfFlightsByDateTime = listOfFlightsByDateTime.stream().filter(it -> it.getDepartureTime().isAfter(firstFlightArrivalDateTime)).collect(Collectors.toList());

            flightObject[1] = listOfFlightsByDateTime.get(0);

            flights.add(flightObject);

        }

        return flights;
    }

    private List<Flight> getDirectFlights(String flightsJson, LocalDateTime departDateTime, String departureAirport, String arrivalAirport) {
        ArrayList<Flight> result = new ArrayList<>();
        if (!flightsJson.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = null;
            try {
                node = mapper.readTree(flightsJson);
            } catch (IOException e) {
                e.printStackTrace();
            }


            assert node != null;
            node = node.get("days");

            LocalDateTime dateTime = departDateTime;
            for (JsonNode flights : node) {
                JsonNode flight = flights.get("flights");
                JsonNode day = flights.get("day");
                dateTime = dateTime.withDayOfMonth(day.asInt());
                for (JsonNode jsonFlight : flight) {

                    LocalTime arrivalTime = LocalTime.parse(jsonFlight.get("arrivalTime").asText());
                    LocalDateTime arrivalDateTime = dateTime.withHour(arrivalTime.getHour());
                    arrivalDateTime = arrivalDateTime.withMinute(arrivalTime.getMinute());

                    LocalTime departureTime = LocalTime.parse(jsonFlight.get("departureTime").asText());
                    departDateTime = arrivalDateTime.withHour(departureTime.getHour());
                    departDateTime = departDateTime.withMinute(departureTime.getMinute());

                    int number = jsonFlight.get("number").asInt();
                    Flight addedFlight = new Flight(0, number, departureAirport, arrivalAirport, departDateTime, arrivalDateTime);
                    if (!result.contains(addedFlight)) {
                        result.add(addedFlight);
                    }
                }
            }
        }

        return result;
    }


    private List<Flight> getListOfFlightsByDateTime(String departureAirport, String arrivalAirport, LocalDateTime departureDateTime) {

        String url = "https://api.ryanair.com/timetable/3/schedules/" + departureAirport + "/" + arrivalAirport + "/years/" + departureDateTime.getYear() + "/months/" + departureDateTime.getMonthValue();
        String source = getSource(url).toString();

        return getDirectFlights(source, departureDateTime, departureAirport, arrivalAirport);
    }


    private StringBuilder getSource(String url) {
        StringBuilder source = new StringBuilder("");
        InputStream is = null;
        BufferedReader br = null;
        String line;
        try {

            is = new URL(url).openStream();
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            while ((line = br.readLine()) != null) {
                source.append(line);
            }

        } catch (IOException e) {
            System.err.println(e);

        } finally {
            if (is != null) {
                try {
                    is.close();
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return source;
    }
}
