/*==============================================================*/
/* Table: SQLTOY_DICT_DETAIL                                    */
/*==============================================================*/
CREATE TABLE SQLTOY_DICT_DETAIL
(
   DICT_KEY             VARCHAR(50) NOT NULL  COMMENT '字典KEY',
   DICT_TYPE            VARCHAR(50) NOT NULL  COMMENT '字典类型代码',
   DICT_NAME            VARCHAR(200) NOT NULL  COMMENT '字典值',
   SHOW_INDEX           NUMERIC(8,0) NOT NULL DEFAULT 1  COMMENT '显示顺序',
   UPDATE_BY            VARCHAR(22) NOT NULL  COMMENT '最后修改人',
   UPDATE_TIME          DATETIME NOT NULL  COMMENT '最后修改时间',
   STATUS               NUMERIC(1,0) NOT NULL DEFAULT 1  COMMENT '状态',
   PRIMARY KEY (DICT_KEY, DICT_TYPE)
);

ALTER TABLE SQLTOY_DICT_DETAIL COMMENT '字典明细表';

/*==============================================================*/
/* Table: SQLTOY_DICT_TYPE                                      */
/*==============================================================*/
CREATE TABLE SQLTOY_DICT_TYPE
(
   DICT_TYPE            VARCHAR(50) NOT NULL  COMMENT '字典类型代码',
   DICT_TYPE_NAME       VARCHAR(100) NOT NULL  COMMENT '字典类型名称',
   COMMENTS             VARCHAR(500)  COMMENT '说明',
   SHOW_INDEX           NUMERIC(8,0) NOT NULL DEFAULT 1  COMMENT '显示顺序',
   CREATE_BY            VARCHAR(22) NOT NULL  COMMENT '创建人',
   CREATE_TIME          DATETIME NOT NULL  COMMENT '创建时间',
   UPDATE_BY            VARCHAR(22) NOT NULL  COMMENT '最后修改人',
   UPDATE_TIME          DATETIME NOT NULL  COMMENT '最后修改时间',
   STATUS               NUMERIC(1,0) NOT NULL DEFAULT 1  COMMENT '状态',
   PRIMARY KEY (DICT_TYPE)
);

ALTER TABLE SQLTOY_DICT_TYPE COMMENT '字典分类表';

/*==============================================================*/
/* Table: SQLTOY_ORGAN_INFO                                     */
/*==============================================================*/
CREATE TABLE SQLTOY_ORGAN_INFO
(
   ORGAN_ID             VARCHAR(22) NOT NULL  COMMENT '机构ID',
   ORGAN_NAME           VARCHAR(100) NOT NULL  COMMENT '机构名称',
   ORGAN_CODE           VARCHAR(20) NOT NULL  COMMENT '机构代码',
   COST_NO              VARCHAR(20)  COMMENT '成本中心代码',
   ORGAN_PID            VARCHAR(22) NOT NULL  COMMENT '父机构ID',
   NODE_ROUTE           VARCHAR(200)  COMMENT '节点路径',
   NODE_LEVEL           NUMERIC(1,0)  COMMENT '节点等级',
   IS_LEAF              NUMERIC(1,0)  COMMENT '是否叶子节点',
   SHOW_INDEX           NUMERIC(8,0) NOT NULL DEFAULT 1  COMMENT '显示顺序',
   CREATE_BY            VARCHAR(22) NOT NULL  COMMENT '创建人',
   CREATE_TIME          DATETIME NOT NULL  COMMENT '创建时间',
   UPDATE_BY            VARCHAR(22) NOT NULL  COMMENT '最后修改人',
   UPDATE_TIME          DATETIME NOT NULL  COMMENT '最后修改时间',
   STATUS               NUMERIC(1,0) NOT NULL DEFAULT 1  COMMENT '状态',
   PRIMARY KEY (ORGAN_ID)
);

ALTER TABLE SQLTOY_ORGAN_INFO COMMENT '机构信息表';

/*==============================================================*/
/* Index: IDX_ORGAN_CODE                                        */
/*==============================================================*/
CREATE UNIQUE INDEX IDX_ORGAN_CODE ON SQLTOY_ORGAN_INFO
(
   ORGAN_CODE
);

