create database RestaurantManagementSystem

CREATE TABLE Customer (
    CustomerID INT IDENTITY(1,1) PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Email VARCHAR(255) UNIQUE NOT NULL,
    PhoneNumber VARCHAR(15) UNIQUE NOT NULL
);

CREATE TABLE Admin (
    AdminID INT IDENTITY(1,1) PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE RestaurantTable (
    TableID INT IDENTITY(1,1) PRIMARY KEY,
    Capacity INT NOT NULL,
    Status BIT DEFAULT 1 -- '1' represents Available
);

CREATE TABLE Reservation (
    ReservationID INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID INT NOT NULL,
    TableID INT,
    ReservationTime DATETIME NOT NULL,
    ConfirmationSent BIT DEFAULT 0, -- '0' represents FALSE
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID) ON DELETE CASCADE,
    FOREIGN KEY (TableID) REFERENCES RestaurantTable(TableID) ON DELETE SET NULL
);

CREATE TABLE Feedback (
    FeedbackID INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID INT NOT NULL,
    Description TEXT,
    Rating INT CHECK (Rating BETWEEN 1 AND 5),
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID) ON DELETE CASCADE
);

CREATE TABLE Payment (
    PaymentID INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID INT NOT NULL,
    Amount DECIMAL(10, 2) NOT NULL,
    PaymentType VARCHAR(10) CHECK (PaymentType IN ('Cash', 'Online')),
    PaymentStatus BIT DEFAULT 0, -- '0' represents FALSE
    ReservationID INT,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID) ON DELETE CASCADE,
    FOREIGN KEY (ReservationID) REFERENCES Reservation(ReservationID) ON DELETE NO ACTION
);

CREATE TABLE Report (
    ReportID INT IDENTITY(1,1) PRIMARY KEY,
    AdminID INT NOT NULL,
    Date DATE NOT NULL,
    Data TEXT,
    FOREIGN KEY (AdminID) REFERENCES Admin(AdminID) ON DELETE CASCADE
);

CREATE TABLE SMSLog (
    LogID INT IDENTITY(1,1) PRIMARY KEY,
    PhoneNumber VARCHAR(15) NOT NULL,
    Message TEXT NOT NULL,
    SentTime DATETIME DEFAULT GETDATE(),
    Status BIT DEFAULT 1 -- '1' represents TRUE
);

