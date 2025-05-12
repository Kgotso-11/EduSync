package za.ac.tut.model;

public class Tutor {
    private int userID;
    private String fullName;

    public Tutor(int userID, String fullName) {
        this.userID = userID;
        this.fullName = fullName;
    }

    public int getUserID() {
        return userID;
    }

    public String getFullName() {
        return fullName;
    }
}
