package org.example.services;

import okhttp3.*;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public class Flux_service {
    // API Configuration
    private static final String HF_API_URL = "https://api-inference.huggingface.co/models/black-forest-labs/FLUX.1-dev";
    private static final String HF_API_TOKEN = "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";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    private final OkHttpClient client;

    public enum PhotoType {
        PORTRAIT, LANDSCAPE, PRODUCT, WILDLIFE, STREET, ARCHITECTURE
    }

    public Flux_service() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Main method for generating realistic photos
     */
    public byte[] generateRealisticPhoto(String description, int size, PhotoType photoType) throws IOException {
        if (!validateParameters(description, size)) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        String enhancedPrompt = buildRealisticPrompt(description, size, photoType);
        Request request = buildRequestWithNegativePrompt(enhancedPrompt);

        return executeRequestWithRetry(request);
    }

    /**
     * Legacy method for general image generation
     */
    public byte[] generateImage(String description, int size, String style) throws IOException {
        String enhancedPrompt = String.format("%s, %s style, %dx%d, 4K",
                description, style, size, size);
        Request request = buildRequest(enhancedPrompt);

        return executeRequestWithRetry(request);
    }

    private byte[] executeRequestWithRetry(Request request) throws IOException {
        IOException lastError = null;

        for (int i = 0; i < MAX_RETRIES; i++) {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body().bytes();
                }

                if (response.code() == 503) { // Model loading
                    Thread.sleep(RETRY_DELAY_MS);
                    continue;
                }

                throw new IOException("HTTP Error " + response.code() + ": " + response.message());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Request interrupted", e);
            } catch (IOException e) {
                lastError = e;
            }
        }

        throw lastError != null ? lastError : new IOException("Max retries exceeded");
    }

    private String buildRealisticPrompt(String description, int size, PhotoType photoType) {
        return String.format(
                "Professional photograph of %s. %s%s %dx%d, 8K UHD, RAW photo, ISO 100%s",
                description,
                getPhotoTypeSpecifics(photoType),
                getCameraSettings(photoType),
                size,
                size,
                getLightingConditions(photoType)
        );
    }

    private String getPhotoTypeSpecifics(PhotoType type) {
        switch (type) {
            case PORTRAIT:
                return "85mm portrait, natural skin texture with pores, subtle facial imperfections, ";
            case LANDSCAPE:
                return "16-35mm wide angle, Ansel Adams style tonal range, ";
            case PRODUCT:
                return "100mm macro, commercial product photography, studio lighting, ";
            case WILDLIFE:
                return "400mm telephoto, National Geographic style, natural habitat, ";
            case STREET:
                return "35mm prime lens, documentary style, candid moment, ";
            case ARCHITECTURE:
                return "24mm tilt-shift lens, architectural photography, golden hour, ";
            default:
                return "";
        }
    }

    private String getCameraSettings(PhotoType type) {
        return String.format("Shot on Sony A7 IV, %s, ",
                type == PhotoType.PORTRAIT ? "f/1.8" :
                        type == PhotoType.PRODUCT ? "f/8" : "f/4");
    }

    private String getLightingConditions(PhotoType type) {
        switch (type) {
            case PORTRAIT: return ", soft diffused lighting, Rembrandt lighting pattern";
            case PRODUCT: return ", three-point studio lighting";
            case LANDSCAPE: return ", golden hour lighting";
            default: return ", natural lighting";
        }
    }

    private Request buildRequestWithNegativePrompt(String prompt) {
        String json = String.format(
                "{\"inputs\": \"%s\", \"parameters\": {\"negative_prompt\": \"%s\"}}",
                prompt,
                "illustration, cartoon, 3D render, anime, painting, drawing, digital art, CGI, " +
                        "unreal engine, blurry, deformed, oversaturated, HDR, fisheye"
        );

        return new Request.Builder()
                .url(HF_API_URL)
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + HF_API_TOKEN)
                .addHeader("Accept", "image/png")
                .build();
    }

    private Request buildRequest(String prompt) {
        return new Request.Builder()
                .url(HF_API_URL)
                .post(RequestBody.create(
                        String.format("{\"inputs\": \"%s\"}", prompt),
                        MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + HF_API_TOKEN)
                .addHeader("Accept", "image/png")
                .build();
    }

    public void saveImage(byte[] imageData, Path outputPath) throws IOException {
        if (imageData == null || imageData.length == 0) {
            throw new IllegalArgumentException("Empty image data");
        }

        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, imageData,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    public javafx.scene.image.Image convertToJavaFXImage(byte[] rawData) {
        if (rawData == null || rawData.length == 0) {
            throw new IllegalArgumentException("Empty image data");
        }
        return new javafx.scene.image.Image(new ByteArrayInputStream(rawData));
    }

    public boolean validateParameters(String description, int size) {
        return description != null && !description.trim().isEmpty()
                && (size == 256 || size == 512 || size == 1024);
    }

    // Utility method for prompt validation
    public static String sanitizePrompt(String prompt) {
        if (prompt == null) return "";
        return prompt.trim()
                .replaceAll("[^a-zA-Z0-9,.!?\\-\\s]", "")
                .replaceAll("\\s+", " ");
    }
}