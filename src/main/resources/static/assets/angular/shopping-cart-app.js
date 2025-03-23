const app = angular.module("shopping-cart-app", []);

app.controller("shopping-cart-ctrl", function($scope, $http) {
    console.log("AngularJS đã kết nối thành công!");

    // Khởi tạo giỏ hàng
    $scope.cartItems = [];
    $scope.totalAmount = 0;

    // Load giỏ hàng từ server
    $scope.loadCart = function() {
        $http.get("/cart/get").then(function(response) {
            $scope.cartItems = response.data;
            $scope.updateTotal();
        }, function(error) {
            console.error("Lỗi khi tải giỏ hàng:", error);
        });
    };

    // Cập nhật tổng tiền
    $scope.updateTotal = function() {
        let selectedItems = $scope.cartItems.filter(item => item.selected);
        if (selectedItems.length === 0) {
            $scope.totalAmount = 0;
            return;
        }

        let selectedIds = selectedItems.map(item => `selectedItems=${item.item_id}`).join("&");

        $http.post('/cart/update-total', selectedIds, {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).then(function(response) {
            $scope.totalAmount = response.data;
        }, function(error) {
            console.error("Lỗi khi cập nhật tổng tiền:", error);
        });
    };

    // Cập nhật số lượng sản phẩm
    $scope.updateQuantity = function(item, change) {
        let newQuantity = item.quantity + change;
        if (newQuantity < 1) {
            if (!confirm("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?")) return;
            newQuantity = 0;
        }

        $http.post('/cart/update-quantity', `itemId=${item.item_id}&quantity=${newQuantity}`, {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).then(function(response) {
            if (response.data === "deleted") {
                $scope.cartItems = $scope.cartItems.filter(cartItem => cartItem.item_id !== item.item_id);
            } else {
                item.quantity = newQuantity;
            }
            $scope.updateTotal();
        }, function(error) {
            console.error("Lỗi khi cập nhật số lượng:", error);
        });
    };

    // Xóa sản phẩm khỏi giỏ hàng
    $scope.removeItem = function(item) {
        if (!confirm("Bạn có chắc muốn xóa sản phẩm này?")) return;

        $http.post('/cart/remove', `itemId=${item.item_id}`, {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).then(function() {
            $scope.cartItems = $scope.cartItems.filter(cartItem => cartItem.item_id !== item.item_id);
            $scope.updateTotal();
        }, function(error) {
            console.error("Lỗi khi xóa sản phẩm:", error);
        });
    };

    // Khởi tạo giỏ hàng
    $scope.loadCart();
});