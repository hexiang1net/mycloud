package com.example.demo02.entity.biz.entity;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * tabe area
 * </p>
 *
 * @author oKong
 * @since 2018-10-07
 */
@Data
@Accessors(chain = true)
public class TbArea extends Model<TbArea> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "area_id", type = IdType.AUTO)
    private Integer areaId;
    /**
     * 区域名称
     */
    private String areaName;
    /**
     * 优先级
     */
    private Integer priority;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后修改时间
     */
    private Date updateTime;


    public static final String AREA_ID = "area_id";

    public static final String AREA_NAME = "area_name";

    public static final String PRIORITY = "priority";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    @Override
    protected Serializable pkVal() {
        return this.areaId;
    }

}
