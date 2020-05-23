/**
 *@Generated by sagacity-quickvo 4.13
 */
package com.sagframe.sqltoy.showcase.vo;

import org.sagacity.sqltoy.config.annotation.SqlToyEntity;
import java.time.LocalDate;
import java.util.List;
import com.sagframe.sqltoy.showcase.vo.base.AbstractDictTypeVO;

/**
 * @project sqltoy-oracle
 * @author zhongxuchen
 * @version 1.0.0
 * Table: SQLTOY_DICT_TYPE,Remark:字典分类表 	
 */
@SqlToyEntity
public class DictTypeVO extends AbstractDictTypeVO {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8183615949726103634L;
	
	/** default constructor */
	public DictTypeVO() {
		super();
	}
	/*---begin-constructor-area---don't-update-this-area--*/
	/** pk constructor */
	public DictTypeVO(String dictType)
	{
		this.dictType=dictType;
	}

	/** minimal constructor */
	public DictTypeVO(String dictType,String dictTypeName,Integer showIndex,String createBy,LocalDateTime createTime,String updateBy,LocalDateTime updateTime,Integer status)
	{
		this.dictType=dictType;
		this.dictTypeName=dictTypeName;
		this.showIndex=showIndex;
		this.createBy=createBy;
		this.createTime=createTime;
		this.updateBy=updateBy;
		this.updateTime=updateTime;
		this.status=status;
	}

	/** full constructor */
	public DictTypeVO(String dictType,String dictTypeName,String comments,Integer showIndex,String createBy,LocalDateTime createTime,String updateBy,LocalDateTime updateTime,Integer status)
	{
		this.dictType=dictType;
		this.dictTypeName=dictTypeName;
		this.comments=comments;
		this.showIndex=showIndex;
		this.createBy=createBy;
		this.createTime=createTime;
		this.updateBy=updateBy;
		this.updateTime=updateTime;
		this.status=status;
	}

    /**
	 * mapping sqltoy_dict_detail data to sqltoy_dict_type oneToMany List
	 */
	public void mappingDictDetailVOs(List<DictTypeVO> mainSet,List<DictDetailVO> itemSet)
    {
    	if(mainSet==null || mainSet.isEmpty() || itemSet==null||itemSet.isEmpty())
    		return;
    	DictTypeVO main;
    	DictDetailVO item;
    	for(int i=0;i<mainSet.size();i++){
    		main=mainSet.get(i);
    		if(itemSet.size()==0)
    			break;
    		for(int j=0;j<itemSet.size();j++){
    			item=itemSet.get(j);
    			if(main.getDictType().equals(item.getDictType())){
    			   main.dictDetailVOs.add(item);
    			   itemSet.remove(j);
    			   j--;
    			}
    		}
    	}
    }
	/*---end-constructor-area---don't-update-this-area--*/
	/**
     *@todo vo columns to String
     */
    @Override
	public String toString() {
		return super.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public DictTypeVO clone() {
		try {
			return (DictTypeVO) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
