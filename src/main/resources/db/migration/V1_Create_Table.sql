CREATE TABLE `seckill_goods`(
	`seckill_id` BIGINT(20) NOT NULL COMMENT '商品库存id',
	`goods_name` VARCHAR(120) NOT NULL COMMENT '商品名称',
	`inventory` INT(11) NOT NULL COMMENT '库存数量',
	`start_time` DATETIME NOT NULL COMMENT '开始时间',
	`end_time` DATETIME NOT NULL COMMENT '结束时间',
	`create_time` DATETIME NOT NULL COMMENT '创建时间',
	`version` BIGINT(20) NOT NULL COMMENT '0',
	PRIMARY KEY(`seckill_id`)

)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT '秒杀商品表';


CREATE TABLE `seckill_order`(
	`order_id` varchar (20) NOT NULL COMMENT '秒杀订单id',
	`seckill_id` BIGINT(20) NOT NULL COMMENT '秒杀库存id',
	`user_phone` INT(11) NOT NULL COMMENT '秒杀用户的手机号',
	`state` INT(5) NOT NULL DEFAULT -1 COMMENT '-1:无效 0：成功 1：已支付 2：已发货',
	`create_time` DATETIME NOT NULL COMMENT '创建时间',
	PRIMARY KEY(`order_id`)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT '秒杀订单表';


