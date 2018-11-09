'use strict';

/* Controllers */
  // signin controller
app.controller('SigninFormController', ['$scope', '$http', '$state', '$localStorage', '$cookies', function($scope, $http, $state, $localStorage, $cookies) {
    $scope.user = {};
    $scope.rememberPassword = true;
    $scope.authError = false;
    $scope.user.username = $cookies.get("userName");
    $scope.user.password = $cookies.get("password");
    var authenticate = function (headers) {
        $scope.alerts = [];
        $http.get('/user?deviceType=web', {headers: headers})
            .success(function (result) {
                if(($cookies.get('userName') == null || $cookies.get('userName') == '') && $scope.rememberPassword){
                    var expireDate = new Date();
                    expireDate.setDate(expireDate.getDate() + 30);
                    $cookies.put("userName", $scope.user.username, {'expires': expireDate});
                    $cookies.put("password", $scope.user.password, {'expires': expireDate});
                }
                if(!$scope.rememberPassword){
                    $cookies.remove('userName');
                    $cookies.remove('password');
                }
                $cookies.put("Authorization", "Basic " + btoa($scope.user.username + ":" + $scope.user.password), {'expires': expireDate});
                $localStorage.isHplUser = result.data.isHplUser;
                if($localStorage.requestUrl != null && $localStorage.requestUrl != ''){
                    $state.go($localStorage.requestUrl);
                }else{
                    $state.go('app.hpl.apply');
                }
            }).error(function () {
                $scope.authError = true;
            });
    };

    $scope.login = function () {
        var headers = {};
        if ('' === $scope.user.password || undefined === $scope.user.password) {
            //$scope.addAlert("danger","请填写密码");
            return;
        } else {
            headers = {authorization: "Basic " + btoa($scope.user.username + ":" + $scope.user.password)};
        }
        authenticate(headers);
    };

  }])
;