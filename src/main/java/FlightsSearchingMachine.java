
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

public class FlightsSearchingMachine {

    public static void main(String[] args) {
        FlightsSearchingMachine flightsSearchingMachine = new FlightsSearchingMachine();
        String flightsByDateTime = flightsSearchingMachine.getFlightsByDateTime("DUB", "WRO", "2017-12-21T07:00", "2017-12-22T21:00");

    }

    private List<Flight> parseJsonToObjects(String flightsJson, LocalDateTime departDateTime, String departureAirport, String arrivalAirport) {
        ArrayList<Flight> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(flightsJson);
        } catch (IOException e) {
            e.printStackTrace();
        }


        assert node != null;

        LocalDateTime dateTime = departDateTime;
        for (JsonNode flights : node) {
            JsonNode flight = flights.get("flights");
            JsonNode day = flights.get("day");
            dateTime = dateTime.withDayOfMonth(day.asInt());
            for (JsonNode jsonFlight : flight) {
                flights = flights.get("flights");

                LocalTime arrivalTime = LocalTime.parse(jsonFlight.get("arrivalTime").asText());
                LocalDateTime arrivalDateTime = dateTime.withHour(arrivalTime.getHour());
                arrivalDateTime = arrivalDateTime.withMinute(arrivalTime.getMinute());

                LocalTime departureTime = LocalTime.parse(jsonFlight.get("departureTime").asText());
                departDateTime = departDateTime.withHour(departureTime.getHour());
                departDateTime = departDateTime.withMinute(departureTime.getMinute());

                int number = jsonFlight.get("number").asInt();

                result.add(new Flight(0, number, departureAirport, arrivalAirport, departDateTime, arrivalDateTime));
            }
        }

        return result;
    }


    public String getFlightsByDateTime(String departureAirport, String arrivalAirport, String departureDateTime, String arrivalDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        LocalDateTime departDateTime = LocalDateTime.parse(departureDateTime, formatter);
        LocalDateTime arrivDateTime = LocalDateTime.parse(arrivalDateTime, formatter);

        String url = "https://api.ryanair.com/timetable/3/schedules/" + departureAirport + "/" + arrivalAirport + "/years/" + departDateTime.getYear() + "/months/" + departDateTime.getMonthValue();
//        return getSource(url).toString();
        String source = "[{\"day\":21,\"flights\":[{\"number\":\"1926\",\"departureTime\":\"19:10\",\"arrivalTime\":\"22:45\"},{\"number\":\"1555\",\"departureTime\":\"19:35\",\"arrivalTime\":\"23:10\"}]},{\"day\":22,\"flights\":[{\"number\":\"1926\",\"departureTime\":\"14:20\",\"arrivalTime\":\"17:55\"}]},{\"day\":23,\"flights\":[{\"number\":\"1555\",\"departureTime\":\"20:15\",\"arrivalTime\":\"23:50\"}]},{\"day\":24,\"flights\":[{\"number\":\"1926\",\"departureTime\":\"12:55\",\"arrivalTime\":\"16:30\"}]},{\"day\":26,\"flights\":[{\"number\":\"1555\",\"departureTime\":\"17:45\",\"arrivalTime\":\"21:20\"}]},{\"day\":27,\"flights\":[{\"number\":\"1926\",\"departureTime\":\"19:10\",\"arrivalTime\":\"22:45\"}]},{\"day\":28,\"flights\":[{\"number\":\"1926\",\"departureTime\":\"19:10\",\"arrivalTime\":\"22:45\"},{\"number\":\"1555\",\"departureTime\":\"19:35\",\"arrivalTime\":\"23:10\"}]},{\"day\":29,\"flights\":[{\"number\":\"1926\",\"departureTime\":\"14:20\",\"arrivalTime\":\"17:55\"}]},{\"day\":30,\"flights\":[{\"number\":\"1555\",\"departureTime\":\"20:15\",\"arrivalTime\":\"23:50\"}]},{\"day\":31,\"flights\":[{\"number\":\"1926\",\"departureTime\":\"15:20\",\"arrivalTime\":\"18:55\"}]}]";
        List<Flight> parsedFlights = parseJsonToObjects(source, departDateTime, departureAirport, arrivalAirport);
        return filterAndParseToJson(parsedFlights, departDateTime, arrivDateTime);
//        return "";
    }

    private String filterAndParseToJson(List<Flight> parsedFlights, LocalDateTime departDateTimeRange, LocalDateTime arrivalDateTimeRange) {
        ArrayList<Flight> filteredList = new ArrayList<>();

        for (Flight parsedFlight : parsedFlights) {

            if (parsedFlight.getDepartureTime().isAfter(departDateTimeRange)) {

                if (parsedFlight.getArrivalTime().isBefore(arrivalDateTimeRange)) {
                    filteredList.add(parsedFlight);
                }
            }
        }


        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        for (Flight flight : filteredList) {


            ObjectNode baseObject = mapper.createObjectNode();
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
            arrayNode.add(baseObject);
        }

        try {
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return arrayNode.toString();
    }

    private StringBuilder getSource(String url) {
        StringBuilder source = new StringBuilder();
        InputStream is = null;
        BufferedReader br = null;
        String line = "";
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
