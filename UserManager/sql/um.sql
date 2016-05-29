/*
Navicat MySQL Data Transfer

Source Server         : ±¾µØ
Source Server Version : 50520
Source Host           : localhost:3306
Source Database       : um

Target Server Type    : MYSQL
Target Server Version : 50520
File Encoding         : 65001

Date: 2016-05-29 23:09:31
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `brokers`
-- ----------------------------
DROP TABLE IF EXISTS `brokers`;
CREATE TABLE `brokers` (
  `id` bigint(15) NOT NULL,
  `name` varchar(128) NOT NULL,
  `des` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of brokers
-- ----------------------------

-- ----------------------------
-- Table structure for `ubs`
-- ----------------------------
DROP TABLE IF EXISTS `ubs`;
CREATE TABLE `ubs` (
  `bid` bigint(15) NOT NULL,
  `uid` bigint(15) NOT NULL,
  `account` varchar(128) NOT NULL,
  `tel` varchar(32) NOT NULL,
  PRIMARY KEY (`bid`,`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ubs
-- ----------------------------

-- ----------------------------
-- Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(15) NOT NULL,
  `fid` bigint(15) NOT NULL,
  `account` varchar(15) CHARACTER SET utf8 NOT NULL,
  `pwd` varchar(32) CHARACTER SET utf8 NOT NULL,
  `registTime` datetime NOT NULL,
  `name` varchar(32) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of users
-- ----------------------------
