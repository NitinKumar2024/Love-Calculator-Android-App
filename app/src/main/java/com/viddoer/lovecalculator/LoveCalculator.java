package com.viddoer.lovecalculator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoveCalculator {
    public static void main(String[] args) {
        String url = "https://love-calculator.p.rapidapi.com/getPercentage";
        Map<String, String> params = new HashMap<>();
        params.put("sname", "Alice");
        params.put("fname", "Nitin Kumar");

        try {
            URL urlObj = new URL(url + getParamsString(params));
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-RapidAPI-Key", "4b7669c2d9msh74c0eea809f3683p172f9ajsnd7b435b8d803");
            connection.setRequestProperty("X-RapidAPI-Host", "love-calculator.p.rapidapi.com");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Extract result and percentage manually from JSON response
                String jsonString = response.toString();
                String result = extractValue(jsonString, "\"result\":\"(.*?)\"");
                String percentage = extractValue(jsonString, "\"percentage\":\"(.*?)\"");

                // Print result and percentage in each line
                System.out.println("Result: " + result);
                System.out.println("Percentage: " + percentage);

            } else {
                System.out.println("HTTP GET request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private static String getParamsString(Map<String, String> params) {
        StringBuilder result = new StringBuilder("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return result.toString();
    }

    private static String extractValue(String jsonString, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jsonString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}

