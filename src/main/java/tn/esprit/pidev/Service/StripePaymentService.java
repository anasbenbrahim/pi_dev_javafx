package tn.esprit.pidev.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import tn.esprit.pidev.Model.Order;

import java.util.HashMap;
import java.util.Map;

public class StripePaymentService {
    // Replace with your actual Stripe API key
    private static final String API_KEY = "sk_test_51RJUkKCcSjrZVorcxm1bYKSqu7UNYNC3GHfdMQoj5KthApGQ53clIcco3skzVYJh3UPlNJj6XSGLQ5NkTvemxmS100D2Z7kH7m";
    private OrderDAO orderDAO;

    public StripePaymentService() {
        Stripe.apiKey = API_KEY;
        this.orderDAO = new OrderDAO();
    }

    /**
     * Creates a payment intent for an order
     * @param order The order to create a payment intent for
     * @return The client secret for the payment intent
     */
    public String createPaymentIntent(Order order) throws StripeException {
        // Convert to cents (Stripe uses smallest currency unit)
        long amountInCents = Math.round(order.getTotal() * 100);
        
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setCurrency("eur")
            .setAmount(amountInCents)
            .setDescription("Order #" + order.getId())
            .putMetadata("orderId", String.valueOf(order.getId()))
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                    .setEnabled(true)
                    .build()
            )
            .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent.getClientSecret();
    }

    /**
     * Confirms a payment was successful and updates the order status
     * @param paymentIntentId The ID of the payment intent
     * @return true if the payment was successful, false otherwise
     */
    public boolean confirmPayment(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            
            if ("succeeded".equals(paymentIntent.getStatus())) {
                // Extract orderId from metadata
                String orderId = paymentIntent.getMetadata().get("orderId");
                if (orderId != null) {
                    // Update order status to PAYEE
                    return orderDAO.updateOrderStatus(Integer.parseInt(orderId), "PAYEE");
                }
            }
            return false;
        } catch (StripeException e) {
            e.printStackTrace();
            return false;
        }
    }
}