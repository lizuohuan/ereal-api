<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <title></title>
</head>
<body>
    <div id="water" style="width: 100%;height: 400px;"></div>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-2.1.0.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/config.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/echarts.min.js"></script>
    <script>
        var waterChart = echarts.init(document.getElementById("water")); // 统计图对象
        var option = {} // 统计图配置

        var userToken = YZ.getUrlParam("userToken"); // 用户登录token
        var startTime = YZ.getUrlParam("startTime"); // 开始时间
        var endTime = YZ.getUrlParam("endTime"); // 结束时间
        /*userToken = "b6f03d22ccbb4f01bd16abc68de7298c";
        startTime = 1495728000000;
        endTime = 1498406400000;*/

        initData (); //默认调用
        //装载统计数据
        function initData () {
            var allAry = new Array(); //此时间段全部项目
            var departmentNameAry = new Array(); //部门名数组
            var poAllAry = new Array(); //全破项目 数组
            var poHalfAry = new Array(); //半破项目 数组
            var poNoneAry = new Array(); //未破项目 数组
            var max = 0;
            var arr = {
                startTime : startTime, //开始时间
                endTime : endTime, //结束时间
            }
            YZ.ajaxRequestData("post", false, YZ.ip + "/project/getByTimeStatistics", arr , userToken , function(result) {
                if (result.flag == 0 && result.code == 200) {
                    allAry = result.data.allAry;
                    departmentNameAry = result.data.departmentNameAry;
                    poAllAry = result.data.poAllAry;
                    poHalfAry = result.data.poHalfAry;
                    poNoneAry = result.data.poNoneAry;
                    //寻找最大的
                    for (var i = 0; i < allAry.length; i++) {
                        if (Number(allAry[i]) > max) {
                            max = allAry[i];
                        }
                    }
                }
            });
            max = parseInt(YZ.getForTen(max));
            option = {
                // 图表标题
                title: {
                    /*"text": "柱状图",*/
                    x: 'center',                 // 水平安放位置，默认为左对齐，可选为：'center' ¦ 'left' ¦ 'right' ¦ {number}（x坐标，单位px）
                    y: 'top',                  // 垂直安放位置，默认为全图顶端，可选为：'top' ¦ 'bottom' ¦ 'center' ¦ {number}（y坐标，单位px）
                    backgroundColor: 'rgba(0,0,0,0)',
                    borderColor: '#ccc',       // 标题边框颜色
                    borderWidth: 0,            // 标题边框线宽，单位px，默认为0（无边框）
                    padding: 5,                // 标题内边距，单位px，默认各方向内边距为5，接受数组分别设定上右下左边距，同css
                    itemGap: 10,               // 主副标题纵向间隔，单位px，默认为10，
                    textStyle: {
                        fontSize: 16,
                        fontWeight: 'bolder',
                        color: '#333'          // 主标题文字颜色
                    },
                    subtextStyle: {
                        color: '#aaa'          // 副标题文字颜色
                    }
                },
                tooltip: {
                    "trigger": "axis",
                    "axisPointer": {
                        "type": "shadow",
                        shadowStyle : {                       // 阴影指示器样式设置
                            width: 'auto',                   // 阴影大小
                            color: 'rgba(150,150,150,0.1)'  // 阴影颜色
                        }
                    },
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                color: ["#F39996", "#8B95C9", "#EDB579", "#81CEF4"],
                legend: [{
                    orient: 'horizontal',
                    x: 'center',
                    y: '30px',
                    itemWidth: 14,             // 图例图形宽度
                    itemHeight: 14,            // 图例图形高度
                    borderWidth: 0,            // 图例边框线宽，单位px，默认为0（无边框）
                    icon:'stack', //无圆角
                    data:[
                        {
                            name:'未破',
                            //icon:'image://img/cbd.jpg'//格式为'image://+icon文件地址'，其中image::后的//不能省略
                        },
                        {
                            name:'半破',
                        },
                        '破','项目总数']
                }],
                calculable: true,
                xAxis: [
                    {
                        type: "category",
                        splitLine: {
                            show: false
                        },
                        axisTick: {
                            show: false
                        },
                        splitArea: {
                            show: false
                        },
                        data: departmentNameAry,
                    }
                ],
                yAxis: [
                    {
                        type: "value",
                        splitLine: {
                            show: false // y轴背景线条
                        },
                        "axisLine": {
                            show: true  // 左侧竖线线
                        },
                        axisTick: {
                            show: true  //刻度标识
                        },
                        splitArea: {
                            show: false //y轴背景颜色
                        },
                        max: max,
                        axisLabel: {
                            formatter : "{value}个", //添加左侧单位
                        },
                    }
                ],
                dataZoom: [{
                    dataZoomIndex: 1,
                    type: 'inside',
                    startValue: departmentNameAry[0],
                    endValue: departmentNameAry[5],
                }, {
                    show: false,
                    dataZoomIndex: 1,
                    type: 'slider',
                    y: '90%',
                }],
                series: [
                    {
                        name: "未破",
                        type: "bar",
                        stack: "未破",
                        barMaxWidth: 30,
                        data: poNoneAry,
                    },
                    {
                        name: "半破",
                        type: "bar",
                        stack: "半破",
                        barMaxWidth: 30,
                        data: poHalfAry,
                    },
                    {
                        name: "破",
                        type: "bar",
                        stack: "破",
                        barMaxWidth: 30,
                        data: poAllAry,
                    },
                    {
                        name: "项目总数",
                        type: "bar",
                        stack: "项目总数",
                        barMaxWidth: 30,
                        data: allAry,
                        /*markPoint : {
                            data : [
                                {name : '当前', value : 4, xAxis: 1, yAxis: 4},
                            ]
                        },*/
                    },
                ]
            }
        }

        waterChart.setOption(option); //渲染统计图
    </script>
</body>
</html>
