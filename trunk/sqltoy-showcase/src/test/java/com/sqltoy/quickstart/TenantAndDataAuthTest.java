/**
 *
 */
package com.sqltoy.quickstart;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.config.annotation.Tenant;
import org.sagacity.sqltoy.model.DataAuthFilterConfig;
import org.sagacity.sqltoy.plugins.interceptors.TenantFilterInterceptor;

/**
 * @project sqltoy-quickstart
 * @description 多租户与数据权限的配置模式演示（编译参考）
 *
 *              1、多租户：实体字段标注 @Tenant + 注册拦截器 +
 *              统一字段处理器实现 authTenants() 返回当前用户授权租户
 *              2、数据权限：统一字段处理器实现 dataAuthFilters()，
 *              框架将条件参数统一注入 sql 并做越权校验
 *
 * @author zhongxuchen
 */
public class TenantAndDataAuthTest {

	/**
	 * 1、实体字段标注 @Tenant（拦截器据此识别租户字段）
	 */
	@Tenant(field = "tenantId")
	public class TenantOrderVO {
		private String tenantId;
	}

	/**
	 * 2、注册拦截器（application.properties）：
	 * spring.sqltoy.sqlInterceptors[0]=org.sagacity.sqltoy.plugins.interceptors.TenantFilterInterceptor
	 *
	 * 3、统一字段处理器实现授权租户（SqlToyUnifyFieldsHandler 中）：
	 * public String[] authTenants(Class entityClass, OperateType operType) {
	 * return new String[] { currentTenantId };
	 * }
	 */
	@Test
	public void tenantPattern() {
		TenantFilterInterceptor interceptor = new TenantFilterInterceptor();
		// 框架在查询/更新时自动追加租户过滤条件
		System.err.println("租户拦截器: " + (interceptor != null));
	}

	/**
	 * 数据权限传参示例：DataAuthFilterConfig 将授权机构等条件统一注入
	 */
	@Test
	public void dataAuthPattern() {
		DataAuthFilterConfig config = new DataAuthFilterConfig();
		// 具体配置参见文档[数据权限传参和越权校验]章节
		System.err.println("DataAuthFilterConfig: " + (config != null));
	}
}
