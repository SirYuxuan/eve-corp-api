package com.yuxuan66.modules.luck.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yuxuan66.modules.user.entity.UserAccount;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Sir丶雨轩
 * @since 2020/10/18
 */
@Setter
@Getter
@TableName("corp_luck_draw_node")
public class LuckDrawNode implements Serializable {

    private Long id;

    private String no;
    private Long uid;
    private Long accountId;
    private String name;
    private Boolean win;

    private Timestamp payTime;

    private Long luckDrawId;

    @TableField(exist = false)
    private UserAccount userAccount;



}
