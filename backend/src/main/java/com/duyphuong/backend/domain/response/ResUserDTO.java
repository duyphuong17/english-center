package com.duyphuong.backend.domain.response;

import java.time.Instant;

import com.duyphuong.backend.util.constant.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
    private long id;
    private String email;
    private String name;
    private GenderEnum gender;
    private Instant updatedAt;
    private Instant createdAt;
}
