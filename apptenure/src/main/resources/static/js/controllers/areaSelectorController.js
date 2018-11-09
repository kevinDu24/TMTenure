/**
 * Created by LEO on 16/8/31.
 */
app.controller('areaSelectorController', ['$scope','$modalInstance', '$localStorage', function($scope,$modalInstance, $localStorage) {
    $scope.selectors = [];
    function initSelector(){
        var topArea = $localStorage.areaLevels[0];
        $scope.selectors.push(topArea);
        for(var i in $localStorage.areaLevels){
            if($localStorage.areaLevels[i].parentId == topArea.levelId){
                $scope.selectors.push($localStorage.areaLevels[i]);
            }
        }

        for(var i in $scope.selectors){
            if(i == 0){
                $scope.selectors[i].childLevels = [];
                continue;
            }
            var tempArray = [];
            for(var j in $localStorage.areaLevels){
                if($scope.selectors[i].levelId == $localStorage.areaLevels[j].parentId){
                    tempArray.push($localStorage.areaLevels[j]);
                }
            }
            $scope.selectors[i].childLevels = tempArray;
        }
    }

    initSelector();

    $scope.close = function (area) {
        $modalInstance.close(area);
    };

}]);