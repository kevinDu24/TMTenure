app.controller('applyReportController', ['$scope', '$http', '$modal', '$window', '$filter','$localStorage', '$cookies', '$state', function ($scope, $http, $modal, $window,$filter,$localStorage, $cookies, $state) {
    $localStorage.requestUrl = 'app.applyReport';
     var userInfo = $cookies.get('userInfo');
     if(userInfo == null || userInfo == ''){
        $state.go('access.login');
     }
    $scope.status="";
    $scope.applyNum="";
     $scope.phoneNum="";
    $scope.fpName="";
    $scope.name="";
    $scope.nowDate = new Date();
    $scope.endTime = $filter('date')($scope.nowDate, 'yyyy-MM-dd');
    $scope.beginTime =  $filter('date')(new Date(new Date().getTime()-7*1000*60*60*24), 'yyyy-MM-dd');
    $scope.monthData;
    $scope.searchType = 1;

    //ngGrid初始化数据
    $scope.filterOptions = {
        filterText: "",
        useExternalFilter: true
    };

    $scope.pagingOptions = {
        pageSizes: [10, 15, 20, 50, 100],
        pageSize: '10',
        currentPage: 1
    };

            $scope.gridOptions0 = {
                    data: 'codes',
                    enablePaging: true,
                    showFooter: true,
                    multiSelect: false,
                    rowHeight: 41,
                    headerRowHeight: 36,
                    enableHighlighting : true,
                    enableColumnResize: true,
                    totalServerItems: 'totalServerItems',
                    pagingOptions: $scope.pagingOptions,
                    columnDefs: [
                        { field: 'name', displayName: '姓名', width:'20%' },
                        { field: 'idcard', displayName: '身份证号', width:'20%' },
                        { field: 'phonenum', displayName: '手机号', width:'20%' },
                        { field: 'applynum', displayName: '申请编号', width:'20%',cellTemplate: '<div class="ui-grid-cell-contents"  style="margin-top: 8px; margin-left:15px">{{ row.entity.applynum == null ? "无" : row.entity.applynum }}</div>' },
                        { field: 'createtime', displayName: '创建时间', width:'20%', cellFilter: "date:'yyyy-MM-dd HH:mm:ss'" }
                    ]
                };

           $scope.gridOptions1 = {
                   data: 'codes',
                   enablePaging: true,
                   showFooter: true,
                   multiSelect: false,
                   rowHeight: 41,
                   headerRowHeight: 36,
                   enableHighlighting : true,
                   enableColumnResize: true,
                   totalServerItems: 'totalServerItems',
                   pagingOptions: $scope.pagingOptions,
                   columnDefs: [
                       { field: 'name', displayName: '姓名', width:'80px' },
                       { field: 'idcard', displayName: '身份证号', width:'165px' },
                       { field: 'phonenum', displayName: '手机号', width:'110px' },
                       { field: 'applynum', displayName: '申请编号', width:'90px',cellTemplate: '<div class="ui-grid-cell-contents"  style="margin-top: 8px; margin-left:15px">{{ row.entity.applynum == null ? "无" : row.entity.applynum }}</div>' },
                       { field: 'money', displayName: '融资额', width:'80px' },
                       { field: 'month', displayName: '期数', width:'60px' },
                       { field: 'updatetime', displayName: '签署时间', width:'150px', cellFilter: "date:'yyyy-MM-dd HH:mm:ss'" },
//                       { field: 'faceimageurl', displayName: '人脸图片', width:'120px', cellTemplate: '<div class="ui-grid-cell-contents"  style="margin-top: 8px; margin-left:15px">{{row.entity.faceimageurl == null ? "无" : row.entity.faceimageurl}}</div>'},
                       { field: 'faceimageurl', displayName: '人脸图', width:'90px', cellTemplate: '<div class="ui-grid-cell-contents"  style="margin-top: 8px; margin-left:15px"><p ng-if = "row.entity.faceimageurl == null || row.entity.faceimageurl === undefined ">无</p><span class="glyphicon glyphicon-eye-open"></span><a style="color: blue;margin-left: 5px" ng-show = "row.entity.faceimageurl" ng-href={{row.entity.faceimageurl}} target="_blank">查看</a></div>'},
                       { field: 'idcardurl', displayName: '身份证图', width:'100px', cellTemplate: '<div class="ui-grid-cell-contents"  style="margin-top: 8px; margin-left:20px"><p ng-if = "row.entity.idcardurl == null || row.entity.idcardurl === undefined ">无</p><span class="glyphicon glyphicon-eye-open"></span><a style="color: blue;margin-left: 5px" ng-show = "row.entity.idcardurl" ng-href={{row.entity.idcardurl}} target="_blank">查看</a></div>'},
                       { field: 'contactsignedpdf', displayName: '合同', width:'80px', cellTemplate: '<div class="ui-grid-cell-contents"  style="margin-top: 8px; margin-left:15px"><p ng-if = "row.entity.contactsignedpdf == null || row.entity.contactsignedpdf === undefined ">无</p><span class="glyphicon glyphicon-eye-open"></span><a style="color: blue;margin-left: 5px" ng-show = "row.entity.contactsignedpdf" ng-href={{row.entity.contactsignedpdf}} target="_blank">查看</a></div>'},
                       { field: 'confirmationsignedpdf', displayName: '确认函', width:'80px', cellTemplate: '<div class="ui-grid-cell-contents"  style="margin-top: 8px; margin-left:15px"><p ng-if = "row.entity.confirmationsignedpdf == null || row.entity.confirmationsignedpdf === undefined ">无</p><span class="glyphicon glyphicon-eye-open"></span><a style="color: blue;margin-left: 5px" ng-show = "row.entity.confirmationsignedpdf" ng-href={{row.entity.confirmationsignedpdf}} target="_blank">查看</a></div>'},
                       { field: 'proof', displayName: '申请出证', width:'90px', cellTemplate: '<div class="ui-grid-cell-contents"  style="margin-top: 8px; margin-left:15px"><span class="glyphicon glyphicon-link"></span><a style="color: blue;margin-left: 5px" ng-href="https://oauth2.tsign.cn/tgoauth2/authorize!login" target="_blank">申请</a></div>'}
                   ]
               };


    // 点击radio按钮
    $scope.onChangeRadio = function () {
        $scope.search();
    };

    $scope.getPagedDataAsync = function (pageSize, page, searchText) {
        var startTime = $scope.beginTime;
        var endTime = $scope.endTime;

        var url = '/systemWeb/search?page=' + page + '&size=' + pageSize
                +'&applyNum='+$scope.applyNum
                +'&phoneNum='+$scope.phoneNum
                +'&name='+$scope.name
                +'&searchType='+$scope.searchType
            ;
        if($scope.beginTime != ""){
            url+='&beginTime='+startTime;
        }
        if($scope.endTime != ""){
            url+='&endTime='+endTime;
        }
        $scope.$emit("BUSY");
        $http.get(encodeURI(url)).success(function (pagedata) {
            $scope.$emit("NOTBUSY");
            if (pagedata.status == 'SUCCESS') {
                $scope.codes = pagedata.data.content;
                $scope.totalServerItems = pagedata.data.totalElements;
                if ($scope.totalServerItems == '0') {
                    alert('无数据');
                }
            } else {
                alert(pagedata.error);
            }
        });
    };

    $scope.search = function(){
        $scope.beginTime = $("#beginTime").val();
        $scope.endTime = $("#endTime").val();
        if( $scope.pagingOptions.currentPage == 1){
            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, '');
        }else{
            $scope.pagingOptions.currentPage = 1;
        }
    }

    $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, "");

    $scope.$on("$destroy", function() {
        $scope.$emit("NOTBUSY");
    })

    $scope.$watch('pagingOptions', function (newVal, oldVal) {
        if (newVal !== oldVal || newVal.currentPage !== oldVal.currentPage || newVal.pageSize !== oldVal.pageSize) {
            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
        }
    }, true);

    $scope.$watch('filterOptions', function (newVal, oldVal) {
        if (newVal !== oldVal) {
            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
        }
    }, true);

}])
;
