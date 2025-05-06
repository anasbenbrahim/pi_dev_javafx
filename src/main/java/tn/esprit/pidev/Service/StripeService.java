package tn.esprit.pidev.Service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

public class StripeService {
    private static final String STRIPE_API_KEY = "sk_test_51RJUkKCcSjrZVorcxm1bYKSqu7UNYNC3GHfdMQoj5KthApGQ53clIcco3skzVYJh3UPlNJj6XSGLQ5NkTvemxmS100D2Z7kH7m";

    public StripeService() {
        Stripe.apiKey = STRIPE_API_KEY;
    }

    /**
     * Creates a Stripe Checkout Session and returns the session URL.
     * @param amountCents The amount in cents (e.g., 1000 = 10.00 USD)
     * @param successUrl  Redirect URL after successful payment
     * @param cancelUrl   Redirect URL if payment is cancelled
     * @return Checkout session URL
     * @throws Exception on Stripe error
     */
    public String createCheckoutSession(long amountCents, String successUrl, String cancelUrl) throws Exception {
        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(successUrl)
            .setCancelUrl(cancelUrl)
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("TND")
                            .setUnitAmount(amountCents)
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName("Commande Payment")
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}
