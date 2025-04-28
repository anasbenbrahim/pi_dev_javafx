module tn.esprit.pidev {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;

    requires java.sql;
    //requires mysql.connector.java;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;

    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires jbcrypt;
    requires java.mail;
    requires java.desktop;


    opens tn.esprit.pidev to javafx.fxml;
    exports tn.esprit.pidev;
}