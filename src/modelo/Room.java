package modelo;

public class Room extends FacilityResource {

    private String roomType;

    public Room(int id, String name, String location, int capacity,
                String description, String roomType) {
        super(id, name, location, capacity, description, "ROOM");
        this.roomType = roomType;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String generateCode() {
        return "ROOM-" + id;
    }
}
