/**
 * Created by LEO on 16/9/19.
 */
angular.module('app').directive('wxImg', function () {
    return {
        restrict: 'E',
        replace: true,
        template: '<img src="" width="100" height="100">',
        link: function (scope, elem, attr) {
            $scope.$watch('per', function (nowVal) {
                elem.attr('src', nowVal);
            })
        }
    };
});