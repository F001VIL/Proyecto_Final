package modelo;

public class Room extends FacilityResource{
    private String roomType;

    public Room(int capacity, String roomType) {
        super(capacity);
        this.roomType = roomType;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String generateCode() {
        // Random number between 1000 and 9999
        int randomNum = 1000 + (int)(Math.random() * 9000);
        return "ROOM-" + randomNum;
    }
}
