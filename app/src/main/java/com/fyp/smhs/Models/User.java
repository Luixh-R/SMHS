package com.fyp.smhs.Models;



  /*  public class User {

        public String username;
        public String email;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }
}
*/

public class User {

    public String date_of_birth;
    public String full_name;
    public String nickname;

    public User(String dateOfBirth, String fullName) {
        // ...
    }

    public User(String dateOfBirth, String fullName, String nickname) {
        // ...
    }

}