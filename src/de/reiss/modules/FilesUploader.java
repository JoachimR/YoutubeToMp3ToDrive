package de.reiss.modules;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import de.reiss.modules.drive.DriveApi;

public class FilesUploader {

    public static File uploadNow(Drive service, java.io.File someFile) {
        if (someFile == null || !someFile.exists()) {
            return null;
        }
        try {
            final String filename = someFile.getName();

            File file = DriveApi.insertFile(service, filename, filename);
            String fileId = file.getId();

            if (fileId != null) {
                DriveApi.insertPermission(service, fileId, "anyone", "reader");
            }
            return file;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
