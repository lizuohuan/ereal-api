<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title>banner详情</title>
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/mui.min.css" />
    <style>
        .context{max-width: 100% !important;}
        .context img {max-width: 100% !important;}
        .context iframe {max-width: 100% !important;}
        .banner {max-width: 100% !important;}
    </style>
    <style>
        /*图片预览css*/
        .mui-preview-image.mui-fullscreen {
            position: fixed;
            z-index: 1001;
            background-color: #000;
        }
        .mui-preview-header,
        .mui-preview-footer {
            position: absolute;
            width: 100%;
            left: 0;
            z-index: 10;
        }
        .mui-preview-header {
            height: 44px;
            top: 0;
        }
        .mui-preview-footer {
            height: 50px;
            bottom: 0px;
        }
        .mui-preview-header .mui-preview-indicator {
            display: block;
            line-height: 25px;
            color: #fff;
            text-align: center;
            margin: 15px auto 4px !important;
            background-color: rgba(0, 0, 0, 0.4);
            border-radius: 12px;
            font-size: 16px;
        }
        .mui-preview-image {
            display: none;
            -webkit-animation-duration: 0.5s;
            animation-duration: 0.5s;
            -webkit-animation-fill-mode: both;
            animation-fill-mode: both;
        }
        .mui-preview-image.mui-preview-in {
            -webkit-animation-name: fadeIn;
            animation-name: fadeIn;
        }
        .mui-preview-image.mui-preview-out {
            background: none;
            -webkit-animation-name: fadeOut;
            animation-name: fadeOut;
        }
        .mui-preview-image.mui-preview-out .mui-preview-header,
        .mui-preview-image.mui-preview-out .mui-preview-footer {
            display: none;
        }
        .mui-zoom-scroller {
            position: absolute;
            display: -webkit-box;
            display: -webkit-flex;
            display: flex;
            -webkit-box-align: center;
            -webkit-align-items: center;
            align-items: center;
            -webkit-box-pack: center;
            -webkit-justify-content: center;
            justify-content: center;
            left: 0;
            right: 0;
            bottom: 0;
            top: 0;
            width: 100%;
            height: 100%;
            margin: 0;
            -webkit-backface-visibility: hidden;
        }
        .mui-zoom {
            -webkit-transform-style: preserve-3d;
            transform-style: preserve-3d;
        }
        #page3 .mui-slider .mui-slider-group .mui-slider-item img {
            width: auto;
            height: auto;
            max-width: 100%;
            max-height: 100%;
        }
        .mui-android-4-1 .mui-slider .mui-slider-group .mui-slider-item img {
            width: 100%;
        }
        .mui-android-4-1 .mui-slider.mui-preview-image .mui-slider-group .mui-slider-item {
            display: inline-table;
        }
        .mui-android-4-1 .mui-slider.mui-preview-image .mui-zoom-scroller img {
            display: table-cell;
            vertical-align: middle;
        }
        .mui-preview-loading {
            position: absolute;
            width: 100%;
            height: 100%;
            top: 0;
            left: 0;
            display: none;
        }
        .mui-preview-loading.mui-active {
            display: block;
        }
        .mui-preview-loading .mui-spinner-white {
            position: absolute;
            top: 50%;
            left: 50%;
            margin-left: -25px;
            margin-top: -25px;
            height: 50px;
            width: 50px;
        }
        .mui-preview-image img.mui-transitioning {
            -webkit-transition: -webkit-transform 0.5s ease, opacity 0.5s ease;
            transition: transform 0.5s ease, opacity 0.5s ease;
        }
        @-webkit-keyframes fadeIn {
            0% {
                opacity: 0;
            }
            100% {
                opacity: 1;
            }
        }
        @keyframes fadeIn {
            0% {
                opacity: 0;
            }
            100% {
                opacity: 1;
            }
        }
        @-webkit-keyframes fadeOut {
            0% {
                opacity: 1;
            }
            100% {
                opacity: 0;
            }
        }
        @keyframes fadeOut {
            0% {
                opacity: 1;
            }
            100% {
                opacity: 0;
            }
        }
        p img {
            max-width: 100%;
            height: auto;
        }
    </style>

</head>

<body ng-app="webApp" ng-controller="bannerCtr" ng-cloak>

    <div class="mui-content">
        <img class="banner" data-preview-src="" data-preview-group="1" id="banner" src="">
        <div class="context" ng-bind-html="banner.context"></div>
    </div>

    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-2.1.0.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/angular.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/config.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/mui.min.js"></script>
    <script src="<%=request.getContextPath()%>/resources/js/mui.zoom.js"></script>
    <script src="<%=request.getContextPath()%>/resources/js/mui.previewimage.js"></script>

    <script type="text/javascript">
        mui.previewImage();//开启图片预览
        $(function () {
            $(".context").find("img").attr("data-preview-src", "").attr("data-preview-group", 1);
        });
        var img = "";
        var webApp=angular.module('webApp',[]);
        //文章详情
        webApp.controller('bannerCtr',function($scope,$http,$timeout,$sce){
            $scope.banner = null;
            $scope.bannerId = YZ.getUrlParam("bannerId");
            $scope.source = YZ.getUrlParam("source");
            $scope.userToken = YZ.getUrlParam("userToken");
//            $scope.imgUrl = YZ.ipImg +"/"+$scope.banner.imgUrl;
            /*$scope.bannerId = 1;
            $scope.userToken = "7b056bb106764f7aa6b85b9df0035269";*/
            var arr = {
                id:$scope.bannerId,
                source:$scope.source
            }
            YZ.ajaxRequestData("post", false, YZ.ip + '/banner/info', arr, $scope.userToken, function(result){
                if(result.flag==0 && result.code==200){
                    console.log(result.data)
                    $scope.banner = result.data;
                    $("#banner").attr('src',YZ.ipImg+"/"+result.data.imgUrl);
                }
            });
            //解析angularJS富文本字符串
            $scope.banner.context = $sce.trustAsHtml($scope.banner.context);

        });



    </script>
</body>

</html>