package assignment_java5.java5.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import assignment_java5.java5.dao.CartDAO;
import assignment_java5.java5.dao.CartItemDAO;
import assignment_java5.java5.dao.ProductDAO;
// import assignment_java5.java5.dao.UserDAO;
import assignment_java5.java5.entitys.Cart;
import assignment_java5.java5.entitys.CartItem;
import assignment_java5.java5.entitys.Product;
import assignment_java5.java5.entitys.User;

@Service
public class CartService {

    @Autowired
    private CartDAO cartdao;

    @Autowired
    private CartItemDAO cartItemdao;

    @Autowired
    private ProductDAO productdao;

    // @Autowired
    // private UserDAO userdao;

    // public void addToCart(User user, Integer productId, Long quantity) {
    // // Tìm giỏ hàng của user, nếu chưa có thì tạo mới
    // Cart cart = cartdao.findByUser(user)
    // .orElseGet(() -> {
    // Cart newCart = new Cart();
    // newCart.setUser(user);
    // newCart.setCreatedAt(new Date());
    // return cartdao.save(newCart);
    // });

    // // Tìm sản phẩm
    // Optional<Product> productOpt =
    // productdao.findById(Math.toIntExact(productId));
    // if (productOpt.isPresent()) {
    // Product product = productOpt.get();

    // // Kiểm tra sản phẩm đã có trong giỏ chưa
    // Optional<CartItem> cartItemOpt = cartItemdao.findByCartAndProduct(cart,
    // product);

    // if (cartItemOpt.isPresent()) {
    // // Nếu đã có, tăng số lượng
    // CartItem cartItem = cartItemOpt.get();
    // cartItem.setQuantity(cartItem.getQuantity() + quantity);
    // cartItem.setTotalamount(cartItem.getQuantity() *
    // product.getPrice().longValue());
    // cartItemdao.save(cartItem);
    // } else {
    // // Nếu chưa có, tạo mới
    // CartItem newItem = new CartItem();
    // newItem.setCart(cart);
    // newItem.setProduct(product);
    // newItem.setQuantity(quantity);
    // newItem.setPrice(product.getPrice());
    // newItem.setTotalamount(quantity * product.getPrice().longValue());
    // newItem.setCreatedAt(new Date());
    // cartItemdao.save(newItem);
    // }
    // }
    // }

    public void addToCart(User user, Integer productId, String size, Long quantity) {
        Optional<Product> productOpt = productdao.findById(productId);
        if (!productOpt.isPresent()) {
            throw new RuntimeException("Sản phẩm không tồn tại!");
        }

        Product product = productOpt.get();

        // Tìm giỏ hàng của người dùng
        Cart cart = cartdao.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartdao.save(newCart);
        });

        // Kiểm tra xem sản phẩm với size này đã có trong giỏ hàng chưa
        Optional<CartItem> existingCartItemOpt = cartItemdao.findByCartAndProductAndSize(cart, product, size);

        CartItem cartItem;
        if (existingCartItemOpt.isPresent()) {
            cartItem = existingCartItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setSize(size); // Lưu size vào giỏ hàng
            cartItem.setPrice(product.getPrice());
            cartItem.setTotalamount((long) (product.getPrice() * quantity));
        }

        cartItemdao.save(cartItem);
    }

    // public List<CartItem> getCartItemsByUser(String username) {
    // User user = userdao.findByUsername(username);
    // if (user == null) {
    // return new ArrayList<>();
    // }
    // return cartItemdao.findByUser(user);
    // }

    public List<CartItem> getCartItemsByUser(User user) {
        return cartItemdao.findByCartUser(user);
    }

    public List<CartItem> getCartItemsByIds(List<Long> itemIds) {
        return cartItemdao.findAllById(itemIds);
    }

    // @Transactional
    // public void clearCart(User user) {
    // Optional<Cart> cartOpt = cartdao.findByUser(user);
    // if (cartOpt.isPresent()) {
    // Cart cart = cartOpt.get();
    // cartItemdao.deleteByCart(cart);
    // }
    // }

    public void removeCartItemsByIds(List<Long> ids) {
        cartItemdao.deleteAllById(ids);
    }

    public List<CartItem> findByCart(Cart cart) {
        return cartItemdao.findByCart(cart);
    }
}
