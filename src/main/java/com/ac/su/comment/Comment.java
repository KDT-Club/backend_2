package com.ac.su.comment;

import com.ac.su.community.post.Post;
import com.ac.su.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-incremnet
    @Column(name="comment_id")
    private Long id;
    @Column
    private String content; //댓글 내용
    @CreationTimestamp
    @Column
    private LocalDateTime createdAt; // 생성 날짜
    @ManyToOne
    @JoinColumn(name="post_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Post post; // 게시글 고유 번호, postId -> post
    @ManyToOne
    @JoinColumn(name="member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member; // 회원 고유번호

    public void setPost(Post post) {
        this.post = post;
        post.getComments().add(this);
    }
}