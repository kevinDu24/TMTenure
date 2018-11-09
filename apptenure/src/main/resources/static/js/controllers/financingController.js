app.controller('financingController', ['$scope', '$state', '$http', '$filter', '$localStorage', '$modal', function($scope,$state, $http, $filter, $localStorage, $modal) {

    $scope.startDate = $scope.endDate = new Date();
    $scope.isHplUser = $localStorage.isHplUser;
    var currentArea;
    function init(){
        //if($localStorage.areaLevels != null && $localStorage.areaLevels.length != 0){
        //    currentArea = $localStorage.areaLevels[0];
        //    getStatisticData(currentArea);
        //}else{
            $http.get('/sales/queryAreaLevel').success(function(result){
                if(result.status == 'SUCCESS'){
                    $localStorage.areaLevels = [];
                    $localStorage.areaLevels = result.data;
                    currentArea = $localStorage.areaLevels[0];
                    getStatisticData(currentArea);
                }
            });
        //}
    }

    function getParentLevel(levelId){
        if(levelId == 0){
            return;
        }
        var area = getAreaInfo(levelId);
        return getAreaInfo(area.parentId);
    }

    function getAreaInfo(levelId){
        var areaLevels = $localStorage.areaLevels;
        for(var i in areaLevels){
            if(areaLevels[i].levelId == levelId){
                return areaLevels[i];
            }
        }
    }

    function getStatisticData(area){
        $http.get('/sales/queryStatisticsByDate?type=5&userLevel='+area.levelId+'&startDate='+$filter('date')($scope.startDate,'yyyyMMdd')+'&endDate='+$filter('date')($scope.endDate,'yyyyMMdd')).success(function(data){
            if(data.status == 'SUCCESS'){
                if(data.data.tableGrid == null || data.data.tableGrid.length == 0 || (data.data.tableGrid.length == 1 && data.data.tableGrid[0].levelId == area.levelId)){
                    return;
                }
                currentArea = area;
                $scope.datas = data.data.tableGrid;
                $scope.header = {realCount:0};
                if(currentArea.area == '' || currentArea.area == null){
                    $scope.header.name = currentArea.areaName;
                }else {
                    $scope.header.name = currentArea.area;
                }
                $scope.header.levelId = currentArea.levelId;
                for(var i in $scope.datas){
                    if($scope.datas[i].realCount == ''){
                        $scope.datas[i].realCount = 0;
                    }
                    $scope.header.realCount += parseFloat($scope.datas[i].realCount);
                }
            }
        });
    }

    init();

    $scope.gotoPage = function(page){
        $state.go(page);
    };

    $scope.goToDeep = function(area){
        if(currentArea.levelId == area.levelId){
            return;
        }
        //currentArea = area;
        getStatisticData(area);
    };

    $scope.goUp = function(area){
        var temp = getParentLevel(area.levelId);
        if(temp == null){
            return;
        }
        currentArea.area = temp.areaName;
        currentArea.levelId = temp.levelId;
        getStatisticData(currentArea);
    };

    $scope.refresh = function(){
        getStatisticData(currentArea);
    };
    $scope.gotoPage = function(page){
        $state.go(page);
    };

    $scope.open = function () {
        var modalInstance = $modal.open({
            templateUrl: 'tpl/areaSelector.html',
            controller: 'areaSelectorController',
            size: 'small'
        });

        modalInstance.result.then(function (area) {
            if(area != null && area != ''){
                //$scope.currentArea = area;
                getStatisticData(area);
            }
        }, function () {
            console.log();
        });
    };
}])
;
