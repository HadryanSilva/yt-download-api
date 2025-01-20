package br.com.hadryan.api.controller;

import br.com.hadryan.api.data.DownloadPostRequest;
import br.com.hadryan.api.service.DownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/download")
public class DownloadController {

    private final DownloadService downloadService;

    @PostMapping
    public ResponseEntity<Resource> requestDownload(@RequestBody DownloadPostRequest request) {
        File file = downloadService.download(request);

        Resource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

}
