package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.CommentDTO;
import com.team2.csmis_api.entity.Announcement;
import com.team2.csmis_api.entity.Comment;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.AnnouncementRepository;
import com.team2.csmis_api.repository.CommentRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.modelmapper.ModelMapper; // ModelMapper ကို Import လုပ်ပါ
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public CommentDTO addComment(Comment comment) {
        User userId = userRepository.findById(comment.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        comment.setUser(userId);

        List<Announcement> announcements = comment.getAnnouncements();
        if (announcements == null || announcements.isEmpty()) {
            throw new IllegalArgumentException("No announcements found");
        }

        Announcement announcement = announcementRepository.findById(announcements.get(0).getId())
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found"));
        announcement.getComments().add(comment);
        comment.setAnnouncements(Collections.singletonList(announcement));

        comment.setDate(LocalDate.now());

        Comment savedComment = commentRepository.save(comment);

        return modelMapper.map(savedComment, CommentDTO.class);
    }

    public CommentDTO convertToCommentDto(Comment comments) {
        return modelMapper.map(comments, CommentDTO.class);
    }

    public List<CommentDTO> showAllComments() {
        return commentRepository.getAllComments().stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());
    }

    public Optional<CommentDTO> showByCommentId(Integer id) {
        return commentRepository.findById(id)
                .map(this::convertToCommentDto);
    }

    public CommentDTO updateComment(Integer id, CommentDTO commentDTO) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with ID: " + id));
        modelMapper.map(commentDTO,existingComment);
        Comment updatedComment = commentRepository.save(existingComment);
        return convertToCommentDto(updatedComment);
    }

    public void deleteComment(Integer id){
        commentRepository.deleteComment(id);
    }

}
