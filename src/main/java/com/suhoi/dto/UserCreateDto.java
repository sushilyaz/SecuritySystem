package com.suhoi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserCreateDto {
    private String username;
    private String password;
    private List<String> path;
    private String cardUid;
}
