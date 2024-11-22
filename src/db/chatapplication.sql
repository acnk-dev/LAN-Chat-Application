CREATE DATABASE IF NOT EXISTS LANChatApplication;
USE LANChatApplication;

CREATE TABLE users (
    userid INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    profile_picture LONGBLOB,
    status VARCHAR(20) DEFAULT 'offline'
);

CREATE TABLE undeliveredMessages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receiver_id INT NOT NULL,
    message TEXT NOT NULL
);

CREATE TABLE RSAPublicKey (
    client_id INT PRIMARY KEY,
    public_key TEXT NOT NULL
);
