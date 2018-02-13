<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>使用协议与隐私政策</title>
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/mui/css/mui.min.css" />
    <style>
        .mui-content{
            background: #fff;
        }
        .mui-content .mui-scroll{
            padding: 20px;
        }
        .mui-content .mui-scroll p.title{
            text-align: center;
            color: #4D4D4D;
            font-size: 18px;
        }
    </style>
</head>
<body>
<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll">
        <div class="serviceprotocol">
            <p class="title"></p>
            <div class="servicecontent">

            </div>
        </div>
    </div>
</div>

<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-2.1.0.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/config.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/mui.min.js"></script>

<script type="text/javascript">
    var  options={
        scrollY:true,
        scrollX:false,
        startY:0,
        startX:0,
        indicators:false,
        deceleration:0.0005,
        bounce:true
    }
    mui('.mui-scroll-wrapper').scroll(options);

    getData();
    function getData(){
        YZ.ajaxRequestData("post", false, YZ.ip + "/agreement/getAgreement", {} , null , function(result) {
            if (result.flag == 0 && result.code == 200) {
                var agreement = result.data[0];
                $(".title").html(agreement.title);
                $(".servicecontent").html(agreement.content);
            }
        });
    }

</script>
</body>
</html>
