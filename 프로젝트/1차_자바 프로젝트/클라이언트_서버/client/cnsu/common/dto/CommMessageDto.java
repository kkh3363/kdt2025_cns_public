package com.cnsu.common.dto;

public class CommMessageDto {
    private String command;
    private String data;
    private String type;



    public CommMessageDto( ) {
    }
    public CommMessageDto(String command, String data, String type) {
        this.command = command;
        this.data = data;
        this.type = type;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
