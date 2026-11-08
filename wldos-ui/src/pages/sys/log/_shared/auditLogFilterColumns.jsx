/**
 * 审计三日志 ProTable 查询项：comId / domainId（与 module、ip 同级，仅 hideInTable）。
 */
export function auditComDomainFilterColumns(meta) {
  const {
    isAdmin,
    isTenantAdmin,
    comSelectOptions,
    domainOptionsForCom,
    optionsReady,
  } = meta;
  const cols = [];
  if (isAdmin) {
    cols.push({
      title: '租户',
      dataIndex: 'comId',
      hideInTable: true,
      valueType: 'select',
      fieldProps: {
        allowClear: true,
        showSearch: true,
        optionFilterProp: 'label',
        options: comSelectOptions,
        loading: !optionsReady,
      },
    });
  }
  if (isAdmin || isTenantAdmin) {
    cols.push({
      title: '站点',
      dataIndex: 'domainId',
      hideInTable: true,
      valueType: 'select',
      dependencies: isAdmin ? ['comId'] : undefined,
      fieldProps: (form) => ({
        allowClear: true,
        showSearch: true,
        optionFilterProp: 'label',
        options: domainOptionsForCom(isAdmin ? form?.getFieldValue?.('comId') : undefined),
        loading: !optionsReady,
      }),
    });
  }
  return cols;
}
