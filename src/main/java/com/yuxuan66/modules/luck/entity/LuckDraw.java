package com.yuxuan66.modules.luck.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author Sir丶雨轩
 * @since 2020/10/18
 */
@Setter
@Getter
@TableName("corp_luck_draw")
public class LuckDraw implements Serializable {

    private Long id;

    private String name;
    private String type;
    private String pic;
    private String address;
    private String createBy;
    private Integer nodeNum;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp createTime;
    private Long winUserId;
    private String winUserName;
    private String winNo;
    private Long winAccountId;
    private String winAccountName;

    private Integer status;
    private String content;
    private Integer lp;

    @TableField(exist = false)
    private List<LuckDrawNode> luckDrawNodes;



}
