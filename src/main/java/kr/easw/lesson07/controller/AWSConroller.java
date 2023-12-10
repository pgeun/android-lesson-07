package kr.easw.lesson07.controller;

import kr.easw.lesson07.model.dto.AWSKeyDto;
import kr.easw.lesson07.service.AWSService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rest/aws")
public class AWSConroller {
    private final AWSService awsController;

    @PostMapping("/auth")
    private ModelAndView onAuth(AWSKeyDto awsKey) {
        try {
            awsController.initAWSAPI(awsKey);
            return new ModelAndView("redirect:/");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ModelAndView("redirect:/server-error?errorStatus=" + ex.getMessage());
        }
    }

    @GetMapping("/list")
    private List<String> onFileList() {
        return awsController.getFileList();
    }

    @PostMapping("/upload")
    private ModelAndView onUpload(@RequestParam("file") MultipartFile file) {
        try {
            awsController.upload(file);
            return new ModelAndView("redirect:/?success=true");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ModelAndView("redirect:/server-error?errorStatus=" + ex.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> onDownload(@RequestParam("filename") String fileName) {
        try {
            Resource resource = awsController.downloadFile(fileName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}