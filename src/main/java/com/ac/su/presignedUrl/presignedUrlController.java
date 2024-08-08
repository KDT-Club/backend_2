package com.ac.su.presignedUrl;

import com.ac.su.ResponseMessage;
import com.ac.su.community.attachment.Attachment;
import com.ac.su.community.attachment.AttachmentRepository;
import com.ac.su.community.post.Post;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class presignedUrlController {
    private final S3Service s3Service;
//    private final AttachmentRepository attachmentRepository;

//    @GetMapping("/writeHtml")
//    public String writeHtml() {
//        return "write";
//    }
//
//    @GetMapping("/writeHtmlMuti")
//    public String writeHtmlMuti() {
//        return "writeMulti";
//    }

    //  - 유저쪽에서 쿼리스트링으로 보내준 정보 받아야하니까 @ResponseBody 어노테이션 추가해주고
//  - String filename으로 유저 쪽에 name 변수를 받아준다
    @GetMapping("/presigned-url")
    @ResponseBody
    String getURL(@RequestParam String filename){
        var result = s3Service.createPresignedUrl("test/" + filename);
        return result;
    }

//    //클라이언트에서 imgUrl을 받아서 DB에저장
//    // imgUrl, Post postId 저장해 줘야함
//    @PostMapping("/attachment")
//    @ResponseBody
//    public ResponseEntity attachmentTest(@RequestBody AttachmentRequest request) {
//
//        var imgUrl = request.getAttachmentName(); //imgurl
//        var postId = request.getPostId(); // 첨부파일이 달린 게시글 Id
//
//        System.out.println("imgURL: " + imgUrl);
//        System.out.println(postId);
//
//        //클라이언트에서 받은 거 첨부파일 데이터 DB에 저장하는 로직
//        Post post = new Post();
//        post.setId(postId);
//
//        Attachment attachment = new Attachment();
//        attachment.setAttachmentName(imgUrl);
//        attachment.setPostId(post); //post라고 보이지만 사실 postId만 저장하는 거임. post 객체에 postId만 설정하고 저장
//
//        attachmentRepository.save(attachment);
//        return ResponseEntity.ok(new ResponseMessage("성공"));
//    }
}


@Getter
@Setter
class AttachmentRequest {
    private String attachmentName; //imgUrl
    private Long postId; // 게시글 고유 번호
}
