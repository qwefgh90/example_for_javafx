define(['angular', 'angularRoute', 'angularSanitize', 'angularAnimate', 'angularBootstrap'
	, 'ngContextmenu'], function(angular){

	var app = angular.module('handyfinderwebapp', ['ngRoute', 'ngSanitize', 'ngAnimate', 'ui.bootstrap', 'ngContextMenu', 'angular-inview']);

	app.run(function(){
	});

	app.config(function() {
	});

	app.controller('MainApplicationController', ['$location', '$http', '$scope', '$log', '$timeout', 
		function($location, $http, $scope, $log, $timeout) {
		$scope.number1 = 0;
		$scope.number2 = 0;
		
		function op(url){
			var headers = {
					'Accept' : 'application/json',
					'Content-Type' : 'application/json'
			};
			var params = {
				n1 : $scope.number1,
				n2 : $scope.number2
			};
			var config = {
					'params' : params,
					'headers' : headers
			};
			var self = this;
			$http.get(url = url, config).then(function(response) {
				if (response.status == 200) {
					$scope.result = response.data;
				} else {
					$scope.result = "connection failed";
				}
			}, function(response) {
					$scope.result = "connection failed";
			}, function(response) {
					$scope.result = "connection failed";
			});
		}
		
		$scope.sum = function(){
			op('/plus');
		}
		$scope.minus = function(){
			op('/minus');
		}
		$scope.times = function(){
			op('/times');
		}
		$scope.divide = function(){
			op('/divide');
		}
	}]);

	return app;
});