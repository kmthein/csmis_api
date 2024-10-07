package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {

    @Query("select c from Comment c where c.isDeleted<>true")
    public List<Comment> getAllComments();

    @Modifying
    @Transactional
    @Query("update Comment c set c.isDeleted=true where c.id=:id")
    public void deleteComment(Integer id);

}
