USE MovieReservationDB;
GO

ALTER TABLE Bookings
ADD PaidPrice DECIMAL(10, 2);
GO

-- Optional: Set existing bookings to have 0 or some default if needed
-- UPDATE Bookings SET PaidPrice = 0 WHERE PaidPrice IS NULL;
GO
