import java.util.ArrayList;

public class User {
    private String username;
    private String email;
    private String password;
    private String profilePictureUrl;
    private String name ;
    private String surname;
    private ArrayList<User> followings = new ArrayList<>();
    private ArrayList<Club> clubs = new ArrayList<>(); 

    User(String name, String surname, String username, String email, String password, String profilePictureUrl) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePictureUrl = profilePictureUrl;
    }    
    User(String name, String surname, String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePictureUrl = null; // Default to null if no profile picture URL is provided
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getusername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setusername(String username) {
        this.username = username;
    }
    public ArrayList<User> getFollowings() {
        return followings;
    }
    public void setFollowings(ArrayList<User> followings) {
        this.followings = followings;
    }
    public ArrayList<Club> getClubs() {
        return clubs;
    }
    public void setClubs(ArrayList<Club> clubs) {
        this.clubs = clubs;
    }
    public void follow(User user) {
        if (!followings.contains(user)) {
            followings.add(user);
        }
    }
    public void unfollow(User user) {
        followings.remove(user);
    }
    public void addToClub(Club club) {
        if (!clubs.contains(club)) {
            clubs.add(club);
            club.addMember(this); 
        }
    }
    public void removeFromClub(Club club) {
        clubs.remove(club);
        club.removeMember(this); 
    }
}