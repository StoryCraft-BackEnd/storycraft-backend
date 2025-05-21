package com.storycraft.auth.dto;

public class RefreshTokenResponseDto {
    private int status;
    private String message;
    private Data data;

    public RefreshTokenResponseDto(int status, String message, String accessToken) {
        this.status = status;
        this.message = message;
        this.data = new Data(accessToken);
    }

    static class Data {
        private String access_token;

        public Data(String access_token) {
            this.access_token = access_token;
        }

        public String getAccess_token() {
            return access_token;
        }
    }

    public int getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
    public Data getData() {
        return data;
    }
}
