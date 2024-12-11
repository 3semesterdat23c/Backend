package org.example.backendclerkio.service;

import jakarta.transaction.Transactional;
import org.example.backendclerkio.dto.CartItemRequestDTO;
import org.example.backendclerkio.dto.CartItemResponseDTO;
import org.example.backendclerkio.dto.PaymentRequestDTO;
import org.example.backendclerkio.dto.UserResponseDTO;
import org.example.backendclerkio.entity.Order;
import org.example.backendclerkio.entity.OrderProduct;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.entity.User;
import org.example.backendclerkio.repository.OrderRepository;
import org.example.backendclerkio.repository.ProductRepository;
import org.example.backendclerkio.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ProductRepository productRepository;

    @InjectMocks
    OrderService orderService;




    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        User testUser = new User("Anders", "Ludvigsen", "a@a.dk", "password");
        testUser.setUserId(1); // Set user ID

        User testUser2 = new User("Anders", "Ludvigsen", "a@a.dk", "password");
        testUser.setUserId(1); // Set user ID

        Product testProduct1 = new Product();
        testProduct1.setProductId(1);
        testProduct1.setTitle("Test Product 1");
        testProduct1.setPrice(100.0);

        Product testProduct2 = new Product();
        testProduct2.setProductId(2);
        testProduct2.setTitle("Test Product 2");
        testProduct2.setPrice(50.0);

        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setProduct(testProduct1);
        orderProduct1.setQuantity(2);
        orderProduct1.setPriceAtTimeOfOrder(100.0);

        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct2.setProduct(testProduct2);
        orderProduct2.setQuantity(1);
        orderProduct2.setPriceAtTimeOfOrder(50.0);

        Order testOrder = new Order();
        testOrder.setId(1);
        testOrder.setUser(testUser);
        testOrder.setOrderDate(System.currentTimeMillis());
        testOrder.setPaid(false);

        testOrder.setOrderProducts(new ArrayList<>());
        testOrder.getOrderProducts().add(orderProduct1);
        testOrder.getOrderProducts().add(orderProduct2);

        orderProduct1.setOrder(testOrder);
        orderProduct2.setOrder(testOrder);

        Order testOrder2 = new Order();
        testOrder2.setId(2);
        testOrder2.setUser(testUser2);
        testOrder2.setOrderDate(System.currentTimeMillis());
        testOrder2.setPaid(true);

        List<Order> paidOrders = new ArrayList<>();
        paidOrders.add(testOrder2);

        Mockito.when(orderRepository.findOrderById(1)).thenReturn(Optional.of(testOrder));

        Mockito.when(orderRepository.findOrderByUserIdAndPaidFalse(1)).thenReturn(Optional.of(testOrder));
        Mockito.when(orderRepository.findOrdersByUserIdAndPaidTrue(2)).thenReturn(Optional.of(paidOrders));

    }


    @Test
    void addToCart() throws Exception {
        UserResponseDTO userDTO = new UserResponseDTO(1, "Anders", "Ludvigsen","A@A.dk", true);
        CartItemRequestDTO cartItemDTO = new CartItemRequestDTO(1, 2); // Requesting 2 items

        User mockUser = new User("Anders", "Ludvigsen", "a@a.dk", "password");
        Mockito.when(userRepository.findById(userDTO.userId())).thenReturn(Optional.of(mockUser));

        Product mockProduct = new Product();
        mockProduct.setProductId(1);
        mockProduct.setStockCount(10);
        Mockito.when(productRepository.findById(cartItemDTO.productId())).thenReturn(Optional.of(mockProduct));


        Order mockCart = new Order();
        mockCart.setId(1);
        mockCart.setOrderProducts(new ArrayList<>());
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(mockCart);

        orderService.addToCart(userDTO, cartItemDTO);

        assertEquals(1, mockCart.getOrderProducts().size(), "Cart should contain 1 product");
        assertEquals(2, mockCart.getOrderProducts().get(0).getQuantity(), "Product quantity should be 2");
    }
    @Test
    void addToCart_ProductOutOfStock() {
        UserResponseDTO userDTO = new UserResponseDTO(1, "Anders", "Ludvigsen","a@A.dk", true);
        CartItemRequestDTO cartItemDTO = new CartItemRequestDTO(1, 2); // Requesting 2 items

        User mockUser = new User("Anders", "Ludvigsen", "a@a.dk", "password");
        Mockito.when(userRepository.findById(userDTO.userId())).thenReturn(Optional.of(mockUser));

        Product mockProduct = new Product();
        mockProduct.setProductId(1);
        mockProduct.setStockCount(0);
        Mockito.when(productRepository.findById(cartItemDTO.productId())).thenReturn(Optional.of(mockProduct));

        Exception exception = assertThrows(Exception.class, () -> {
            orderService.addToCart(userDTO, cartItemDTO);
        });

        assertEquals("Product is out of stock", exception.getMessage());
    }


    @Test
    void removeItemFromCart() throws Exception {
        UserResponseDTO userDTO = new UserResponseDTO(1, "Anders", "Ludvigsen","a@a.dk",true);
        CartItemResponseDTO cartItemDTO = new CartItemResponseDTO(1, "Test Product", 2, 90.0, 100.0, "imageUrl");


        User mockUser = new User("Anders", "Ludvigsen", "a@a.dk", "password");
        Mockito.when(userRepository.findById(userDTO.userId())).thenReturn(Optional.of(mockUser));


        Product mockProduct = new Product();
        mockProduct.setProductId(1);
        mockProduct.setTitle("Test Product");
        mockProduct.setPrice(100.0);
        mockProduct.setStockCount(10);


        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(mockProduct);
        orderProduct.setQuantity(2);


        Order mockCart = new Order();
        mockCart.setId(1);
        mockCart.setUser(mockUser);
        mockCart.setOrderProducts(new ArrayList<>());
        mockCart.getOrderProducts().add(orderProduct);

        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(mockCart);

        orderService.removeItemFromCart(userDTO, cartItemDTO);

        assertTrue(mockCart.getOrderProducts().isEmpty(), "The cart should be empty after removing the product");

        Mockito.verify(orderRepository, Mockito.times(1)).save(mockCart);
    }


    @Test
    void checkout() {
        Mockito.when(orderRepository.save(Mockito.any(Order.class)))
                .thenReturn(new Order() {{
                    setPaid(true);
                }});
        Product product1 = new Product();
        product1.setProductId(1);
        product1.setTitle("Test Product 1");
        product1.setPrice(100.0);
        product1.setStockCount(10);

        Product product2 = new Product();
        product2.setProductId(2);
        product2.setTitle("Test Product 2");
        product2.setPrice(50.0);
        product2.setStockCount(5);
        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setProduct(product1);
        orderProduct1.setQuantity(2);

        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct2.setProduct(product2);
        orderProduct2.setQuantity(1);

        Order order = new Order();
        order.setId(1);
        order.setUser(new User("Anders", "Ludvigsen", "a@a.dk", "password"));
        order.setPaid(false);
        order.setOrderDate(System.currentTimeMillis());
        order.setOrderProducts(List.of(orderProduct1, orderProduct2));

        orderProduct1.setOrder(order);
        orderProduct2.setOrder(order);


        orderService.checkout(order);

        assertTrue(order.isPaid(), "Order should be marked as paid");

        assertNotNull(order.getOrderDate(), "Order date should be set");

        assertEquals(8, product1.getStockCount(), "Stock count for product 1 should be reduced by the ordered quantity");
        assertEquals(4, product2.getStockCount(), "Stock count for product 2 should be reduced by the ordered quantity");

    }

    @Test
    void findOrderById() {
        int orderId = 1;

        Order order = orderService.findOrderById(orderId).orElse(null);
        assertNotNull(order, "Order should not be null");
        assertEquals(orderId, order.getId(), "Order ID should match");
        assertEquals("Anders", order.getUser().getFirstName(), "User's first name should match");
        assertFalse(order.isPaid(), "Order should not be paid initially");
        assertEquals(2, order.getOrderProducts().size(), "Order should have 2 products");
        assertEquals(100.0, order.getOrderProducts().get(0).getPriceAtTimeOfOrder(), "First product price should match");
        assertEquals(50.0, order.getOrderProducts().get(1).getPriceAtTimeOfOrder(), "Second product price should match");
    }

    @Test
    void paymentIsValidTrue() {
        PaymentRequestDTO validPaymentRequest = new PaymentRequestDTO(1111111111111111L, "Anders",1111, 111);

        boolean isValid = orderService.paymentIsValid(validPaymentRequest);

        assertTrue(isValid);
    }

@Test
    void paymentIsValidFalse() {
        PaymentRequestDTO validPaymentRequest = new PaymentRequestDTO(1111111111111111L, "Anders",111, 111);

        boolean isValid = orderService.paymentIsValid(validPaymentRequest);

        assertFalse(isValid);
    }


    @Test
    void findOrderByUserIdAndPaidFalse() {
        Optional<Order> actual = orderService.findOrderByUserIdAndPaidFalse(1);
        assertFalse(actual.get().isPaid());
    }

    @Test
    void findOrdersByUserIdAndPaidTrue() {
        List<Order> paidOrders = orderService.findOrdersByUserIdAndPaidTrue(2);
        assertTrue(paidOrders.size() == 1, "Orders should not be empty");
    }
}