package de.reiss.modules.drive;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import de.reiss.Config;
import de.reiss.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;


public class DriveService {

    private static Drive service;

    public static Drive getDriveService() {
        if (service == null) {
            service = getDriveServiceByCall();
        }
        return service;
    }

    private static Drive getDriveServiceByCall() {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        GoogleCredential credential1 = new GoogleCredential.Builder().setJsonFactory(jsonFactory)
                .setTransport(httpTransport).setClientSecrets(Config.DRIVE_CLIENT_ID, Config.DRIVE_CLIENT_SECRET).build();
        credential1.setAccessToken(Config.DRIVE_ACCESS_TOKEN);
        credential1.setRefreshToken(Config.DRIVE_REFRESH_TOKEN);
        Drive result = new Drive.Builder(httpTransport, jsonFactory, credential1).build();
        return result;
    }

    /**
     * Use this method if Drive app tokens are outdated
     */
    private static void refreshTokens() throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, Config.DRIVE_CLIENT_ID, Config.DRIVE_CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("offline")
                .setApprovalPrompt("auto").build();
        String url = flow.newAuthorizationUrl().setRedirectUri(Config.DRIVE_REDIRECT_URI).build();
        Logger.log("Please open the following URL in your browser then type the authorization code:");
        Logger.log("  " + url);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String code = br.readLine();
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(Config.DRIVE_REDIRECT_URI).execute();
        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(Config.DRIVE_CLIENT_ID, Config.DRIVE_CLIENT_SECRET)
                .build()
                .setFromTokenResponse(response);
        String accessToken = credential.getAccessToken();
        String refreshToken = credential.getRefreshToken();

        Logger.log("accessToken");
        Logger.log(accessToken);
        Logger.log("refreshToken");
        Logger.log(refreshToken);

        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).build();
    }
}