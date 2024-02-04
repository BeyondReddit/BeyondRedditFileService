package com.example.fileservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController {
    private final FileStorageService fileStorageService;

    @Autowired
    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUri = fileStorageService.uploadFile(file);
        return ResponseEntity.ok().body(fileUri);
    }

    @PostMapping("/update/{fileName}")
    public ResponseEntity<String> updateFile(@RequestParam("file") MultipartFile file, @PathVariable String fileName) {
        String fileUri = fileStorageService.updateFile(file, fileName);
        return ResponseEntity.ok().body(fileUri);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<String> getFile(@PathVariable String fileName) {
        String fileUrl = fileStorageService.getFileUrl(fileName);
        return ResponseEntity.ok().body(fileUrl);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileName) {
        fileStorageService.deleteFile(fileName);
        return ResponseEntity.noContent().build();
    }
}
