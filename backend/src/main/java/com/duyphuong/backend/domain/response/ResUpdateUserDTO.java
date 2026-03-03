package com.duyphuong.backend.domain.response;

import java.time.Instant;

import com.duyphuong.backend.util.constant.GenderEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private Instant updateAt;
}
