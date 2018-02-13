<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <title>折线图</title>
</head>
<body>
<div id="water" style="width: 100%;height: 400px;"></div>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-2.1.0.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/config.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/echarts.min.js"></script>
<script>

    var waterChart = echarts.init(document.getElementById("water")); // 统计图对象
    var option = {} // 统计图配置
    var openEye = "image://" + YZ.ip + "/resources/img/icon/openEye.png"; //睁眼
    var closeEye = "image://" + YZ.ip + "/resources/img/icon/closeEye.png"; //闭眼

    var userToken = YZ.getUrlParam("userToken"); // 用户登录token
    var userId = YZ.getUrlParam("userId"); // 用户ID
    var departmentId = YZ.getUrlParam("departmentId"); // 部门ID
    var companyId = YZ.getUrlParam("companyId"); // 分公司ID
    var time = YZ.getUrlParam("time"); // 查询时间
    var flag = YZ.getUrlParam("flag"); // 查询类型
    /*userToken = "03dd914d9520400088bddcd9e0a13c0f";
    userId = 163;
    time = 1498219791959;
    flag = 0;*/
    var clearTime = "";//明文时间
    if (flag == 0) {
        clearTime = new Date(Number(time)).format("yyyy年MM月dd日");
    }
    else {
        clearTime = new Date(Number(time)).format("yyyy年MM月");
    }
    var timeIndex = 0; //存放传入时间存在数据里面的对应下标
    initData (); //默认调用
    //装载统计数据
    function initData () {
        var dateStr = new Array(); //时间数组
        var studys = new Array(); //学习工时
        var works = new Array(); //工作工时
        var sports = new Array(); //运动工时
        var totals = new Array(); //总工时
        var max = 0;
        var arr = {
            userId : userId,
            //departmentId : departmentId,
            //companyId : companyId,
            time : time,
            flag : flag
        }
        YZ.ajaxRequestData("post", false, YZ.ip + "/workDiary/countWorkDiary", arr , userToken , function(result) {
            if (result.flag == 0 && result.code == 200) {
                dateStr = result.data.dateStr;
                for (var i = 0; i < dateStr.length; i++) {
                    if (dateStr[i] == clearTime) {
                        timeIndex = i;
                        break;
                    }
                }
                studys = result.data.studys;
                works = result.data.works;
                sports = result.data.sports;
                totals = result.data.totals;
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
                text: "统计表",
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
                trigger: 'axis', // 可选为：’item’ | ‘axis’
                show: true,
                hideDelay: 100,
                //position:[0, 0],  //赋予一个[x,y]的位置
                /*position: function (pt) {
                    return [pt[0], '10%'];
                },*/
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'line',         // 默认为直线，可选为：'line' | 'shadow'
                    lineStyle : {          // 直线指示器样式设置
                        color: '#48b',
                        width: 1,
                        type: 'solid'
                    },
                    shadowStyle : {                       // 阴影指示器样式设置
                        width: 'auto',                   // 阴影大小
                        color: 'rgba(150,150,150,0.3)'  // 阴影颜色
                    }
                },
                /*formatter: function (data) {
                 console.log(data);
                 return data;
                 },*/
                /*hideDelay: 100,
                alwaysShowContent: false,
                formatter: "{c}"*/

            },
            color: ["#FECC21", "#6CCEFE", "#FF7B7C", "#86BE3E"],
            legend: {
                orient: 'horizontal',      // 布局方式，默认为水平布局，可选为：'horizontal' ¦ 'vertical'
                x: 'center',               // 水平安放位置，默认为全图居中，可选为： 'center' ¦ 'left' ¦ 'right' ¦ {number}（x坐标，单位px）
                y: '30px',                  // 垂直安放位置，默认为全图顶端，可选为：'top' ¦ 'bottom' ¦ 'center' ¦ {number}（y坐标，单位px）
                backgroundColor: 'rgba(0,0,0,0)',
                borderColor: '#ccc',       // 图例边框颜色
                borderWidth: 0,            // 图例边框线宽，单位px，默认为0（无边框）
                padding: 5,                // 图例内边距，单位px，默认各方向内边距为5，接受数组分别设定上右下左边距，同css
                itemGap: 15,               // 各个item之间的间隔，单位px，默认为10， 横向布局时为水平间隔，纵向布局时为纵向间隔
                itemWidth: 20,             // 图例图形宽度
                itemHeight: 14,            // 图例图形高度
                textStyle: {
                    color: '#000'          // 图例文字颜色
                },
                data:[
                    {
                        name:'学习工时',
                        //icon:"image://" + YZ.ip + "/resources/img/icon/openEye.png", //格式为'image://+icon文件地址'，其中image::后的//不能省略
                    },
                    {
                        name:'工作工时',
                        //icon: openEye
                    },
                    {
                        name:'运动工时',
                        //icon:"image://" + YZ.ip + "/resources/img/icon/openEye.png"
                    },
                    {
                        name:'总工时',
                        //icon:"image://" + YZ.ip + "/resources/img/icon/openEye.png"
                    }],
                //selectedMode:'single', //单选
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: {
                type: 'category',
                boundaryGap: false,
                splitLine: {
                    show: false // 隐藏X轴线条
                },
                //data: ['4月1日','4月2日','4月3日','4月4日','4月5日','4月6日','4月7日','4月8日','4月9日','4月10日','4月11日','4月12日','4月13日','4月14日','4月15日','4月16日','4月17日','4月18日','4月19日','4月20日',]
                data: dateStr,
            },
            yAxis: {
                type: 'value',
                max: max,
                //tickPositions: [0, 20, 50, 100], // 指定竖轴坐标点的值
                axisLabel: {
                    formatter : function (value) {//添加左侧单位
                        var number = Number(value).toFixed(0) + "h";
                        return number;
                    }
                },
                splitLine: {
                    show: false // 隐藏y轴线条
                },
            },
            dataZoom: [{
                dataZoomIndex: 1,
                type: 'inside',
                startValue: dateStr[timeIndex - 5],
                endValue: dateStr[timeIndex + 5],
            }, {
                show: false,
                dataZoomIndex: 1,
                type: 'slider',
                y: '90%',
            }],
            series: [
                {
                    name:'学习工时',
                    type:'line',
                    smooth:false, //true:圆角,false:直角
                    //data:[100, 65, 101, 134, 90, 230, 210,54,111,44,20, 182, 191, 134, 190, 30, 110,88,67,56,],
                    data: studys,
                    /*markPoint: {
                        data: [
                            {name: '学习工时', value: 55, xAxis: 7, yAxis: 55} //value : 值、 xAxis : 横向位置、 yAxis : 竖向位置
                        ]
                    },*/
                    /*markLine: {
                     data: [
                     {type: 'average', name: '平均值'},
                     [{
                     symbol: 'none',
                     x: '90%',
                     yAxis: 'max'
                     }, {
                     symbol: 'circle',
                     label: {
                     normal: {
                     position: 'start',
                     formatter: '最大值'
                     }
                     },
                     type: 'max',
                     name: '最高点'
                     }]
                     ]
                     }*/
                },
                {
                    name:'工作工时',
                    type:'line',
                    smooth:false, //true:圆角,false:直角
                    //data:[20, 182, 191, 134, 190, 30, 110,88,67,56,50, 132, 201, 154, 190, 330, 210,34,111,78,]
                    data: works,
                },
                {
                    name:'运动工时',
                    type:'line',
                    smooth:false, //true:圆角,false:直角
                    //data:[50, 132, 201, 154, 190, 330, 210,34,111,78,50, 132, 201, 154, 190, 330, 210,34,111,78,]
                    data: sports
                },
                {
                    name:'总工时',
                    type:'line',
                    smooth:false, //true:圆角,false:直角
                    //data:[120, 32, 101, 134, 90, 30, 50,87,99,45,120, 32, 101, 134, 90, 30, 50,87,99,45,]
                    data: totals
                }
            ]
        };

    }

    waterChart.setOption(option);

    //单击标题栏
    /*waterChart.on('legendselectchanged', function (params) {
        console.log(params);
        //waterChart.showLoading({text : '数据获取中',effect: 'whirling'}); //加载数据loading
        //waterChart.hideLoading(); //隐藏loading
        var name = params.name;
        console.log(name);
        if (name == "学习工时") {
            //openEye = "image://" + YZ.ip + "/resources/img/icon/closeEye.png";
            console.log("成立了.");
        }
        //waterChart.setOption(option); //重新渲染
    });*/

    //单击拖动
    /*waterChart.on("dataZoom", function(param){
        console.log(param)
    });*/



</script>
</body>
</html>
