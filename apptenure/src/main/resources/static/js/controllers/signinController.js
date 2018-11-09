'use strict';

/* Controllers */
  // signin controller
app.controller('signFormController', ['$scope', '$http', '$state', '$rootScope', '$cookies', '$localStorage', function($scope, $http, $state, $rootScope, $cookies, $localStorage) {

    $scope.user = {};
    $scope.authError = null;
    $scope.showMsg = '获取验证码';
    var timestamp = '';
    $scope.getSmsCode = function(){
        if($scope.startCountdown){
            return;
        }
        if($scope.user.phone == null || $scope.user.phone == ''){
            $scope.authError = '请输入手机号码';
            return;
        }

    $http.get('/systemWeb/getRadomCodeWeb?phoneNum=' + $scope.user.phone).success(function(result){
        if(result.status == 'SUCCESS'){
            $scope.$broadcast('timer-start');//开始倒计时
            $scope.showMsg = 's 后重新获取';
            $scope.startCountdown = true;
            timestamp = result.data;
        }else{
            $scope.authError = result.error;
        }
    });
    };

    $scope.$on('timer-stopped', function (event, data){
        $scope.startCountdown = false;
        $scope.showMsg = "获取验证码";
        $scope.$digest();//通知视图模型的变化
    });

    $scope.login = function() {
        $scope.authError = null;
        $http.get('/systemWeb/loginWeb?timeStamp=' + timestamp + '&code=' + $scope.user.code).success(function(result){
            if(result.status == 'SUCCESS'){
                var expireDate = new Date();
                expireDate.setDate(expireDate.getDate() + 1);
                $cookies.put('userInfo', result.status, {'expires': expireDate});
                var url = ($localStorage.requestUrl == null || $localStorage.requestUrl == '') ? 'app.applyReport' : $localStorage.requestUrl;
                $state.go(url);
            }else{
                $scope.authError = result.error;
            }
        });
    };
  }])
;