package com.magic.ereal.api.controller;

import com.magic.ereal.business.entity.*;
import com.magic.ereal.business.service.*;
import com.magic.ereal.business.util.DateTimeHelper;
import com.magic.ereal.business.util.ExcelUtil;
import com.magic.ereal.business.util.NumTrans;
import com.magic.ereal.business.util.Timestamp;
import freemarker.template.Template;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by Eric Xie on 2017/7/10 0010.
 */
@Controller
@RequestMapping("/excel")
public class ExcelController extends BaseController {

    @Resource
    private DepartmentService departmentService;
    @Resource
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Resource
    private StaticService staticService;

//    @RequestMapping("/test")
//    public void  excel(HttpServletResponse response, Model model) throws IOException {
//        List<Department> departments = departmentService.queryDepartmentOfExcelProjectInterior(10);
////        try {
//            Map<String,Object> data = new HashMap<>();
//            data.put("departments",departments);
//            model.addAttribute("departments",departments);
//            response.setContentType("application/x-excel");
//            response.setHeader("Content-Disposition", "attachment; filename="+"detail.xls");
//        response.setCharacterEncoding("utf-8");
//                String templatePath = "ftl/projectTemplate.ftl";
//        staticService.build(templatePath, data, response.getWriter());
////            return "ftl/projectTemplate";
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }



