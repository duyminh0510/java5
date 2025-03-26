var app = angular.module("shoppingCartApp", []);

app.controller("shoppingCartCtrl", function($scope, $http) {
    $scope.cart = {
            items: [],
            // Thêm sản phẩm vào giỏ hàng
            add(id) {
                var item = this.items.find(item => item.id == id);
                if (item) {
                    item.quantity++;
                    this.saveTolocalStorage();
                } else {
                    $http.get(`/rest/products/${id}`).then(resp => {
                        resp.data.quantity = 1;
                        this.items.push(resp.data);
                        this.saveTolocalStorage();
                    })
                }
            },
            //xóa sản phẩm khỏi giỏ hàng
            remove(id) {
                var index = this.items.findIndex(item => item.id == id);
                this.items.splice(index, 1);
                this.saveToLocalStorage();
            },
            //xóa sạch các mặt hàng trong giỏ
            clear() {
                this.items = [];
                this.saveToLocalStorage();
            },
            //Tính tổng tiền của 1 sản phẩm
            amt_of(item) {},

            //Tính tổng số lượng các mặt hàng trong giỏ
            get count() {
                return this.items
                    .map(item => item.quantity)
                    .reduce((totalAmount, quantity) => totalAmount += quantity, 0);
            },

            //Tổng thành tiền các mặt hàng mặt hàng trong giỏ
            get amount() {
                return this.items
                    .map(item => item.quantity * item.price)
                    .reduce((totalAmount, quantity) => totalAmount += quantity, 0);
            },



            //lưu giỏ hàng vào local storage
            saveToLocalStorage() {
                var json = JSON.stringify(angular.copy(this.items));
                localStorage.setItem("cart", json);
            },
            //đọc giỏ hàng từ local storage
            loadFromLocalStorage() {
                var json = localStorage.getItem("cart");
                this.items = json ? JSON.parse(json) : [];
            }
        }
        // Load giỏ hàng khi trang được tải
    $scope.getCart();
});