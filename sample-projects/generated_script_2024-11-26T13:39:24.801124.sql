CREATE TABLE IF NOT EXISTS Computer
(
    ID              INT PRIMARY KEY,
    Name            VARCHAR(255),
    Manufacturer    VARCHAR(255),
    Processor       VARCHAR(255),
    RAM             BIGINT,
    Storage         BIGINT,
    OperatingSystem VARCHAR(255),
    PurchaseDate    DATETIME
);
