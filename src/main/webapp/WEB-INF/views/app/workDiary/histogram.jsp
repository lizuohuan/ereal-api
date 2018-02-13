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
    var departmentId = YZ.getUrlParam("departmentId"); // 部门ID
    var time = YZ.getUrlParam("time"); // 查询时间
    var flag = YZ.getUrlParam("flag"); // 查询类型

    /*userToken = "b3fcb10abcf647fa87b75eba999a733e";
    time = 1496246400000;
    departmentId = 52;
    flag = 1;*/

    initData (); //默认调用
    //装载统计数据
    function initData () {
        var dateStr = new Array(); //时间数组
        var studys = new Array(); //学习工时
        var works = new Array(); //工作工时
        var sports = new Array(); //运动工时
        var totals = new Array(); //总工时
        var userNames = new Array(); //员工名字
        var max = 0; // 存放最大的工时
        var arr = {
            departmentId : departmentId,
            time : time,
            flag : flag
        }

        YZ.ajaxRequestData("post", false, YZ.ip + "/workDiary/countWorkDiary", arr , userToken , function(result) {
            if (result.flag == 0 && result.code == 200) {
                dateStr = result.data.dateStr;
                studys = result.data.studys;
                works = result.data.works;
                sports = result.data.sports;
                totals = result.data.totals;
                userNames = result.data.userNames;
                //计算总工时
                for (var i = 0; i < studys.length; i++) {
                    studys[i] = Number(studys[i]).toFixed(1);
                    works[i] = Number(works[i]).toFixed(1);
                    sports[i] = Number(sports[i]).toFixed(1);
                    totals[i] = Number(Number(studys[i]) + Number(works[i]) + Number(sports[i])).toFixed(1);
                }

                //寻找最大的总工时
                for (var i = 0; i < totals.length; i++) {
                    if (Number(totals[i]) > max) {
                        max = totals[i];
                    }
                }
            }
        });
        max = parseInt(YZ.getForTen(max));
        option = {
            // 图表标题
            title: {
                "text": "柱状图",
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
                //position:[0, 0],  //赋予一个[x,y]的位置
                /*position: function (pt) {
                    return [pt[0], '10%'];
                },*/
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
            color: ["#FECC21", "#6CCEFE", "#FF7B7C", "#86BE3E"],
            legend: [{
                orient: 'horizontal',
                x: 'center',
                y: '30px',
                itemWidth: 14,             // 图例图形宽度
                itemHeight: 14,            // 图例图形高度
                borderWidth: 0,            // 图例边框线宽，单位px，默认为0（无边框）
                icon:'stack', //无圆角
                data:['学习工时', '工作工时' , '运动工时', '总工时',]
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
                    data: userNames,
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
                        formatter : function (value) {//添加左侧单位
                            var number = Number(value).toFixed(0) + "h";
                            return number;
                        }
                    },
                }
            ],
            dataZoom: [{
                dataZoomIndex: 1,
                type: 'inside',
                startValue: userNames[0],
                endValue: userNames[5],
            }, {
                show: false,
                dataZoomIndex: 1,
                type: 'slider',
                y: '90%',
            }],
            series: [
                {
                    barMaxWidth: 30,
                    name:'学习工时',
                    stack: "学习工时",
                    type:'bar',
                    data: studys,
                },
                {
                    barMaxWidth: 30,
                    name:'工作工时',
                    stack: "工作工时",
                    type:'bar',
                    data: works,
                },
                {
                    barMaxWidth: 30,
                    name:'运动工时',
                    stack: "运动工时",
                    type:'bar',
                    data: sports,
                },
                {
                    barMaxWidth: 30,
                    name:'总工时',
                    stack: "总工时",
                    type:'bar',
                    data: totals,
                }
            ]
        }
    }

    waterChart.setOption(option); //渲染统计图
</script>
</body>
</html>
