/*
SQLyog Community Edition- MySQL GUI v8.01 
MySQL - 5.1.32-community : Database - scir
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`scir` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `scir`;

/*Table structure for table `sci_tickets` */

DROP TABLE IF EXISTS `sci_tickets`;

CREATE TABLE `sci_tickets` (
  `ticket_id` int(11) NOT NULL AUTO_INCREMENT,
  `summary` varchar(1000) NOT NULL,
  `ticket_type` enum('Electricity','Road','Sanitation','Sewage','Water') NOT NULL,
  `severity` enum('Low','Normal','High','Urgent') NOT NULL DEFAULT 'Normal',
  `image_url` varchar(1000) DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `ticket_time` datetime NOT NULL,
  `msisdn` varchar(20) DEFAULT NULL,
  `device_id` varchar(255) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `updated_on` datetime DEFAULT NULL,
  `updated_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`ticket_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Table structure for table `sci_users` */

DROP TABLE IF EXISTS `sci_users`;

CREATE TABLE `sci_users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `msisdn` varchar(20) NOT NULL,
  `device_id` varchar(100) DEFAULT NULL,
  `user_type` tinyint(4) NOT NULL DEFAULT '1',
  `is_admin` tinyint(4) NOT NULL DEFAULT '0',
  `is_active` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
