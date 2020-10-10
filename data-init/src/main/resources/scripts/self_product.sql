CREATE TABLE `self_product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `storehouse_id` int NOT NULL,
  `product_id` int NOT NULL,
  `inventory` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `store_house_id` (`storehouse_id`,`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 