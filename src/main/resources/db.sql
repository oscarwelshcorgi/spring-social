CREATE TABLE users
(
     id INT PRIMARY KEY AUTO_INCREMENT,
     name VARCHAR(30) NOT NULL,
     email VARCHAR(30),
     password VARCHAR(30),
     role VARCHAR(30),
     provider VARCHAR(30)
) ENGINE=INNODB;