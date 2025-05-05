package org.example.services;

import org.example.utils.Database;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class Ollama {
    private String model;
    private String text;
    private Connection connection = Database.getInstance().getConnection();
    private static final List<String> AGRICULTURE_KEYWORDS = Arrays.asList(
            "agriculture", "farming", "crop", "soil", "harvest", "irrigation",
            "fertilizer", "pesticide", "livestock", "cattle", "poultry",
            "horticulture", "agronomy", "cultivation", "agribusiness", "tractor",
            "farm", "farmer", "organic", "sustainable", "crop rotation", "yield"
    );

    public Ollama(String model, String text) throws IOException {
        this.model = model;
        this.text = text;
    }
    public void ajouter(String text) throws IOException {
        String req="insert into chat_ai (question) values (?)";
        try{
            PreparedStatement ps= connection.prepareStatement(req);
            ps.setString(1, text);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String afficher(){
        String req="select last(question) from chat_ai ";
        try{
            Statement ps= connection.createStatement();
            ResultSet res=ps.executeQuery(req);
            String resultat=res.getString("question");
            return resultat;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean isAgricultureRelated(String text) {
        String lowerText = text.toLowerCase();
        return AGRICULTURE_KEYWORDS.stream()
                .anyMatch(keyword -> lowerText.contains(keyword.toLowerCase()));
    }

    public String generate()throws IOException{
        if (!isAgricultureRelated(text)) {
            return "I'm sorry, I only answer questions related to agriculture. " +
                    "Please ask me about farming, crops, soil, or other agriculture topics.";
        }
        URL url =new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        String jsonoutput=String.format("{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\": false}", model, text);

        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonoutput.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int responseCode = conn.getResponseCode();
        System.out.println("la reponse est " + responseCode);

        BufferedReader in= new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject obj = new JSONObject(response.toString());
        String result = obj.getString("response");
        System.out.println(result);

        conn.disconnect();
        return result;

    }

}
