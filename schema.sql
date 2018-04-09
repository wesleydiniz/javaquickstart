/**
    Schema and tables creation script

    user: root
    password: root
    mysqlport: 3306

**/

CREATE DATABASE logparser;

CREATE TABLE logparser.log(
	id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	logDate DATETIME,
    ipAddress VARCHAR(50),
    protocol VARCHAR(50),
    statusCode SMALLINT,
    userAgent VARCHAR(255)
);

CREATE TABLE logparser.blockedip (
	id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    ipAddress VARCHAR(50),
    startDate DATETIME,
    endDate DATETIME,
    reason VARCHAR(255)
);