/*==============================================================*/
/* Table: SQLTOY_STAFF_INFO                                     */
/*==============================================================*/
CREATE TABLE SQLTOY_STAFF_INFO
(
   STAFF_ID             VARCHAR(22) NOT NULL  COMMENT '员工ID',
   STAFF_CODE           VARCHAR(22) NOT NULL  COMMENT '工号',
   STAFF_NAME           VARCHAR(30) NOT NULL  COMMENT '姓名',
   ORGAN_ID             VARCHAR(22) NOT NULL  COMMENT '部门',
   SEX_TYPE             CHAR(1) NOT NULL  COMMENT '性别',
   BIRTHDAY             DATETIME  COMMENT '出生日期',
   ENTRY_DATE           DATETIME NOT NULL  COMMENT '入职日期',
   TERM_DATE            DATETIME  COMMENT '离职日期',
   PHOTO                LONGBLOB  COMMENT '照片',
   COUNTRY              VARCHAR(10)  COMMENT '国家',
   CENSUS_REGISTER      VARCHAR(150)  COMMENT '籍贯',
   ADDRESS              VARCHAR(250)  COMMENT '家庭地址',
   EMAIL                VARCHAR(100)  COMMENT '邮箱',
   TEL_NO               VARCHAR(20)  COMMENT '移动电话',
   POST                 VARCHAR(20)  COMMENT '岗位',
   POST_GRADE           VARCHAR(20)  COMMENT '职位级别',
   CREATE_BY            VARCHAR(22) NOT NULL  COMMENT '创建人',
   CREATE_TIME          DATETIME NOT NULL  COMMENT '创建时间',
   UPDATE_BY            VARCHAR(22) NOT NULL  COMMENT '最后修改人',
   UPDATE_TIME          DATETIME NOT NULL  COMMENT '最后修改时间',
   STATUS               NUMERIC(1,0) NOT NULL DEFAULT 1  COMMENT '状态',
   PRIMARY KEY (STAFF_ID)
);

ALTER TABLE SQLTOY_STAFF_INFO COMMENT '员工信息表';

/*==============================================================*/
/* Index: IDX_STAFF_CODE                                        */
/*==============================================================*/
CREATE UNIQUE INDEX IDX_STAFF_CODE ON SQLTOY_STAFF_INFO
(
   STAFF_CODE
);

ALTER TABLE SQLTOY_DICT_DETAIL ADD CONSTRAINT FK_DICT_TYPE_REF_ITEM FOREIGN KEY (DICT_TYPE)
      REFERENCES SQLTOY_DICT_TYPE (DICT_TYPE) ON DELETE RESTRICT ON UPDATE RESTRICT;
	  
INSERT INTO sqltoy.sqltoy_dict_type (DICT_TYPE,DICT_TYPE_NAME,COMMENTS,SHOW_INDEX,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME,STATUS) VALUES 
('COMPANY_TYPE','企业类型','企业类型',1,'S0001','2019-08-01 16:47:01','S0001','2019-08-01 16:47:01',1)
,('DEVICE_TYPE','设备类型','设备类型',1,'S0001','2019-08-01 16:47:01','S0001','2019-08-01 16:47:01',1)
,('ORDER_STATUS','订单状态','订单状态',3,'S0001','2019-08-01 16:47:01','S0001','2019-08-01 16:47:01',1)
,('POST_LEVEL','岗位级别','岗位级别',5,'S0001','2019-08-01 16:47:01','S0001','2019-08-01 16:47:01',1)
,('POST_TYPE','岗位类别','岗位类别',4,'S0001','2019-08-01 16:47:01','S0001','2019-08-01 16:47:01',1)
,('PURCHASE_SALE_TYPE','采购销售分类','采购销售分类',2,'S0001','2019-08-01 16:47:01','S0001','2019-08-01 16:47:01',1)
,('SEX_TYPE','性别','性别',6,'S0001','2019-08-01 16:47:01','S0001','2019-08-01 16:47:01',1)
,('TRANS_CHANNEL','交易渠道','交易渠道',7,'S0001','2019-08-01 16:47:01','S0001','2019-08-01 16:47:01',1)
,('TRANS_CODE','交易代码表','交易代码表',8,'S0001','2019-08-01 16:47:01','S0001','2019-08-01 16:47:01',1)
,('TRANS_TYPE','交易类型','交易类型',8,'S0001','2019-08-01 16:47:01','S0001','2019-08-01 16:47:01',1)
;


