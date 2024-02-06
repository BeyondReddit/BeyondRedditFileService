package com.example.fileservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController {
    private final FileStorageService fileStorageService;
    private final AuthServiceFeignClient authServiceFeignClient;

    @Autowired
    public FileController(FileStorageService fileStorageService, AuthServiceFeignClient authServiceFeignClient) {
        this.fileStorageService = fileStorageService;
        this.authServiceFeignClient = authServiceFeignClient;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,  @RequestHeader("Authorization") String authorizationHeader) {
        UserInfo userInfo = authServiceFeignClient.getCurrentUser(authorizationHeader).orElse(null);
        System.out.println(userInfo.getAuthorities());
        if(!userInfo.getAuthorities().equals("USER") && !userInfo.getAuthorities().equals("admin"))
            return ResponseEntity.badRequest().body("You don't have permission");
        String fileUri = fileStorageService.uploadFile(file);
        return ResponseEntity.ok().body(fileUri);
    }

    @PostMapping("/update/{fileName}")
    public ResponseEntity<String> updateFile(@RequestParam("file") MultipartFile file, @PathVariable String fileName,  @RequestHeader("Authorization") String authorizationHeader) {
        UserInfo userInfo = authServiceFeignClient.getCurrentUser(authorizationHeader).orElse(null);
        if(!userInfo.getAuthorities().equals("USER") && !userInfo.getAuthorities().equals("admin"))
            return ResponseEntity.badRequest().body("You don't have permission");
        String fileUri = fileStorageService.updateFile(file, fileName);
        return ResponseEntity.ok().body(fileUri);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<String> getFile(@PathVariable String fileName,  @RequestHeader("Authorization") String authorizationHeader) {
        UserInfo userInfo = authServiceFeignClient.getCurrentUser(authorizationHeader).orElse(null);
        if(!userInfo.getAuthorities().equals("USER") && !userInfo.getAuthorities().equals("admin"))
            return ResponseEntity.badRequest().body("You don't have permission");
        String fileUrl = fileStorageService.getFileUrl(fileName);
        return ResponseEntity.ok().body(fileUrl);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName,  @RequestHeader("Authorization") String authorizationHeader) {
        UserInfo userInfo = authServiceFeignClient.getCurrentUser(authorizationHeader).orElse(null);
        if(!userInfo.getAuthorities().equals("USER") && !userInfo.getAuthorities().equals("admin"))
            return ResponseEntity.badRequest().body("You don't have permission");
        fileStorageService.deleteFile(fileName);
        return ResponseEntity.noContent().build();
    }
}
