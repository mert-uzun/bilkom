import java.util.ArrayList;

public class Club {
    String name;
    ArrayList<User> users = new ArrayList<>();
    ClubAdmin admin; 
    String clubPictureUrl;

    Club(String name, ClubAdmin admin, String clubPictureUrl) {
        this.name = name;
        this.admin = admin;
        this.clubPictureUrl = clubPictureUrl;
    }
    Club(String name, ClubAdmin admin) {
        this.name = name;
        this.admin = admin;
        this.clubPictureUrl = null;
    }
    Club(String name) {
        this.name = name;
        this.admin = null;
        this.clubPictureUrl = null;
    }
    public void addMember(User user) {
        users.add(user);
    }
    public void removeMember(User user) {
        users.remove(user);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAdmin(ClubAdmin admin) {
        this.admin = admin;
    }
    public void setClubPictureUrl(String clubPictureUrl) {
        this.clubPictureUrl = clubPictureUrl;
    }
    public ClubAdmin getAdmin() {
        return admin;
    }
    public String getClubPictureUrl() {
        return clubPictureUrl;
    }
    public ArrayList<User> getUsers() {
        return users;
    }
    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
