package kr.easw.lesson07.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AWSPermissionKeyDto {
    @Getter
    private final String apiKey;

    @Getter
    private final String apiSecretKey;


    @Getter
    private final String apiKeyAdmin;

    @Getter
    private final String apiSecretKeyAdmin;
}