//    @RequestMapping("/excelHtml")
//    public String  excelHtml(HttpServletResponse response, Model model) throws IOException {
//        try {
//            List<Department> departments = departmentService.queryDepartmentOfExcelProjectInterior(10);
//
//            //项目阶段验收结果 最大成员数
//            int excelUserKsMaxNum = 1;
//            //最大验收次数
//            int weekAcceptanceMaxNum = 1;
//
//            for (Department department : departments) {
//                for (ExcelProjectInterior interior : department.getInteriors()) {
//                    if (null != interior.getNewReport()) {
//                        if (excelUserKsMaxNum < interior.getNewReport().getExcelUserKs().size()) {
//                            excelUserKsMaxNum = interior.getNewReport().getExcelUserKs().size();
//                        }
//                    }
//                    if (weekAcceptanceMaxNum < interior.getWeekAcceptances().size()) {
//                        weekAcceptanceMaxNum = interior.getWeekAcceptances().size();
//                    }
//                }
//
//            }
//            //每次验收对应的最大人数
//            Map<String ,Object> data = new LinkedHashMap<>();
//            //每次验收最大人数 总和
//            int allWeekAcceptanceUserKNum = 0;
//            for (int i = 0 ; i < weekAcceptanceMaxNum ; i ++) {
//                //每次验收成员数量
//                int weekAcceptanceUserKNum = 0;
//                for (Department department : departments) {
//                    for (ExcelProjectInterior interior : department.getInteriors()) {
//                        if (interior.getWeekAcceptances().size() > i) {
//                            if (weekAcceptanceUserKNum < interior.getWeekAcceptances().get(i).getWeekAllocations().size()) {
//                                weekAcceptanceUserKNum = interior.getWeekAcceptances().get(i).getWeekAllocations().size();
//                                continue;
//                            }
//
//                        }
//
//                    }
//                }
//                data.put(NumTrans.get(String.valueOf(i+1)),weekAcceptanceUserKNum);
//                allWeekAcceptanceUserKNum += weekAcceptanceUserKNum * 3 + 8;
//            }
//
//
//            JSONArray jsonArrayResult = new JSONArray();
//
//            for (Department department : departments) {
//                Map<String ,Object> map = new HashMap<>();
//                int i = 1;
//                if (null == department.getInteriors() || department.getInteriors().size() < 1) {
//                    map.put("departmentNameRowSpanNum",department.getInteriors().size() > 0 ? department.getInteriors().size() : 1);
//                    map.put("departmentName",department.getDepartmentName());
//                    //任务序号
//                    map.put("interiorNum",i);
//                    //是否创建项目
//                    map.put("isHave","否");
//                    map.put("projectNumber", "--");
//                    map.put("month", "--");
//                    map.put("projectName", "--");
//                    map.put("initWorkload", "--");
//                    map.put("projectManagerName", "--");
//                    map.put("directReportName", "--");
//                    //构造出 项目阶段验收结果的项目成员数量
//                    ExcelProjectInteriorNewReport newReport = new ExcelProjectInteriorNewReport();
//                    List<ExcelUserK> list = new ArrayList<>();
//                    ExcelUserK e = new ExcelUserK();
//                    e.setUserName("--");
//                    e.setTime(0);
//                    for (int j = 0 ; j < excelUserKsMaxNum ; j ++) {
//                        if (j == 0) {
//                            e.setIsManager(1);
//                        } else {
//                            e.setIsManager(0);
//                        }
//                        list.add(e);
//                    }
//                    newReport.setType("--");
//                    newReport.setExcelUserKs(list);
//                    map.put("newReport",newReport);
//                    List<ProjectInteriorWeekAcceptance> weekAcceptances = new ArrayList<>();
//                    //构造每次最大验收次数 与验收人数
//                    for (String key : data.keySet()) {
//                        ProjectInteriorWeekAcceptance projectInteriorWeekAcceptance = new ProjectInteriorWeekAcceptance();
//                        List<ProjectInteriorWeekKAllocation> projectInteriorWeekKAllocations = new ArrayList<>();
//                        ProjectInteriorWeekKAllocation projectInteriorWeekKAllocation = new ProjectInteriorWeekKAllocation();
//                        for (int j = 0 ; j < Integer.parseInt(data.get(key).toString()) ; j ++) {
//                            if (j==0) {
//                                projectInteriorWeekKAllocation.setIsManager(1);
//                            } else {
//                                projectInteriorWeekKAllocation.setIsManager(0);
//                            }
//                            projectInteriorWeekKAllocation.setUserName("--");
//                            projectInteriorWeekKAllocations.add(projectInteriorWeekKAllocation);
//                        }
//                        projectInteriorWeekAcceptance.setWeekAllocations(projectInteriorWeekKAllocations);
//                        projectInteriorWeekAcceptance.setCreateTimes("--");
//                        weekAcceptances.add(projectInteriorWeekAcceptance);
//                    }
//                    map.put("weekAcceptances",weekAcceptances);
//                    jsonArrayResult.add(map);
//                } else {
//                    for (ExcelProjectInterior interior : department.getInteriors()) {
//                        map.put("departmentNameRowSpanNum",department.getInteriors().size() > 0 ? department.getInteriors().size() : 1);
//                        map.put("departmentName",department.getDepartmentName());
//                        //任务序号
//                        map.put("interiorNum",i);
//                        //是否创建项目
//                        map.put("isHave","是");
//                        map.put("projectNumber", interior.getProjectNumber());
//                        map.put("month", Timestamp.DateTimeStamp(interior.getStartTime(),"yy年MM月"));
//                        map.put("projectName", interior.getProjectName());
//                        map.put("initWorkload", interior.getInitWorkload());
//                        map.put("projectManagerName", interior.getProjectManagerName());
//                        map.put("directReportName", interior.getDirectReportName());
//                        if (null == interior.getNewReport()) {
//                            //构造出 项目阶段验收结果的项目成员数量
//                            ExcelProjectInteriorNewReport newReport = new ExcelProjectInteriorNewReport();
//                            List<ExcelUserK> list = new ArrayList<>();
//                            ExcelUserK e = new ExcelUserK();
//                            e.setUserName("--");
//                            e.setTime(0);
//                            for (int j = 0 ; j < excelUserKsMaxNum ; j ++) {
//                                if (j != 0) {
//                                    e.setIsManager(0);
//                                } else {
//                                    e.setIsManager(1);
//                                }
//                                list.add(e);
//                            }
//                            newReport.setType("--");
//                            newReport.setExcelUserKs(list);
//                            map.put("newReport",newReport);
//                        } else {
//                            //构造出 项目阶段验收结果的项目成员数量
//                            if (interior.getNewReport().getExcelUserKs().size() < excelUserKsMaxNum) {
//                                ExcelUserK e = new ExcelUserK();
//                                e.setIsManager(0);
//                                e.setTime(0);
//                                e.setUserName("--");
//                                int s = excelUserKsMaxNum - interior.getNewReport().getExcelUserKs().size();
//                                for (int j = 0 ; j < s  ; j ++) {
//                                    interior.getNewReport().getExcelUserKs().add(e);
//                                }
//                            }
//                            map.put("newReport",interior.getNewReport());
//                        }
//                        //验收次数
//                        List<ProjectInteriorWeekAcceptance> weekAcceptances = interior.getWeekAcceptances();
//                        for (ProjectInteriorWeekAcceptance acceptance : weekAcceptances) {
//                            if (null != acceptance.getCreateTime()) {
//                                acceptance.setCreateTimes(Timestamp.DateTimeStamp(acceptance.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
//                            }
//                        }
//
//
//                        //构造每次最大验收次数 与验收人数
//                        int j = 0;
//                        //验收次数
//                        int ss = weekAcceptances.size();
//                        //验收次数 临时记录
//                        List<ProjectInteriorWeekAcceptance> ps = new ArrayList<>();
//                        for (String key : data.keySet()) {
//                            //此验收最大人数
//                            int personTotalNum = Integer.parseInt(data.get(key).toString());
//                            //此验收目前人数
//                            int personNum = 0;
//                            if (ss > j) {
//                                //本次验收记录 临时
//                                ProjectInteriorWeekAcceptance pw = weekAcceptances.get(j);
//                                if (null != weekAcceptances.get(j).getWeekAllocations()) {
//                                    personNum = weekAcceptances.get(j).getWeekAllocations().size();
//                                }
//                                //如果目前人数 小于最大人数 则新增相应人数
//                                if (personNum < personTotalNum) {
//                                    for (int l = 0 ; l < personTotalNum - personNum ; l ++) {
//                                        //内部项目 周验收发比例分配 临时记录
//                                        ProjectInteriorWeekKAllocation p = new ProjectInteriorWeekKAllocation();
//                                        p.setUserName("--");
//                                        if (personNum == 0) {
//                                            p.setIsManager(1);
//                                        } else {
//                                            p.setIsManager(0);
//                                        }
//                                        pw.getWeekAllocations().add(p);
//                                    }
//                                }
//                                //记录验收次数
//                                ps.add(pw);
//                            } else {
//                                ProjectInteriorWeekAcceptance pw = new ProjectInteriorWeekAcceptance();
//                                pw.setCreateTimes("--");
//                                List<ProjectInteriorWeekKAllocation> projectInteriorWeekKAllocations = new ArrayList<>();
//                                ProjectInteriorWeekKAllocation projectInteriorWeekKAllocation = new ProjectInteriorWeekKAllocation();
//                                for (int js = 0 ; js < Integer.parseInt(data.get(key).toString()) ; js ++) {
//                                    if (js!=0) {
//                                        projectInteriorWeekKAllocation.setIsManager(0);
//                                    } else {
//                                        projectInteriorWeekKAllocation.setIsManager(1);
//                                    }
//                                    projectInteriorWeekKAllocation.setUserName("--");
//                                    projectInteriorWeekKAllocations.add(projectInteriorWeekKAllocation);
//                                }
//                                pw.setWeekAllocations(projectInteriorWeekKAllocations);
//
//                                ps.add(pw);
//                            }
//                            j ++;
//                        }
//
//                        map.put("weekAcceptances",ps);
//                        jsonArrayResult.add(map);
//                        i ++;
//                    }
//                }
//            }
//
//
//            //项目台帐 最大格数 请不要修改ui格式
//            model.addAttribute("allWeekAcceptanceUserKNum",allWeekAcceptanceUserKNum + 17 + excelUserKsMaxNum * 3);
//            //每次验收对应的最大人数
//            model.addAttribute("weekAcceptance",data);
//            //任务信息及验收数据
//            model.addAttribute("departments",jsonArrayResult);
//            //项目阶段验收结果 最大成员数
//            model.addAttribute("excelUserKsMaxNum",excelUserKsMaxNum);
//            //最大验收次数
//            model.addAttribute("weekAcceptanceMaxNum",weekAcceptanceMaxNum);
//
//            response.setContentType("application/x-excel");
//            response.setHeader("Content-Disposition", "attachment; filename="+"detail.xls");
//            response.setCharacterEncoding("utf-8");
//            return "ftl/test";
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//            return "ftl/test";
//        }
////        model.addAttribute("departments",departments);
////        response.setContentType("application/x-excel");
////        response.setHeader("Content-Disposition", "attachment; filename="+"detail.xls");
////        response.setCharacterEncoding("utf-8");
////        String templatePath = "ftl/projectTemplate.ftl";
////        staticService.build(templatePath, data, response.getWriter());
//    }
}
