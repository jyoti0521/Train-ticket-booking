package ticket.booking.entities;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Ticket {
    @JsonProperty("ticket_id")
    private String ticketId;
    private String userId;
    private String source;
    private String destination;
    @JsonProperty("date_of_travel")
    private Date dateOfTravel;
    private Train train;
    // ✅ Add this new field
    @JsonProperty("ticket_info")
    private String ticketInfo;

    // constructors...


    public void setTicketInfo(String ticketInfo) {
        this.ticketInfo = ticketInfo;
    }

    public Ticket() {
    }

    public Ticket(String ticketId, String userId, String source,String destination, Date dateOfTravel,Train train ) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.source = source;
        this.destination = destination;
        this.dateOfTravel = dateOfTravel;
        this.train= train;
    }


    public String getTicketInfo(){
        return String.format("Ticket Id: %s belongs to user %s from %s to %s on %s", ticketId, userId, source,destination,dateOfTravel);
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public Date getDateOfTravel() {
        return dateOfTravel;
    }

    public Train getTrain() {
        return train;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDateOfTravel(Date dateOfTravel) {
        this.dateOfTravel = dateOfTravel;
    }

    public void setTrain(Train train) {
        this.train = train;
    }
}
