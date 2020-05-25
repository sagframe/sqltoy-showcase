/*==============================================================*/
/* Table: SQLTOY_DEVICE_ORDER_INFO                              */
/*==============================================================*/
CREATE TABLE SQLTOY_DEVICE_ORDER_INFO
(
   ORDER_ID             VARCHAR(22) NOT NULL  COMMENT '订单ID',
   DEVICE_TYPE          VARCHAR(10) NOT NULL  COMMENT '设备类型',
   PS_TYPE              VARCHAR(10) NOT NULL  COMMENT '购销标志',
   TOTAL_CNT            NUMERIC(12,3)  COMMENT '商品总量',
   TOTAL_AMT            NUMERIC(12,2)  COMMENT '总金额',
   BUYER                VARCHAR(22)  COMMENT '购买方',
   SALER                VARCHAR(22)  COMMENT '销售方',
   TRANS_DATE           DATETIME NOT NULL  COMMENT '成交日期',
   DELIVERY_TERM        DATETIME  COMMENT '交货期限',
   STAFF_ID             VARCHAR(22)  COMMENT '业务员',
   ORGAN_ID             VARCHAR(22)  COMMENT '业务机构',
   CREATE_BY            VARCHAR(22) NOT NULL  COMMENT '创建人',
   CREATE_TIME          DATETIME NOT NULL  COMMENT '创建时间',
   UPDATE_BY            VARCHAR(22) NOT NULL  COMMENT '最后修改人',
   UPDATE_TIME          DATETIME NOT NULL  COMMENT '最后修改时间',
   STATUS               INT NOT NULL  COMMENT '状态',
   PRIMARY KEY (ORDER_ID)
);

ALTER TABLE SQLTOY_DEVICE_ORDER_INFO COMMENT '硬件购销定单表(演示有规则单号)';

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