INSERT INTO sqltoy.sqltoy_dict_detail (DICT_KEY,DICT_TYPE,DICT_NAME,SHOW_INDEX,UPDATE_BY,UPDATE_TIME,STATUS) VALUES 
('0','ORDER_STATUS','作废',1,'S0001','2019-08-01 16:47:01',1)
,('012001','TRANS_CODE','贷记卡消费',5,'S0001','2019-08-01 16:47:01',1)
,('012401','TRANS_CODE','借记卡消费',6,'S0001','2019-08-01 16:47:01',1)
,('1','COMPANY_TYPE','股份制',1,'S0001','2019-08-01 16:47:01',1)
,('1','ORDER_STATUS','编辑',2,'S0001','2019-08-01 16:47:01',1)
,('2','COMPANY_TYPE','国有企业',2,'S0001','2019-08-01 16:47:01',1)
,('2','ORDER_STATUS','待审核',3,'S0001','2019-08-01 16:47:01',1)
,('3','ORDER_STATUS','已生效',4,'S0001','2019-08-01 16:47:01',1)
,('D','TRANS_TYPE','微信交易',2,'S0001','2019-08-01 16:47:01',1)
,('F','POST_TYPE','职能岗',3,'S0001','2019-08-01 16:47:01',1)
;
INSERT INTO sqltoy.sqltoy_dict_detail (DICT_KEY,DICT_TYPE,DICT_NAME,SHOW_INDEX,UPDATE_BY,UPDATE_TIME,STATUS) VALUES 
('F','SEX_TYPE','女',1,'S0001','2019-08-01 16:47:01',1)
,('JD','TRANS_CHANNEL','京东',1,'S0001','2019-08-01 16:47:01',1)
,('L1','POST_LEVEL','L1',1,'S0001','2019-08-01 16:47:01',1)
,('L10','POST_LEVEL','L10',10,'S0001','2019-08-01 16:47:01',1)
,('L2','POST_LEVEL','L2',2,'S0001','2019-08-01 16:47:01',1)
,('L3','POST_LEVEL','L3',3,'S0001','2019-08-01 16:47:01',1)
,('L4','POST_LEVEL','L4',4,'S0001','2019-08-01 16:47:01',1)
,('L5','POST_LEVEL','L5',5,'S0001','2019-08-01 16:47:01',1)
,('L6','POST_LEVEL','L6',6,'S0001','2019-08-01 16:47:01',1)
,('L7','POST_LEVEL','L7',7,'S0001','2019-08-01 16:47:01',1)
;
INSERT INTO sqltoy.sqltoy_dict_detail (DICT_KEY,DICT_TYPE,DICT_NAME,SHOW_INDEX,UPDATE_BY,UPDATE_TIME,STATUS) VALUES 
('L8','POST_LEVEL','L8',8,'S0001','2019-08-01 16:47:01',1)
,('L9','POST_LEVEL','L9',9,'S0001','2019-08-01 16:47:01',1)
,('M','POST_TYPE','管理岗',1,'S0001','2019-08-01 16:47:01',1)
,('M','SEX_TYPE','男',2,'S0001','2019-08-01 16:47:01',1)
,('N','TRANS_TYPE','普通交易',1,'S0001','2019-08-01 16:47:01',1)
,('NET','DEVICE_TYPE','网络设备',2,'S0001','2019-08-01 16:47:01',1)
,('O','POST_TYPE','其他',4,'S0001','2019-08-01 16:47:01',1)
,('OFFICE','DEVICE_TYPE','办公用品',3,'S0001','2019-08-01 16:47:01',1)
,('OT','DEVICE_TYPE','其他',5,'S0001','2019-08-01 16:47:01',1)
,('PC','DEVICE_TYPE','个人电脑',1,'S0001','2019-08-01 16:47:01',1)
;
INSERT INTO sqltoy.sqltoy_dict_detail (DICT_KEY,DICT_TYPE,DICT_NAME,SHOW_INDEX,UPDATE_BY,UPDATE_TIME,STATUS) VALUES 
('PO','PURCHASE_SALE_TYPE','采购',1,'S0001','2019-08-01 16:47:01',1)
,('SHOP','TRANS_CHANNEL','线下门店',3,'S0001','2019-08-01 16:47:01',1)
,('SO','PURCHASE_SALE_TYPE','销售',2,'S0001','2019-08-01 16:47:01',1)
,('SOFTWARE','DEVICE_TYPE','软件',4,'S0001','2019-08-01 16:47:01',1)
,('T','POST_TYPE','技术岗',2,'S0001','2019-08-01 16:47:01',1)
,('T001','TRANS_CODE','下单',1,'S0001','2019-08-01 16:47:01',1)
,('T002','TRANS_CODE','撤销',2,'S0001','2019-08-01 16:47:01',1)
,('T003','TRANS_CODE','付款',3,'S0001','2019-08-01 16:47:01',1)
,('T004','TRANS_CODE','订单查询',4,'S0001','2019-08-01 16:47:01',1)
,('TIANMAO','TRANS_CHANNEL','天猫',2,'S0001','2019-08-01 16:47:01',1)
;
