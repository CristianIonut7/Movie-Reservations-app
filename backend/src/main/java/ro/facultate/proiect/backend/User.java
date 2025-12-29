package ro.facultate.proiect.backend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer userID;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private String userRole;
    private Integer age;
    private String city;
    private Integer loyaltyPoints;
    private String phoneNumber;
}