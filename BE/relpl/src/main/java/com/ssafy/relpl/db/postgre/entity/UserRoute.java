package com.ssafy.relpl.db.postgre.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="userroute")
public class UserRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_move_id")
    private Long userMoveId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "user_nickname", nullable = false, length = 100)
    private String userNickname;

    @Column(name = "project_name", nullable = false, length = 100)
    private String projectName;

    @Column(name = "user_move_start", nullable = false, length = 30)
    private String userMoveStart;

    @Column(name = "user_move_end", nullable = false, length = 30)
    private String userMoveEnd;

    @Column(name = "user_move_distance")
    private int userMoveDistance;

    @Column(name = "user_move_time", nullable = false)
    private int userMoveTime;

    @Column(name = "user_move_path", nullable = false, length = 100)
    private String userMovePath;

    @Column(name = "user_move_memo", length = 300)
    private String userMoveMemo;

    @Column(name = "user_move_image", length = 300)
    private String userMoveImage;

}