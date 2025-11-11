package modelo;

public class CampusSpace extends FacilityResource{
    private String spaceType;

    public CampusSpace(int capacity, String spaceType) {
        super(capacity);
        this.spaceType = spaceType;
    }

    public String getSpaceType() {
        return spaceType;
    }

    @Override
    public String generateCode() {
        // Random number between 1000 and 9999
        int randomNum = 1000 + (int)(Math.random() * 9000);
        return "CAMPUSSPACE-" + randomNum;
    }
}
