package ca.uwaterloo.iss4e.dto;

/**
 * Created by xiliu on 27/11/14.
 */
public class Account {
    int userID;
    String username;
    String password;
    String retypePassword;
    String firstName;
    String lastName;
    String email;
    int roleID;
    String roleName;
    int[] powerMeterIDs;
    int[] waterMeterIDs;

    String message;

    public Account(String username, String password, String retypePassword, String firstName, String lastName, String email, String message) {
        this.username = username;
        this.password = password;
        this.retypePassword = retypePassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.message = message;
    }
    public Account() {
        this("","", "","","","","");
    }


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int[] getPowerMeterIDs() {
        return powerMeterIDs;
    }

    public void setPowerMeterIDs(int[] powerMeterIDs) {
        this.powerMeterIDs = powerMeterIDs;
    }

    public int[] getWaterMeterIDs() {
        return waterMeterIDs;
    }

    public void setWaterMeterIDs(int[] waterMeterIDs) {
        this.waterMeterIDs = waterMeterIDs;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRetypePassword() {
        return retypePassword;
    }

    public void setRetypePassword(String retypePassword) {
        this.retypePassword = retypePassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
