package com.ligf.androiddatabaselib.liteorm.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;

/**
 * 任务详情实体类
 * @author ligangfan
 */
@Table("taskdetail")
public class TaskDetail {
	
	public static final String COL_TASK_ID = "_task_id";
	public static final String COL_TASK_STATUS = "_task_status";

	//数据表的主键
	@PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    public long id;
	@Column("_task_id")
	public String taskId;
	@Column("_title")
	public String title;
	@Column("_content")
	public String content;
	@Column("_task_status")
	public int taskStatus;
	@Mapping(Relation.OneToMany)
	public ArrayList<StoreDetail> storeDetailList;
	
}
