package com.ligf.androiddatabaselib.liteorm.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 门店详情信息实体类
 * @author ligangfan
 */
@Table("storedetail")
public class StoreDetail {
	
	public static final String COL_STORE_ID = "_store_id";
	
	@PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    public long id;
	@Column("_store_id")
	public String storeId;
	@Column("_store_name")
	public String storeName;
	@Column("_status")
	public int storeStatus;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		return "StoreDetail{id:" + id + ",storeName:" + storeName + ",storeId:" + storeId  
				+ ", status:" + storeStatus
				+ "}";
	}
}
