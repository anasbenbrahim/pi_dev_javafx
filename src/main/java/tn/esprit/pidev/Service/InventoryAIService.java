package tn.esprit.pidev.Service;

import java.util.List;
import java.util.Arrays;

/**
 * InventoryAIService provides predictive analytics for inventory management using a weighted moving average forecast.
 */
public class InventoryAIService {
    /**
     * Forecasts future demand for a product based on historical sales data using weighted moving average.
     * @param salesHistory List of historical sales (e.g., daily/weekly/monthly units sold)
     * @param periods Number of future periods to forecast
     * @return Forecasted demand for each future period
     */
    public double[] forecastDemand(List<Double> salesHistory, int periods) {
        if (salesHistory == null || salesHistory.size() < 2) {
            throw new IllegalArgumentException("Not enough sales history for forecasting (minimum 2 data points required).");
        }
        // Weighted moving average: more recent sales have higher weights
        int n = salesHistory.size();
        double totalWeight = 0;
        double weightedSum = 0;
        for (int i = 0; i < n; i++) {
            int weight = i + 1; // More recent = higher weight
            weightedSum += salesHistory.get(i) * weight;
            totalWeight += weight;
        }
        double wma = weightedSum / totalWeight;
        double[] forecast = new double[periods];
        Arrays.fill(forecast, wma);
        return forecast;
    }
}
