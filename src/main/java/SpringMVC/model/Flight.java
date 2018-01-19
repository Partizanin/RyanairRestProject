package SpringMVC.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Flight {
    private int stops;
    private int number;
    private String departureAirport;
    private String connectingAirport;
    private String arrivalAirport;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;

    public Flight(int stops, String departureAirport, String arrivalAirport, String connectingAirport) {
        this.stops = stops;
        this.departureAirport = departureAirport;
        this.connectingAirport = connectingAirport;
        this.arrivalAirport = arrivalAirport;
    }

    public Flight(int stops, int number, String departureAirport, String arrivalAirport, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        this.stops = stops;
        this.number = number;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
    }

    private int getNumber() {
        return number;
    }

    public LocalDateTime getDepartureTime() {
        return departureDateTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalDateTime;
    }

    @Override
    public String toString() {
        String departureDateTime = "";
        String arrivalDateTime = "";
        String result;
        if (this.departureDateTime != null) {
            departureDateTime = this.departureDateTime.toString();
        }
        if (this.arrivalDateTime != null) {
            arrivalDateTime = this.arrivalDateTime.toString();
        }

        if (stops == 1) {
            result = "{\n" +
                    "\"departureAirport\": \"" + departureAirport + "\",\n" +
                    "\"arrivalAirport\": \"" + arrivalAirport + "\",\n" +
                    "\"connectingAirport\": \"" + connectingAirport + "\",\n" +
                    "\"departureDateTime\": \"" + departureDateTime + "\",\n" +
                    "\"arrivalDateTime\": \"" + arrivalDateTime + "\"\n" +
                    '}';
        } else {
            result = "{\n" +
                    "\"departureAirport\": \"" + departureAirport + "\",\n" +
                    "\"arrivalAirport\": \"" + arrivalAirport + "\",\n" +
                    "\"departureDateTime\": \"" + departureDateTime + "\",\n" +
                    "\"arrivalDateTime\": \"" + arrivalDateTime + "\"\n" +
                    '}';
        }

        return result;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public String getConnectingAirport() {
        return connectingAirport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Flight)) return false;
        Flight flight = (Flight) o;
        return getNumber() == flight.getNumber() &&
                Objects.equals(getDepartureAirport(), flight.getDepartureAirport()) &&
                Objects.equals(getArrivalAirport(), flight.getArrivalAirport()) &&
                Objects.equals(departureDateTime, flight.departureDateTime) &&
                Objects.equals(arrivalDateTime, flight.arrivalDateTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(getNumber(), getDepartureAirport(), getArrivalAirport(), departureDateTime, arrivalDateTime);
    }
}
