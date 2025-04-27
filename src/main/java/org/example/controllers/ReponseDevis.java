package org.example.controllers;

import javafx.event.ActionEvent;
import org.example.models.Devis;

public class ReponseDevis {
    private Devis devis;

    public void handleValider(ActionEvent event) {
    }

    public void handleAnnuler(ActionEvent event) {
    }

    public void initData(Devis devisSelectionne) {
        this.devis = devisSelectionne;
    }
}
