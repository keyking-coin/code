/*
Navicat MySQL Data Transfer

Source Server         : ±¾µØ
Source Server Version : 50520
Source Host           : localhost:3306
Source Database       : coin

Target Server Type    : MYSQL
Target Server Version : 50520
File Encoding         : 65001

Date: 2016-02-24 01:28:30
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `accountapply`
-- ----------------------------
DROP TABLE IF EXISTS `accountapply`;
CREATE TABLE `accountapply` (
  `id` int(15) NOT NULL AUTO_INCREMENT,
  `bourse` varchar(32) NOT NULL,
  `bankName` varchar(64) NOT NULL,
  `tel` varchar(12) NOT NULL,
  `email` varchar(128) DEFAULT NULL,
  `indentFront` varchar(128) NOT NULL,
  `indentBack` varchar(128) NOT NULL,
  `bankFront` varchar(128) NOT NULL,
  `completed` smallint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of accountapply
-- ----------------------------
INSERT INTO `accountapply` VALUES ('2', '(åäº¬)åäº¬æäº¤æé±å¸é®ç¥¨äº¤æä¸­å¿', 'å·¥åé¶è¡', '13856094894', 'keyking@163.com', '13856094894-indent-front.png', '13856094894-indent-back.png', '13856094894-bank-front-2015-11-49-6-33.png', '0');

-- ----------------------------
-- Table structure for `deal`
-- ----------------------------
DROP TABLE IF EXISTS `deal`;
CREATE TABLE `deal` (
  `id` bigint(15) NOT NULL COMMENT 'æäº¤èªå·±çç¼å·',
  `uid` int(12) NOT NULL COMMENT 'åå®¶ç¼å·',
  `sellFlag` tinyint(1) NOT NULL COMMENT 'æ¯å¦æ¯åºå®',
  `_revoke` smallint(1) NOT NULL COMMENT '0',
  `type` tinyint(1) NOT NULL COMMENT 'äº¤å²æ¹å¼',
  `bourse` varchar(32) NOT NULL COMMENT 'æäº¤æåç§°',
  `name` varchar(32) NOT NULL COMMENT 'èååç§°',
  `price` float(15,0) NOT NULL COMMENT 'åä»·',
  `monad` varchar(10) NOT NULL,
  `num` int(10) NOT NULL COMMENT 'æ°é',
  `validTime` varchar(128) NOT NULL COMMENT 'æææ¶é´',
  `createTime` datetime NOT NULL COMMENT 'å»ºåæ¶é´',
  `other` varchar(100) NOT NULL COMMENT 'ä»å¶æè¿°',
  `needDeposit` float(15,0) NOT NULL,
  `helpFlag` smallint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deal
-- ----------------------------
INSERT INTO `deal` VALUES ('0', '1', '1', '0', '0', '0,åäº¬æäº¤æ', 'ç´å¹´æå', '50', 'ç', '1000', '2016-03-03 23:59:00', '2016-02-22 20:36:20', 'éåº¦äºï¼æ²¡å¤å°äº', '50000', '0');
INSERT INTO `deal` VALUES ('7', '1', '0', '0', '1', '1,åè¥', 'é£æºå°ç', '0', 'ç', '500', '2015-10-10 10:14:24', '2015-10-03 10:14:57', 'æ¥éï¼æççµè¯èç³»æ13856094894', '0', '1');
INSERT INTO `deal` VALUES ('8', '1', '1', '0', '0', '0,åäº¬æäº¤æ', 'è§é³å', '20', 'ç', '1000', '2015-10-10 22:42:24', '2015-10-03 22:42:57', 'æ¥å®ï¼è¦çéåº¦äº', '0', '0');
INSERT INTO `deal` VALUES ('9', '1', '1', '0', '0', '0,åäº¬æäº¤æ', 'è§é³åå¤§', '20', 'ç­¾', '2000', '2015-10-17 22:42:24', '2015-10-03 22:43:22', 'æ¥å®ï¼è¦çéåº¦äº', '0', '1');
INSERT INTO `deal` VALUES ('10', '1', '0', '0', '1', '1,åè¥', 'æµè¯çé®ç¥¨', '0', 'ä¸ª', '500', '2015-10-24 17:35:45', '2015-10-12 17:37:00', 'è¿ä¸ªæ¯ç¨æ¥æµè¯ä¸å¿è¾ç', '0', '0');
INSERT INTO `deal` VALUES ('11', '2', '0', '0', '0', '0,åäº¬æäº¤æ', 'ææçºªå¿µå¸', '0', 'å¼ ', '500', '2015-11-10 12:00:00', '2015-10-27 11:44:59', 'æçéåº¦çµè¯èç³»æã', '0', '1');
INSERT INTO `deal` VALUES ('12', '2', '0', '0', '1', '1,åè¥', 'ææçºªå¿µå¸', '3', 'æ¬', '1000', '2015-11-20 12:00:00', '2015-10-27 13:34:23', 'é¿ææ¶è´­', '0', '1');
INSERT INTO `deal` VALUES ('13', '2', '0', '0', '1', '1,åè¥', 'ææçºªå¿µå¸', '3', 'å°', '1000', '2015-11-20 12:00:00', '2015-10-27 13:35:34', 'é¿ææ¶è´­', '0', '1');
INSERT INTO `deal` VALUES ('14', '1', '1', '0', '1', '1,åè¥', 'ææçºªå¿µç', '25', 'ç®±', '500', '2016-01-09 23:59:00', '2015-12-31 22:47:34', 'æ°éæéï¼è¦çéåº¦äºã', '12500', '0');

-- ----------------------------
-- Table structure for `deal_order`
-- ----------------------------
DROP TABLE IF EXISTS `deal_order`;
CREATE TABLE `deal_order` (
  `id` int(15) NOT NULL,
  `dealId` int(15) NOT NULL,
  `buyId` int(15) NOT NULL,
  `state` smallint(2) NOT NULL,
  `times` text NOT NULL,
  `num` int(15) NOT NULL,
  `price` float(15,0) NOT NULL,
  `appraise` text,
  `helpFlag` smallint(1) NOT NULL,
  `_revoke` smallint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deal_order
-- ----------------------------
INSERT INTO `deal_order` VALUES ('1', '9', '2', '0', '[\"2015-10-04 22:15:48\"]', '40', '20', '0,0,null,null|0,0,null,null', '1', '0');
INSERT INTO `deal_order` VALUES ('2', '9', '2', '0', '[\"2015-10-06 22:04:10\"]', '60', '20', '0,0,null,null|0,0,null,null', '1', '0');
INSERT INTO `deal_order` VALUES ('3', '14', '2', '0', '[\"2015-12-31 22:48:51\"]', '50', '25', '0,0,null,null|0,0,null,null', '0', '0');
INSERT INTO `deal_order` VALUES ('4', '14', '2', '0', '[\"2015-12-31 22:48:57\"]', '50', '25', '0,0,null,null|0,0,null,null', '0', '0');
INSERT INTO `deal_order` VALUES ('5', '14', '2', '0', '[\"2016-01-02 12:01:51\"]', '50', '25', '0,0,null,null|0,0,null,null', '0', '0');
INSERT INTO `deal_order` VALUES ('6', '14', '2', '0', '[\"2016-01-02 12:03:23\"]', '50', '25', '0,0,null,null|0,0,null,null', '0', '0');
INSERT INTO `deal_order` VALUES ('7', '14', '2', '0', '[\"2016-01-02 12:06:45\"]', '50', '25', '0,0,null,null|0,0,null,null', '0', '0');
INSERT INTO `deal_order` VALUES ('8', '14', '2', '0', '[\"2016-01-02 12:08:30\"]', '50', '25', '0,0,null,null|0,0,null,null', '0', '0');
INSERT INTO `deal_order` VALUES ('9', '14', '2', '0', '[\"2016-01-02 16:34:56\"]', '50', '25', '0,0,null,null|0,0,null,null', '0', '0');
INSERT INTO `deal_order` VALUES ('10', '14', '2', '0', '[\"2016-01-03 16:06:45\"]', '50', '25', '0,0,null,null|0,0,null,null', '0', '0');
INSERT INTO `deal_order` VALUES ('11', '0', '2', '0', '[\"2016-02-22 20:43:03\"]', '20', '50', '0,0,null,null|0,0,null,null', '0', '0');
INSERT INTO `deal_order` VALUES ('12', '0', '2', '0', '[\"2016-02-22 20:49:25\"]', '1', '50', '0,0,null,null|0,0,null,null', '0', '0');
INSERT INTO `deal_order` VALUES ('13', '0', '2', '0', '[\"2016-02-22 21:03:17\"]', '1', '50', '0,0,null,null|0,0,null,null', '0', '0');

-- ----------------------------
-- Table structure for `deal_revert`
-- ----------------------------
DROP TABLE IF EXISTS `deal_revert`;
CREATE TABLE `deal_revert` (
  `id` int(15) NOT NULL,
  `dependentId` int(15) NOT NULL COMMENT 'é¶å±äº',
  `uid` int(15) NOT NULL,
  `tar` int(15) DEFAULT NULL,
  `context` text NOT NULL,
  `createTime` datetime NOT NULL,
  `_revoke` smallint(1) NOT NULL COMMENT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deal_revert
-- ----------------------------
INSERT INTO `deal_revert` VALUES ('1', '9', '2', '1', 'æä¸è¦è¿ä¸ªã', '2015-10-04 10:43:50', '0');
INSERT INTO `deal_revert` VALUES ('2', '9', '2', '1', 'ææ¯æµè¯çåå¤', '2015-10-04 11:38:35', '0');
INSERT INTO `deal_revert` VALUES ('3', '7', '2', '1', 'æèï¼æä¹æ¯æµè¯ç', '2015-10-04 11:39:00', '0');
INSERT INTO `deal_revert` VALUES ('4', '14', '2', '1', 'ä½ å¥½ï¼æä¹æ¯åè¥çãæä»¬è½é¢è°ä¹?', '2016-01-03 16:12:41', '0');
INSERT INTO `deal_revert` VALUES ('5', '14', '1', '1', 'å¯ä»¥çï¼ä½ æ¯å¨åè¥åªä¸ªåºï¼æ¹ä¾¿åè¯æä¹ï¼', '2016-01-03 16:14:57', '0');

-- ----------------------------
-- Table structure for `email`
-- ----------------------------
DROP TABLE IF EXISTS `email`;
CREATE TABLE `email` (
  `id` int(15) NOT NULL,
  `type` smallint(1) NOT NULL,
  `status` smallint(1) NOT NULL,
  `userId` int(15) NOT NULL,
  `senderId` int(15) NOT NULL,
  `isNew` smallint(1) NOT NULL,
  `time` varchar(64) NOT NULL,
  `theme` varchar(64) NOT NULL,
  `content` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of email
-- ----------------------------
INSERT INTO `email` VALUES ('1', '1', '0', '2', '1', '1', '2015-11-20 16:06:41', 'æµè¯é®ä»¶ç³»ç»', 'æè¯è¯æ¯æ¯æ¯æ¯ æ¹¿åå1234566');

-- ----------------------------
-- Table structure for `friend`
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend` (
  `uid` int(15) NOT NULL,
  `fid` int(15) NOT NULL,
  `pass` smallint(1) NOT NULL,
  `time` datetime DEFAULT NULL,
  `other` text,
  PRIMARY KEY (`uid`,`fid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of friend
-- ----------------------------
INSERT INTO `friend` VALUES ('1', '2', '1', '2016-01-11 23:13:41', null);
INSERT INTO `friend` VALUES ('2', '1', '1', '2015-12-31 22:17:55', 'å æï¼å¤§å¥');

-- ----------------------------
-- Table structure for `message`
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int(15) NOT NULL,
  `actors` text NOT NULL,
  `sendId` int(15) NOT NULL,
  `time` datetime NOT NULL,
  `content` text NOT NULL,
  `type` smallint(1) NOT NULL,
  `look` smallint(1) NOT NULL,
  `showTime` smallint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES ('1', '[1,2]', '1', '2016-02-21 23:06:16', 'ä½ å¥½ï¼æç±ä½ ', '0', '0', '0');
INSERT INTO `message` VALUES ('2', '[2,1]', '2', '2016-02-21 23:07:00', 'æ©ï¼æä¹ç±ä½ ', '0', '0', '1');

-- ----------------------------
-- Table structure for `timeline`
-- ----------------------------
DROP TABLE IF EXISTS `timeline`;
CREATE TABLE `timeline` (
  `id` bigint(15) NOT NULL AUTO_INCREMENT,
  `type` smallint(1) NOT NULL,
  `title` varchar(128) NOT NULL,
  `createTime` date NOT NULL,
  `contents` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of timeline
-- ----------------------------
INSERT INTO `timeline` VALUES ('1', '1', 'åäº¬ç³è´­', '2015-11-28', '[{\"type\":0,\"value\":\"æµè¯ä»£ç ,æ²¡ææä¹\"}]');
INSERT INTO `timeline` VALUES ('2', '3', 'ä¸æµ·æç®¡', '2015-11-30', '[{\"type\":0,\"value\":\"æµè¯ä»£ç ,æ²¡ææä¹\"}]');
INSERT INTO `timeline` VALUES ('3', '2', 'åæ¹é¢çº¦', '2015-11-29', '[{\"type\":0,\"value\":\"æµè¯ä»£ç ,æ²¡ææä¹\"}]');
INSERT INTO `timeline` VALUES ('4', '4', 'åäº¬é«çº§ä¼è®®', '2015-11-30', '[{\"type\":0,\"value\":\"æµè¯ä»£ç ,æ²¡ææä¹\"},{\"type\":1,\"value\":\"timeLineTest.png\"},{\"type\":0,\"value\":\"æµè¯ä»£ç ,æ²¡ææä¹\"}]');
INSERT INTO `timeline` VALUES ('5', '2', 'é¢çº¦', '2015-11-28', '[{\"type\":0,\"value\":\"æµè¯ä»£ç ,æ²¡ææä¹\"}]');
INSERT INTO `timeline` VALUES ('6', '3', 'é©¬å­ç²æç®¡', '2015-11-28', '[{\"type\":0,\"value\":\"æµè¯ä»£ç ,æ²¡ææä¹\"}]');
INSERT INTO `timeline` VALUES ('7', '4', 'ææ©èpart', '2015-11-28', '[{\"type\":0,\"value\":\"æµè¯ä»£ç ,æ²¡ææä¹\"}]');

-- ----------------------------
-- Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(15) NOT NULL,
  `account` varchar(15) CHARACTER SET utf8 NOT NULL,
  `pwd` varchar(32) CHARACTER SET utf8 NOT NULL,
  `face` varchar(16) CHARACTER SET utf8 NOT NULL DEFAULT 'face1',
  `nikeName` varchar(32) CHARACTER SET utf8 NOT NULL,
  `title` varchar(32) CHARACTER SET utf8 NOT NULL,
  `registTime` varchar(32) CHARACTER SET utf8 NOT NULL,
  `name` varchar(32) CHARACTER SET utf8 NOT NULL,
  `address` varchar(128) CHARACTER SET utf8 NOT NULL,
  `age` int(3) DEFAULT NULL,
  `identity` varchar(18) CHARACTER SET utf8 DEFAULT NULL,
  `push` tinyint(1) DEFAULT NULL,
  `signature` varchar(128) CHARACTER SET utf8 DEFAULT NULL,
  `recharge` text CHARACTER SET utf8,
  `bankAccount` text CHARACTER SET utf8,
  `interests` text CHARACTER SET utf8,
  `credit` text CHARACTER SET utf8,
  `seller` text CHARACTER SET utf8,
  `forbid` text CHARACTER SET utf8,
  `breach` smallint(2) DEFAULT NULL,
  `favorites` text CHARACTER SET utf8,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('1', '13856094894', '123456789', 'face1', 'èèæ§', 'éçåå®¶', '2015-07-24 08:45:36', 'å¼ ä¸', 'adadada', '30', '510725198507073834', '1', 'å¤§å®¶å¥½', '{\"curMoney\":100000,\"historyMoney\":100000,\"orders\":[]}', '[]', null, '0.0,100000.0,100000.0,1000.0,0,0,0,', '{\"deposit\":1233456,\"key\":\"124548766645\",\"needSave\":true,\"pass\":true,\"pic\":\"13856094894-indent-front.png\",\"time\":\"2015-06-23 10:15:30\",\"type\":1}', '0|null', '0', '[]');
INSERT INTO `users` VALUES ('2', '13721056986', '123456789', 'face2', 'å¤§åæ§', 'æ®éä¼å', '2015-08-02 11:23:15', 'æå', 'æ¯æ¯æ¯', '16', '125648335544565254', '1', 'å¤§å®¶å¥½', '{\"curMoney\":100000,\"historyMoney\":100000,\"orders\":[]}', '[]', null, '0.0,100000.0,100000.0,1000.0,0,0,0,', '{\"deposit\":1233456,\"key\":\"124548766645\",\"needSave\":true,\"pass\":true,\"pic\":\"13856094894-indent-front.png\",\"time\":\"2015-06-23 10:15:30\",\"type\":1}', '0|null', '0', '[14]');
INSERT INTO `users` VALUES ('3', '13135648865', '123456789', 'face3', 'å¼ ä¸', 'æ®éä¼å', '2015-08-23 15:33:29', 'çäº', 'sss', '25', '356451125465546454', '1', 'å¤§å®¶å¥½', '', '[]', null, '', '{\"deposit\":1233456,\"key\":\"124548766645\",\"needSave\":true,\"pass\":true,\"pic\":\"13856094894-indent-front.png\",\"time\":\"2015-06-23 10:15:30\",\"type\":1}', '0|null', '0', '[]');
