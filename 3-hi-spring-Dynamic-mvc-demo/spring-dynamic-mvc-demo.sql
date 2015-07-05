
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


CREATE DATABASE IF NOT EXISTS `springmvc` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
USE `springmvc`;



DROP TABLE IF EXISTS `t_teacher`;
CREATE TABLE IF NOT EXISTS `t_teacher` (
  `id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `teacherName` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `age` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `birthday` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


DROP TABLE IF EXISTS `t_user`;
CREATE TABLE IF NOT EXISTS `t_user` (
  `id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `username` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `age` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `birthday` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
