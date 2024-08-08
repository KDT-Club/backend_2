package com.ac.su.community.post;

import com.ac.su.comment.Comment;
import com.ac.su.community.attachment.AttachmentFlag;
import com.ac.su.community.board.Board;
import com.ac.su.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increasement
    @Column(name="post_id")
    private Long id;
    @Column
    private String title;
    @Column
    private String content;
    @Column
    @CreationTimestamp
    private LocalDateTime createdAt; // 생성 날짜
    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt; // 수정 날짜
    @Column
    @Enumerated(EnumType.STRING)
    private AttachmentFlag attachmentFlag; // Enum 타입으로 변경
    @Column
    private String postType;
    @Column
    private String clubName;
    @ManyToOne
    @JoinColumn(name="member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member; // 회원 고유번호
    @ManyToOne
    @JoinColumn(name="board_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    private Board boardId;  //게시판 고유 번호

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>(); // 초기화
}
