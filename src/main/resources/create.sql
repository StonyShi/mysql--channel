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


CREATE TABLE `t4` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `driver_id` bigint(21) NOT NULL,
  `order_id` varchar(128) NOT NULL,
  `status` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `order_time` datetime NOT NULL,
  `t_no` char(4),
  PRIMARY KEY (`id`),
  UNIQUE KEY `t4_repeat_index` (`driver_id`,`order_id`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

insert into t4(driver_id, order_id, status, price, order_time, t_no) values (923233, '10345164564564563', 0, 10.5, now(),'n300');


CREATE TABLE `t5` (
  `id` bigint(20) NOT NULL,
  `input` varchar(4000) DEFAULT NULL,
  `is_ok` bit(1) NOT NULL,
  `carid` char(4) NOT NULL,
  `serde_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `t5` (`id`, `input`, `is_ok`, carid, serde_id) VALUES (11, '你好呀发撒地方', 0, 'no24', 234234545);

CREATE TABLE `t6` (
  `id` int(11) NOT NULL,
  `birth_date` date NOT NULL,
  `first_name` varchar(20) NOT NULL,
  `last_name` varchar(2000) NOT NULL,
  `gender` enum('M','F') NOT NULL,
  `hire_date` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `t6` (`id`, `birth_date`, `first_name`, last_name, gender, hire_date) VALUES (13, now(), 'Ruve', 'Uok', 'M', now());


CREATE USER 'slave'@'%.%.%.%' IDENTIFIED BY 'slave';
GRANT REPLICATION SLAVE ON *.* TO 'slave'@'%.%.%.%';
GRANT REPLICATION CLIENT ON *.* TO 'slave'@'%.%.%.%';

GRANT SELECT,CREATE,DELETE,DROP,UPDATE,ALTER on *.* to 'slave'@'%.%.%.%' IDENTIFIED BY 'slave';
GRANT INSERT on *.* to 'slave'@'%.%.%.%' IDENTIFIED BY 'slave';

FLUSH PRIVILEGES;


