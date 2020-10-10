CREATE TABLE `third_product` (
  `id` int NOT NULL,
  `sid` int NOT NULL,
  `inventory` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `sid` (`sid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;