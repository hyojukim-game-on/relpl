package com.ssafy.relpl.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@ToString
public class RankingEntry {
        private String nickname;
        private int distance;
}
