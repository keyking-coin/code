/*
Navicat MySQL Data Transfer

Source Server         : ����
Source Server Version : 50520
Source Host           : localhost:3306
Source Database       : coin

Target Server Type    : MYSQL
Target Server Version : 50520
File Encoding         : 65001

Date: 2016-01-18 23:10:55
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
INSERT INTO `accountapply` VALUES ('2', '(南京)南京文交所钱币邮票交易中心', '工商银行', '13856094894', 'keyking@163.com', '13856094894-indent-front.png', '13856094894-indent-back.png', '13856094894-bank-front-2015-11-49-6-33.png', '0');

-- ----------------------------
-- Table structure for `deal`
-- ----------------------------
DROP TABLE IF EXISTS `deal`;
CREATE TABLE `deal` (
  `id` bigint(15) NOT NULL COMMENT '易交自己的编号',
  `uid` int(12) NOT NULL COMMENT '卖家编号',
  `sellFlag` tinyint(1) NOT NULL COMMENT '是否是出售',
  `_revoke` smallint(1) NOT NULL COMMENT '0',
  `type` tinyint(1) NOT NULL COMMENT '交割方式',
  `bourse` varchar(32) NOT NULL COMMENT '文交所名称',
  `name` varchar(32) NOT NULL COMMENT '藏品名称',
  `price` float(15,0) NOT NULL COMMENT '单价',
  `monad` varchar(10) NOT NULL,
  `num` int(10) NOT NULL COMMENT '数量',
  `validTime` varchar(128) NOT NULL COMMENT '有效时间',
  `createTime` datetime NOT NULL COMMENT '建创时间',
  `other` varchar(100) NOT NULL COMMENT '他其描述',
  `needDeposit` float(15,0) NOT NULL,
  `helpFlag` smallint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deal
-- ----------------------------
INSERT INTO `deal` VALUES ('7', '1', '0', '0', '1', '1,合肥', '飞机小版', '0', '盒', '500', '2015-10-10 10:14:24', '2015-10-03 10:14:57', '急需，有的电话联系我13856094894', '0', '1');
INSERT INTO `deal` VALUES ('8', '1', '1', '0', '0', '0,南京文交所', '观音像', '20', '版', '1000', '2015-10-10 22:42:24', '2015-10-03 22:42:57', '急售，要的速度了', '0', '0');
INSERT INTO `deal` VALUES ('9', '1', '1', '0', '0', '0,南京文交所', '观音像大', '20', '签', '2000', '2015-10-17 22:42:24', '2015-10-03 22:43:22', '急售，要的速度了', '0', '1');
INSERT INTO `deal` VALUES ('10', '1', '0', '0', '1', '1,合肥', '测试的邮票', '0', '个', '500', '2015-10-24 17:35:45', '2015-10-12 17:37:00', '这个是用来测试不必较真', '0', '0');
INSERT INTO `deal` VALUES ('11', '2', '0', '0', '0', '0,南京文交所', '抗战纪念币', '0', '张', '500', '2015-11-10 12:00:00', '2015-10-27 11:44:59', '有的速度电话联系我。', '0', '1');
INSERT INTO `deal` VALUES ('12', '2', '0', '0', '1', '1,合肥', '抗战纪念币', '3', '本', '1000', '2015-11-20 12:00:00', '2015-10-27 13:34:23', '长期收购', '0', '1');
INSERT INTO `deal` VALUES ('13', '2', '0', '0', '1', '1,合肥', '抗战纪念币', '3', '封', '1000', '2015-11-20 12:00:00', '2015-10-27 13:35:34', '长期收购', '0', '1');
INSERT INTO `deal` VALUES ('14', '1', '1', '0', '1', '1,合肥', '抗战纪念版', '25', '箱', '500', '2016-01-09 23:59:00', '2015-12-31 22:47:34', '数量有限，要的速度了。', '12500', '0');

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

-- ----------------------------
-- Table structure for `deal_revert`
-- ----------------------------
DROP TABLE IF EXISTS `deal_revert`;
CREATE TABLE `deal_revert` (
  `id` int(15) NOT NULL,
  `dependentId` int(15) NOT NULL COMMENT '隶属于',
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
INSERT INTO `deal_revert` VALUES ('1', '9', '2', '1', '我不要这个。', '2015-10-04 10:43:50', '0');
INSERT INTO `deal_revert` VALUES ('2', '9', '2', '1', '我是测试的回复', '2015-10-04 11:38:35', '0');
INSERT INTO `deal_revert` VALUES ('3', '7', '2', '1', '我草，我也是测试的', '2015-10-04 11:39:00', '0');
INSERT INTO `deal_revert` VALUES ('4', '14', '2', '1', '你好，我也是合肥的。我们能面谈么?', '2016-01-03 16:12:41', '0');
INSERT INTO `deal_revert` VALUES ('5', '14', '1', '1', '可以的，你是在合肥哪个区，方便告诉我么？', '2016-01-03 16:14:57', '0');

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
INSERT INTO `email` VALUES ('1', '1', '0', '2', '1', '1', '2015-11-20 16:06:41', '测试邮件系统', '我试试是是是是 湿哒哒1234566');

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
INSERT INTO `friend` VALUES ('2', '1', '1', '2015-12-31 22:17:55', '加我，大哥');

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
INSERT INTO `timeline` VALUES ('1', '1', '南京申购', '2015-11-28', '[{\"type\":0,\"value\":\"测试代码,没有意义\"}]');
INSERT INTO `timeline` VALUES ('2', '3', '上海托管', '2015-11-30', '[{\"type\":0,\"value\":\"测试代码,没有意义\"}]');
INSERT INTO `timeline` VALUES ('3', '2', '南方预约', '2015-11-29', '[{\"type\":0,\"value\":\"测试代码,没有意义\"}]');
INSERT INTO `timeline` VALUES ('4', '4', '北京高级会议', '2015-11-30', '[{\"type\":0,\"value\":\"测试代码,没有意义\"},{\"type\":1,\"value\":\"timeLineTest.png\"},{\"type\":0,\"value\":\"测试代码,没有意义\"}]');
INSERT INTO `timeline` VALUES ('5', '2', '预约', '2015-11-28', '[{\"type\":0,\"value\":\"测试代码,没有意义\"}]');
INSERT INTO `timeline` VALUES ('6', '3', '马六甲托管', '2015-11-28', '[{\"type\":0,\"value\":\"测试代码,没有意义\"}]');
INSERT INTO `timeline` VALUES ('7', '4', '感恩节part', '2015-11-28', '[{\"type\":0,\"value\":\"测试代码,没有意义\"}]');

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
  `weixin_num` varchar(32) CHARACTER SET utf8 NOT NULL,
  `qq_num` varchar(32) CHARACTER SET utf8 NOT NULL,
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
INSERT INTO `users` VALUES ('1', '13856094894', '123456789', 'face1', '萝莉控', 'keyking@163.com', '13565422', '张三', 'adadada', '30', '510725198507073834', '1', '大家好', '{\"curMoney\":100000,\"historyMoney\":100000,\"orders\":[]}', '[]', null, '0.0,100000.0,100000.0,0.0,0,0,0,', '{\"deposit\":1233456,\"key\":\"124548766645\",\"needSave\":true,\"pass\":true,\"pic\":\"13856094894-indent-front.png\",\"time\":\"2015-06-23 10:15:30\",\"type\":1}', '0|null', '0', '[]');
INSERT INTO `users` VALUES ('2', '13721056986', '123456789', 'face2', '大叔控', 'luoji@163.com', '125644221', '李四', '是是是', '16', '125648335544565254', '1', '大家好', '{\"curMoney\":100000,\"historyMoney\":100000,\"orders\":[]}', '[]', null, '0.0,100000.0,100000.0,0.0,0,0,0,', '{\"deposit\":1233456,\"key\":\"124548766645\",\"needSave\":true,\"pass\":true,\"pic\":\"13856094894-indent-front.png\",\"time\":\"2015-06-23 10:15:30\",\"type\":1}', '0|null', '0', '[14]');
INSERT INTO `users` VALUES ('3', '13135648865', '123456789', 'face3', '张三', 'zhangsan@163.com', '1596895412511', '王五', 'sss', '25', '356451125465546454', '1', '大家好', '', '[]', null, '', '{\"deposit\":1233456,\"key\":\"124548766645\",\"needSave\":true,\"pass\":true,\"pic\":\"13856094894-indent-front.png\",\"time\":\"2015-06-23 10:15:30\",\"type\":1}', '0|null', '0', '[]');
