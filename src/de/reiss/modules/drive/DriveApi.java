package de.reiss.modules.drive;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DriveApi {

    /**
     * Print a file's metadata.
     *
     * @param service Drive API service instance.
     * @param fileId  ID of the file to print metadata for.
     */
    public static void printFile(Drive service, String fileId) {

        try {
            File file = service.files().get(fileId).execute();
            System.out.println(file);
//            System.out.println("Title: " + file.getTitle());
//            System.out.println("Description: " + file.getDescription());
//            System.out.println("MIME type: " + file.getMimeType());
        } catch (IOException e) {
            System.out.println("An error occured: " + e);
        }
    }

    /**
     * Download a file's content.
     *
     * @param service Drive API service instance.
     * @param file    Drive File instance.
     * @return InputStream containing the file's content if successful,
     * {@code null} otherwise.
     */
    public static InputStream downloadFile(Drive service, File file) {
        if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
            try {
                HttpResponse resp =
                        service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()))
                                .execute();
                return resp.getContent();
            } catch (IOException e) {
                // An error occurred.
                e.printStackTrace();
                return null;
            }
        } else {
            // The file doesn't have any content stored on Drive.
            return null;
        }
    }

    /**
     * Update a permission's role.
     *
     * @param service      Drive API service instance.
     * @param fileId       ID of the file to update permission for.
     * @param permissionId ID of the permission to update.
     * @param newRole      The value "owner", "writer" or "reader".
     * @return The updated permission if successful, {@code null} otherwise.
     */
    public static Permission updatePermission(Drive service, String fileId,
                                              String permissionId, String newRole) {
        try {
            // First retrieve the permission from the API.
            Permission permission = service.permissions().get(
                    fileId, permissionId).execute();
            permission.setRole(newRole);
            return service.permissions().update(
                    fileId, permissionId, permission).execute();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
        return null;
    }


    /**
     * https://developers.google.com/drive/v2/reference/files/insert
     */
    public static File insertFile(Drive service, String title, java.io.File fileToInsert) {
        if(fileToInsert==null || !fileToInsert.exists() || fileToInsert.isDirectory()) {
            return null;
        }

        // File's metadata.
        File body = new File();
        body.setTitle(title);

//        // Set the parent folder.
//        if (parentId != null && parentId.length() > 0) {
//            body.setParents(
//                    Arrays.asList(new ParentReference().setId(parentId)));
//        }

        // File's content.

        FileContent mediaContent = new FileContent("", fileToInsert);
        try {
            File file = service.files().insert(body, mediaContent).execute();


            System.out.println("File ID: " + file.getId());

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occured: " + e);
        }
        return null;
    }

    /**
     * Print information about the specified permission.
     *
     * @param service      Drive API service instance.
     * @param fileId       ID of the file to print permission for.
     * @param permissionId ID of the permission to print.
     */
    public static void printPermission(Drive service, String fileId,
                                       String permissionId) {
        try {
            Permission permission = service.permissions().get(
                    fileId, permissionId).execute();

            System.out.println("Name: " + permission.getName());
            System.out.println("Role: " + permission.getRole());
//            for (String additionalRole : permission.getAdditionalRoles()) {
//                System.out.println("Additional role: " + additionalRole);
//            }
        } catch (IOException e) {
            System.out.println("An error occured: " + e);
        }
    }

    /**
     * Retrieve a list of permissions.
     *
     * @param service Drive API service instance.
     * @param fileId  ID of the file to retrieve permissions for.
     * @return List of permissions.
     */
    public static List<Permission> retrievePermissions(Drive service,
                                                       String fileId) {
        try {
            PermissionList permissions = service.permissions().list(fileId).execute();
            return permissions.getItems();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }

        return null;
    }

    public static Permission insertPermission(Drive service, String fileId,
                                              String type, String role) {
        Permission newPermission = new Permission();

        newPermission.setType(type);
        newPermission.setRole(role);
        try {
            return service.permissions().insert(fileId, newPermission).execute();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
        return null;
    }


    /**
     * Retrieve a list of File resources.
     *
     * @param service Drive API service instance.
     * @return List of File resources.
     */
    public static List<File> retrieveAllUploadedFiles(Drive service) throws IOException {
        List<File> result = new ArrayList<File>();
        Drive.Files.List request = service.files().list();

        do {
            try {
                FileList files = request.execute();

                result.addAll(files.getItems());
                request.setPageToken(files.getNextPageToken());
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0);

        return result;
    }


    /**
     * https://developers.google.com/drive/v2/reference/files/update
     */
    public static File updateFile(Drive service, String fileId, String filename) {
        try {
            // First retrieve the file from the API.
            File file = service.files().get(fileId).execute();
            String mimeType = file.getMimeType();

            // File's new content.
            java.io.File fileContent = new java.io.File(filename);
            if (fileContent == null || !fileContent.exists()) {
                System.out.println("Did not find file '" + filename + "'");
                return null;
            }
            FileContent mediaContent = new FileContent(mimeType, fileContent);

            // Send the request to the API.
            File updatedFile = service.files().update(fileId, file, mediaContent).execute();

            return updatedFile;
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
            return null;
        }
    }
}
