import java.time.LocalDateTime;

public class Flight {
    private int stops;
    private int number;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;

    public Flight(int stops, int number, String departureAirport, String arrivalAirport, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        this.stops = stops;
        this.number = number;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
    }

    public Flight() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public LocalDateTime getDepartureTime() {
        return departureDateTime;
    }

    public void setDepartureTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalDateTime;
    }

    public void setArrivalTime(LocalDateTime arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    @Override
    public String toString() {
        return "{\n" +
                "\"departureAirport\": \"" + departureAirport + "\",\n" +
                "\"arrivalAirport\": \"" + arrivalAirport + "\",\n" +
                "\"departureDateTime\": \"" + departureDateTime.toString() + "\",\n" +
                "\"arrivalDateTime\": \"" + arrivalDateTime.toString() + "\"\n" +
                '}';
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public int getStops() {
        return stops;
    }

    public void setStops(int stops) {
        this.stops = stops;
    }
}
