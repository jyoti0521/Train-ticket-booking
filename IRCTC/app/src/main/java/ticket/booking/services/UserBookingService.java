package ticket.booking.services;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ticket.booking.entities.Ticket;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.Util.UserServiceUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class UserBookingService{

    private ObjectMapper objectMapper = new ObjectMapper();

    private List<User> userList;

    private User user;

    private final String USER_FILE_PATH = "app/src/main/java/ticket/booking/localDb/users.json";

    public UserBookingService(User user) throws IOException {
        this.user = user;
        loadUserListFromFile();
    }

    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }

//    private void loadUserListFromFile() throws IOException {
//        userList = objectMapper.readValue(new File(USER_FILE_PATH), new TypeReference<List<User>>() {});
//    }

    private void loadUserListFromFile() throws IOException {
        File userFile = new File(USER_FILE_PATH);

        // ✅ Debug logs to check the absolute path and if it exists
        System.out.println("Looking for users.json at: " + userFile.getAbsolutePath());
        System.out.println("File exists? " + userFile.exists());

        userList = objectMapper.readValue(userFile, new TypeReference<List<User>>() {});
    }


//    public Boolean loginUser(){
//        Optional<User> foundUser = userList.stream().filter(user1 -> {
//            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
//        }).findFirst();
//        return foundUser.isPresent();
//    }                      //sir ka h

    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();

        if (foundUser.isPresent()) {
            this.user = foundUser.get(); // Set current user from list
            return true;
        } else {
            return false;
        }
    }


    public Boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USER_FILE_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

//    public void fetchBookings(){
//        Optional<User> userFetched = userList.stream().filter(user1 -> {
//            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
//        }).findFirst();
//        if(userFetched.isPresent()){
//            userFetched.get().printTickets();
//        }
//    }                //sir ka h


    public void fetchBookings(){
        Optional<User> userFetched = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        if(userFetched.isPresent()){
            userFetched.get().printTickets();
        }
    }


    // todo: Complete this function
    public Boolean cancelBooking(String ticketId){

        Scanner s = new Scanner(System.in);
        System.out.println("Enter the ticket id to cancel");
        ticketId = s.next();

        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Ticket ID cannot be null or empty.");
            return Boolean.FALSE;
        }

        String finalTicketId1 = ticketId;  //Because strings are immutable
        boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId1));

        String finalTicketId = ticketId;
        user.getTicketsBooked().removeIf(Ticket -> Ticket.getTicketId().equals(finalTicketId));
        if (removed) {
            System.out.println("Ticket with ID " + ticketId + " has been canceled.");
            return Boolean.TRUE;
        }else{
            System.out.println("No ticket found with ID " + ticketId);
            return Boolean.FALSE;
        }
    }


    public List<Train> getTrains(String source, String destination){
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }

//    public Boolean bookTrainSeat(Train train, int row, int seat) {
//        try{
//            TrainService trainService = new TrainService();
//            List<List<Integer>> seats = train.getSeats();
//            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
//                if (seats.get(row).get(seat) == 0) {
//                    seats.get(row).set(seat, 1);
//                    train.setSeats(seats);
//                    trainService.addTrain(train);
//                    return true; // Booking successful
//                } else {
//                    return false; // Seat is already booked
//                }
//            } else {
//                return false; // Invalid row or seat index
//            }
//        }catch (IOException ex){
//            return Boolean.FALSE;
//        }
//    }
public Boolean bookTrainSeat(Train train, int row, int seat) {
    try {
        TrainService trainService = new TrainService();
        List<List<Integer>> seats = train.getSeats();

        if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
            if (seats.get(row).get(seat) == 0) {
                // 1. Book the seat
                seats.get(row).set(seat, 1);
                train.setSeats(seats);
                trainService.updateTrain(train);  // Save updated train

                // 2. Create a Ticket with required fields
                String ticketId = UUID.randomUUID().toString();
                String userId = user.getUserId();
                String source = train.getStations().get(0);  // First station (or get from user input)
                String destination = train.getStations().get(train.getStations().size() - 1); // Last station
                java.sql.Date dateOfTravel = new java.sql.Date(System.currentTimeMillis()); // Use current date

                Ticket ticket = new Ticket(ticketId, userId, source, destination, dateOfTravel, train);

                // 3. Add ticket to user's booking list and save
//                user.getTicketsBooked().add(ticket);
//                saveUserListToFile();

                user.getTicketsBooked().add(ticket);

//  update the corresponding user in the userList
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getUserId().equals(user.getUserId())) {
                        userList.set(i, user);  // update the stored user
                        break;
                    }
                }

                saveUserListToFile();  //  persist the updated list

                return Boolean.TRUE;
            } else {
                return Boolean.FALSE; // Seat already booked
            }
        } else {
            return Boolean.FALSE; // Invalid seat index
        }
    } catch (IOException ex) {
        ex.printStackTrace();  // For debugging
        return Boolean.FALSE;
    }
}

}