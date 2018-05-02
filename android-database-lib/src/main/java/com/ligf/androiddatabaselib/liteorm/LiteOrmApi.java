package com.ligf.androiddatabaselib.liteorm;

import android.content.Context;
import android.text.TextUtils;

import com.ligf.androiddatabaselib.liteorm.bean.TaskDetail;
import com.ligf.androidutilslib.BuildConfig;
import com.ligf.androidutilslib.file.FileUtil;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.List;

/**
 * Created by ligf on 2018/4/28.
 *
 *     通过分析库源码，LiteOrm数据库框架是对SQLite的增删改查操作的封装；在创建数据表时根据实体类注解进行创建，包括表名、表的字段。
 *     其实现原理就是通过传入的对象，创建出对应的sql语句，本质上还是用Android系统自带的SQLite。
 *     sql语句的对应的操作的表名有两种情况：
 *     1）实体类有设置@Table注解的话，对应的表名就是注解中设置的表名。
 *     2）实体类没有设置@Table注解的话，对应的表名就是实体类的类名。
 */

public class LiteOrmApi {

    private String DB_PATH ;
    private String DB_NAME = "liteorm.db";

    private static LiteOrmApi instance;

    private LiteOrm liteOrm = null;

    private LiteOrmApi(){

    }

    public static LiteOrmApi getInstance(){
        if (instance == null){
            synchronized (LiteOrmApi.class){
                if (instance == null){
                    instance = new LiteOrmApi();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化数据库，使用数据库前调用
     * @param context
     */
    public void init(Context context){
        DB_PATH = FileUtil.getDatabasePath(context, DB_NAME).getAbsolutePath();
        DataBaseConfig dataBaseConfig = new DataBaseConfig(context);
        dataBaseConfig.dbName = DB_PATH;
        dataBaseConfig.dbVersion = 1;
        dataBaseConfig.debugged = BuildConfig.DEBUG;
        liteOrm = LiteOrm.newCascadeInstance(dataBaseConfig);
    }

    /****************************   数据库操作的通用接口，与具体的业务无关 begin  *****************************/

    /**
     * 保存一个对象数据到数据库中
     * 原理：在底层的实现中，使用Replace进行SQL语句的创建
     * @param object
     * @return
     */
    public long save(Object object){
        if (object == null){
            return -1;
        }
        return liteOrm.save(object);
    }

    /**
     * 批量保存数据
     * @param list
     * @param <T>
     * @return
     */
    public <T>  int saveList(List<T> list){
        if (list == null || list.isEmpty()){
            return -1;
        }
        return liteOrm.save(list);
    }

    /**
     * 插入数据到数据库中，使用的是Insert进行SQL语句的创建（与save接口不同）
     * @param object
     * @return
     */
    public long insert(Object object){
        if (object == null){
            return -1;
        }
        return liteOrm.insert(object);
    }

    /**
     * 批量插入数据到数据库中
     * @param list
     * @param <T>
     * @return
     */
    public <T> int insertList(List<T> list){
        if (list == null || list.isEmpty()){
            return -1;
        }
        return liteOrm.insert(list);
    }

    /**
     * 从数据库中删除一个对象<p>
     *     这个删除的sql语句有两种情况：
     *     1）对应的实体类有设置主键值PrimaryKey，这时就根据PrimaryKey值来确定需要删除的数据库中的数据项。
     *     2）对应的实体类没有设置主键值PrimaryKey，这时就需要判断需要删除数据库中的哪个数据项：删除的对象对应的每个数据表字段都相同
     * @param object
     * @return
     */
    public int delete(Object object){
        if (object == null){
            return -1;
        }
        return liteOrm.delete(object);
    }

    /**
     * 批量删除数据库中的数据
     * 原理：遍历删除列表，逐个删除
     * @param list
     * @param <T>
     * @return
     */
    public <T> int deleteList(List<T> list){
        if (list == null || list.isEmpty()){
            return -1;
        }
        return liteOrm.delete(list);
    }

    /**
     * 删除一个数据表中的所有数据
     * 实现原理：先查找出数据表中所有的数据保存到列表中，然后遍历列表，逐一删除
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> int deleteAll(Class<T> tClass){
        if (tClass == null){
            return -1;
        }
        return liteOrm.deleteAll(tClass);
    }

    /**
     * 更新对象数据到数据库中（会修改所有的字段的数据）
     * @param object
     * @return
     */
    public int update(Object object){
        if (object == null){
            return -1;
        }
        return liteOrm.update(object);
    }

    /**
     * 查询对应的对象类在数据库中的所有数据（对应数据表的数据）
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> List<T> query(Class<T> tClass){
        if (tClass == null){
            return null;
        }
        return liteOrm.query(tClass);
    }

    /****************************   数据库操作的通用接口，与具体的业务无关 end *****************************/


    /****************************   数据库操作的通用接口，与具体的业务相关 begin *****************************/

    /**
     * 删除对应taskId的数据
     * @param taskId
     * @return
     */
    public int deleteTaskByTaskId(String taskId){
        if (TextUtils.isEmpty(taskId)){
            return -1;
        }
        return liteOrm.delete(new WhereBuilder(TaskDetail.class)
                .where(TaskDetail.COL_TASK_ID + " = ?", new String[]{taskId}));
    }

    /**
     * 查询对应taskId的数据
     * @param taskId
     * @return
     */
    public List<TaskDetail> queryTaskByTaskId(String taskId){
        if (TextUtils.isEmpty(taskId)){
            return null;
        }
        return liteOrm.query(new QueryBuilder<TaskDetail>(TaskDetail.class)
                        .where(TaskDetail.COL_TASK_ID + " = ?", new String[]{taskId}));
    }

    /**
     * 修改对应的taskId的任务的状态
     * @param taskId
     * @param status
     * @return
     */
    public int updateTaskStatusByTaskId(String taskId, int status){
        if (TextUtils.isEmpty(taskId)){
            return -1;
        }
        ColumnsValue columnsValue = new ColumnsValue(new String[]{TaskDetail.COL_TASK_STATUS}, new Integer[]{status});
        return liteOrm.update(new WhereBuilder(TaskDetail.class).where(TaskDetail.COL_TASK_ID + " = ?", new String[]{taskId}),
                columnsValue, ConflictAlgorithm.None);
    }

    /****************************   数据库操作的通用接口，与具体的业务相关 end *****************************/


}
