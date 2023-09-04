package com.github.tvbox.osc.bbox.cache;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * 类描述:
 *
 * @author pj567
 * @since 2020/5/15
 */
@Dao
public interface CacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(Cache cache);

    /**
     * 注意，冒号后面必须紧跟参数名，中间不能有空格。大于小于号和冒号中间是有空格的。
     * select *from cache where【表中列名】 =:【参数名】------>等于
     * where 【表中列名】 < :【参数名】 小于
     * where 【表中列名】 between :【参数名1】 and :【参数2】------->这个区间
     * where 【表中列名】like :参数名----->模糊查询
     * where 【表中列名】 in (:【参数名集合】)---->查询符合集合内指定字段值的记录
     *
     * @param key
     * @return
     */

    //如果是一对多,这里可以写List<Cache>
    @Query("select *from cache where `key`=:key")
    Cache getCache(String key);

    //只能传递对象昂,删除时根据Cache中的主键 来比对的
    @Delete
    int delete(Cache cache);

    //只能传递对象昂,删除时根据Cache中的主键 来比对的
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Cache cache);
}
