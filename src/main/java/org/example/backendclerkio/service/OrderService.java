package org.example.backendclerkio.service;

import org.example.backendclerkio.dto.CartItemRequestDTO;
import org.example.backendclerkio.dto.CartItemResponseDTO;
import org.example.backendclerkio.dto.UserResponseDTO;
import org.example.backendclerkio.entity.Order;
import org.example.backendclerkio.entity.OrderProduct;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.entity.User;
import org.example.backendclerkio.repository.OrderProductRepository;
import org.example.backendclerkio.repository.OrderRepository;
import org.example.backendclerkio.repository.ProductRepository;
import org.example.backendclerkio.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;


    private final ProductRepository productRepository;


    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, OrderProductRepository orderProductRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }


    private Order getCartForUser(User user) {
        Optional<Order> optionalCart = orderRepository.findByUserAndPaidWithProducts(user, false);

        if (optionalCart.isPresent()) {
            return optionalCart.get();
        } else {
            Order newCart = new Order();
            newCart.setUser(user);
            newCart.setOrderDate(System.currentTimeMillis());
            newCart.setPaid(false);
            return orderRepository.save(newCart);
        }
    }

    public void addToCart(UserResponseDTO userDTO, CartItemRequestDTO cartItemDTO) throws Exception {
        // Fetch the user entity from the DTO
        User user = userRepository.findById(userDTO.userId())
                .orElseThrow(() -> new Exception("User not found"));

        // Fetch the product entity
        Product product = productRepository.findById(cartItemDTO.productId())
                .orElseThrow(() -> new Exception("Product not found"));

        // Get or create the cart (Order entity)
        Order cart = getCartForUser(user);

        // Check if the product is already in the cart
        Optional<OrderProduct> optionalOrderProduct = cart.getOrderProducts().stream()
                .filter(op -> op.getProduct().getProductId() == cartItemDTO.productId())
                .findFirst();

        if (optionalOrderProduct.isPresent()) {
            // Update the quantity
            OrderProduct orderProduct = optionalOrderProduct.get();
            orderProduct.setQuantity(orderProduct.getQuantity() + cartItemDTO.quantity());
        } else {
            // Create a new OrderProduct
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(cart);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(cartItemDTO.quantity());
            orderProduct.setPriceAtTimeOfOrder(product.getPrice());

            // Add to the cart
            cart.getOrderProducts().add(orderProduct);
        }

        // Save the cart (cascades to OrderProduct)
        orderRepository.save(cart);
    }

    public List<CartItemResponseDTO> getAllProductsInCart(UserResponseDTO userDTO) throws Exception {
        User user = userRepository.findById(userDTO.userId())
                .orElseThrow(() -> new Exception("User not found"));

        Order cart = getCartForUser(user);

        // Map OrderProduct entities to CartItemResponseDTOs
        List<CartItemResponseDTO> cartItemsDTO = cart.getOrderProducts().stream()
                .map(orderProduct -> {
                    Product product = orderProduct.getProduct();
                    String imageUrl = product.getImages() != null && !product.getImages().isEmpty()
                            ? product.getImages().get(0)
                            : null;
                    return new CartItemResponseDTO(
                            product.getProductId(),
                            product.getName(),
                            orderProduct.getQuantity(),
                            orderProduct.getPriceAtTimeOfOrder(),
                            imageUrl
                    );
                })
                .toList();

        return cartItemsDTO;
    }

    public void removeItemFromCart(UserResponseDTO userDTO, CartItemResponseDTO cartItemResponseDTO) throws Exception {
        // Fetch the user entity from the DTO
        User user = userRepository.findById(userDTO.userId())
                .orElseThrow(() -> new Exception("User not found"));

        // Get or create the cart (Order entity)
        Order cart = getCartForUser(user);

        // Find the OrderProduct corresponding to the given product ID
        Optional<OrderProduct> optionalOrderProduct = cart.getOrderProducts().stream()
                .filter(op -> op.getProduct().getProductId() == cartItemResponseDTO.productId())
                .findFirst();

        if (optionalOrderProduct.isPresent()) {
            OrderProduct orderProduct = optionalOrderProduct.get();

            // Remove the OrderProduct from the cart's list
            cart.getOrderProducts().remove(orderProduct);

            // Since orphanRemoval = true, the OrderProduct will be deleted from the database
            orderRepository.save(cart);
        } else {
            throw new Exception("Product not found in cart");
        }
    }




}






