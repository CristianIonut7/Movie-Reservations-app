PRINT 'Populating Users table...';
INSERT INTO Users (FirstName, LastName, Email, PasswordHash, UserRole, Age, City, LoyaltyPoints)
VALUES 
('Admin', 'Principal', 'admin@cinema.com', 'admin_hash_placeholder', 'admin', 30, 'Bucharest', 0),
('Cristian', 'Popescu', 'cristian@email.com', 'client_hash_placeholder', 'client', 21, 'Bucharest', 150),
('Andrei', 'Ionescu', 'andrei@email.com', 'client_hash_placeholder', 'client', 25, 'Cluj-Napoca', 75),
('Maria', 'Georgescu', 'maria@email.com', 'client_hash_placeholder', 'client', 29, 'Timisoara', 320),
('Elena', 'Vasilescu', 'elena@email.com', 'client_hash_placeholder', 'client', 19, 'Iasi', 50),
('Mihai', 'Radu', 'mihai@email.com', 'client_hash_placeholder', 'client', 35, 'Bucharest', 210);
GO

PRINT 'Populating Movies table...';
INSERT INTO Movies (Title, [Description], Genre, DurationMinutes, ReleaseDate, MinAge, DirectorFirstName, DirectorLastName)
VALUES 
('Inception', 'A thief who steals corporate secrets through use of dream-sharing technology.', 'Sci-Fi', 148, '2010-07-16', 13, 'Christopher', 'Nolan'),
('Oppenheimer', 'The story of American scientist J. Robert Oppenheimer...', 'Biographical Drama', 180, '2023-07-21', 16, 'Christopher', 'Nolan'),
('Dune: Part Two', 'Paul Atreides unites with Chani and the Fremen...', 'Sci-Fi', 166, '2024-03-01', 13, 'Denis', 'Villeneuve'),
('The Shawshank Redemption', 'Two imprisoned men bond over a number of years...', 'Drama', 142, '1994-10-14', 16, 'Frank', 'Darabont'),
('The Godfather', 'The aging patriarch of an organized crime dynasty transfers control...', 'Crime', 175, '1972-03-24', 18, 'Francis Ford', 'Coppola'),
('Pulp Fiction', 'The lives of two mob hitmen, a boxer, a gangster and his wife...', 'Crime', 154, '1994-10-14', 18, 'Quentin', 'Tarantino');
GO

PRINT 'Populating Rooms table...';
INSERT INTO Rooms (RoomType, Capacity)
VALUES
('IMAX', 150),
('VIP', 40),
('Normal', 100),
('4K', 120);
GO

PRINT 'Populating Seats for all rooms...';
DECLARE @Room1_ID INT = 1; DECLARE @Row INT = 1; DECLARE @Seat INT;
WHILE @Row <= 10 BEGIN SET @Seat = 1; WHILE @Seat <= 15 BEGIN INSERT INTO Seats (RoomID, RowNumber, SeatNumber) VALUES (@Room1_ID, @Row, @Seat); SET @Seat = @Seat + 1; END SET @Row = @Row + 1; END;

DECLARE @Room2_ID INT = 2; SET @Row = 1;
WHILE @Row <= 5 BEGIN SET @Seat = 1; WHILE @Seat <= 8 BEGIN INSERT INTO Seats (RoomID, RowNumber, SeatNumber) VALUES (@Room2_ID, @Row, @Seat); SET @Seat = @Seat + 1; END SET @Row = @Row + 1; END;

DECLARE @Room3_ID INT = 3; SET @Row = 1;
WHILE @Row <= 10 BEGIN SET @Seat = 1; WHILE @Seat <= 10 BEGIN INSERT INTO Seats (RoomID, RowNumber, SeatNumber) VALUES (@Room3_ID, @Row, @Seat); SET @Seat = @Seat + 1; END SET @Row = @Row + 1; END;

DECLARE @Room4_ID INT = 4; SET @Row = 1;
WHILE @Row <= 10 BEGIN SET @Seat = 1; WHILE @Seat <= 12 BEGIN INSERT INTO Seats (RoomID, RowNumber, SeatNumber) VALUES (@Room4_ID, @Row, @Seat); SET @Seat = @Seat + 1; END SET @Row = @Row + 1; END;
GO

PRINT 'Populating Showtimes table...';
INSERT INTO Showtimes (MovieID, RoomID, StartTime, TicketPrice)
VALUES
(1, 1, '2025-11-20 18:00:00', 35.50),
(1, 1, '2025-11-20 21:00:00', 35.50),
(2, 4, '2025-11-20 19:00:00', 32.00),
(3, 1, '2025-11-21 17:00:00', 40.00),
(4, 3, '2025-11-21 20:00:00', 25.00),
(5, 2, '2025-11-21 21:00:00', 50.00),
(6, 3, '2025-11-20 22:00:00', 25.00);
GO

PRINT 'Populating Bookings table...';
INSERT INTO Bookings (UserID, ShowtimeID, [Status])
VALUES
(2, 1, 'Confirmed'),
(3, 3, 'Confirmed'),
(4, 6, 'Pending'),
(2, 4, 'Confirmed');
GO

PRINT 'Populating BookedSeats (N:N relationship)...';
INSERT INTO BookedSeats (BookingID, SeatID) VALUES (1, 70);
INSERT INTO BookedSeats (BookingID, SeatID) VALUES (1, 71);

INSERT INTO BookedSeats (BookingID, SeatID) VALUES (2, 312);

INSERT INTO BookedSeats (BookingID, SeatID) VALUES (3, 160);

INSERT INTO BookedSeats (BookingID, SeatID) VALUES (4, 100);
GO

PRINT 'All tables populated.';