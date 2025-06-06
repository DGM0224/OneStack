package com.onestack.project.domain;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Professional {
    private int proNo;
    private int memberNo;
    private int categoryNo;
    private String selfIntroduction;
    private String career;
    private String awardCareer;
    private int studentCount;
    private int rate;
    private Integer professorStatus;
    private String screeningMsg;
    private Timestamp proDate;
    private String contactableTime;
    private int averagePrice;
    private int reviewCount;
}

