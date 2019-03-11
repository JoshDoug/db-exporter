package com.joshuastringfellow.dbexporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipInputStream;

@RestController("/")
public class ExportController {

    private final
    DbConnectionService dbConnectionService;

    @Autowired
    public ExportController(DbConnectionService dbConnectionService) {
        this.dbConnectionService = dbConnectionService;
    }

    @GetMapping(path = "hello")
    public ResponseEntity<String> getHelloWorld() {
        return ResponseEntity.ok().body("Hello World");
    }

    /**
     * TODO: Improve this, it's probably not how it should actually be done
     * @return ResponseEntity containing an InputStreamResource
     * @throws FileNotFoundException if no file is found
     */
    @GetMapping(path = "v1")
    public ResponseEntity<Resource> getDbExport() throws FileNotFoundException {

        File file = dbConnectionService.getDbExportZipFile();
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @GetMapping(path = "export/zip_download", produces = "application/zip")
    public ResponseEntity<InputStreamResource> getDbExportZipFile() throws FileNotFoundException {
        File file = dbConnectionService.getDbExportZipFile();
        FileInputStream fis = new FileInputStream(file);

        int length = 0;
        try {
            length = fis.available();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Length", String.valueOf(length));
        httpHeaders.add("Content-Disposition", "attachment; filename=" + file.getName());

        return new ResponseEntity<>(new InputStreamResource(fis), httpHeaders, HttpStatus.CREATED);
    }
    
    @GetMapping(path = "export/download")
    public ResponseEntity<InputStreamResource> getDbExportFile() throws FileNotFoundException {
        ZipInputStream zipInputStream = dbConnectionService.getDbExportFile();
        InputStreamResource inputStreamResource = new InputStreamResource(zipInputStream);

        int length = 0;
        try {
            length = zipInputStream.available();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        var localDate = LocalDateTime.now();
        var dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm");
        var printDate = localDate.format(dtf);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Length", String.valueOf(length));
        httpHeaders.add("Content-Disposition", "attachment; filename=" + printDate + "-DbExport.sql");

        return new ResponseEntity<>(inputStreamResource, httpHeaders, HttpStatus.CREATED);
    }

    // This really doesn't seem the right way to do this
    @GetMapping(path = "export/volume")
    public ResponseEntity<?> exportToVolume() {

        boolean success = dbConnectionService.saveExportedDbFile();

        if(success) {
            return ResponseEntity.ok().body(HttpStatus.CREATED);
        } else {
            return ResponseEntity.ok().body(HttpStatus.EXPECTATION_FAILED);
        }
    }

}
