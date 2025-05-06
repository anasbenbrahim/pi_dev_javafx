module tn.esprit.pidev {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires java.sql;
    //requires mysql.connector.java;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires jbcrypt;
    requires java.mail;
    requires stripe.java;
    requires smile.core;
    requires smile.data;

    opens tn.esprit.pidev.icons to javafx.fxml;

    opens tn.esprit.pidev to javafx.fxml;
    exports tn.esprit.pidev;
    exports tn.esprit.pidev.Controller;
    opens tn.esprit.pidev.Controller to javafx.fxml;
    requires javafx.base;
    opens tn.esprit.pidev.Model to javafx.base, javafx.fxml;
    exports tn.esprit.pidev.Model;
}
