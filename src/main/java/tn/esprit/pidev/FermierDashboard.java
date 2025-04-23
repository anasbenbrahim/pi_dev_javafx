package tn.esprit.pidev;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class FermierDashboard {

    @FXML
    private TableView<ArticleData> articlesTable;

    @FXML
    private TableColumn<ArticleData, String> articlesCol;

    @FXML
    private TableColumn<ArticleData, String> articlesCountCol;

    @FXML
    private TableColumn<ArticleData, String> likesCol;

    @FXML
    private TableColumn<ArticleData, String> commentsCol;

    @FXML
    private TableColumn<ArticleData, String> readersCol;

    @FXML
    private ListView<String> updatesList;

    @FXML
    private ListView<String> newsList;

    @FXML
    public void initialize() {
        // Initialize table columns
        articlesCol.setCellValueFactory(new PropertyValueFactory<>("articles"));
        articlesCountCol.setCellValueFactory(new PropertyValueFactory<>("count"));
        likesCol.setCellValueFactory(new PropertyValueFactory<>("likes"));
        commentsCol.setCellValueFactory(new PropertyValueFactory<>("comments"));
        readersCol.setCellValueFactory(new PropertyValueFactory<>("readers"));

        // Add sample data to table
        ObservableList<ArticleData> tableData = FXCollections.observableArrayList(
                new ArticleData("Address", "142", "128K", "2K", "245K")
        );
        articlesTable.setItems(tableData);

        // Add updates data
        ObservableList<String> updates = FXCollections.observableArrayList(
                "Scott Commented on Your Post",
                "Images like your Post",
                "Coron Commented on Your Post",
                "Coron Started follow you",
                "Rick Commented on Your Post",
                "Krim Commented on Your Post",
                "James like your post"
        );
        updatesList.setItems(updates);

        // Add news data
        ObservableList<String> news = FXCollections.observableArrayList(
                "New Articles",
                "News All",
                "Peace of Nature",
                "Peace of Nature"
        );
        newsList.setItems(news);
    }

    // Model class for table data
    public static class ArticleData {
        private final String articles;
        private final String count;
        private final String likes;
        private final String comments;
        private final String readers;

        public ArticleData(String articles, String count, String likes, String comments, String readers) {
            this.articles = articles;
            this.count = count;
            this.likes = likes;
            this.comments = comments;
            this.readers = readers;
        }

        public String getArticles() { return articles; }
        public String getCount() { return count; }
        public String getLikes() { return likes; }
        public String getComments() { return comments; }
        public String getReaders() { return readers; }
    }
}