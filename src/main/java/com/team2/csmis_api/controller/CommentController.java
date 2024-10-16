//package com.team2.csmis_api.controller;
//
//import com.team2.csmis_api.dto.CommentDTO;
//import com.team2.csmis_api.dto.HolidayDTO;
//import com.team2.csmis_api.entity.Comment;
//import com.team2.csmis_api.service.CommentService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/comments")
//public class CommentController {
//    @Autowired
//    CommentService commentService;
//
//    @PostMapping("")
//    public ResponseEntity<?> saveComment(@RequestBody Comment comment) {
//        try {
//            CommentDTO commentDTO = commentService.addComment(comment);
//            return ResponseEntity.ok(commentDTO);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }
//
//    @GetMapping("")
//    public List<CommentDTO> showAllComments() {
//        return commentService.showAllComments();
//    }
//
//    @GetMapping("{id}")
//    public ResponseEntity<CommentDTO> showById(@PathVariable("id") Integer id) {
//        return commentService.showByCommentId(id)
//                .map(CommentDTO -> ResponseEntity.ok(CommentDTO))
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<CommentDTO> updateComment(@PathVariable("id") Integer id, @RequestBody CommentDTO commentDTO) {
//        CommentDTO updatedComment = commentService.updateComment(id, commentDTO);
//        return ResponseEntity.ok(updatedComment);
//    }
//
//    @DeleteMapping("{id}")
//    public ResponseEntity<Void> deleteComment(@PathVariable("id") Integer id) {
//        commentService.deleteComment(id);
//        return ResponseEntity.noContent().build();
//    }
//
//}
