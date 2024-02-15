package com.ssafy.relpl.db.postgre.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 생성 전략을 설정해주어야 합니다.
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "report_date")
    private String reportDate;

    @Column(name = "report_coordinate")
    private Point reportCoordinate;

    @Column(name = "tmap_id", nullable = true)
    private Long tmapId;

    //외래키
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER) // 다대일(N:1) 관계를 표시
    @JoinColumn(name = "user_id")
    private User user;

}
