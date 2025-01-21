package br.com.hadryan.api.service;

import br.com.hadryan.api.data.DownloadPostRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class DownloadService {

    public File download(DownloadPostRequest request) {
        String tempDirectory = System.getProperty("java.io.tmpdir") + File.separator + "yt-dlp-downloads";
        File outputDir = new File(tempDirectory);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new RuntimeException("Failed to create temporary directory for downloads");
        }

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(
                "yt-dlp",
                "--cookies", "/home/ubuntu/app/cookies.txt",
                "-f", "bv+ba/b",
                "-S", "res:" + request.getResolution(),
                "-o", tempDirectory + File.separator + "%(title)s.%(ext)s",
                request.getUrl()
        );
        log.info("Download command: {}", processBuilder.command());
        try {
            log.info("Starting download process");
            Process process = processBuilder.start();

            new Thread(() -> consumeStream(process.getInputStream(), "INFO")).start();
            new Thread(() -> consumeStream(process.getErrorStream(), "ERROR")).start();

            boolean completed = process.waitFor(5, TimeUnit.MINUTES);
            if (!completed) {
                log.error("Download process timed out");
                process.destroy();
                throw new RuntimeException("Download timed out");
            }

            if (process.exitValue() != 0) {
                log.error("Download failed with exit code: {}", process.exitValue());
                throw new RuntimeException("Download process failed");
            }

            log.info("Download completed successfully");
            return getDownloadFile(outputDir.toPath());
        } catch (Exception e) {
            log.error("Error during download", e);
            throw new RuntimeException("Failed to download video", e);
        }
    }

    private File getDownloadFile(Path path) throws IOException {
        return Files.list(path)
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Downloaded file not found"));
    }

    private void consumeStream(InputStream stream, String logLevel) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if ("INFO".equals(logLevel)) {
                    log.info(line);
                } else if ("ERROR".equals(logLevel)) {
                    log.error(line);
                }
            }
        } catch (IOException e) {
            log.error("Failed to consume process stream", e);
        }
    }
}
