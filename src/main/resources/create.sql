CREATE database test;

use test;

drop TABLE if exists test;



CREATE TABLE `test` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id，标示',
  `name` varchar(255) NOT NULL COMMENT '用户名称',
  `add_time` datetime NOT NULL COMMENT '添加时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
);


INSERT INTO `test` (`name`, `add_time`, `update_time`) VALUES ('v3', now(), now()),('v2', now(), now());

delete from test where id = 1;


drop TABLE if exists t2;

CREATE TABLE `t2` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id，用户标示',
  `name` varchar(255) NOT NULL COMMENT '用户名称',
  `extra` varchar(255) DEFAULT NULL COMMENT '补充信息',
  `mark` bigint(20) DEFAULT NULL COMMENT '标记时间',
  `add_time` datetime NOT NULL COMMENT '添加时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ;


INSERT INTO `t2` (`name`, `add_time`, `update_time`, `mark`)
VALUES ('v1', now(), now(), UNIX_TIMESTAMP()),('v2', now(), now(), UNIX_TIMESTAMP());

INSERT INTO `t2` (`name`, `add_time`, `update_time`, `mark`)
VALUES ('v3', now(), now(), UNIX_TIMESTAMP()),('v4', now(), now(), UNIX_TIMESTAMP());


update t2 set update_time = now() where id > 1;

drop TABLE if exists t3;


CREATE TABLE `t3` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id，用户标示',
  `name` varchar(255) NOT NULL COMMENT '用户名称',
  `status` tinyint(3) NOT NULL DEFAULT '0' COMMENT '用户状态: 0-正常；-1封禁',
  `extra` text DEFAULT NULL COMMENT '补充信息',
  `add_time` datetime NOT NULL COMMENT '添加时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='表';


INSERT INTO `t3` (`name`, `add_time`, `update_time`, `extra`) VALUES ('v4', now(), now(), "补充信息");

delete from t3 where id >1;

INSERT INTO `t3` (`name`, `add_time`, `update_time`) VALUES ('v4', now(), now()),('v2', now(), now()),('v3', now(), now());


show columns from t3;

select * from test.t3 limit 0;


CREATE USER 'slave'@'%.%.%.%' IDENTIFIED BY 'slave';
GRANT REPLICATION SLAVE ON *.* TO 'slave'@'%.%.%.%';
GRANT REPLICATION CLIENT ON *.* TO 'slave'@'%.%.%.%';

GRANT SELECT,CREATE,DELETE,DROP,UPDATE,ALTER on *.* to 'slave'@'%.%.%.%' IDENTIFIED BY 'slave';
GRANT INSERT on *.* to 'slave'@'%.%.%.%' IDENTIFIED BY 'slave';

FLUSH PRIVILEGES;


