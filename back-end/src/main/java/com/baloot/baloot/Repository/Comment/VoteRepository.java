package com.baloot.baloot.Repository.Comment;

import com.baloot.baloot.models.Comment.Comment;
import com.baloot.baloot.models.Comment.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Vote getVoteByCommentCommentIdAndUserUsername(int commentId, String username);

    List<Vote> findByComment(Comment comment);

    List<Vote> findVotesByComment_CommentId(int commentId);

    List<Vote> findVotesByUserUsername(String username);

    int countByCommentAndVote(Comment comment, int vote);

    int countByComment_CommentIdAndVote(int commentId, int vote);

}
