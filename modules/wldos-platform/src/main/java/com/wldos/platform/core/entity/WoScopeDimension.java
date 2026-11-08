package com.wldos.platform.core.entity;

import org.springframework.data.relational.core.mapping.Table;

import com.wldos.framework.mvc.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Table("wo_scope_dimension")
@Getter
@Setter
public class WoScopeDimension extends BaseEntity {
	private String dimensionCode;
	private String dimensionName;
	private String valueSource;
	private String matchMode;
	private String enabled;
	private Integer sortOrder;
	private String scopeUiTier;
}
