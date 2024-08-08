package com.ac.su.community.post;


import com.ac.su.community.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByBoardId(Board boardId);
    List<Post> findByBoardIdAndClubName(Board board, String clubName);
    List<Post> findByMemberId(Long memberId);
}

