package ro.facultate.proiect.backend;

import jakarta.persistence.Column; // <-- Importă adnotarea
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer userID;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "LastName")
    private String lastName;

    @Column(name = "Email")
    private String email;

    @Column(name = "PasswordHash")
    private String passwordHash;

    @Column(name = "UserRole")
    private String userRole;

    @Column(name = "Age")
    private Integer age;

    @Column(name = "City")
    private String city;

    @Column(name = "LoyaltyPoints")
    private Integer loyaltyPoints;

    @Column(name = "PhoneNumber")
    private String phoneNumber;
}