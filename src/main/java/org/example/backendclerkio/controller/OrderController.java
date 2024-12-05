package org.example.backendclerkio.controller;

import org.example.backendclerkio.dto.ApiResponse;
import org.example.backendclerkio.dto.CartItemRequestDTO;
import org.example.backendclerkio.dto.CartItemResponseDTO;
import org.example.backendclerkio.dto.UserResponseDTO;
import org.example.backendclerkio.entity.Order;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.entity.User;
import org.example.backendclerkio.service.EmailService;
import org.example.backendclerkio.service.OrderService;
import org.example.backendclerkio.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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

    private UserResponseDTO getCurrentUserDTO(Principal principal) throws Exception {
        User user = userService.findByUsername(principal.getName());
        return new UserResponseDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserEmail(),
                user.isAdmin()
        );
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



}
