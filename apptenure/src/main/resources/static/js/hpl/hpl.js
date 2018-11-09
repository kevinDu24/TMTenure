/**
 * Created by HJYang on 2016/12/15.
 */
var privNum = 0;
var lastData = [];
var nowData = [];
var showData = [];
var showDataMin = [];

var refreshTime = 240000;
var eachRreshTime = 0;
var time_first;
var time_day;

$(document).ready(function(){
    $('.burning').burn();
    $("#demoDiv2").MyFloatingBg({direction:0, speed:50});
    firstOpenPage();
    time_day = window.setInterval("time_fn()",refreshTime);
});

function firstOpenPage(){
    privNum = 0;
    lastData = [];
    nowData = [];
    showData = [];
    showDataMin = [];

    $.ajax({
        url: "/contracts/applyNum",
        type: 'GET',
        async: false,
        success: function(data){
            if(1 > data.data.length ){
                alert("获取'HPL当日申请量'数据为空！");
            }else {
                nowData =  data.data;
            }
        },
        error : function(data){
            alert("获取'HPL当日申请量'数据失败！");
        }
    });
    lastData = nowData;
    showData = nowData;
    if(showData.length > 0){
        $('.testNumber').html("");
        eachRreshTime = parseInt(refreshTime/showData.length);
        $('#province').html(showData[privNum].area);
        var num = showData[privNum].count;
        if(showData[privNum].count == 0){
            $(".testNumber").text(0);
        }else{
            $(".testNumber").numberRock({min:0, max:showData[privNum].count, speed:setMinTime(num)});
        }
        privNum ++;
        for(var i = 1; i < showData.length; i++){
            window.setTimeout("refresh_first()",i*eachRreshTime);
        }
    }
}
function refresh_first(){
    if (privNum >= showData.length){
        lastData = nowData;
        window.clearInterval(time_first);
    }else {
        $('.testNumber').html("");
        $('#province').html(showData[privNum].area);
        var num = showData[privNum].count;
        if(showData[privNum].count == 0){
            $(".testNumber").text(0);
        }else{
            $(".testNumber").numberRock({min:0, max:showData[privNum].count, speed:setMinTime(num)});
        }
        privNum ++;
    }
}
function time_fn(){
    nowData = [];
    $.ajax({
        url: "/contracts/applyNum",
        type: 'GET',
        async: false,
        success: function(data){
            if(1 > data.data.length ){
                alert("获取'HPL当日申请量'数据为空！");
            }else {
                nowData = data.data;

                if (ifNextDay()){
                    firstOpenPage();
                }else if(nowData.length != lastData.length){
                    firstOpenPage();
                }else {
                    repeat_fn();
                }
            }
        },
        error : function(data){
            alert("获取'HPL当日申请量'数据失败！");
        }
    });
}
function repeat_fn(){
    privNum = 0;
    showData = [];
    showDataMin = [];
    for (var i = 0;i < nowData.length; i++ ){
        for(var j = 0;j < lastData.length;j++ ){
            if(nowData[i].area == lastData[j].area && nowData[i].count != lastData[j].count){
                showData.push(nowData[i]);
                showDataMin.push(lastData[j])
                lastData[j] = nowData[i];
            }
        }
    }
    if(showData.length > 0){
        $('.testNumber').html("");
        eachRreshTime = parseInt(refreshTime/showData.length);
        $('#province').html(showData[privNum].area);
        var num = showData[privNum].count;
        if(showData[privNum].count == 0){
            $(".testNumber").text(0);
        }else{
            $(".testNumber").numberRock({min:showDataMin[privNum].count, max:showData[privNum].count, speed:setMinTime(num)});
        }
        privNum ++;
        for(var i = 1; i < showData.length; i++){
            window.setTimeout("refresh_first_2()",i*eachRreshTime);
        }
    }
}
function refresh_first_2(){
    if (privNum >= showData.length){
        lastData = nowData;
        window.clearInterval(time_first);
    }else {
        $('.testNumber').html("");
        $('#province').html(showData[privNum].area);
        console.log(showData[privNum].area);
        var num = showData[privNum].count;
        if(showData[privNum].count == 0){
            $(".testNumber").text(0);
        }else{
            $(".testNumber").numberRock({min:showDataMin[privNum].count, max:showData[privNum].count, speed:setMinTime(num)});
        }
        privNum ++;
    }
}
function ifNextDay() {
    if(getDate(nowData[0].XTCZRQ) > 10 && getDate(lastData[0].XTCZRQ) <= 10)
        return true;
    else
        return false;
}
function getDate(date) {
    var day = date.substring(6,8);
    return day;
}
function setMinTime(num){
    if(num <= 40){
        return 50;
    }else if (40< num <= 300){
        return 30;
    }else if (300<  num <=800){
        return 18;
    }else if (800<  num <=1200){
        return 15;
    }else if(1200<  num <=3000){
        return 10;
    }else if(3000<  num <=5000){
        return 5;
    }else if(5000<  num <=8000){
        return 3;
    }else{
        return 2;
    }
}