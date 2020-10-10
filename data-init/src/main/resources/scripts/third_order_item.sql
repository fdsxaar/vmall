CREATE TABLE `third_order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `oid` bigint NOT NULL DEFAULT '0',
  `pid` int NOT NULL DEFAULT '0',
  `num` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `oid` (`oid`),
  KEY `pid` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 