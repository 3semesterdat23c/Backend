package org.example.backendclerkio.controller;

import org.example.backendclerkio.dto.*;
import org.example.backendclerkio.entity.Order;
import org.example.backendclerkio.entity.OrderProduct;
import org.example.backendclerkio.entity.User;
import org.example.backendclerkio.service.EmailService;
import org.example.backendclerkio.service.OrderService;
import org.example.backendclerkio.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@RequestMapping("api/v1/order")
@RestController
public class OrderController {


    private final OrderService orderService;
    private final UserService userService;
    private final EmailService emailService;

    public OrderController(OrderService orderService, UserService userService, EmailService emailService) {
        this.orderService = orderService;
        this.userService = userService;
        this.emailService = emailService;
    }

    private UserResponseDTO getCurrentUserDTO(Principal principal) {
        UserResponseDTO userResponseDTO = userService.getUserResponseDTOFromPrincipal(principal);
        return userResponseDTO;
    }

    @GetMapping("/myOrders")
    public ResponseEntity<?> getAllOrdersForUser(Principal principal) {
        try {
            UserResponseDTO userResponseDTO = getCurrentUserDTO(principal);
            List<Order> allOrders = orderService.findOrdersByUserIdAndPaidTrue(userResponseDTO.userId());
            return ResponseEntity.ok(allOrders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/cart")
    public ResponseEntity<ApiResponse> addToCart(@RequestBody CartItemRequestDTO cartItemRequestDTO, Principal principal) {
        try {
            UserResponseDTO userDTO = getCurrentUserDTO(principal);
            orderService.addToCart(userDTO, cartItemRequestDTO);
            return ResponseEntity.ok(new ApiResponse("Product added to cart successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/cart")
    public ResponseEntity<List<CartItemResponseDTO>> getAllProductsInCart(Principal principal) {
        try {
            UserResponseDTO userDTO = getCurrentUserDTO(principal);
            List<CartItemResponseDTO> cartItems = orderService.getAllProductsInCart(userDTO);
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProductFromCart(Principal principal, @RequestBody CartItemResponseDTO cartItemResponseDTO) {
        try {
            // Delegate the user retrieval to the service
            UserResponseDTO userDTO = getCurrentUserDTO(principal);

            // Call the service to remove the product from the cart
            orderService.removeItemFromCart(userDTO, cartItemResponseDTO);

            return ResponseEntity.ok("Product removed from cart successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/checkout/{orderId}")
    public ResponseEntity<?> checkoutOrder(@PathVariable int orderId) {
        Optional optionalOrder = orderService.findOrderById(orderId);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Order not found."));
        }
        Order order = (Order) optionalOrder.get();
        try {
            if (order == null || order.getUser() == null || order.getOrderProducts().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid order details");
            }

            orderService.checkout(order);

            emailService.sendConfirmationEmail(
                    order.getUser().getUserEmail(),
                    "Order Confirmation for order: " + order.getId(),
                    buildEmailBody(order)
            );
            return ResponseEntity.ok("Order successfully checked out!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while checking out the order: " + e.getMessage());
        }
    }

    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail() {
        try {
            String testEmail = "guso0001@stud.kea.dk"; // Replace with your test email
            emailService.sendConfirmationEmail(
                    testEmail,
                    "Hej flotte gustav",
                    "<h1>This is a test email</h1><p>If you're seeing this, the email service works!</p>"
            );

            return ResponseEntity.ok("Test email sent successfully to " + testEmail);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send test email: " + e.getMessage());
        }
    }

    @PostMapping("/validatePayment")
    public ResponseEntity<?> validatePayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        if (orderService.paymentIsValid(paymentRequestDTO)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Payment failed");
        }
    }





    private String buildEmailBody(Order order) {
        Optional<User> optionalUser = userService.findUserById(order.getUser().getUserId());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("No user found for email");
        }
        User user = optionalUser.get();
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(order.getOrderDate()), // Convert milliseconds to Instant
                ZoneOffset.ofHours(1)                      // Specify GMT+1
        );


        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<h1>Hi ").append(user.getFirstName()).append(" ").append(user.getLastName()).append("</h1>")
                .append("<h3>Your order was confirmed at ").append(orderDateTime).append("</h3>")
                .append("<h4>Items you have ordered are listed below:</h4>")
                .append("<table style='border-collapse: collapse; width: 100%;'>")
                .append("<thead>")
                .append("<tr>")
                .append("<th style='border: 1px solid black; padding: 8px;'>Image</th>")
                .append("<th style='border: 1px solid black; padding: 8px;'>Product</th>")
                .append("<th style='border: 1px solid black; padding: 8px;'>Price</th>")
                .append("<th style='border: 1px solid black; padding: 8px;'>Quantity</th>")
                .append("<th style='border: 1px solid black; padding: 8px;'>Subtotal</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

        double totalPrice = 0.0;

        for (OrderProduct orderProduct : order.getOrderProducts()) {
            double subtotal = orderProduct.getPriceAtTimeOfOrder() * orderProduct.getQuantity();
            totalPrice += subtotal;

            stringBuilder.append("<tr>")
                    .append("<td style='border: 1px solid black; padding: 8px; text-align: center;'>")
                    .append("<img src='").append(orderProduct.getProduct().getImages().get(0))
                    .append("' style='width: auto; height: 50px;'>") // Maintain proportions
                    .append("</td>")
                    .append("<td style='border: 1px solid black; padding: 8px;'>").append(orderProduct.getProduct().getTitle()).append("</td>")
                    .append("<td style='border: 1px solid black; padding: 8px;'>$")
                    .append(String.format("%.2f", orderProduct.getPriceAtTimeOfOrder())).append("</td>")
                    .append("<td style='border: 1px solid black; padding: 8px;'>").append(orderProduct.getQuantity()).append("</td>")
                    .append("<td style='border: 1px solid black; padding: 8px;'>$")
                    .append(String.format("%.2f", subtotal)).append("</td>")
                    .append("</tr>");
        }

        stringBuilder.append("</tbody>")
                .append("</table>")
                .append("<h3 style='text-align: right;'>Total: $")
                .append(String.format("%.2f", totalPrice)).append("</h3>");

        return stringBuilder.toString();
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveOrder(Principal principal) {
        try {
            // Get the current user using Principal
            UserResponseDTO userDTO = getCurrentUserDTO(principal);

            // Find the active order for the user
            Optional<Order> optionalOrder = orderService.findOrderByUserIdAndPaidFalse(userDTO.userId());

            if (optionalOrder.isPresent()) {
                return ResponseEntity.ok(optionalOrder.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No active order forund for the user");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}


