<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <title>柱状图</title>
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
        var time = YZ.getUrlParam("time"); // 时间
        var timeType = YZ.getUrlParam("timeType"); // 查询时间阶段类型 1:周 2：月 3：年
        var type = YZ.getUrlParam("type"); //  1:部门 2：公司
        var departmentId = YZ.getUrlParam("departmentId");
        var companyId = YZ.getUrlParam("companyId");
        /*userToken = "81e2abe2109e42df8b299ee544d66486";
        time = 1497456000000;
        timeType = 1;
        type = 1;
        departmentId = 27;*/
        //companyId = 10;
        initData (); //默认调用
        //装载统计数据
        function initData () {
            var kw = new Array(); //k外数组
            var kn = new Array(); //k内数组
            var kt = new Array(); //项目总k数组
            var name = new Array(); //名称
            var max = 0; // 存放最大的
            var arr = {
                time : time,
                timeType : timeType,
                type : type,
                departmentId : departmentId,
                companyId : companyId
            }
            YZ.ajaxRequestData("post", false, YZ.ip + "/kStatistics/getCarToGramDapCom", arr , userToken , function(result) {
                if (result.flag == 0 && result.code == 200) {
                    kw = result.data.kw;
                    kn = result.data.kn;
                    kt = result.data.kt;
                    name = result.data.name;
                    //保留二位
                    for (var i = 0; i < kt.length; i++) {
                        kw[i] = Number(kw[i]).toFixed(2);
                        kn[i] = Number(kn[i]).toFixed(2);
                        kt[i] = Number(kt[i]).toFixed(2);
                    }
                    //寻找最大的
                    for (var i = 0; i < kt.length; i++) {
                        if (Number(kt[i]) > max) {
                            max = kt[i];
                        }
                    }
                }
            });
            max = parseInt(YZ.getForTen(max));

            option = {
                // 图表标题
                title: {
                    text: "柱状图",
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
                    /*position: function (pt) {
                        return [pt[0], '10%'];
                    },*/
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
                color: ["#FF7B7C", "#6ECFFF", "#85BD3D", "#85CDCA", "#A788B9"],
                legend: [{
                    orient: 'horizontal',
                    x: 'center',
                    y: '30px',
                    itemWidth: 14,             // 图例图形宽度
                    itemHeight: 14,            // 图例图形高度
                    borderWidth: 0,            // 图例边框线宽，单位px，默认为0（无边框）
                    icon:'stack', //无圆角
                    data:['K外', 'K内' , '总K']
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
                        data: name,
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
                        axisLabel: {
                            formatter : function (value) {//添加左侧单位
                                var number = Number(value).toFixed(0) + "K";
                                return number;
                            }
                        },
                        max: max
                    }
                ],
                dataZoom: [{
                    dataZoomIndex: 1,
                    type: 'inside',
                    startValue: name[0],
                    endValue: name[5],
                }, {
                    show: false,
                    dataZoomIndex: 1,
                    type: 'slider',
                    y: '90%',
                }],
                series: [
                    {
                        name: "K外",
                        type: "bar",
                        stack: "K外",
                        barMaxWidth: 30,
                        data: kw,
                    },
                    {
                        name: "K内",
                        type: "bar",
                        stack: "K内",
                        barMaxWidth: 30,
                        data: kn,
                    },
                    {
                        name: "总K",
                        type: "bar",
                        stack: "总K",
                        barMaxWidth: 30,
                        data: kt,
                    }
                ]
            }
        }

        waterChart.setOption(option); //渲染统计图
    </script>
</body>
</html>
