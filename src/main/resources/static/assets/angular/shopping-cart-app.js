const app = angular.module("shopping-cart-app", []);

app.controller("shopping-cart-ctrl", function($scope, $http) {
    console.log("AngularJS đã kết nối thành công!");
    $scope.testMessage = "Kết nối thành công!";
});