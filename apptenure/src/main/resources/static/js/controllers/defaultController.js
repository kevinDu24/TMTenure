/**
 * Created by LEO on 16/9/9.
 */
app.controller('defaultController', ['$scope', '$state', '$http', '$filter', '$localStorage', '$modal', function($scope,$state, $http, $filter, $localStorage, $modal) {
    $scope.alert = function(){
        alert("功能正在开发中...");
    };
    $scope.logout = function(){
        $http.post('logout').success(function(){
            $state.go('access.signin');
        })
    }
}]);