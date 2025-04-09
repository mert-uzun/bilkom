public class ClubAdmin extends User{
    private Club club;
    ClubAdmin(String name, String surname, String username, String email, String password, Club club) {
        super(name, surname, username, email, password);
        this.club = club;
    }
    ClubAdmin(String name, String surname, String username, String email, String password, Club club, String profilePictureUrl) {
        super(name, surname, username, email, password, profilePictureUrl);
        this.club = club;
    }
    public Club getClub() {
        return club;
    }
    public void setClub(Club club) {
        this.club = club;
    }

    
}