/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2021/6/9 16:29:27                            */
/*==============================================================*/
DROP TABLE IF EXISTS SQLTOY_ORDER_INFO;

/*==============================================================*/
/* Table: SQLTOY_ORDER_INFO                                     */
/*==============================================================*/
CREATE TABLE SQLTOY_ORDER_INFO
(
   ORDER_ID             VARCHAR(32) NOT NULL  COMMENT '订单ID',
   ORDER_AMT            NUMERIC(12,3) NOT NULL  COMMENT '订单金额',
   ORDER_CNT            NUMERIC(8,3) NOT NULL  COMMENT '订单数量',
   COMMENTS             VARCHAR(500)  COMMENT '订单备注',
   CREATE_TIME          DATETIME NOT NULL  COMMENT '创建时间',
   UPDATE_TIME          DATETIME NOT NULL  COMMENT '修改时间',
   PRIMARY KEY (ORDER_ID)
);

ALTER TABLE SQLTOY_ORDER_INFO COMMENT '支付订单表';

