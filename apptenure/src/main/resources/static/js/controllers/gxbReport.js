app.controller('gxbReportController', ['$scope', '$http', '$state', '$localStorage', '$filter', '$rootScope', '$modal', '$cookies', '$location',function($scope, $http, $state, $localStorage, $filter, $rootScope, $modal, $cookies, $location) {

    $scope.token = $location.search().token;
    $scope.applyNum = $location.search().applyNum;
    $scope.approvalUuid = "";
    $scope.yes = "是";
    $scope.no = "否";
    $scope.married = "已婚";
    $scope.unMarried = "未婚";
    $scope.unknown = "未知";
    $scope.man = "男";
    $scope.woman = "女";
    $scope.typePersonal = "个人号";
    $scope.typePublic = "公众号";
    $scope.debitCard = "借记卡";
    $scope.creditCard = "信用卡";
    $scope.hit = "命中";
    $scope.noHit = "未命中";
    $scope.emptyString = "";
    $scope.hitWithBrackets = "（命中）";
    var date = new Date();
    var year = date.getFullYear();
    var month=date.getMonth()+1;
    var day = date.getDate();
    var hour = date.getHours();
    var minute = date.getMinutes();
    var second = date.getSeconds();
    month =(month<10 ? "0"+month:month);
    day =(day<10 ? "0"+day:day);
    hour =(hour<10 ? "0"+hour:hour);
    minute =(minute<10 ? "0"+minute:minute);
    second =(second<10 ? "0"+second:second);
    $scope.reportDate = year+"-"+ month+"-"+day+" "+hour+":"+minute+":"+second;
    $scope.isWechatContactShow = false;
    $scope.isWechatContactGroupShow = false;
    $scope.isWechatShareContactShow = false;
    $scope.isWechatShareContactGroupShow = false;
    $scope.isTaobaoTradeShow = false;
    $scope.isTaobaoTradeAmountShow = false;
    $scope.isAlipayPayTradeShow = false;
    $scope.isAlipayPayTradeAmountShow = false;
    $scope.isAlipayIncomeTradeShow = false;
    $scope.isAlipayIncomeTradeAmountShow = false;

    function init() {
        $http.get( '/gxb/getReportData?applyNum=' + $scope.applyNum + '&token=' + $scope.token).success(function (data) {
            if (data.status == 'SUCCESS') {
                $scope.reportData = data.data;
                $scope.approvalUuid =data.data.approvalUuid;
                var birthDate = data.data.applyFromDto.idCardInfoDto.dateOfBirth.substring(0,4);
                $scope.age =  parseInt(year) - parseInt(birthDate);
                $scope.name =  data.data.applyFromDto.idCardInfoDto.name;
                $scope.telephone =  data.data.applyFromDto.otherInfoDto.phoneNumber;
                var taobaoConsigneeAddressesDtoList = data.data.taobaoConsigneeAddressesDtoList;
                var telString = "";
                for (var i = 0; i < taobaoConsigneeAddressesDtoList.length; i++) {
                    telString = telString.concat(taobaoConsigneeAddressesDtoList[i].telNumber)
                }
                // 淘宝收货电话的拼接
                $scope.taobaotelString = telString;
                // 主贷人地址的拼接
                // $scope.applicantAddresses = ($scope.reportData.applyFromDto.idCardInfoDto.address).concat($scope.reportData.applyFromDto.driveLicenceInfoDto.address);
                // 主贷人电话的拼接
                $scope.applicantTel = $scope.reportData.applyFromDto.otherInfoDto.phoneNumber;
                if ($scope.reportData.applyFromDto.otherInfoDto.vicePhoneNumber != null && $scope.reportData.applyFromDto.otherInfoDto.vicePhoneNumber != '') {
                    $scope.applicantTel = $scope.applicantTel.concat($scope.reportData.applyFromDto.otherInfoDto.vicePhoneNumber);
                }
                // 淘宝姓名和主贷人姓名是否一致
                if ($scope.reportData.taobaoInfo != null && $scope.reportData.taobaoInfo != '') {
                    $scope.taobaoNameSame = $scope.reportData.taobaoInfo.name == $scope.reportData.applyFromDto.idCardInfoDto.name ? "（和主贷一致）" : "（和主贷不一致）";
                    // 淘宝手机号码和主贷人手机号码是否一致
                    var taobaoTelSameString = (($scope.reportData.taobaoInfo.mobile.substring(0, 3)) == ($scope.reportData.applyFromDto.otherInfoDto.phoneNumber.substring(0, 3))
                    && ($scope.reportData.taobaoInfo.mobile.substring(7, 11)) == ($scope.reportData.applyFromDto.otherInfoDto.phoneNumber.substring(7, 11))) ? "（和主贷一致）" : "（和主贷不一致）";
                    if (taobaoTelSameString == ("（和主贷不一致）") && $scope.reportData.applyFromDto.otherInfoDto.vicePhoneNumber != null && $scope.reportData.applyFromDto.otherInfoDto.vicePhoneNumber != '') {
                        taobaoTelSameString = (($scope.reportData.taobaoInfo.mobile.substring(0, 3)) == ($scope.reportData.applyFromDto.otherInfoDto.vicePhoneNumber.substring(0, 3))
                        && ($scope.reportData.taobaoInfo.mobile.substring(7, 11)) == ($scope.reportData.applyFromDto.otherInfoDto.vicePhoneNumber.substring(7, 11))) ? "（和主贷一致）" : "（和主贷不一致）";
                    }
                    $scope.taobaoTelSame = taobaoTelSameString;
                    // 淘宝身份证和主贷人身份证是否一致
                    $scope.taobaoIdCardSame = (($scope.reportData.taobaoInfo.identityCard.substring(0, 1)) == ($scope.reportData.applyFromDto.idCardInfoDto.idCardNum.substring(0, 1))
                    && ($scope.reportData.taobaoInfo.identityCard.substring(17, 18)) == ($scope.reportData.applyFromDto.idCardInfoDto.idCardNum.substring(17, 18))) ? "（和主贷一致）" : "（和主贷不一致）";
                }

                if ($scope.reportData.alipayInfo != null && $scope.reportData.alipayInfo != '') {
                    // 支付宝姓名和主贷人姓名是否一致
                    $scope.alipayNameSame = $scope.reportData.alipayInfo.name == ($scope.reportData.applyFromDto.idCardInfoDto.name) ? "（和主贷一致）" : "（和主贷不一致）";
                    // 支付宝手机号码和主贷人手机号码是否一致
                    var alipayTelSameString = (($scope.reportData.alipayInfo.mobile.substring(0, 3)) == ($scope.reportData.applyFromDto.otherInfoDto.phoneNumber.substring(0, 3))
                    && ($scope.reportData.alipayInfo.mobile.substring(7, 11)) == ($scope.reportData.applyFromDto.otherInfoDto.phoneNumber.substring(7, 11))) ? "（和主贷一致）" : "（和主贷不一致）";
                    if (alipayTelSameString == ("（和主贷不一致）") && $scope.reportData.applyFromDto.otherInfoDto.vicePhoneNumber != null && $scope.reportData.applyFromDto.otherInfoDto.vicePhoneNumber != '') {
                        alipayoTelSameString = (($scope.reportData.alipayInfo.mobile.substring(0, 3)) == ($scope.reportData.applyFromDto.otherInfoDto.vicePhoneNumber.substring(0, 3))
                        && ($scope.reportData.alipayInfo.mobile.substring(7, 11)) == ($scope.reportData.applyFromDto.otherInfoDto.vicePhoneNumber.substring(7, 11))) ? "（和主贷一致）" : "（和主贷不一致）";
                    }
                    $scope.alipayTelSame = alipayTelSameString;
                    // 支付宝身份证和主贷人身份证是否一致
                    $scope.alipayIdCardSame = (($scope.reportData.alipayInfo.identityCard.substring(0, 1)) == ($scope.reportData.applyFromDto.idCardInfoDto.idCardNum.substring(0, 1))
                    && ($scope.reportData.alipayInfo.identityCard.substring(17, 18)) == ($scope.reportData.applyFromDto.idCardInfoDto.idCardNum.substring(17, 18))) ? "（和主贷一致）" : "（和主贷不一致）";
                }

            } else {
                alert(data.error);
            }
            getWechatContactList();
            getWechatContactGroupList();
            getWechatShareContactList();
            getWechatShareContactGroupList();
            getTaobaoTradeList();
            getAlipayPayTradeList();
            getAlipayIncomeTradeList();
        });

    }
    // 微信联系人详细
    function getWechatContactList() {
        $http.get( '/gxb/getWechatContactList?approvalUuid=' + $scope.approvalUuid + '&token=' + $scope.token).success(function (data) {
            if (data.status == 'SUCCESS') {
                $scope.wechatContactList = data.data;
            } else {
                alert(data.error);
            }
        });

    }

    // 微信群详细
    function getWechatContactGroupList() {
            $http.get( '/gxb/getWechatContactGroupList?approvalUuid=' + $scope.approvalUuid + '&token=' + $scope.token).success(function (data) {
                if (data.status == 'SUCCESS') {
                    $scope.wechatContactGroupList = data.data;
                } else {
                    alert(data.error);
                }

            });
    }

    // 微信共享联系人详细
    function getWechatShareContactList() {
        $http.get( '/gxb/getWechatShareContactList?approvalUuid=' + $scope.approvalUuid + '&token=' + $scope.token).success(function (data) {
            if (data.status == 'SUCCESS') {
                $scope.wechatShareContactList = data.data;
            } else {
                alert(data.error);
            }

        });
    }

    // 微信共享群组详细
    function getWechatShareContactGroupList() {
        $http.get( '/gxb/getWechatShareContactGroupList?approvalUuid=' + $scope.approvalUuid + '&token=' + $scope.token).success(function (data) {
            if (data.status == 'SUCCESS') {
                $scope.wechatShareContacGrouptList = data.data;
            } else {
                alert(data.error);
            }

        });
    }

    // 淘宝交易记录详细
    function getTaobaoTradeList() {
        $http.get( '/gxb/getTaobaoTradeList?approvalUuid=' + $scope.approvalUuid + '&token=' + $scope.token).success(function (data) {
            if (data.status == 'SUCCESS') {
                $scope.taobaoTradeList = data.data;
            } else {
                alert(data.error);
            }

        });
    }

    // 支付宝支出交易详细
    function getAlipayPayTradeList() {
        $http.get( '/gxb/getAlipayTradeList?approvalUuid=' + $scope.approvalUuid + '&token=' + $scope.token + '&isIncomeOrPayFlag=' + '1').success(function (data) {
            if (data.status == 'SUCCESS') {
                $scope.alipayTradeList = data.data;
            } else {
                alert(data.error);
            }

        });
    }

    // 支付宝收入交易详细
    function getAlipayIncomeTradeList() {
        $http.get( '/gxb/getAlipayTradeList?approvalUuid=' + $scope.approvalUuid + '&token=' + $scope.token + '&isIncomeOrPayFlag=' + '0').success(function (data) {
            if (data.status == 'SUCCESS') {
                $scope.alipayIncomeTradeList = data.data;
            } else {
                alert(data.error);
            }

        });
    }

    // 画面初始化
    if($scope.applyNum != null && $scope.applyNum != '' && $scope.token != null && $scope.token != ''){
        init();
    } else {
        alert('参数不正确！');
    }

    $scope.wechatContactClick = function () {
        if ($scope.isWechatContactShow) {
            $scope.isWechatContactShow = false;
        } else if ($scope.reportData.wechatContactNum > 0 ) {
            $scope.isWechatContactShow = true;
        }
    }

    $scope.wechatContactGroupClick = function () {
        if ($scope.isWechatContactGroupShow) {
            $scope.isWechatContactGroupShow = false;
        } else if ($scope.reportData.wechatContactGroupNum > 0 ) {
            $scope.isWechatContactGroupShow = true;
        }
    }

    $scope.wechatShareContactClick = function () {
        if ($scope.isWechatShareContactShow) {
            $scope.isWechatShareContactShow = false;
        } else if ($scope.reportData.wechatShareContactNum > 0 ) {
            $scope.isWechatShareContactShow = true;
        }
    }

    $scope.wechatShareContactGroupClick = function () {
        if ($scope.isWechatShareContactGroupShow) {
            $scope.isWechatShareContactGroupShow = false;
        } else if ($scope.reportData.wechatShareContactGroupNum > 0 ) {
            $scope.isWechatShareContactGroupShow = true;
        }
    }

    $scope.taobaoTradeClick = function () {
        if ($scope.isTaobaoTradeShow) {
            $scope.isTaobaoTradeShow = false;
        } else if ($scope.reportData.taobaoInfo != null && $scope.reportData.taobaoInfo.wholeCount > 0 ) {
            $scope.isTaobaoTradeShow = true;
        }
    }

    $scope.taobaoTradeAmountClick = function () {
        if ($scope.isTaobaoTradeAmountShow) {
            $scope.isTaobaoTradeAmountShow = false;
        } else if ($scope.reportData.taobaoInfo != null && $scope.reportData.taobaoInfo.wholeFee > 0 ) {
            $scope.isTaobaoTradeAmountShow = true;
        }
    }

    $scope.alipayPayTradeClick = function () {
        if ($scope.isAlipayPayTradeShow) {
            $scope.isAlipayPayTradeShow = false;
        } else if ($scope.reportData.alipayPayTradeNum > 0 ) {
            $scope.isAlipayPayTradeShow = true;
        }
    }

    $scope.alipayPayTradeAmountClick = function () {
        if ($scope.isAlipayPayTradeAmountShow) {
            $scope.isAlipayPayTradeAmountShow = false;
        } else if ($scope.reportData.alipayPayTradeNum > 0 ) {
            $scope.isAlipayPayTradeAmountShow = true;
        }
    }

    $scope.alipayIncomeTradeClick = function () {
        if ($scope.isAlipayIncomeTradeShow) {
            $scope.isAlipayIncomeTradeShow = false;
        } else if ($scope.reportData.alipayPayTradeNum > 0 ) {
            $scope.isAlipayIncomeTradeShow = true;
        }
    }

    $scope.alipayIncomeTradeAmountClick = function () {
        if ($scope.isAlipayIncomeTradeAmountShow) {
            $scope.isAlipayIncomeTradeAmountShow = false;
        } else if ($scope.reportData.alipayPayTradeNum > 0 ) {
            $scope.isAlipayIncomeTradeAmountShow = true;
        }
    }


}]);
