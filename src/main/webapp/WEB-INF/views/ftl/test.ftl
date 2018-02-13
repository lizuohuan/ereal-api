<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>无标题文档</title>
</head>

<body>
<table style="width:100%" border="1">
  <tr>
    <th colspan="${allWeekAcceptanceUserKNum}" scope="col">一真咨询2016年内部项目台帐</th>
  </tr>
  <tr>
    <th scope="col">&nbsp;</th>
    <th colspan="5" scope="col">项目立项信息</th>
    <th colspan="3" scope="col">&nbsp;</th>
    <th colspan="${excelUserKsMaxNum * 3 + 8}" scope="col">项目阶段验收结果</th>
    [#list weekAcceptance.keySet()  as k  ]
        <th colspan="${weekAcceptance.get(k) * 3 + 8 }" scope="col">第${k}次验收</th>
    [/#list]
  </tr>
  <tr>
    <td rowspan="2">团队</td>
    <td rowspan="2">任务序号</td>
    <td rowspan="2">编号</td>
    <td rowspan="2">立项月份</td>
    <td rowspan="2">是否建立</td>
    <td rowspan="2">项目名称</td>
    <td rowspan="2">初始工作量</td>
    <td rowspan="2">直接责任人/项目经理</td>
    <td rowspan="2">直接汇报人</td>
    <td rowspan="2">当前进度</td>
    <td colspan="5">质量得分</td>
    <td rowspan="2">类型</td>
    <td rowspan="2">质量系数</td>
    <td colspan="3">项目经理</td>
    <td colspan="${excelUserKsMaxNum * 3 - 3}">项目成员</td>
  [#list weekAcceptance.keySet()  as k]
      <td rowspan="2">验收时间</td>
      <td rowspan="2">验收人</td>
      <td rowspan="2">总体进度</td>
      <td colspan="5">质量得分</td>
      <td colspan="3">项目经理</td>
      <td colspan="${weekAcceptance.get(k) * 3 - 3}">项目成员</td>
  [/#list]

  </tr>
  <tr>
    <td>P</td>
    <td>A</td>
    <td>N</td>
    <td>E</td>
    <td>L</td>
    <td>姓名</td>
    <td>用时</td>
    <td>K值</td>
    [#list 1..excelUserKsMaxNum-1 as e]
        <td>姓名</td>
        <td>用时</td>
        <td>K值</td>
    [/#list]
    [#list weekAcceptance.keySet()  as k ]
      <td>P</td>
      <td>A</td>
      <td>N</td>
      <td>E</td>
      <td>L</td>
      <td>姓名</td>
      <td>贡献比例</td>
      <td>K值</td>
      [#list 1..weekAcceptance.get(k)-1 as n ]
        <td>姓名</td>
        <td>贡献比例</td>
        <td>K值</td>
      [/#list]
    [/#list]
  </tr>
  [#list departments as d ]
      <tr>
          [#if d.interiorNum == 1]
            <td rowspan="${d.departmentNameRowSpanNum}">${d.departmentName}</td>
          [/#if]
          <td>${d.interiorNum?default("--")}</td>
          <td>${d.projectNumber?default("--")}</td>
          <td>${d.month?default("--")}</td>
          <td>${d.isHave}</td>
          <td>${d.projectName?default("--")}</td>
          <td>${d.initWorkload?default("--")}</td>
          <td>${d.projectManagerName?default("--")}</td>
          <td>${d.directReportName?default("--")}</td>
          <td>${d.newReport.progress?default("--")}</td>
          <td>${d.newReport.p?default("--")}</td>
          <td>${d.newReport.a?default("--")}</td>
          <td>${d.newReport.n?default("--")}</td>
          <td>${d.newReport.e?default("--")}</td>
          <td>${d.newReport.l?default("--")}</td>
          <td>${d.newReport.type?default("--")}</td>
          <td>${d.newReport.quality?default("--")}</td>
          [#list d.newReport.excelUserKs as e]
              [#if e.isManager == 1]
                  <td>${e.userName?default("--")}</td>
                  <td>${e.time?default("--")}</td>
                  <td>${e.k?default("--")}</td>
              [/#if]
          [/#list]
          [#list d.newReport.excelUserKs as e]
              [#if e.isManager == 0]
                  <td>${e.userName?default("--")}</td>
                  <td>${e.time?default("--")}</td>
                  <td>${e.k?default("--")}</td>
              [/#if]
          [/#list]
        [#list d.weekAcceptances as w]

          <td>${w.createTimes?default("--")}</td>
          <td>${d.directReportName?default("--")}</td>
          <td>${w.progress?default("--")}</td>
          <td>${w.p?default("--")}</td>
          <td>${w.a?default("--")}</td>
          <td>${w.n?default("--")}</td>
          <td>${w.e?default("--")}</td>
          <td>${w.l?default("--")}</td>
          [#list w.weekAllocations as e]
            [#if e.isManager == 1]
                <td>${e.userName?default("--")}</td>
                <td>${e.ratio?default("--")}</td>
                <td>${e.k?default("--")}</td>
            [/#if]
          [/#list]
          [#list w.weekAllocations as e]
            [#if e.isManager == 0]
                <td>${e.userName?default("--")}</td>
                <td>${e.ratio?default("--")}</td>
                <td>${e.k?default("--")}</td>
            [/#if]
          [/#list]
        [/#list]
      </tr>
  [/#list]


</table>
</body>
</html>
