CREATE TABLE `storehouse` (
  `id` int NOT NULL AUTO_INCREMENT,
  `shid` int NOT NULL,
  `nshid` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `shid` (`shid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 