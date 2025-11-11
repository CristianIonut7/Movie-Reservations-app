CREATE TABLE Users (
    UserID INT PRIMARY KEY IDENTITY(1,1),
    FirstName VARCHAR(100) NOT NULL,
    LastName VARCHAR(100) NOT NULL,
    Email VARCHAR(255) NOT NULL UNIQUE,
    PhoneNumber VARCHAR(20),
    PasswordHash VARCHAR(255) NOT NULL,
    Age INT,
    City VARCHAR(100),
    LoyaltyPoints INT DEFAULT 0,
    UserRole VARCHAR(10) DEFAULT 'client' NOT NULL,
    
    CONSTRAINT CHK_UserRole CHECK (UserRole IN ('client', 'admin'))
);
GO

CREATE TABLE Movies (
    MovieID INT PRIMARY KEY IDENTITY(1,1),
    Title VARCHAR(255) NOT NULL,
    [Description] TEXT,
    Genre VARCHAR(100),
    DurationMinutes INT NOT NULL,
    ReleaseDate DATE,
    MinAge INT DEFAULT 0,
    DirectorFirstName VARCHAR(100),
    DirectorLastName VARCHAR(100),
    
    CONSTRAINT CHK_DurationMinutes CHECK (DurationMinutes > 0)
);
GO

CREATE TABLE Rooms (
    RoomID INT PRIMARY KEY IDENTITY(1,1),
    RoomType VARCHAR(50) DEFAULT 'Normal',
    Capacity INT NOT NULL,
    
    CONSTRAINT CHK_RoomType CHECK (RoomType IN ('VIP', '7D', 'Normal', '4K', 'IMAX'))
);
GO

CREATE TABLE Seats (
    SeatID INT PRIMARY KEY IDENTITY(1,1),
    RoomID INT NOT NULL,
    RowNumber INT NOT NULL,
    SeatNumber INT NOT NULL,
    
    CONSTRAINT FK_Seats_Rooms FOREIGN KEY (RoomID)
        REFERENCES Rooms(RoomID)
        ON DELETE CASCADE,
        
    CONSTRAINT UQ_Room_Row_Seat UNIQUE (RoomID, RowNumber, SeatNumber)
);
GO

CREATE TABLE Showtimes (
    ShowtimeID INT PRIMARY KEY IDENTITY(1,1),
    MovieID INT NOT NULL,
    RoomID INT NOT NULL,
    StartTime DATETIME NOT NULL,
    TicketPrice DECIMAL(10, 2) NOT NULL,
    
    CONSTRAINT FK_Showtimes_Movies FOREIGN KEY (MovieID)
        REFERENCES Movies(MovieID)
        ON DELETE CASCADE,
        
    CONSTRAINT FK_Showtimes_Rooms FOREIGN KEY (RoomID)
        REFERENCES Rooms(RoomID)
        ON DELETE CASCADE,
        
    CONSTRAINT CHK_TicketPrice CHECK (TicketPrice >= 0)
);
GO

CREATE TABLE Bookings (
    BookingID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT NOT NULL,
    ShowtimeID INT NOT NULL,
    BookingTime DATETIME DEFAULT GETDATE(),
    [Status] VARCHAR(20) NOT NULL,
    
    CONSTRAINT FK_Bookings_Users FOREIGN KEY (UserID)
        REFERENCES Users(UserID),
        
    CONSTRAINT FK_Bookings_Showtimes FOREIGN KEY (ShowtimeID)
        REFERENCES Showtimes(ShowtimeID),
        
    CONSTRAINT CHK_BookingStatus CHECK ([Status] IN ('Confirmed', 'Pending', 'Cancelled'))
);
GO

CREATE TABLE BookedSeats (
    BookingID INT NOT NULL,
    SeatID INT NOT NULL,
    
    CONSTRAINT PK_BookedSeats PRIMARY KEY (BookingID, SeatID),
    
    CONSTRAINT FK_BookedSeats_Bookings FOREIGN KEY (BookingID)
        REFERENCES Bookings(BookingID)
        ON DELETE CASCADE,
        
    CONSTRAINT FK_BookedSeats_Seats FOREIGN KEY (SeatID)
        REFERENCES Seats(SeatID)
);
GO