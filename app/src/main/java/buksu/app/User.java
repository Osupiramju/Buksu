package buksu.app;

import androidx.appcompat.app.AppCompatActivity;

public class User extends AppCompatActivity{

    private String nick, email, password, city, town;

    //Constructor
    public User() { }

    //"Getters" y "Setters"
    public String getNick() {return nick;}

    public void setNick(String nick) {this.nick = nick;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {return city;}

    public void setCity(String city) {this.city = city;}

    public String getTown() {return town;}

    public void setTown(String town) {this.town = town;}

}
