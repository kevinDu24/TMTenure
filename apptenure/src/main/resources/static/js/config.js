// config

var app =  
angular.module('app')
  .run(
    [          '$http', '$window', '$location', '$rootScope',
      function ($http,   $window,   $location, $rootScope) {
          $rootScope.wxloadSuccess = false;
//          $http.get('/weChats/urlSignature?url=' + $location.$$absUrl).success(function(result){
//              if(result.status == 'SUCCESS'){
//                  var config = {};
//                  config.debug = false;
//                  config.appId = result.data.appid;
//                  config.timestamp = result.data.timestamp;
//                  config.nonceStr = result.data.noncestr;
//                  config.signature = result.data.signature;
//                  config.jsApiList = ['getLocation', 'chooseImage', 'uploadImage', 'previewImage'];
//                  $window.wx.config(config);
//                  $rootScope.wxloadSuccess = true;
//              }
//          });
      }
    ]
  )
  .config(
    [        '$controllerProvider', '$compileProvider', '$filterProvider', '$provide',
    function ($controllerProvider,   $compileProvider,   $filterProvider,   $provide) {
        
        // lazy controller, directive and service
        app.controller = $controllerProvider.register;
        app.directive  = $compileProvider.directive;
        app.filter     = $filterProvider.register;
        app.factory    = $provide.factory;
        app.service    = $provide.service;
        app.constant   = $provide.constant;
        app.value      = $provide.value;
    }
  ])
  .config(['$translateProvider', function($translateProvider){
    // Register a loader for the static files
    // So, the module will search missing translation tables under the specified urls.
    // Those urls are [prefix][langKey][suffix].
    $translateProvider.useStaticFilesLoader({
      prefix: 'l10n/',
      suffix: '.js'
    });
    // Tell the module what language to use by default
    $translateProvider.preferredLanguage('en');
    // Tell the module to store the language in the local storage
    $translateProvider.useLocalStorage();
  }])
    .config(['$httpProvider', function($httpProvider){

        var $cookies;
          angular.injector(['ngCookies']).invoke(['$cookies', function(_$cookies_) {
            $cookies = _$cookies_;
          }]);
        if($cookies.get("Authorization") == null){
            return;
        }
        $httpProvider.defaults.headers.common = {
        'Authorization': $cookies.get("Authorization"),
        'Accept': 'application/json;odata=verbose'
      };
    }]);