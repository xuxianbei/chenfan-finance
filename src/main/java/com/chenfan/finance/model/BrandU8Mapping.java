package com.chenfan.finance.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author lizhejin
 * @since 2020-12-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("brand_u8_mapping")
public class BrandU8Mapping implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer brandId;

    private String u8DbV1;

    private String u8DbV2;

    private String u8DbV3;

    private String pDbV1;

    private String pDbV2;

    private String pDbV3;

    private LocalDateTime startDateV1;

    private LocalDateTime startDateV2;

    private LocalDateTime startDateV3;


}
