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

    var userToken = YZ.getUrlParam("userToken"); // 用户登录token
    var time = YZ.getUrlParam("time"); // 时间
    var timeType = YZ.getUrlParam("timeType"); // 查询时间阶段类型 1:周 2：月 3：年
    var type = YZ.getUrlParam("type"); //  1:部门 2：公司
    var departmentId = YZ.getUrlParam("departmentId");
    var companyId = YZ.getUrlParam("companyId");
    /*userToken = "db337cc4b17347769c4d0736db9a2735";
    time = 1495036800000;
    timeType = 3;
    type = 2;
    departmentId = 10;
    companyId = 10;*/
    var date = new Date(Number(time));
    var clearTime = "";
    if (timeType == 2) {
        clearTime = (date.getMonth() + 1) + "月" + date.getDate() + "日";
    }
    else if (timeType == 3) {
        var str = date.getFullYear() + "";
        clearTime = str.substring(2, str.length) + "年" + (date.getMonth() + 1) + "月";//new Date(Number(time)).format("yy年MM月");
    }
    var timeIndex = 0; //存放传入时间存在数据里面的对应下标

    initData (); //默认调用
    //装载统计数据
    function initData () {
        var kw = new Array(); //K外数组
        var kn = new Array(); //K内工时
        var kt = new Array(); //K总
        var date = Array(); //日期
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
                date = result.data.name;
                for (var i = 0; i < date.length; i++) {
                    if (date[i] == clearTime) {
                        timeIndex = i;
                        break;
                    }
                }
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
                /*position: function (pt) { //出现在顶部位置
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
                data:['K外', 'K内' , '总K'],
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
                data: date,
            },
            yAxis: {
                type: 'value',
                axisLabel: {
                    formatter : function (value) {//添加左侧单位
                        var number = Number(value).toFixed(0) + "K";
                        return number;
                    }
                },
                max: max,
                splitLine: {
                    show: false // 隐藏y轴线条
                },
            },
            dataZoom: [{
                dataZoomIndex: 1,
                type: 'inside',
                startValue: date[timeIndex - 5],
                endValue: date[timeIndex + 5],
            }, {
                show: false,
                dataZoomIndex: 1,
                type: 'slider',
                y: '90%',
            }],
            series: [
                {
                    name:'K外',
                    type:'line',
                    smooth:false, //true:圆角,false:直角
                    //data:[100, 65, 101, 134, 90, 230, 210,54,111,44,20, 182, 191, 134, 190, 30, 110,88,67,56,],
                    data: kw,
                },
                {
                    name:'K内',
                    type:'line',
                    smooth:false, //true:圆角,false:直角
                    //data:[20, 182, 191, 134, 190, 30, 110,88,67,56,50, 132, 201, 154, 190, 330, 210,34,111,78,]
                    data: kn,
                },
                {
                    name:'总K',
                    type:'line',
                    smooth:false, //true:圆角,false:直角
                    //data:[50, 132, 201, 154, 190, 330, 210,34,111,78,50, 132, 201, 154, 190, 330, 210,34,111,78,]
                    data: kt
                },
            ]
        };

    }

    waterChart.setOption(option);

</script>
</body>
</html>
