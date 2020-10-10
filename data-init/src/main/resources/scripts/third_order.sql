CREATE TABLE `third_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `oid` bigint NOT NULL,
  `sid` int NOT NULL,
  `firstname` varchar(100) DEFAULT NULL,
  `lastname` varchar(100) DEFAULT NULL,
  `telephone` varchar(15) NOT NULL,
  `address` varchar(250) NOT NULL,
  `status` varchar(10) NOT NULL,
  `created` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `oid` (`oid`),
  KEY `sid` (`sid`),
  KEY `sid_and_oid` (`sid`,`oid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;