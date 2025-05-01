package view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class StarRatingControl extends HBox {
    private final IntegerProperty rating = new SimpleIntegerProperty(0);
    private final Image starFilled = new Image(getClass().getResourceAsStream("/images/star_filled.png"));
    private final Image starEmpty = new Image(getClass().getResourceAsStream("/images/star_empty.png"));
    private final ImageView[] stars = new ImageView[5];

    public StarRatingControl() {
        setSpacing(5);
        getStyleClass().add("star-rating");

        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(starEmpty);
            star.setFitWidth(24);
            star.setFitHeight(24);
            final int starIndex = i + 1;
            star.setUserData(starIndex);

            star.setOnMouseEntered(e -> highlightStars(starIndex));
            star.setOnMouseExited(e -> highlightStars(rating.get()));

            // Click to set rating
            star.setOnMouseClicked(e -> setRating(starIndex));

            stars[i] = star;
            getChildren().add(star);
        }

        // Update UI when rating changes
        rating.addListener((obs, old, newValue) -> highlightStars(newValue.intValue()));
    }

    private void highlightStars(int count) {
        for (int i = 0; i < 5; i++) {
            stars[i].setImage(i < count ? starFilled : starEmpty);
        }
    }

    public IntegerProperty ratingProperty() {
        return rating;
    }

    public int getRating() {
        return rating.get();
    }

    public void setRating(int value) {
        rating.set(value);
    }

    public void setReadOnly(boolean readOnly) {
        for (ImageView star : stars) {
            star.setDisable(readOnly);
            star.setOpacity(readOnly ? 0.7 : 1.0);
        }
    }
}