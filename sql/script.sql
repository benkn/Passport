CREATE SCHEMA IF NOT EXISTS `passport` DEFAULT CHARACTER SET latin1 ;
USE `passport` ;

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `password` varchar(128) NOT NULL,
  `email` varchar(100) NOT NULL,
  `confirmationCode` varchar(100) NULL,
  `creationDate` datetime NOT NULL,
  `verified` boolean NOT NULL DEFAULT false,
  `role` varchar(128) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`username`)
);
