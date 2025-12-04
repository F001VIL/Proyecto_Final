package modelo;

public class CampusSpace extends FacilityResource {

    private String spaceType;

    public CampusSpace(int id, String name, String location, int capacity,
                       String description, String spaceType) {
        super(id, name, location, capacity, description, "CAMPUS");
        this.spaceType = spaceType;
    }

    public String getSpaceType() {
        return spaceType;
    }

    @Override
    public String generateCode() {
        return "CAMPUS-" + id;
    }
}
