package com.magic.ereal.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 移动端H5 页面 控制器
 * Created by Eric Xie on 2017/4/19 0019.
 */
@RequestMapping("/page")
@Controller
public class PageController {


    /**工作日志--折线图统计页面*/
    @RequestMapping("/workDiary/lineChart")
    public String lineChart() { return "app/workDiary/lineChart"; }

    /**工作日志--柱状图统计页面*/
    @RequestMapping("/workDiary/histogram")
    public String histogram() { return "app/workDiary/histogram"; }

    /**破题统计--柱状图统计页面*/
    @RequestMapping("/theEssay/histogram")
    public String theEssayHistogram() { return "app/theEssay/histogram"; }

    /**K值统计个人--柱状图统计页面*/
    @RequestMapping("/kValue/histogramWeek")
    public String kValueHistogramWeek() { return "app/kValue/histogramWeek"; }

    /**K值统计个人--折线图统计页面*/
    @RequestMapping("/kValue/lineChart")
    public String kValueLineChart() { return "app/kValue/lineChart"; }

    /**K值统计个人--(公司和部门)柱状图统计页面*/
    @RequestMapping("/kValue/histogramDepartmentOrCompanyWeek")
    public String histogramDepartmentOrCompanyWeek() { return "app/kValue/histogramDepartmentOrCompanyWeek"; }

    /**K值统计个人--(公司和部门)折线图统计页面*/
    @RequestMapping("/kValue/lineChartDepartmentOrCompany")
    public String lineChartDepartmentOrCompany() { return "app/kValue/lineChartDepartmentOrCompany"; }

    /**使用协议--移动端展示页面*/
    @RequestMapping("/agreement/agreement")
    public String agreement() { return "app/agreement/agreement"; }

    /**关于一真--移动端展示页面*/
    @RequestMapping("/agreement/about")
    public String about() { return "app/agreement/about"; }

    /**banner详情--H5页面*/
    @RequestMapping("/banner/bannerInfo")
    public String bannerInfo() { return "app/banner/bannerInfo"; }


}
