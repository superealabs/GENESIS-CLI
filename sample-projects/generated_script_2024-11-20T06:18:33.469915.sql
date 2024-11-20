CREATE TABLE IF NOT EXISTS `houses` (
  `house_id` INT PRIMARY KEY,
  `address` VARCHAR(255),
  `city` VARCHAR(100),
  `state` VARCHAR(50),
  `zip` VARCHAR(10),
  `purchase_price` FLOAT,
  `purchase_date` DATETIME,
  `current_value` FLOAT,
  `square_feet` INT,
  ` bedrooms` INT,
  `bathrooms` INT
);
